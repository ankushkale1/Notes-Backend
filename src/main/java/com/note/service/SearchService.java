//package com.note.service;
//
//
//import com.note.pojo.Note;
//import com.note.repo.NoteRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.NoResultException;
//import org.apache.lucene.search.Query;
//import org.hibernate.search.jpa.FullTextEntityManager;
//import org.hibernate.search.jpa.Search;
//import org.hibernate.search.query.dsl.QueryBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.LinkedHashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//public class SearchService {
//    private final EntityManager em;
//
//    @Autowired
//    NoteRepository note_repo;
//
//    @Autowired
//    public SearchService(final EntityManagerFactory entityManagerFactory) {
//        this.em = entityManagerFactory.createEntityManager();
//    }
//
//    @PostConstruct
//    public void initializeHibernateSearch() {
//        try {
//            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
//            fullTextEntityManager.createIndexer().startAndWait();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public Set<Note> fuzzySearch(String searchTerm) {
//
//        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
//        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Note.class).get();
//        Query luceneQuery = qb.keyword()
//                .fuzzy()
//                .withEditDistanceUpTo(1)
//                .withPrefixLength(1).onFields("plain_content")
//                .matching(searchTerm).createQuery();
//
//        jakarta.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Note.class);
//
//        // execute search
//
//        Set<Note> notes = null;
//        try {
//            notes = new LinkedHashSet<>(jpaQuery.getResultList());
//            //removeTags(notes);
//            //notes.addAll(note_repo.searchExactMatch(searchTerm)); //priority for exact match
//            notes = removeNonMatchedLines(notes, searchTerm);
//        } catch (NoResultException nre) {
//            // do nothing
//        }
//
//        return notes;
//    }
//
//    private Set<Note> removeNonMatchedLines(Set<Note> notes, String searchTerm) {
//        Set<Note> newnotes = new LinkedHashSet<>();
//
//        for (Note note : notes) {
//            String[] lines = note.getPlain_content().split("\n");
//            //if(lines.length == 1)
//            //	lines = note.getPlain_content().split("\r\n");
//
//            //System.out.println("Lines: "+lines.length);
//
//            String newline = Stream.of(lines)
//                    //.filter(line -> line.contains(searchTerm))
//                    .map(line -> line.replaceAll(searchTerm, "<mark>" + searchTerm + "</mark>"))
//                    .collect(Collectors.joining("\n"));
//
//            //System.out.println("Lines: "+newline.split("\n").length);
//
//            Note newnote = new Note();
//            newnote.setNote_id(note.getNote_id());
//            newnote.setPlain_content(newline);
//            newnote.setNotebook_id(note.getNotebook_id());
//            newnote.setNotename(note.getNotename());
//
//            newnotes.add(newnote);
//        }
//
//        return newnotes;
//    }
//
//	/*public void removeTags(List<Note> notes)
//	{
//		List<String> tagsToRemove = Arrays.asList(new String[] {"img"});
//
//		for(Note note : notes)
//		{
//			String content = note.getJsonnotes();
//
//			if(content != null) {
//			    Document document = Jsoup.parse(content);
//
//			    for(String tag : tagsToRemove)
//			    	document.select(tag).remove();
//
//			    content = document.text();
//
//			    //content = document.toString();
//			}
//
//			note.setJsonnotes(content);
//		}
//	}*/
//}
