package io.github.jiyuuuuun.PRMemo.domain.github.controller;

import io.github.jiyuuuuun.PRMemo.domain.github.dto.GitHubRepoDto;
import io.github.jiyuuuuun.PRMemo.domain.github.service.GitHubSyncService;
import io.github.jiyuuuuun.PRMemo.domain.repository.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/github")
public class GitHubController {

    private final GitHubSyncService gitHubSyncService;
    private final RepositoryRepository repositoryRepository;

    @GetMapping("/repos/{login}")
    public ResponseEntity<List<GitHubRepoDto>> getUserRepos(
            @PathVariable String login,
            @RequestParam(defaultValue = "false") boolean refresh) {

        if (refresh || repositoryRepository.findAllRepoDtoByUsername(login).isEmpty()) {
            gitHubSyncService.syncUserRepos(login);
        }

        List<GitHubRepoDto> repos = repositoryRepository.findAllRepoDtoByUsername(login);
        return ResponseEntity.ok(repos);
    }
}

