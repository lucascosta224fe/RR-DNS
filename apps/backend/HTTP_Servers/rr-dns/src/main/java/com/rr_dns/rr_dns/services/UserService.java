package com.rr_dns.rr_dns.services;

import com.rr_dns.rr_dns.dtos.*;
import com.rr_dns.rr_dns.entities.User;
import com.rr_dns.rr_dns.exception.*;
import com.rr_dns.rr_dns.repositories.UserRepository;
import com.rr_dns.rr_dns.security.authentication.JwtTokenService;
import com.rr_dns.rr_dns.security.config.SecurityConfiguration;
import com.rr_dns.rr_dns.security.userDetails.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisSessionService redisSessionService;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUserDto.email(),
                            loginUserDto.password()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtTokenService.generateToken(userDetails);

            redisSessionService.saveLoginAt(token, System.currentTimeMillis());

            return new RecoveryJwtTokenDto(token);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
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

        String token = jwtTokenService.recoveryToken(request);
        if (token == null) throw new MissingTokenException();

        String userEmail = jwtTokenService.getSubjectFromToken(token);
        if (userEmail == null) throw new InvalidTokenException();

        HttpSession session = request.getSession(false);
        if (session == null) throw new SessionExpiredException();

        String sessionId = session.getId();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException());

        Long loginAt = redisSessionService.getLoginAt(token);

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
