package com.note.service;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import com.note.repo.NoteRepository;
import com.note.repo.NotebookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;

//@Component
public class CopyFromOtherNotes implements CommandLineRunner
{
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    NotebookRepository notebookRepository;

    @Autowired
    NoteRepository noteRepository;

    String url = "https://aknotesapp.herokuapp.com/";

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    @Override
    public void run(String... args) throws Exception
    {
        HttpHeaders httpHeaders = createHeaders("admin","omsai_0078910");
        Notebook[] books = restTemplate.exchange(url+"/notes/getNoteBooks", HttpMethod.GET,new HttpEntity<Notebook>(httpHeaders),Notebook[].class).getBody();
        Arrays.asList(books).stream().forEach(book -> notebookRepository.save(book));

        for(Notebook book: books){
            for(Note bnote: book.getNotes()){
                Note note = restTemplate.exchange(url+"/notes/getNote/"+bnote.getNote_id(), HttpMethod.GET,new HttpEntity<Note>(httpHeaders),Note.class).getBody();
                note.setNotebook(notebookRepository.findById(note.getNotebook_id()).get());
                noteRepository.save(note);
            }
        }
    }
}
