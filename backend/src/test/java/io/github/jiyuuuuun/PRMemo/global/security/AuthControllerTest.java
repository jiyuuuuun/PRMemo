package io.github.jiyuuuuun.PRMemo.global.security;


import io.github.jiyuuuuun.PRMemo.global.service.RefreshTokenService;
import io.github.jiyuuuuun.PRMemo.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @AfterEach
    void tearDown() {
        refreshTokenService.deleteRefreshToken("testuser");
    }

    @DisplayName("refresh 토큰 없이 요청 시 UNAUTHORIZED 응답")
    @Test
    void refreshToken_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("유효하지 않은 refresh 토큰으로 요청 시 UNAUTHORIZED 응답")
    @Test
    void refreshToken_invalidToken_returns401() throws Exception {
        Cookie invalidRefreshToken = new Cookie("refresh_token", "invalid.token.value");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(invalidRefreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("유효한 refresh 토큰으로 요청 시 Access/Refresh 토큰 재발급 성공")
    @Test
    void refreshToken_validToken_returns200() throws Exception {
        // given
        String login = "testuser";
        Map<String, Object> claims = Map.of("login", login);

        // 실제 Redis에 저장될 토큰 생성
        String refreshToken = jwtUtil.generateRefreshToken(claims, login);

        // 토큰 만료 시간까지 Redis에 저장
        refreshTokenService.storeRefreshToken(refreshToken);

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk());
    }


    @DisplayName("로그아웃 요청 시 200 OK 반환 및 Redis 토큰 삭제 확인")
    @Test
    void logout_success() throws Exception {
        // given
        String login = "testuser";
        Map<String, Object> claims = Map.of("login", login);

        String refreshToken = jwtUtil.generateRefreshToken(claims, login);
        refreshTokenService.storeRefreshToken(refreshToken);

        // Redis 저장 확인
        String savedToken = redisTemplate.opsForValue().get("refresh:" + login);
        assertThat(savedToken).isEqualTo(refreshToken);

        // when: 로그아웃 요청
        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk());

        // then: Redis에서 삭제되었는지 확인
        String deletedToken = redisTemplate.opsForValue().get("refresh:" + login);
        assertThat(deletedToken).isNull(); // 삭제되었으면 null
    }

}