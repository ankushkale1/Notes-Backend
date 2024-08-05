package com.note.repo;

import com.note.pojo.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByKeywords(String keyword);

    @Query(value = "select * from note where plain_content like '%?1%'", nativeQuery = true)
    List<Note> searchExactMatch(String keyword);
}
