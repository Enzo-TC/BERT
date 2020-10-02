package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

public class Art {
	private String fileName;//"art.png"
	private String artName;//"art"
	private String fileType;//".png"
	
	private TreeSet<String> artistTags;
	private TreeSet<String> linkTags;
	private TreeSet<String> tagTags;
	private TreeSet<String> metaTags;
	
	private LinkedHashMap<String, MenuButton> linkButtonsMap;
	
	public Art(File fl){
		//Setting names
		fileName=fl.getName();
		fileType=FilenameUtils.getExtension(fileName);
		artName=FilenameUtils.removeExtension(fileName);
		
		//Initializing and setting tags
		artistTags=new TreeSet<String>();
		linkTags=new TreeSet<String>();
		tagTags=new TreeSet<String>();
		metaTags=new TreeSet<String>();
		getTags();
		
		linkButtonsMap=new LinkedHashMap<>();
	}
	
	public String getFileName(){
		return(fileName);
	}
	
	public String getArtName(){
		return(artName);
	}
	
	public String getFileType(){
		return(fileType);
	}
	
	public LinkedHashMap<String, MenuButton> getLinkBtnMap(){
		return(linkButtonsMap);
	}
	
	//Returns a tree set of the tags in the art's 'section' file
	public void getTags(){
		//Get tags for an image
		int spot=0;//Keeps track of the current row
		try {
			//Opening the CSV file
			Reader in=new FileReader("resource/RSC/"+artName+".csv");
			Iterable<CSVRecord> records=CSVFormat.DEFAULT
					.parse(in);
			//Reading the 4 data rows
			for(CSVRecord record:records){
				for(int n=0;n<record.size();n++){
					switch(spot){
					case(0)://Artists
						artistTags.add(record.get(n));
						break;
					case(1)://Links
						linkTags.add(record.get(n));
						break;
					case(2)://Tags
						tagTags.add(record.get(n));
						break;
					case(3)://Meta
						metaTags.add(record.get(n));
						break;
					}
				}
				spot+=1;
			}
			in.close();
		//Make new file
		}catch(FileNotFoundException e){
			writeTagsToFile();
		//Reading error
		}catch(IOException e){
			System.out.println(e);
		}
	}
	
	//Returns the image file for the object
	public Image getImage(){
		Image img=null;
		try{
			FileInputStream stream=new FileInputStream("resource/Art/"+fileName);
			img=new Image(stream);
			stream.close();
		}catch(FileNotFoundException e){
			System.out.println(e);
		}catch(IOException e){
			System.out.println(e);
		}
		return(img);
	}
	
	//Returns the file of the object's image
	public File getArtFile(){
		File img=null;
		img=new File("resource/ART/"+fileName);
		return(img);
	}
	
	//Returns the file of the object's tags
	public File getTagFile(){
		File rtn=null;
		rtn=new File("resource/RSC/"+artName+".csv");
		return(rtn);
	}
	
	//Adding tags to section file
	public void addTags(String section,String[] addTags){
		//Adding new tags
		switch(section.toUpperCase()){
		case("ARTISTS"):
			for(String tag:addTags)
				artistTags.add(tag);
			break;
		case("LINKS"):
			for(String tag:addTags)
				linkTags.add(tag);
			break;
		case("TAGS"):
			for(String tag:addTags)
				tagTags.add(tag);
			break;
		case("META"):
			for(String tag:addTags)
				metaTags.add(tag);
			break;
		}
		//Writing new tags back to file
		writeTagsToFile();
	}
	
	//Removes tags from 'section'
	public void removeTags(String section,String[] rmTags){
		//Removing tags
		switch(section.toUpperCase()){
		case("ARTISTS"):
			for(String tag:rmTags)
				artistTags.remove(tag);
			break;
		case("LINKS"):
			for(String tag:rmTags)
				linkTags.remove(tag);
			break;
		case("TAGS"):
			for(String tag:rmTags)
				tagTags.remove(tag);
			break;
		case("META"):
			for(String tag:rmTags)
				metaTags.remove(tag);
			break;
		}
		
		//Writing new tags back to file
		writeTagsToFile();
	}
	
	//Checks if an array of tags show up for the art
	public boolean hasTags(String[] searchTags){
		for(String tag:searchTags){
			if(!hasTag(tag))
				return(false);
		}
		return(true);
	}
	
	//Checks if a single tag shows up for the art
	private boolean hasTag(String tag){
		if(artistTags.contains(tag))
			return(true);
		if(linkTags.contains(tag))
			return(true);
		if(tagTags.contains(tag))
			return(true);
		if(metaTags.contains(tag))
			return(true);
		return(false);
	}
	
	public boolean linkMapContains(String tag){
		return(linkButtonsMap.keySet().contains(tag));
	}
	
	//Removes the image and files from 
	public void deleteImage(){
		File del=getTagFile();
		del.delete();
		del=getArtFile();
		del.delete();
	}
	
	//Writes the tags back to the CSV file
	public void writeTagsToFile(){
		try(PrintWriter writer = new PrintWriter(getTagFile())){
			StringBuilder rtn=new StringBuilder();
			rtn.append(formatTags(artistTags)+"\n");
			rtn.append(formatTags(linkTags)+"\n");
			rtn.append(formatTags(tagTags)+"\n");
			rtn.append(formatTags(metaTags));
			writer.write(rtn.toString());
			writer.close();
		}catch(FileNotFoundException e){
			System.out.println("writeTagsToFile fail, file not found.");
		}
	}
	
	//Returns formated tags for the section
	public String displayTags(String section){
		switch(section){
		case("ARTISTS"):
			return(formatTags(artistTags));
		case("LINKS"):
			return(formatTags(linkTags));
		case("TAGS"):
			return(formatTags(tagTags));
		case("META"):
			return(formatTags(metaTags));
		}
		return("");
	}
	
	//Returns a nice looking list of tags in string form
	public String formatTags(TreeSet<String> tags){
		Iterator<String> tIterator=tags.iterator();
		String rtn="";
		while(tIterator.hasNext()){
			rtn+=tIterator.next();
			if(tIterator.hasNext())
				rtn+=",";
		}
		return(rtn);
	}	
	
	public void putLink(String s, MenuButton m){
		linkButtonsMap.put(s,m);
	}
	
	public void removeLink(String s, MenuButton m){
		linkButtonsMap.remove(s, m);
	}
}