package io.github.jiyuuuuun.PRMemo.security;

import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import io.github.jiyuuuuun.PRMemo.domain.user.repository.UserRepository;
import io.github.jiyuuuuun.PRMemo.security.util.JwtUtil;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

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

        // JWT 토큰 만들기
        String token = jwtUtil.generateToken(Map.of("login", login, "name", name), login);

        // 👇 쿠키에 토큰 저장 (httpOnly로 설정)
        Cookie jwtCookie = new Cookie("access_token", token);
        jwtCookie.setHttpOnly(true);              // JS에서 못 읽음
        jwtCookie.setPath("/");                   // 전체 경로에서 사용 가능
        jwtCookie.setMaxAge(60 * 60);             // 1시간 유효
        // jwtCookie.setSecure(true);             // HTTPS 환경에서만 전송 (배포 시 설정)

        response.addCookie(jwtCookie);

        // 👉 프론트로 리디렉션 (쿼리 파라미터 없음!)
        response.sendRedirect("http://localhost:3000/oauth-success");
    }
}
