package com.note.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.note.pojo.Note;
import java.util.*;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer>
{
	@Query(value="from Note where jsonnotes like '%?0%'")
	public List<Note> findByContentMatch(String textToSearch);
	
	public List<Note> findByKeywords(String keyword);
}
