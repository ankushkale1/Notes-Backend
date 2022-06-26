package com.note;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import com.note.service.NoteService;

//@Component
public class MonteParser implements CommandLineRunner
{
    private final String baseNotesPath = "/home/ankush/Documents/MonteNote Notebooks";

    @Autowired
    NoteService note_service;

    @Override
    public void run(String... args) throws Exception
    {
        String base64 = "data:image/{ext};base64, {b64}";

        int count = 0;

        for (File dir : new File(baseNotesPath).listFiles())
        {
            if (dir.isDirectory()) //create notebook
            {
                Notebook book = new Notebook();
                book.setNotebookname(dir.getName());
                book = note_service.addNotebook(book);

                for (File ndir : dir.listFiles())
                {
                    if (ndir.isDirectory()) //create note
                    {
                        Note note = new Note();
                        note.setNotename(ndir.getName());

                        File imagesFolder = new File(ndir, "assets/images");
                        File indexFile = new File(ndir, "index.html");
                        File tags = new File(ndir, "tags.dat");

                        String template = FileUtils.readFileToString(indexFile);

                        for (File image : imagesFolder.listFiles())
                        {
                            //System.out.println("Img: "+image.getName()+" Size: "+FileUtils.sizeOfAsBigInteger(image));

                            String b64_img = base64
                                    .replace("{ext}", FilenameUtils.getExtension(image.getName()))
                                    .replace("{b64}", Base64Utils.encodeToString(FileUtils.readFileToByteArray(image)));

                            //System.out.println("Path: "+image.getAbsolutePath()+" "+b64_img.length());
                            template = template.replace(image.getAbsolutePath(), b64_img);

                            //if(template.contains(image.getAbsolutePath()))
                            //	System.out.println("Not gone..");
                        }
						
						/*Document doc = Jsoup.parse(FileUtils.readFileToString(indexFile));
						Elements imgs = doc.getElementsByTag("img");
						
						for(Element img : imgs)
						{
							try
							{
								String src = img.attr("src");
								
								String b64_img = base64
										.replace("{ext}", FilenameUtils.getExtension(src))
										.replace("{b64}", Base64Utils.encodeToString(FileUtils.readFileToByteArray(new File(src))));
								
								img.attr("src", b64_img);
							}
							catch(Exception e) {}
						}*/


                        Set<String> keywords = new HashSet<>();
                        keywords.addAll(FileUtils.readLines(tags));

                        if (keywords.size() == 0)
                            keywords.add("key");

                        note.setJsonnotes(template);
                        //note.setNotebook(book);
                        note.setKeywords(keywords);
                        note_service.addUpdateNote(note);
                        count++;
                    }
                }
            }
        }

        System.out.println("Notes imported: " + count);
    }
}
