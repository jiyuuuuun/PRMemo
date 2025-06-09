package io.github.jiyuuuuun.PRMemo.domain.user.repository;

import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
