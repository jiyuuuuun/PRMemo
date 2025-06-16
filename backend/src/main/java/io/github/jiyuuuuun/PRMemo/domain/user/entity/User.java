package io.github.jiyuuuuun.PRMemo.domain.user.entity;

import io.github.jiyuuuuun.PRMemo.global.entity.BaseEntity;
import io.github.jiyuuuuun.PRMemo.domain.repository.entity.Repository;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Getter@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    private String login;
    private String name;
    private String avatarUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Repository> repositories = new ArrayList<>();

}
