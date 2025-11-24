package com.rr_dns.rr_dns.controllers;

import com.rr_dns.rr_dns.dtos.CreateUserDto;
import com.rr_dns.rr_dns.dtos.LoginUserDto;
import com.rr_dns.rr_dns.dtos.RecoveryJwtTokenDto;
import com.rr_dns.rr_dns.dtos.SessionResponseDto;
import com.rr_dns.rr_dns.services.RedisSessionService;
import com.rr_dns.rr_dns.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisSessionService redisSessionService;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(
            @RequestBody LoginUserDto loginUserDto,
            HttpServletRequest request
    ) {
        // autentica usuário e gera JWT
        RecoveryJwtTokenDto tokenDto = userService.authenticateUser(loginUserDto);

        String jwtToken = tokenDto.token();
        String userEmail = loginUserDto.email();

        // Cria/recupera sessão HTTP e guarda email + token
        HttpSession session = request.getSession(true);
        session.setAttribute("email", userEmail);
        session.setAttribute("token", jwtToken);

        // Se em algum momento você ligar Redis de verdade, isso continua funcionando.
        Duration ttl = Duration.ofHours(1); // TTL simbólico
        redisSessionService.saveToken(jwtToken, userEmail, ttl);
        redisSessionService.saveSession(
                session.getId(),
                Map.of("email", userEmail),
                ttl
        );

        // Front não precisa do token pra funcionar, mas devolvemos mesmo assim
        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    // REGISTRO
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody CreateUserDto createUserDto) {
        userService.registerUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // PERFIL (usa token do header ou da sessão)
    @GetMapping("/profile")
    public ResponseEntity<SessionResponseDto> getProfile(HttpServletRequest request) {
        try {
            SessionResponseDto response = userService.getProfile(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
