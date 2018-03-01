package org.rta.citizen.common.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.model.UserSessionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3787799406661972082L;
    private static final String CLAIM_KEY_SESSION_ID = "sub";
    private static final String CLAIM_KEY_SCOPE = "scope";
    private static final String CLAIM_KEY_JTI = "jti";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_LIST_DELIMITERS = ",";
    private static final String CLAIM_TOKEN_TYPE = "type";

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${rta.registration.jwt.secret}")
    private String registrationSecret;
            
    @Value("${jwt.expiration}")
    private Long expiration;

    public Long getSessionIdFromToken(String token) {
        Long sessionId;
        try {
            final Claims claims = getClaimsFromToken(token);
            sessionId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            sessionId = null;
        }
        return sessionId;
    }
    
    public String getTokenType(String token) {
        String tokenType;
        try {
            final Claims claims = getClaimsFromToken(token);
            tokenType = (String) claims.get(CLAIM_TOKEN_TYPE);
        } catch (Exception e) {
            tokenType = null;
        }
        return tokenType;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public Date getRegExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromRegToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }
    
    public Long getUserIdFromToken(String token) {
        Long userId = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            String userIdStr = claims.getId();
            if (!StringUtils.isEmpty(userIdStr)) {
                userId = Long.parseLong(userIdStr);
            }
        } catch (Exception e) {
        }
        return userId;
    }

    public List<String> getUserRoleFromToken(String token) {
        List<String> roles;
        try {
            final Claims claims = getClaimsFromToken(token);
            String roleStr = (String) claims.get(CLAIM_KEY_SCOPE);
            roles = Arrays.asList(roleStr.split(CLAIM_LIST_DELIMITERS));
        } catch (Exception e) {
            roles = null;
        }
        return roles;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
    
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        if (null == expiration) return true; 
        return expiration.before(new Date());
    }
    
    public Boolean isRegTokenExpired(String token) {
        final Date expiration = getRegExpirationDateFromToken(token);
        if (null == expiration) return true; 
        return expiration.before(new Date());
    }

    public String generateToken(UserSessionModel userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_JTI, userDetails.getAadharNumber());
        claims.put(CLAIM_KEY_SESSION_ID, userDetails.getSessionId());
        String roles = userDetails.getAuthorities().stream().map(i -> i.toString())
                .collect(Collectors.joining(CLAIM_LIST_DELIMITERS));
        claims.put(CLAIM_KEY_SCOPE, roles);
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put(CLAIM_TOKEN_TYPE, TokenType.CITIZEN.toString());
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }
    
    private Claims getClaimsFromRegToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(registrationSecret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
    
    public Long getUserIdFromRegToken(String token) {
        Long userId = null;
        try {
            final Claims claims = getClaimsFromRegToken(token);
            String userIdStr = claims.getId();
            if (!StringUtils.isEmpty(userIdStr)) {
                userId = Long.parseLong(userIdStr);
            }
        } catch (Exception e) {
        }
        return userId;
    }
    
    public String getUserNameFromRegToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromRegToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    
    public List<String> getUserRoleFromRegistrationTokenToken(String token) {
        List<String> roles;
        try {
            final Claims claims = getClaimsFromRegToken(token);
            String roleStr = (String) claims.get(CLAIM_KEY_SCOPE);
            roles = Arrays.asList(roleStr.split(CLAIM_LIST_DELIMITERS));
        } catch (Exception e) {
            roles = null;
        }
        return roles;
    }
}
