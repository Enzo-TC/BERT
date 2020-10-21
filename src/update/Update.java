package update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Update {
	public static void main(String args[]){
		String currVersion="V0.2.0";
		String latestVersion=getLatestTag();
		if(currVersion.equals(latestVersion)){
			
		}
	}
	
	
	public static String getLatestTag(){
		try{
			URL updateSite=new URL("https://github.com/Enzo-TC/Sina-Sees/releases/latest/");
			BufferedReader in=new BufferedReader(
			new InputStreamReader(updateSite.openStream()));
			
			String inputLine;
			while((inputLine=in.readLine()) != null){
				System.out.println(inputLine);
			}
			in.close();
		}catch(MalformedURLException e){
			System.out.println("AB");
		}catch(IOException e){
			System.out.println("Offline");
		}
		String rtn="";
		return(rtn);
	}
}
