package com.epam.esm.security;

import com.epam.esm.entity.Role;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider{
    private static final String SECRET_KEY = "secret-key-for-encryption";
    private static final long VALIDITY = 600000;
    private static final String AUTHORITY_KEY = "authority";
    private static final String ROLES_KEY = "role";

    private JwtParser parser;

    private String secretKey;
    private long validityInMilliseconds;

    @Autowired
    public JwtProvider() {

        this.secretKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        this.validityInMilliseconds = VALIDITY;
    }

    public String createToken(String username, Role role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLES_KEY, new SimpleGrantedAuthority(role.getAuthority()));
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiresAt)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    public boolean isValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public GrantedAuthority getRoles(String token) {
        Map<String, String> claims = (Map<String, String>) Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get(ROLES_KEY);
        return new SimpleGrantedAuthority(claims.get(AUTHORITY_KEY));
    }

}
