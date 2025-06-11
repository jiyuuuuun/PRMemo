package io.github.jiyuuuuun.PRMemo.domain.user.repository;

import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
}
