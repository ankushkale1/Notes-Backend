package com.note.aspects;

import org.apache.commons.io.FileUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.util.*;

import com.note.pojo.Note;

import io.github.biezhi.webp.WebpIO;

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
			FileUtils.write(
					new File(NOTE_PATH+"/"+note.getNotebook().getNotebookname()
					+"/"+note.getNotename()+".json"),
					note.getJsonnotes());
		}
		catch(Exception e) {}
	}
	
	@Before("execution(* com.note.service.*.addUpdateNote(*))")
	public void convertImages(JoinPoint point)
	{
		try
		{
			Object[] args = point.getArgs();
			Note note = (Note) args[0];
			System.out.println("Converting Images Note: "+note.getNotename());
			convertImages(note);
		}
		catch(Exception e) {}
	}
	
	@AfterReturning(pointcut="execution(* com.note.service.NoteService.*(..))"
			,returning = "result")
	public void logNotesOps(JoinPoint joinPoint, Object result)
	{
		System.out.println("Executed for every note service method..");
		//System.out.println("Method: "+joinPoint.getSignature().getName()
		//+" Args: "+joinPoint.getArgs()
		//+" Return Value: "+result);
	}
	
	public void convertImages(Note note)
	{
		JSONObject jobj = new  JSONObject(note.getJsonnotes());
		JSONArray arr = jobj.getJSONArray("ops");
		
		for(int i=0;i<arr.length();i++)
		{
			JSONObject op = (JSONObject) arr.get(i);
			
			if(op.get("insert") instanceof JSONObject)
			{
				JSONObject image = (JSONObject) op.get("insert");
				
				if((image.getString("image") != null) &&
						(image.getString("image").indexOf("data:image/") > -1)) //i.e this is an image node & have base64 data
				{
					//System.out.println("Image length: "+image.getString("image").length());
					String img_src = image.getString("image");
					String img_type = image.getString("image").substring(0, image.getString("image").indexOf(";"));
					String ext = img_type.substring(img_type.indexOf("/")+1, img_type.length());
					//System.out.println("Img type: "+img_type+" File Ext: "+ext);
					
					if(!img_type.trim().contains("webp"))
					{
						try
						{
							File src = File.createTempFile(note.getNote_id()+"_"+i, null);
							OutputStream out = new FileOutputStream(src);
							
							String b64 = img_src.substring(img_src.indexOf(",")+2, img_src.length());
							byte[] rdata = Base64Utils.decodeFromString(b64);
							out.write(rdata);
							out.flush();
							
							File dest = File.createTempFile(note.getNote_id()+"_"+i+"_webp", null);
							
							WebpIO.create().toWEBP(src, dest);
							
							byte[] wbytes = FileUtils.readFileToByteArray(dest);
							String b64_w = Base64Utils.encodeToString(wbytes);
							
							//System.out.println("Outlength: "+b64_w.length());
							
							image.put("image","data:image/webp;base64,"+b64_w);
							
							src.delete();
							dest.delete();
						}
						catch(Exception e)
						{
							e.printStackTrace(System.out);
						}
					}
				}
			}
		}
		
		note.setJsonnotes(jobj.toString());
	}
}
