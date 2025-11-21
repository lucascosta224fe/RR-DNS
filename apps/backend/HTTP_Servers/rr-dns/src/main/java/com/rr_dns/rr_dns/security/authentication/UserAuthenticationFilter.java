package com.rr_dns.rr_dns.security.authentication;

import com.rr_dns.rr_dns.exception.AuthenticationProcessException;
import com.rr_dns.rr_dns.exception.InvalidTokenException;
import com.rr_dns.rr_dns.exception.MissingTokenException;
import com.rr_dns.rr_dns.security.config.SecurityConfiguration;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rr_dns.rr_dns.security.userDetails.UserDetailsServiceImpl;
import com.rr_dns.rr_dns.services.RedisSessionService;
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
import java.util.Arrays;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisSessionService redisSessionService;

    public UserAuthenticationFilter(JwtTokenService jwtTokenService,
                                    UserDetailsServiceImpl userDetailsService,
                                    RedisSessionService redisSessionService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
        this.redisSessionService = redisSessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (shouldNotFilter(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 1 - Recupera o token
            String token = recoveryToken(request);
            if (token == null) {
                throw new MissingTokenException();
            }

            // 2 - VERIFICAÇÃO NO REDIS (revogação, logout, expiração no Redis)
            if (!redisSessionService.isTokenValid(token)) {
                throw new InvalidTokenException();
            }

            // 3 - Extrai e valida via JWT
            String userEmail = jwtTokenService.getSubjectFromToken(token);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                var userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtTokenService.isTokenValid(token, userDetails)) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new InvalidTokenException();
                }
            }

        } catch (JWTVerificationException e) {
            writeErrorResponse(response, 401, "Token inválido");
            return;
        } catch (Exception e) {
            writeErrorResponse(response, 500, "Erro no processo de autenticação");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String recoveryToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return Arrays.stream(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED)
                .anyMatch(uri -> uri.equals(requestURI));
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"error\": \"%s\"}", message)
        );
    }
}
