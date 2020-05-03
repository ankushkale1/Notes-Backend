package com.note.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.note.pojo.Note;
import java.util.*;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer>
{
	@Query(value="select * from Note inner join Notebook on Note.notebook_id=Notebook.notebook_id "
			+ "where jsonnotes like '%?1%'",nativeQuery=true)
	public List<Note> findByContentMatch(String textToSearch);
	
	public List<Note> findByKeywords(String keyword);
}
