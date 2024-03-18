package com.readease.userservice.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@Service
public class JwtService {
    @Value("${spring.application.security.jwt.expiration}")
    private long accessTokenExpiration;
    @Value("${spring.application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;
    @Value("${spring.application.security.jwt.expiration-reset-password}")
    private long resetPasswordExpiration;
    @Value("${spring.application.security.jwt.secret-key}")
    private String SECRET_KEY;

    public String extractUserEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }
    public <T> T extractClaims(String token , Function<Claims , T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSecretKey(){
        byte[] keyByte = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public String generateResetPasswordToken(UserDetails userDetails){
        return buildToken(new HashMap<>(), userDetails, this.resetPasswordExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails){
        return buildToken(new HashMap<>(), userDetails, this.refreshTokenExpiration);
    }
    public String generateToken(UserDetails userDetails){
        return buildToken(new HashMap<>(), userDetails, this.accessTokenExpiration);
    }
    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ){
      return Jwts
              .builder()
              .setClaims(extraClaims)
              .setSubject(userDetails.getUsername())
              .setIssuedAt(new Date(System.currentTimeMillis()))
              .setExpiration(new Date(System.currentTimeMillis() + expiration))
              .signWith(getSecretKey(), SignatureAlgorithm.HS256)
              .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userEmail = extractUserEmail(token);
        return (userEmail.equals(userDetails.getUsername())) && !isTokenExpried(((token)));
    }

    public boolean isTokenExpried(String token) {
        return extractExpriration(token).before(new Date());
    }

    private Date extractExpriration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

}
