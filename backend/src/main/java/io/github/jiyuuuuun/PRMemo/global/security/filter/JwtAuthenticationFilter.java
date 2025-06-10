package io.github.jiyuuuuun.PRMemo.global.security.filter;

import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import io.github.jiyuuuuun.PRMemo.domain.user.repository.UserRepository;
import io.github.jiyuuuuun.PRMemo.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository,UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 1. 쿠키에서 JWT 추출
        String token = extractTokenFromCookies(request);

        if (token != null && jwtUtil.validateToken(token)) {
            // 2. 토큰에서 사용자 정보 추출
            String login = jwtUtil.getSubject(token); // "sub" claim

            UserDetails userDetails = userDetailsService.loadUserByUsername(login);

            // 3. 사용자 정보 로드
            Optional<User> userOptional = userRepository.findByLogin(login);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // 4. 인증 객체 생성
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, List.of());

                // 5. SecurityContext에 설정
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 6. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("access_token")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
