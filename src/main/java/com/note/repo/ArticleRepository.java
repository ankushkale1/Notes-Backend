package com.note.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.note.pojo.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer>
{
	
}
