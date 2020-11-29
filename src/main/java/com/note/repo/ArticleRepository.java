package com.note.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.note.pojo.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>
{
	
}
