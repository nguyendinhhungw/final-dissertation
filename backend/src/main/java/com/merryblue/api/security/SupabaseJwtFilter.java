package com.merryblue.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filter xác thực JWT token phát hành bởi Supabase Auth.
 * - Đọc token từ header: Authorization: Bearer <token>
 * - Verify chữ ký bằng SUPABASE_JWT_SECRET
 * - Trích xuất userId (sub) và email
 * - Set SecurityContext để Spring Security nhận diện user
 */
@Slf4j
@Component
public class SupabaseJwtFilter extends OncePerRequestFilter {

    @Value("${supabase.jwt-secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            try {
                Claims claims = parseToken(token);

                String userId = claims.getSubject();           // Supabase user UUID
                String email = claims.get("email", String.class);
                String supabaseRole = claims.get("role", String.class); // 'authenticated'

                // Tạo principal với thông tin từ JWT
                MerryblueUserPrincipal principal = new MerryblueUserPrincipal(userId, email);

                // Roles sẽ được load lazy từ DB khi controller cần
                // Ở đây set quyền cơ bản "ROLE_USER" cho mọi user đã auth
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_AUTHENTICATED")
                );

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, token, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authenticated user: {}, email: {}", userId, email);

            } catch (JwtException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                // Không throw exception - để SecurityContext trống
                // Spring Security sẽ từ chối nếu endpoint cần auth
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
