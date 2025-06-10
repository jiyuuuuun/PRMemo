package io.github.jiyuuuuun.PRMemo.global.security;

import io.github.jiyuuuuun.PRMemo.global.util.JwtUtil;
import io.github.jiyuuuuun.PRMemo.global.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@Tag(name = "Auth", description = "인증·토큰 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshService;
    private final UserDetailsService userDetailsService;

    @Operation(summary = "토큰 재발급", description = "refresh_token 쿠키 검증 후 새로운 Access/Refresh 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest req,
                                             HttpServletResponse res) {
        String refreshToken = extractCookie(req, "refresh_token");
        if (refreshToken == null ||
                !refreshService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String login = jwtUtil.getSubject(refreshToken);
        Map<String,Object> claims = Map.of("login", login);
        String newAccess  = jwtUtil.generateAccessToken(claims, login);
        String newRefresh = jwtUtil.generateRefreshToken(claims, login);

        // Redis 갱신
        refreshService.storeRefreshToken(newRefresh);

        // 쿠키 재설정
        Cookie aCookie = new Cookie("access_token", newAccess);
        aCookie.setHttpOnly(true);
        aCookie.setPath("/");
        aCookie.setMaxAge(60 * 60);

        Cookie rCookie = new Cookie("refresh_token", newRefresh);
        rCookie.setHttpOnly(true);
        rCookie.setPath("/");
        rCookie.setMaxAge(7 * 24 * 60 * 60);

        res.addCookie(aCookie);
        res.addCookie(rCookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃", description = "refresh_token 쿠키를 사용해 Redis에서 토큰을 삭제하고, 쿠키를 만료 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest req,
                                         HttpServletResponse res) {
        String refreshToken = extractCookie(req, "refresh_token");
        if (refreshToken != null) {
            String login = jwtUtil.getSubject(refreshToken);
            refreshService.deleteRefreshToken(login);
        }
        // 쿠키 삭제
        Cookie clearA = new Cookie("access_token", null);
        clearA.setHttpOnly(true);
        clearA.setPath("/");
        clearA.setMaxAge(0);
        Cookie clearR = new Cookie("refresh_token", null);
        clearR.setHttpOnly(true);
        clearR.setPath("/");
        clearR.setMaxAge(0);

        res.addCookie(clearA);
        res.addCookie(clearR);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    private String extractCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        return Arrays.stream(req.getCookies())
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}