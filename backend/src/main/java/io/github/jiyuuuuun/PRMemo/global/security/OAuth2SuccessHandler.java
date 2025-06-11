package io.github.jiyuuuuun.PRMemo.global.security;

import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import io.github.jiyuuuuun.PRMemo.domain.user.repository.UserRepository;
import io.github.jiyuuuuun.PRMemo.global.util.JwtUtil;
import io.github.jiyuuuuun.PRMemo.global.service.RefreshTokenService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        // GitHub 사용자 정보 가져오기
        String login = user.getAttribute("login");
        String name = user.getAttribute("name");
        String avatar = user.getAttribute("avatar_url");


        // DB에 저장 (없으면 생성, 있으면 업데이트)
        User member = userRepository.findByLogin(login).orElseGet(User::new);
        member.setLogin(login);
        member.setName(name);
        member.setAvatarUrl(avatar);
        userRepository.save(member);

        // 토큰 생성
        Map<String,Object> claims = Map.of("login", login, "name", name);
        String accessToken  = jwtUtil.generateAccessToken(claims, login);
        String refreshToken = jwtUtil.generateRefreshToken(claims, login);

        // Redis에 저장
        refreshTokenService.storeRefreshToken(refreshToken);

        // Access Token 쿠키
        Cookie aCookie = new Cookie("access_token", accessToken);
        aCookie.setHttpOnly(true);
        aCookie.setPath("/");
        aCookie.setMaxAge(60 * 60); // 1시간
        // aCookie.setSecure(true);

        // Refresh Token 쿠키
        Cookie rCookie = new Cookie("refresh_token", refreshToken);
        rCookie.setHttpOnly(true);
        rCookie.setPath("/");
        rCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        // rCookie.setSecure(true);

        response.addCookie(aCookie);
        response.addCookie(rCookie);
        response.sendRedirect("http://localhost:3000/oauth-success");
    }
}
