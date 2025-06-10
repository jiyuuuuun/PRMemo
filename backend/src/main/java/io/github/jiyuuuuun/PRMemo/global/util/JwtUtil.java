package io.github.jiyuuuuun.PRMemo.global.util;


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

    // 1시간짜리 Access 토큰
    public String generateAccessToken(Map<String, Object> claims, String subject) {
        return buildToken(claims, subject, 1000L * 60 * 60);
    }

    // 7일짜리 Refresh 토큰
    public String generateRefreshToken(Map<String, Object> claims, String subject) {
        return buildToken(claims, subject, 1000L * 60 * 60 * 24 * 7);
    }

    private String buildToken(Map<String, Object> claims, String subject, long ttlMillis) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMillis);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
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

    // 토큰 만료일자 조회를 위해 parserBuilder 사용
    public Date getExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
