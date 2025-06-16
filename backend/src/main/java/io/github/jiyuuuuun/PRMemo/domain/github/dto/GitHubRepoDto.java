package io.github.jiyuuuuun.PRMemo.domain.github.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubRepoDto {
    private Long id;
    private String name;
    private GitHubUserDto owner;
}

