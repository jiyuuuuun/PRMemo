package io.github.jiyuuuuun.PRMemo.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
public class ApiV1UserController {

    @GetMapping("/login")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return Map.of("error", "로그인 안 됨");
        }
        return Map.of(
                "name", principal.getAttribute("name"),
                "login", principal.getAttribute("login"),
                "avatar_url", principal.getAttribute("avatar_url"),
                "html_url", principal.getAttribute("html_url")
        );
    }
}