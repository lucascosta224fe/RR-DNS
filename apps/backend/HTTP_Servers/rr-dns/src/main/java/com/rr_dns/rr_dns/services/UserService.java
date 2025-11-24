package com.rr_dns.rr_dns.services;

import com.rr_dns.rr_dns.dtos.CreateUserDto;
import com.rr_dns.rr_dns.dtos.LoginUserDto;
import com.rr_dns.rr_dns.dtos.RecoveryJwtTokenDto;
import com.rr_dns.rr_dns.dtos.SessionResponseDto;
import com.rr_dns.rr_dns.dtos.UserDto;
import com.rr_dns.rr_dns.entities.User;
import com.rr_dns.rr_dns.exception.EmailAlreadyExistsException;
import com.rr_dns.rr_dns.exception.InvalidCredentialsException;
import com.rr_dns.rr_dns.exception.InvalidTokenException;
import com.rr_dns.rr_dns.exception.MissingTokenException;
import com.rr_dns.rr_dns.exception.SessionExpiredException;
import com.rr_dns.rr_dns.exception.UserNotFoundException;
import com.rr_dns.rr_dns.repositories.UserRepository;
import com.rr_dns.rr_dns.security.authentication.JwtTokenService;
import com.rr_dns.rr_dns.security.config.SecurityConfiguration;
import com.rr_dns.rr_dns.security.userDetails.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisSessionService redisSessionService;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    /**
     * Autenticação MANUAL:
     *  - Busca o usuário por e-mail
     *  - Confere a senha com o PasswordEncoder
     *  - Gera o JWT
     *  - (Tenta) salvar info no Redis, mas NÃO deixa o erro do Redis virar 500
     */
    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {

        // 1) Busca usuário pelo e-mail
        User user = userRepository.findByEmail(loginUserDto.email())
                .orElseThrow(InvalidCredentialsException::new);

        // 2) Confere senha com o mesmo encoder usado no cadastro
        boolean senhaOk = securityConfiguration
                .passwordEncoder()
                .matches(loginUserDto.password(), user.getPassword());

        if (!senhaOk) {
            throw new InvalidCredentialsException();
        }

        // 3) Monta UserDetails e gera JWT
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = jwtTokenService.generateToken(userDetails);

        // 4) Tenta salvar no Redis, mas se der erro só loga e segue
        try {
            redisSessionService.saveLoginAt(token, System.currentTimeMillis());
        } catch (Exception e) {
            System.out.println("[AVISO] Redis indisponível em authenticateUser: " + e.getMessage());
        }

        return new RecoveryJwtTokenDto(token);
    }

    public void registerUser(CreateUserDto createUserDto) {

        Optional<User> existingUser = userRepository.findByEmail(createUserDto.email());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        User newUser = User.builder()
                .email(createUserDto.email())
                .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                .nome(createUserDto.nome())
                .dataNascimento(createUserDto.dataNascimento())
                .descricao(createUserDto.descricao())
                .build();

        userRepository.save(newUser);
    }

    public SessionResponseDto getProfile(HttpServletRequest request) {

        // Recupera o token (header Authorization OU sessão HTTP)
        String token = jwtTokenService.recoveryToken(request);
        if (token == null || token.isBlank()) {
            throw new MissingTokenException();
        }

        // Extrai o e-mail (subject) do token
        String email;
        try {
            email = jwtTokenService.getSubject(token);
        } catch (Exception e) {
            throw new InvalidTokenException();
        }

        // Verifica sessão HTTP
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new SessionExpiredException();
        }

        String sessionId = session.getId();

        // Busca o usuário no banco
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        // Tenta ler o loginAt do Redis (se não der, segue sem isso)
        Long loginAt;
        try {
            loginAt = redisSessionService.getLoginAt(token);
        } catch (Exception e) {
            System.out.println("[AVISO] Redis indisponível em getProfile: " + e.getMessage());
            loginAt = null;
        }

        UserDto userDto = new UserDto(
                user.getNome(),
                user.getDataNascimento() != null ? user.getDataNascimento().toString() : null,
                user.getDescricao()
        );

        return new SessionResponseDto(
                sessionId,
                loginAt,
                userDto
        );
    }
}
