package utils;//package ru.kafedra.spring.Securityjwt.utils;
//
//package ru.kafedra.spring.Securityjwt.utils;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.security.Key;
//import java.time.Duration;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class JwtTokenUtils {
//
//    @Value("${security.jwt.secret.access}")
//    private String secret;
//
//    @Value("${security.jwt.secret.access.time}")
//    private Duration jwtLifetime;
//
//    public static Key key;
//
//
//    public String generateToken(UserDetails userDetails){
//        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
//        Map<String, Object> claims = new HashMap<>();
//        List<String> roleList = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
//
//        claims.put("roles", roleList);
//        System.out.println("claims" + claims);
//        Date issueDate = new Date();
//
//        System.out.println("secret" + secret);
//        System.out.println("lifetime" + jwtLifetime);
//        Date expireDate = new Date(issueDate.getTime()+ jwtLifetime.toMillis());
//
//        return Jwts.builder()
//                .claims(claims)
//                .subject(userDetails.getUsername())
//                .issuedAt(issueDate)
//                .expiration(expireDate)
//                .signWith(key)
//                .compact();
//    }
//
//    public String getUsername(String token){
//        String s = getAllClaimsFromToken(token).getSubject();
//        System.out.println("Subject s = " +s);
//        return s;
//    }
//
//    public List<String> getRoles(String token) {
//        return getAllClaimsFromToken(token).get("roles", List.class);
//    }
//
//    private Claims getAllClaimsFromToken(String token){
//        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
//        return Jwts.parser()
//                .verifyWith((SecretKey) key)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//
//
//    }
//
//
//
//
//
//}