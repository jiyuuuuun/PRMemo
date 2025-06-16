package io.github.jiyuuuuun.PRMemo.domain.reviewNote.entity;

import io.github.jiyuuuuun.PRMemo.domain.pullRequest.entity.PullRequest;
import io.github.jiyuuuuun.PRMemo.domain.user.entity.User;
import io.github.jiyuuuuun.PRMemo.global.entity.BaseEntity;
import io.github.jiyuuuuun.PRMemo.global.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewNote extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ReviewStatus status;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id")
    private PullRequest pullRequest;
}

