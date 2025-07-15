package com.example.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtToken {

    private static final String SECRET_KEY = "mia-chiave-segreta-super-lunga-per-jwt1234567890"; // almeno 32 caratteri
    private static final long EXPIRATION_TIME = 10 * 60 * 1000;  // 10 minuti * 60 secondi * 1000 millisecondi

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generaToken(String username,String ruolo) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", ruolo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String estraiRuolo(String token) {
        return estraiClaims(token).get("role", String.class);
    }
    

public String estraiUsername(String token) {
    return estraiClaims(token).getSubject();
}

public boolean isTokenValido(String token, UserDetails userDetails) {
    final String username = estraiUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenScaduto(token);
}

private boolean isTokenScaduto(String token) {
    return estraiClaims(token).getExpiration().before(new Date());
}

private Claims estraiClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
}

}