package com.organization.webapp.security.jwt;

import com.organization.webapp.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${webapp.jwtSecret}")
    private String jwtSecret;

    @Value("${webapp.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException e) {
            logger.error("Invalid Jwt Signature: {}", e.getMessage());
        }
        catch (MalformedJwtException e) {
            logger.error("Invalid Jwt Token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e) {
            logger.error("Jwt token expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e) {
            logger.error("Jwt token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.error("Jwt Claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
