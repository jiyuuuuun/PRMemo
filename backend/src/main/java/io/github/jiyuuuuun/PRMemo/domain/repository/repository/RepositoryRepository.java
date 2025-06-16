package io.github.jiyuuuuun.PRMemo.domain.repository.repository;

import io.github.jiyuuuuun.PRMemo.domain.github.dto.GitHubRepoDto;
import io.github.jiyuuuuun.PRMemo.domain.repository.entity.Repository;
import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    Optional<Repository> findByGithubRepoId(Long githubRepoId);
    List<Repository> findAllByUser(User user);

    @Query("""
    SELECT new io.github.jiyuuuuun.PRMemo.domain.github.dto.GitHubRepoDto(
        r.githubRepoId,
        r.name,
        new io.github.jiyuuuuun.PRMemo.domain.github.dto.GitHubUserDto(
            u.login,
            u.name,
            u.avatarUrl
        )
    )
    FROM Repository r
    JOIN r.user u
    WHERE u.login = :login
""")
    List<GitHubRepoDto> findAllRepoDtoByUsername(@Param("login") String login);

}

