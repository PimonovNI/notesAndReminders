package com.example.notesAndReminders.security;

import com.example.notesAndReminders.entities.enums.Role;
import com.example.notesAndReminders.util.exceptions.IdNotFoundException;
import com.example.notesAndReminders.util.exceptions.JWTAuthenticationException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private Long validityInMilliseconds;

    private final UserDetailsService userDetailsService;

    @Autowired
    public JWTTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Long id, String username, Role role, String email) {
        Claims claims = Jwts.claims()
                .setSubject(username);
        claims.put("id", id);
        claims.put("email", email);
        claims.put("role", role);

        Date nowDate = new Date();
        Date expDate = new Date(nowDate.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(nowDate)
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) throws IdNotFoundException {
        UserDetails userDetails = userDetailsService.loadUserById(getId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) throws JWTAuthenticationException {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (JwtException | IllegalArgumentException e) {
            throw new JWTAuthenticationException("JWT token is expired or invalid");
        }
    }

    private Long getId(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
    }

    private String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
