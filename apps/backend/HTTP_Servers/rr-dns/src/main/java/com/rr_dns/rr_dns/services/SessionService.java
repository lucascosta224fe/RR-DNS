package com.rr_dns.rr_dns.services;

import com.rr_dns.rr_dns.entities.Session;
import com.rr_dns.rr_dns.entities.User;
import com.rr_dns.rr_dns.exception.MissingTokenException;
import com.rr_dns.rr_dns.exception.SessionExpiredException;
import com.rr_dns.rr_dns.exception.UserNotFoundException;
import com.rr_dns.rr_dns.repositories.UserRepository;
import com.rr_dns.rr_dns.security.authentication.JwtTokenService;
import com.rr_dns.rr_dns.security.userDetails.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    public void isValidToken(HttpServletRequest request, Session session) {
        String token = jwtTokenService.recoveryToken(request);
        if (token == null) throw new MissingTokenException();

        if (token.equals(session.getToken()) && session.getExpiration().isAfter(LocalDateTime.now()))
            return;
        throw new SessionExpiredException();
    }

    public String createSession(UserDetailsImpl userDetails) {
        String token = jwtTokenService.generateToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);

        Session session = user.getSession();

        if (session == null) {
            session = Session.builder().token(token).expiration(LocalDateTime.now().plusMinutes(5)).build();
            user.setSession(session);
        } else {
            user.getSession().setToken(token);

            user.getSession().setExpiration(LocalDateTime.now().plusMinutes(5));
        }

        userRepository.save(user);

        return token;
    }
}
