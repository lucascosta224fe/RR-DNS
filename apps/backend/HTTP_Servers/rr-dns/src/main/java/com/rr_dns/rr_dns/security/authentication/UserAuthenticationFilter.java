package com.rr_dns.rr_dns.security.authentication;

import com.rr_dns.rr_dns.services.RedisSessionService;
import com.rr_dns.rr_dns.security.userDetails.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisSessionService redisSessionService;

    public UserAuthenticationFilter(
            JwtTokenService jwtTokenService,
            UserDetailsServiceImpl userDetailsService,
            RedisSessionService redisSessionService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
        this.redisSessionService = redisSessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Pega o token do header Authorization OU da sessão HTTP
        String token = jwtTokenService.recoveryToken(request);

        if (token != null && !token.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                String userEmail = jwtTokenService.getSubject(token);

                var userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Valida o token com base no usuário
                if (jwtTokenService.isTokenValid(token, userDetails)) {

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (Exception ex) {
                // Token inválido ou erro na validação → segue a requisição sem autenticar
            }
        }

        filterChain.doFilter(request, response);
    }
}
