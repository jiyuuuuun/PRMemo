package io.github.jiyuuuuun.PRMemo.domain.user.entity;

import io.github.jiyuuuuun.PRMemo.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter@Setter
public class User extends BaseEntity {
    private String login;
    private String name;
    private String avatarUrl;


    public User() {
        super();
    }
}
