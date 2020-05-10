package com.note.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.note.pojo.Note;
import java.util.*;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer>
{
	public List<Note> findByKeywords(String keyword);
	
	@Query(value="select * from note where plain_content like '%?1%'",nativeQuery = true)
	public List<Note> searchExactMatch(String keyword);
}
