package com.kingston.koratuwa.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;


@Service
public class JwtService {

    private static final String secretKey = "3273357638792F423F4528482B4D6250655368566D597133743677397A244326";
    private final Logger logger = LogManager.getLogger(getClass());

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails
    ) {
        // Calculate the timestamp for 11:59:59 PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date expirationTime = calendar.getTime();

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .claim("authorities", userDetails.getAuthorities())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationTime)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

//    public boolean tokenValidate(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }

    public boolean tokenValidate(String token, UserDetails userDetails) throws ExpiredJwtException {
        final String userName = extractUserName(token);
        if (userName.equals(userDetails.getUsername()) && !isTokenExpired(token)) {
            return true;
        } else {
            throw new ExpiredJwtException(null, null, "Request unauthorized - Expired JWT");
        }
    }

//    public boolean tokenValidate(String token, UserDetails userDetails) {
//        try {
//            final String userName = extractUserName(token);
//            return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
//        } catch (ExpiredJwtException eje) {
//            logger.error("Expired JWT Exception: {}", eje.getMessage());
//            return false; // or throw a custom exception, depending on your requirements
//        }
//    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
