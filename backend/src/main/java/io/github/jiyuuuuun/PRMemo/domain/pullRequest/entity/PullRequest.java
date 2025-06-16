package io.github.jiyuuuuun.PRMemo.domain.pullRequest.entity;

import io.github.jiyuuuuun.PRMemo.domain.repository.entity.Repository;
import io.github.jiyuuuuun.PRMemo.domain.reviewNote.entity.ReviewNote;
import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import io.github.jiyuuuuun.PRMemo.global.entity.BaseEntity;
import io.github.jiyuuuuun.PRMemo.global.enums.PullRequestState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pull_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PullRequest extends BaseEntity {

    @Column(name = "pr_number")
    private Integer prNumber;

    @Column(length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PullRequestState state; // open, closed 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", referencedColumnName = "id")
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL)
    private List<ReviewNote> reviewNotes = new ArrayList<>();
}

