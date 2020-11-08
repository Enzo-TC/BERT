package application;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;

import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;

public class Art {
	private File fl;
	private String fileName;//"art.png"
	private String artName;//"art"
	private String fileType;//".png"
	
	private TreeSet<String> artistTags;
	private TreeSet<String> linkTags;
	private TreeSet<String> tagTags;
	private LinkedHashSet<String> metaTags;
	//metaTags is a LinkedHashSet instead of TreeSet so that it can be ordered non-alphabetically
	
	private LinkedHashMap<String, MenuButton> linkButtonsMap;
	
	public Art(File f){
		//Setting file
		fl=f;
		//Setting names
		fileName=fl.getName();
		fileType=FilenameUtils.getExtension(fileName);
		artName=FilenameUtils.removeExtension(fileName);
		
		//Initializing and setting tags
		artistTags=new TreeSet<String>();
		linkTags=new TreeSet<String>();
		tagTags=new TreeSet<String>();
		metaTags=new LinkedHashSet<String>();
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
	
	//Returns the number of tags the specific section of an art object has
	public int getNumTags(String section){
		int rtn=-1;
		switch(section){
		case("ARTISTS"):
			rtn=artistTags.size();
			break;
		case("LINKS"):
			rtn=linkTags.size();
			break;
		case("TAGS"):
			rtn=tagTags.size();
			break;
		case("META"):
			rtn=metaTags.size();
			break;
		}
		return(rtn);
	}
	
	//Returns a tree set of the tags in the art's 'section' file
	public void getTags(){
		//Get tags for an image
		int spot=0;//Keeps track of the current row
		try {
			//Opening the CSV file
			Reader in=new FileReader("User Art/CSV/"+artName+".csv");
			Iterable<CSVRecord> records=CSVFormat.EXCEL
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
			//Empty meta tags
			if(metaTags.size()<=0){
				createMeta();
			}
			in.close();
		//Make new file
		}catch(FileNotFoundException e){
			writeTagsToFile();
			createMeta();
		//Reading error
		}catch(IOException e){
			System.out.println(e);
		}
	}
	
	//Returns the image file for the object
	public Image getImage(){
		Image img=null;
		try{
			FileInputStream stream=new FileInputStream("User Art/Art/"+fileName);
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
		img=new File("User Art/Art/"+fileName);
		return(img);
	}
	
	//Returns the file of the object's tags
	public File getTagFile(){
		File rtn=null;
		rtn=new File("User Art/CSV/"+artName+".csv");
		return(rtn);
	}
	
	//Creating the meta tags for when an image is initially added
	public void createMeta(){
		//Data to add
		String[] rtn=new String[6];
		
		//Day added
		String day,time;
		SimpleDateFormat dayFormat=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
		Date date = new Date(System.currentTimeMillis());
		time="Time Added: "+timeFormat.format(date);
		day="Day Added: "+dayFormat.format(date);
		rtn[0]=time;
		rtn[1]=day;
		
		//Image size and aspect ratio
		String res="";
		String aspect="";
		try {
			Dimension d=(getImageDimension(fl));
			res="Image Size: "+(int)d.getWidth()+"x"+(int)d.getHeight();
			aspect="Aspect Ratio: "+getAspect()[0]+":"+getAspect()[1];
		}catch(IOException e){
			System.out.println(e);
		}
		rtn[2]=res;
		rtn[3]=aspect;
		
		//File size and type
		String flSize,flType;
		flSize="File Size: "+fl.length()+" bytes";
		flType="File Type: ."+fileType;
		rtn[4]=flSize;
		rtn[5]=flType;
		
		addTags("META",rtn);
	}
	
	public static Dimension getImageDimension(File imgFile) throws IOException {
		int pos = imgFile.getName().lastIndexOf(".");
		if (pos == -1)
			throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
		String suffix = imgFile.getName().substring(pos + 1);
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		while(iter.hasNext()) {
			ImageReader reader = iter.next();
			try {
				ImageInputStream stream = new FileImageInputStream(imgFile);
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			} catch (IOException e) {
				System.out.println("Error reading: " + imgFile.getAbsolutePath()+"\n"+e);
			} finally {
				reader.dispose();
			}
		}
		throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
	}
	
	public int[] getAspect(){
		try{
			Dimension d=(getImageDimension(fl));
			double aspX,aspY;
			int rat=1;
			//While the ratio ends up creating a decimal
			while((d.getHeight()*rat)%d.getWidth()!=0){
				rat+=1;
			}
			aspX=rat;
			aspY=((d.getHeight()*rat)/d.getWidth());
			int[] rtn={(int)aspX, (int)aspY};
			return(rtn);
		}catch(IOException e){
			System.out.println("OwO"+e);
			return(null);
		}
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
			rtn.append(formatTags(artistTags,",","")+"\n");
			rtn.append(formatTags(linkTags,",","")+"\n");
			rtn.append(formatTags(tagTags,",","")+"\n");
			rtn.append(formatTags(metaTags,",",""));
			writer.write(rtn.toString());
			writer.close();
		}catch(FileNotFoundException e){
			System.out.println("writeTagsToFile fail, file not found.");
		}
	}
	
	//Returns formated tags for the section
	public String displayTags(String section,String split){
		String start="  ";
		switch(section){
		case("ARTISTS"):
			return(formatTags(artistTags,split,start));
		case("LINKS"):
			return(formatTags(linkTags,split,""));
		case("TAGS"):
			return(formatTags(tagTags,split,start));
		case("META"):
			return(formatTags(metaTags,split,start));
		}
		return("");
	}
	
	//Returns a nice looking list of tags in string form
	public String formatTags(TreeSet<String> tags,String split,String start){
		Iterator<String> tIterator=tags.iterator();
		String rtn="";
		while(tIterator.hasNext()){
			rtn+=start+tIterator.next();
			if(tIterator.hasNext())
				rtn+=split;
		}
		return(rtn);
	}	
	
	//Returns a nice looking list of tags in string form
	public String formatTags(LinkedHashSet<String> tags,String split,String start){
		Iterator<String> tIterator=tags.iterator();
		String rtn="";
		while(tIterator.hasNext()){
			rtn+=start+tIterator.next();
			if(tIterator.hasNext())
				rtn+=split;
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