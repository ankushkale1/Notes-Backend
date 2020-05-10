package com.note.aspects;

import org.apache.commons.io.FileUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

import com.note.pojo.Note;

@Aspect
@Component
public class NotesExport
{
	final static String NOTE_PATH = "JSON_NOTES";
	
	@AfterReturning(pointcut="execution(* com.note.service.*.addUpdateNote(*))"
			,returning="note")
	public void exportNote(Note note)
	{
		try
		{
			System.out.println("Called before save / update Note: "+note.getNotename());
			new File(NOTE_PATH+"/"+note.getNotebook().getNotebookname()).mkdirs();
			FileUtils.write(new File(NOTE_PATH+"/"+note.getNotebook().getNotebookname()+"/"+note.getNotename()+".json"),
					note.getJsonnotes());
		}
		catch(Exception e) {}
	}
	
	@AfterReturning(pointcut="execution(* com.note.service.NoteService.*(..))",returning = "result")
	public void logNotesOps(JoinPoint joinPoint, Object result)
	{
		System.out.println("Executed for every note service method..");
		//System.out.println("Method: "+joinPoint.getSignature().getName()+" Args: "+joinPoint.getArgs()+" Return Value: "+result);
	}
}
