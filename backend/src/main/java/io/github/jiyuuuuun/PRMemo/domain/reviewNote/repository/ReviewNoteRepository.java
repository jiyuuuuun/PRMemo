package io.github.jiyuuuuun.PRMemo.domain.reviewNote.repository;

import io.github.jiyuuuuun.PRMemo.domain.reviewNote.entity.ReviewNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewNoteRepository extends JpaRepository<ReviewNote, Long> {
}
