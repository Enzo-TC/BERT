package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage; 

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application{

	private ArrayList<Art> allArt=new ArrayList<Art>();
	//There was an issue with random Buttons other than link disappearing when trying to remove a link button
	//By pairing each link button with it's link the program can filter the buttons that are going to be removed
	
	
	public void start(Stage stage) throws FileNotFoundException{
		//Adding art
		File[] addArt=new File("User Art/Art").listFiles();
		for(File a:addArt){
			allArt.add(new Art(a));
		}
			
		//Starting search page
		startPage(stage);
	}
	

	
	
	//Main search page
	public void startPage(Stage stage){		
		StackPane layout=new StackPane();
		
		//Tags-to-search text
		Text tagsText=new Text("Tags:");
		tagsText.setFill(Color.WHITE);
		tagsText.getStyleClass().add("tagsText");
		StackPane.setAlignment(tagsText,  Pos.TOP_LEFT);
		tagsText.setTranslateX(20);
		tagsText.setTranslateY(20);
		
		//Search box
		TextField tagField=new TextField();
		tagField.getStyleClass().add("tagField");
		StackPane.setAlignment(tagField,  Pos.CENTER);
		
		//Search button
		Button searchBtn=new Button("Search");
		searchBtn.getStyleClass().add("blueButton");
		StackPane.setAlignment(searchBtn,  Pos.CENTER_LEFT);
		searchBtn.setTranslateX(20);
		searchBtn.setTranslateY(40);
		
		//Help button
		Button helpBtn=new Button("Help");
		helpBtn.getStyleClass().add("blueButton");
		StackPane.setAlignment(helpBtn, Pos.CENTER);
		helpBtn.setTranslateX(-10);
		helpBtn.setTranslateY(40);
		
		//Add new image button
		Button addBtn=new Button("Add image");
		addBtn.getStyleClass().add("blueButton");
		StackPane.setAlignment(addBtn,  Pos.CENTER_RIGHT);
		addBtn.setTranslateX(-20);
		addBtn.setTranslateY(40);
		
		ScrollPane scrollPane=new ScrollPane();
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		//scrollPane.setContent();
		
		//Returning buttons & field
		layout.getChildren().addAll(tagsText,tagField,searchBtn,helpBtn,addBtn);		
		
		Scene scene = new Scene(layout, 280, 140);//Initializing scene
		scene.getStylesheets().add(getClass().getResource("start.css").toExternalForm());
		stage.setTitle("Sina");
		stage.setScene(scene);
		stage.show();
		
		
		//When search button is pressed
		searchBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				//Getting tags to search
				String sT=tagField.getCharacters().toString();
				String[] searchTags=sT.split(" ");
				tagField.clear();
				
				//New window with art results
				imageSearch(stage,searchTags);
			}
		});
		
		//Bring up help menu
		helpBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				System.out.println("There's no help here.");
			}
		});
		
		//Adding a new image
		addBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				Art newArt;//New art object to be added
				//Opening directory to choose file
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Chooce art file");
				chooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Desktop"));
				File file=chooser.showOpenDialog(stage);
				if(file!=null){
					//Adding new art object to arraylist
					newArt=new Art(file);
					newArt.createMeta();
					allArt.add(new Art(file));
					
					try{
						Files.copy(file.toPath(),
								(new File("User Art/Art/"+file.getName())).toPath(),
								StandardCopyOption.REPLACE_EXISTING);
					}catch(IOException er){
						System.out.println("IO Exception: "+er);
					}
					bigImage(stage,allArt.get(allArt.size()-1));
				}
			}
		});
	}
	
	
	
	
	//Gallery of images section
	public void imageSearch(Stage stage, String[] searchTags){
		//New window
		Pane tempPane=new Pane();
		ScrollPane newPane=new ScrollPane();
		//Disabling vertical scrolling
		newPane.setFitToWidth(true);
		
		//Adding the new images				
		ArrayList<Art> displayImages=getSmallImages(searchTags);
		ImageView[] imageViews=new ImageView[displayImages.size()];
		
		Button[] imageButton=new Button[displayImages.size()];
		
		for(int n=displayImages.size()-1;n>=0;n--){
			//Getting cropped preview for image
			imageViews[n]=new ImageView(getCroppedImage(displayImages.get(n).getImage()));
	
			//Scaling image
			imageViews[n].setPreserveRatio(true);
			imageViews[n].setFitWidth(300);
			imageViews[n].setFitHeight(300);
			
			//Adding image to button
			imageButton[n]=new Button("",imageViews[n]);
			imageButton[n].getStyleClass().add("art-button");
			
			//Positioning button
			imageButton[n].setLayoutX((300*(n%3))-8);
			imageButton[n].setLayoutY(300*(n/3)-8);
			
			//Setting up scrollpane
			tempPane.getChildren().add(imageButton[n]);			
			newPane.setContent(tempPane);
			
			imageButton[n].setOnAction(new EventHandler<ActionEvent>(){
				@Override public void handle(ActionEvent e){
					bigImage(stage,displayImages.get(findArtButton(e.getSource(),imageButton)));
				}
			});
		}
		
		Scene newScene=new Scene(newPane,900,900);
		newScene.getStylesheets().add(getClass().getResource("images.css").toExternalForm());
		
		Stage newWindow=new Stage();
		newWindow.setTitle(Arrays.toString(searchTags));
		newWindow.setScene(newScene);
		
		newWindow.show();
	}
	
	//Finds the index of an action event
	public int findArtButton(Object action,Button[] buttons){
		for(int n=0;n<buttons.length;n++){
			if((buttons[n].toString()).equals(action.toString()))
				return(n);
		}
		return(-1);
	}
		
	//Returns an array list of the images matching the search tags
	public ArrayList<Art> getSmallImages(String[] searchTags){
		ArrayList<Art> rtn=new ArrayList<Art>();
		for(Art a:allArt){
			//If search tags are empty, all art shows up
			if(searchTags[0].equals("") || a.hasTags(searchTags)){
				rtn.add(a);
			}
		}
		return(rtn);
	}
	
	public WritableImage getCroppedImage(Image img){
		PixelReader reader=img.getPixelReader();
		
		int scale;
		if(img.getWidth()>img.getHeight()){//Wide image
			scale=(int) img.getHeight();
		}else{//Tall image
			scale=(int) img.getWidth();
		}
		WritableImage rtn=new WritableImage(reader,(int) ((img.getWidth()-scale)/2), (int) ((img.getHeight()-scale)/2), scale,scale);
		return(rtn);
	}
	
	
	
	
	//Main detailed page
	public void bigImage(Stage stage, Art art){
		//Creating new window
		Stage newWindow=new Stage();
		Pane newPane = getPaneForArt(art, newWindow);
		Scene newScene=new Scene(newPane,1800,800);
		newScene.getStylesheets().add(getClass().getResource("detailed.css").toExternalForm());

		newWindow.setTitle(art.getArtName());
		newWindow.setScene(newScene);

		newWindow.show();
	}

	//Adding link buttons to linkButtonsMap
	public void setupButtonLinks(Art art){
		//If links exist
		if(!art.displayTags("LINKS",",").equals("")) {
			String[] tags=art.displayTags("LINKS",",").split(",");
			//Buttons to return
			MenuButton[] rtn=new MenuButton[tags.length];
			for(int n=0;n<tags.length;n++){
				Label link=new Label("");
				VBox box;
				MenuItem menuRtn;
				try {
					//Getting each tag
					//sections is declared final so it can be used on button press
					final String[] sections=tags[n].split("]");
					sections[0]=sections[0].substring(1);
					
					FileInputStream stream=new FileInputStream("resource/LNK/"+sections[0]+".png");
					Image img=new Image(stream);
					stream.close();
					ImageView linkView=new ImageView(img);
					
					//Setting up dropdown menu items
					box=new VBox();
					link.setText(sections[1]);
					Button copyButton=new Button("Copy");
					box.getChildren().addAll(link,copyButton);
					menuRtn=new MenuItem();
					menuRtn.setGraphic(box);
					
					rtn[n]=new MenuButton("",linkView);
					rtn[n].getStyleClass().add("link-button");
					rtn[n].setLayoutX(1020+(80*n));
					rtn[n].setLayoutY(75);
					rtn[n].getItems().setAll(menuRtn);

					//Pressing copy dropdown button
					copyButton.setOnAction(new EventHandler<ActionEvent>(){
						@Override public void handle(ActionEvent e){
							//Copying link to clipboard
							StringSelection stringSelection=new StringSelection(sections[1]);
							Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(stringSelection, null);
							System.out.println(sections[1]);
						}
					});
					art.putLink(tags[n], rtn[n]);
				}catch(IOException e){
					rtn[n]=new MenuButton("");
					art.putLink(tags[n], rtn[n]);
					System.out.println(e);
				}
			}
		}
	}
	

	//Seperating this part of the code as a function lets me call the code inside the function
	//See -> how the page is updated in addBtn.setOnAction and removeBtn.setOnAction
	public Pane getPaneForArt(Art art, Stage newWindow){
		//New window
		Pane newPane=new Pane();
		//Getting the image itself
		Image image = art.getImage();
		//Image file is supported
		if(image!=null){
			ImageView imView=new ImageView(image);

			//Positioning the image
			imView.setPreserveRatio(true);
			imView.setFitWidth(1000);
			imView.setFitHeight(800);
			imView.setLayoutX(0);
			imView.setLayoutX(0);

			//Returning image
			newPane.getChildren().add(imView);
			//Image file isn't supported
		}else{
			//Error text
			Text imageError=new Text("It seems this file type isn't supported.\nPlease spam EnzoTC#2358 with this message and your image.");
			imageError.setX(350);
			imageError.setY(380);
			newPane.getChildren().add(imageError);
		}

		//Getting the tags for the image
		Text artists=new Text("Artist(s):\n"+art.displayTags("ARTISTS",","));
		artists.setX(1020);
		artists.setY(20);

		Text links=new Text("Link(s):\n");
		links.setX(1020);
		links.setY(60);
		//Buttons for web links
		setupButtonLinks(art);
		newPane.getChildren().addAll(art.getLinkBtnMap().values());
		
		Text tags=new Text("Tags(s):\n"+art.displayTags("TAGS",","));
		tags.setX(1020);
		tags.setY(140);

		Text meta=new Text("Meta:\n"+art.displayTags("META","\n"));
		meta.setX(1020);
		meta.setY(180);

		//Returning tags
		newPane.getChildren().addAll(artists,links,tags,meta);

		//Setting up the drop-down menu for different tags
		ChoiceBox<String> tagBox=new ChoiceBox<String>();
		tagBox.getItems().addAll("Artists","Links","Tags","Meta");
		tagBox.setValue("Tags");
		tagBox.setLayoutX(1020);
		tagBox.setLayoutY(700);

		//Setting up the drop-down menu for websites
		ChoiceBox<String> webBox=new ChoiceBox<String>();
		webBox.getItems().addAll("Deviant Art","E926","Fur Affinity","Inkbunny","SoFurry","Tumblr","Twitter","Weasyl");
		webBox.setValue("Fur Affinity");
		webBox.setLayoutX(1100);
		webBox.setLayoutY(700);

		//Returning the drop-down
		newPane.getChildren().addAll(tagBox,webBox);

		//Setting up the text field for tags
		TextField tagField=new TextField("Enter tag(s) here");
		tagField.setLayoutX(1020);
		tagField.setLayoutY(730);

		//Setting up the add and remove buttons for tags
		Button addBtn=new Button("Add");
		addBtn.setLayoutX(1020);
		addBtn.setLayoutY(760);

		Button removeBtn=new Button("Remove");
		removeBtn.setLayoutX(1100);
		removeBtn.setLayoutY(760);

		//Setting up the button for deleting an image
		Button deleteBtn=new Button("Delete image");
		deleteBtn.setLayoutX(1200);
		deleteBtn.setLayoutY(760);

		//Returning the tag buttons and field
		newPane.getChildren().addAll(tagField,addBtn,removeBtn,deleteBtn);

		//When add button is pressed, adding tags
		addBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				//Checking values aren't null or empty
				if(!tagField.getCharacters().toString().equals("")){
					//Data for adding
					String section=(String) tagBox.getValue();
					String[] addTags=tagField.getCharacters().toString().split(" ");

					//Adding tag
					if(section.equals("Links")){
						for(int n=0;n<addTags.length;n++){
							addTags[n]="["+(String) webBox.getValue()+"]"+addTags[n];
						}
						//Makes sure the tags are updated before updating the page
						art.addTags(section, addTags);

						//Adding link buttons

						//Updates the Pairing map.
						for(int n=0;n<addTags.length;n++) {
							if (art.linkMapContains(addTags[n])) {
								//No need to add buttons in the for loop above because getPaneForArt takes care of it
								art.putLink(addTags[n], art.getLinkBtnMap().get(addTags[n]));
							}
						}
						//Letting bigImage make it's own Pane then trying to copy it in here would result in an IllegalArgumntException
						//Because of some duplicating issue? idk
						//Seperating the code that makes the pane then using it as a function to get a replica fixes the bug.
						Pane p = getPaneForArt(art, newWindow);
						//Now we can update the window without having to close and reopen it!
						Scene newerScene=new Scene(p,1800,800);
						newerScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

						newWindow.setScene(newerScene);
					}else{
						//I put the second addTags in an else bracket to make sure no link tag is added twice
						art.addTags(section, addTags); 
					}

					//Updating text
					artists.setText("Artist(s):\n"+art.displayTags("ARTISTS",","));
					tags.setText("Tags(s):\n"+art.displayTags("TAGS",","));
					meta.setText("Meta:\n"+art.displayTags("META","\n"));
				}
				//Clearing tag entry box
				tagField.clear();
			}
		});

		//When remove button is pressed
		removeBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				//Checking values aren't null or empty
				if(!tagField.getCharacters().toString().equals("")){
					//Data for removing
					String section=(String) tagBox.getValue();
					String[] removeTags=tagField.getCharacters().toString().split(" ");

					//Removing tags
					if(section.equals("Links")){
						for(int n=0;n<removeTags.length;n++){
							removeTags[n]="["+(String) webBox.getValue()+"]"+removeTags[n];
						}
						art.removeTags(section, removeTags);

						//Removes Link buttons from the window

						Pane p = getPaneForArt(art, newWindow);
						//Updates the Pairing map.
						for(int n=0;n<removeTags.length;n++) {
							if (art.linkMapContains(removeTags[n])) {
								//getPaneForArt should remove the buttons but there was an issue
								//Where i couldn't remove all the link buttons
								//and one would always come back from the grave
								//So this line is here as a fail-check to make sure they go away
								//Like they should! *bonk*
								p.getChildren().removeAll(art.getLinkBtnMap().get(removeTags[n]));
								art.removeLink(removeTags[n], art.getLinkBtnMap().get(removeTags[n]));
							}
						}
						Scene newerScene=new Scene(p,1800,800);
						newerScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

						newWindow.setScene(newerScene);
					}else{ 
						art.removeTags(section, removeTags);
					}

					//Updating text
					artists.setText("Artist(s):\n"+art.displayTags("ARTISTS",","));
					tags.setText("Tags(s):\n"+art.displayTags("TAGS",","));
					meta.setText("Meta:\n"+art.displayTags("META","\n"));
				}
				//Clearing tag entry box
				tagField.clear();
			}
		});

		//When delete button is pressed
		deleteBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				art.deleteImage();
				allArt.remove(art);
			}
		});
		return newPane;
	}
	
	public static void main(String[] args){
		launch(args);
	}
}
