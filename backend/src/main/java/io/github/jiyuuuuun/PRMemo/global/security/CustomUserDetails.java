package io.github.jiyuuuuun.PRMemo.global.security;

import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;


public class CustomUserDetails implements UserDetails {

    private final User user; // 당신의 User 엔티티

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예: "ROLE_USER" 같은 role 값을 반환
        return null; // user.getRole() → "ROLE_USER"
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호가 필요 없으면 null도 가능
    }

    @Override
    public String getUsername() {
        return user.getLogin(); // 고유 식별자
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요 시 로직 추가
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 필요 시 로직 추가
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 필요 시 로직 추가
    }

    @Override
    public boolean isEnabled() {
        return true; // 사용자 활성화 여부
    }

    public User getUser() {
        return user;
    }
}

