package io.github.jiyuuuuun.PRMemo.domain.github.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubUserDto {
    private String login;
    private String name;
    private String avatar_url;
}
