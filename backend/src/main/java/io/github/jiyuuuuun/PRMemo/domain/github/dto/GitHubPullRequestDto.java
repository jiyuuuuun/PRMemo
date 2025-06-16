package io.github.jiyuuuuun.PRMemo.domain.github.dto;

import lombok.Data;

@Data
public class GitHubPullRequestDto {
    private int number;
    private String title;
    private String state;
    private GitHubUserDto user;
}
