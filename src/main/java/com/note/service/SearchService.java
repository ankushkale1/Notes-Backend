package com.note.service;


import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.note.pojo.*;

@Service
public class SearchService
{
	private EntityManager em;

    @Autowired
    public SearchService(final EntityManagerFactory entityManagerFactory) {
        this.em = entityManagerFactory.createEntityManager();
    }
	
    @PostConstruct
	public void initializeHibernateSearch()
	{
		try
		{
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
			fullTextEntityManager.createIndexer().startAndWait();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Transactional
	public List<Note> fuzzySearch(String searchTerm)
	{

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
		QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Note.class).get();
		Query luceneQuery = qb.keyword()
				.fuzzy()
				.withEditDistanceUpTo(1)
				.withPrefixLength(1).onFields("jsonnotes")
				.matching(searchTerm).createQuery();

		javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Note.class);

		// execute search

		List<Note> notes = null;
		try
		{
			notes = jpaQuery.getResultList();
			removeTags(notes);
		}
		catch (NoResultException nre)
		{
			// do nothing
		}

		return notes;
	}
	
	public void removeTags(List<Note> notes)
	{
		List<String> tagsToRemove = Arrays.asList(new String[] {"img"});
		
		for(Note note : notes)
		{
			String content = note.getJsonnotes();
			
			if(content != null) {
			    Document document = Jsoup.parse(content);
			    
			    for(String tag : tagsToRemove)
			    	document.select(tag).remove();
			    
			    content = document.text();
			    
			    //content = document.toString();
			}
			
			note.setJsonnotes(content);
		}
	}
}