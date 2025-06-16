package io.github.jiyuuuuun.PRMemo.domain.pullRequest.repository;

import io.github.jiyuuuuun.PRMemo.domain.pullRequest.entity.PullRequest;
import io.github.jiyuuuuun.PRMemo.domain.repository.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    List<PullRequest> findAllByRepository(Repository repository);

    Optional<PullRequest> findByRepositoryAndPrNumber(Repository repo, int number);
}
