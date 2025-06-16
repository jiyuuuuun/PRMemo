package io.github.jiyuuuuun.PRMemo.domain.github.service;

import io.github.jiyuuuuun.PRMemo.domain.github.dto.GitHubPullRequestDto;
import io.github.jiyuuuuun.PRMemo.domain.github.dto.GitHubRepoDto;
import io.github.jiyuuuuun.PRMemo.domain.pullRequest.entity.PullRequest;
import io.github.jiyuuuuun.PRMemo.domain.pullRequest.repository.PullRequestRepository;
import io.github.jiyuuuuun.PRMemo.domain.repository.entity.Repository;
import io.github.jiyuuuuun.PRMemo.domain.repository.repository.RepositoryRepository;
import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import io.github.jiyuuuuun.PRMemo.domain.user.repository.UserRepository;
import io.github.jiyuuuuun.PRMemo.global.enums.PullRequestState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubSyncService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${github.token}")
    private String token;

    private final UserRepository userRepository;
    private final RepositoryRepository repositoryRepository;
    private final PullRequestRepository pullRequestRepository;

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    public void syncUserRepos(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        HttpEntity<Void> entity = new HttpEntity<>(headers());

        ResponseEntity<GitHubRepoDto[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, GitHubRepoDto[].class);
        List<GitHubRepoDto> fetchedRepos = Arrays.asList(response.getBody());

        // 1. 사용자 정보
        User user = userRepository.findByLogin(username)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .login(username)
                                .build()
                ));

        // 2. 현재 DB에 저장된 리포지토리 목록
        List<Repository> existingRepos = repositoryRepository.findAllByUser(user);
        Set<Long> fetchedRepoIds = fetchedRepos.stream()
                .map(GitHubRepoDto::getId)
                .collect(Collectors.toSet());

        // 3. DB에만 존재하는 리포지토리 삭제
        for (Repository repo : existingRepos) {
            if (!fetchedRepoIds.contains(repo.getGithubRepoId())) {
                repositoryRepository.delete(repo);
            }
        }

        // 4. 남은/신규 리포 저장 및 PR 동기화
        for (GitHubRepoDto repoDto : fetchedRepos) {
            Repository repo = repositoryRepository.findByGithubRepoId(repoDto.getId())
                    .orElseGet(() -> new Repository());

            repo.setGithubRepoId(repoDto.getId());
            repo.setName(repoDto.getName());
            repo.setOwner(repoDto.getOwner().getLogin());
            repo.setUser(user);
            repositoryRepository.save(repo);

            syncPullRequests(user, repo);
        }
    }


    public void syncPullRequests(User user, Repository repo) {
        String url = "https://api.github.com/repos/" + repo.getOwner() + "/" + repo.getName() + "/pulls";
        HttpEntity<Void> entity = new HttpEntity<>(headers());

        ResponseEntity<GitHubPullRequestDto[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, GitHubPullRequestDto[].class);

        List<GitHubPullRequestDto> fetched = Arrays.asList(response.getBody());
        Set<Integer> fetchedPrNumbers = fetched.stream()
                .map(GitHubPullRequestDto::getNumber)
                .collect(Collectors.toSet());

        // 기존 PR 삭제 처리
        List<PullRequest> existing = pullRequestRepository.findAllByRepository(repo);
        for (PullRequest pr : existing) {
            if (!fetchedPrNumbers.contains(pr.getPrNumber())) {
                pullRequestRepository.delete(pr); // or deleteById(pr.getId())
            }
        }

        // 새로 저장 or 업데이트
        for (GitHubPullRequestDto dto : fetched) {
            PullRequest pr = pullRequestRepository
                    .findByRepositoryAndPrNumber(repo, dto.getNumber())
                    .orElseGet(() -> new PullRequest());

            pr.setRepository(repo);
            pr.setUser(user);
            pr.setPrNumber(dto.getNumber());
            pr.setTitle(dto.getTitle());
            pr.setState(PullRequestState.valueOf(dto.getState().toUpperCase()));

            pullRequestRepository.save(pr);
        }
    }

}

