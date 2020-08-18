package com.example.springdemo.security;

import com.example.springdemo.model.role.Role;
import com.example.springdemo.model.user.User;
import com.example.springdemo.model.userrole.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;


@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.accessTokenExpirationInMs}")
    private int accessTokenExpirationInMs;

    @Value("${app.refreshTokenExpirationInMs}")
    private int refreshTokenExpirationInMs;

    public String generateToken(String subjectId, int expirationInMs, Map<String, Object>... claims) {

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expirationInMs);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(subjectId)
                .setIssuedAt(now)
                .setExpiration(expiredDate);
        if (claims.length > 0) {
            for (String key: claims[0].keySet()) {
                jwtBuilder.claim(key, claims[0].get(key));
            }
        }
        return jwtBuilder.signWith(getSecretKey()).compact();
    }

    public String generateAccessToken(User user) {
        String roleName = user.getUserRoles().get(0).getRole().getName();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("name", user.getName());
        claims.put("roleName", roleName);
        return generateToken(user.getId().toString(), accessTokenExpirationInMs, claims);
    }


    public String generateAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String username = userPrincipal.getUsername();
        String name = userPrincipal.getName();
        String roleName = userPrincipal.getAuthorities().iterator().next().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("name", name);
        claims.put("roleName", roleName);
        return generateToken(userPrincipal.getId().toString(), accessTokenExpirationInMs, claims);
    }

    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getId().toString(), refreshTokenExpirationInMs);
    }
    public String generateRefreshToken(String subjectId) {
        return generateToken(subjectId, refreshTokenExpirationInMs);
    }

    public User getUserObjFromJWT(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Integer id = Integer.parseInt(claims.getSubject());
        String username = claims.get("username").toString();
        String name = claims.get("name").toString();
        String roleName = claims.get("roleName").toString();

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setName(name);
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(new UserRole(user, new Role(roleName)));
        return user;
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("ExpiredJwt: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("UnsupportedJwt: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("MalformedJwt: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgument: " + e.getMessage());
        } catch (SignatureException e) {
            logger.error("Signature: " + e.getMessage());
        }
        return false;
    }

    private Key getSecretKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        Key key = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        return key;
    }

    public Integer getUserIdFromJWT(String accessToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }
}
