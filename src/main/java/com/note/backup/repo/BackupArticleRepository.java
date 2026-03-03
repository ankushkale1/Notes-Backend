package com.note.backup.repo;

import com.note.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupArticleRepository extends JpaRepository<Article, Integer> {
}
