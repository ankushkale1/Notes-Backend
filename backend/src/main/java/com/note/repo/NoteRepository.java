package com.note.repo;

import com.note.pojo.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByKeywords(String keyword);

    @NativeQuery("select * from note where plain_content like '%?1%'")
    List<Note> searchExactMatch(String keyword);
}
