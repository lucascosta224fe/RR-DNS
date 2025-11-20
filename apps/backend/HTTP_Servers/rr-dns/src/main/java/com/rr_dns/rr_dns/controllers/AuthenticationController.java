package com.rr_dns.rr_dns.controllers;

import com.rr_dns.rr_dns.dtos.CreateUserDto;
import com.rr_dns.rr_dns.dtos.LoginUserDto;
import com.rr_dns.rr_dns.dtos.RecoveryJwtTokenDto;
import com.rr_dns.rr_dns.services.UserService;
import com.rr_dns.rr_dns.services.RedisSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(
            @RequestBody LoginUserDto loginUserDto,
            HttpServletRequest request
    ) {
        //processo de autenticação
        RecoveryJwtTokenDto tokenDto = userService.authenticateUser(loginUserDto);

        String jwtToken = tokenDto.token();
        String userEmail = loginUserDto.email();

        //criar sessão no servidor
        HttpSession session = request.getSession(true);
        session.setAttribute("email", userEmail);

        //salvar token no Redis com TTL igual ao JWT
        Duration ttl = Duration.ofMillis(jwtExpiration);
        redisSessionService.saveToken(jwtToken, userEmail, ttl);

        //salvar dados adicionais na sessão do Redis
        redisSessionService.saveSession(
                session.getId(),
                Map.of("email", userEmail),
                ttl
        );

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody CreateUserDto createUserDto) {
        userService.registerUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
