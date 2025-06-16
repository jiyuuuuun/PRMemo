package io.github.jiyuuuuun.PRMemo.domain.repository.entity;

import io.github.jiyuuuuun.PRMemo.domain.pullRequest.entity.PullRequest;
import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import io.github.jiyuuuuun.PRMemo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repositories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Repository extends BaseEntity {

    @Column(name = "github_repo_id")
    private Long githubRepoId;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 관계
    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    private List<PullRequest> pullRequests = new ArrayList<>();
}
