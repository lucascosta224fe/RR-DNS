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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    private final LocalDate DATA_NASCIMENTO_VALIDA = LocalDate.of(1910, 1, 1);

    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        try {

            if(loginUserDto.email() == null || loginUserDto.email().isEmpty()) throw new EmailEmptyException();
            if(!EmailIsValid(loginUserDto.email())) throw new InvalidEmailFormatException();
            if(loginUserDto.password() == null || loginUserDto.password().isEmpty()) throw new PasswordEmptyException();


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUserDto.email(),
                            loginUserDto.password()
                    )
            );

            User user = userRepository.findByEmail(loginUserDto.email()).orElseThrow(UserNotFoundException::new);

           String token = sessionService.createSession((UserDetailsImpl) authentication.getPrincipal());

            return new RecoveryJwtTokenDto(token, user.getId());

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

    public void registerUser(CreateUserDto createUserDto) {

        Optional<User> existingUser = userRepository.findByEmail(createUserDto.email());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        if(createUserDto.dataNascimento().isBefore(DATA_NASCIMENTO_VALIDA)) throw new InvalidDataException();

        User newUser = User.builder()
                .email(createUserDto.email())
                .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                .nome(createUserDto.nome())
                .dataNascimento(createUserDto.dataNascimento())
                .descricao(createUserDto.descricao())
                .build();

        userRepository.save(newUser);
    }

    public SessionResponseDto getProfile(HttpServletRequest request, Long id) {

        HttpSession session = request.getSession(false);
        if (session == null) throw new SessionExpiredException();

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        sessionService.isValidToken(request, user.getSession());

        UserDto userDto = new UserDto(
                user.getNome(),
                user.getDataNascimento() != null ? user.getDataNascimento().toString() : null,
                user.getDescricao()
        );

        return new SessionResponseDto(
                session.getId(),
                user.getSession().getExpiration(),
                "192.168.1.21",
                userDto
        );
    }

    public boolean EmailIsValid(String email) {
        String EMAIL_REGEX =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(EMAIL_REGEX);
    }


}
