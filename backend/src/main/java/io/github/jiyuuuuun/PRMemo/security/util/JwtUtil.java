package io.github.jiyuuuuun.PRMemo.security.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final String secret;

    public JwtUtil(@Value("${JWT_SECRET_KEY}") String secret) {
        this.secret = secret;
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000 * 60 * 60); // 1시간

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret) // 서명 검증
                    .parseClaimsJws(token); // 예외 없으면 유효함
            return true;
        } catch (SignatureException e) {
            System.out.println("❌ JWT 서명 오류: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("❌ 잘못된 JWT 형식: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("❌ 만료된 JWT: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ 지원되지 않는 JWT: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 비어 있는 JWT claims: " + e.getMessage());
        }

        return false;
    }

    public String getSubject(String token) {
        return Jwts.parser()
                .setSigningKey(secret) // 서명 키 설정
                .parseClaimsJws(token) // 토큰 파싱 및 검증
                .getBody()             // 클레임 접근
                .getSubject();         // subject(claims.sub) 추출
    }
}
