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
import javafx.scene.control.Tooltip;
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
import javafx.scene.text.Font;
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
				helpMenu();
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
	
	
	
	
	//Bringing up the help menu
	public void helpMenu(){
		//New window
		StackPane helpPane=new StackPane();
		
		Text helpText=new Text("Welcome to Sina Sees, your own personal image gallery tool! "
				+ "\nTo get started, switch back to the starting tab and enter in tags that you want your art gallery to have. "
				+ "\nUse spaces to separate different tags and use underscore '_' for multi_word tags. "
				+ "ex: \"dog red_hair solo simple_background\""
				+ "\nUse 'Add Image' to add another image to the program."
				+ "\nAfter searching, feel free to scroll around the newly created image gallery."
				+ "\nClick on an image to open up a detailed view of it and see its tags."
				+ "\nHere you can see its tags and their categories. For links you can click on their specific icons and copy their links."
				+ "\nTo add or remove tags enter them into the tag box, using the same system as for searching, then select the tag category;"
				+ "\nif adding a link make sure to specify which site it's from using the 2nd drop down menu."
				+ "\nUse the red 'Delete Image' button to permanently remove the image from the program.");
		helpText.setFill(Color.WHITE);
		helpText.setFont(new Font("Arial", 20));
		helpText.wrappingWidthProperty().bind(helpPane.widthProperty());
		helpPane.getChildren().add(helpText);
		
		StackPane.setAlignment(helpText, Pos.TOP_LEFT);
		
		Scene newScene=new Scene(helpPane,1120,260);
		newScene.getStylesheets().add(getClass().getResource("images.css").toExternalForm());
		
		Stage newWindow=new Stage();
		newWindow.setTitle("Help");
		newWindow.setScene(newScene);
		
		newWindow.show();
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
		//How much space image takes up
		int[] imgDim=getDetailImageSize(art);
		//How much space tags take up
		int tagDim=getTagSize(art);
		Pane newPane = getPaneForArt(art, newWindow, imgDim, tagDim);
		
		Scene newScene=new Scene(newPane,imgDim[0]+tagDim,imgDim[1]);
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
					//
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
					rtn[n].setLayoutX(1020+(65*n));
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
	public Pane getPaneForArt(Art art, Stage newWindow, int[] imgDimensions, int tagDimensions){
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
			imageError.setX(0);
			imageError.setY(imgDimensions[1]/2);
			newPane.getChildren().add(imageError);
		}

		//Getting the tags for the image
		Text artists=new Text("Artist(s):\n"+art.displayTags("ARTISTS",","));
		artists.getStyleClass().add("tagsText");
		artists.setFill(Color.WHITE);
		artists.setWrappingWidth(tagDimensions-12);
		artists.setX(imgDimensions[0]+10);
		artists.setY(20);

		Text links=new Text("Link(s):\n");
		links.getStyleClass().add("tagsText");
		links.setFill(Color.WHITE);
		links.setX(imgDimensions[0]+10);
		links.setY(60);
		//Buttons for web links
		setupButtonLinks(art);
		newPane.getChildren().addAll(art.getLinkBtnMap().values());
		
		Text tags=new Text("Tags(s):\n");
		tags.setFill(Color.WHITE);
		tags.setWrappingWidth(tagDimensions-12);
		tags.setX(imgDimensions[0]+10);
		tags.setY(140);
		
		ScrollPane tagsScroll=new ScrollPane();
		tagsScroll.getStyleClass().add("scroll-pane");
		tagsScroll.setMaxHeight(360);
		tagsScroll.setLayoutX(imgDimensions[0]+20);
		tagsScroll.setLayoutY(150);
		tagsScroll.setContent(new Text(art.displayTags("TAGS",",\n")));

		Text meta=new Text("Meta:\n"+art.displayTags("META","\n"));
		meta.getStyleClass().add("tagsText");
		meta.setFill(Color.WHITE);
		meta.setX(imgDimensions[0]+10);
		meta.setY(540);

		//Returning tags
		newPane.getChildren().addAll(artists,links,tags,tagsScroll,meta);
		
		
		//Setting up the drop-down menu for different tags
		ChoiceBox<String> tagBox=new ChoiceBox<>();
		tagBox.getStyleClass().add("choiceBox");
		tagBox.getItems().addAll("Artists","Links","Tags","Meta");
		tagBox.setValue("Tags");
		tagBox.setTooltip(new Tooltip("Select the type of tag"));
		tagBox.setLayoutX(imgDimensions[0]+10);
		tagBox.setLayoutY(imgDimensions[1]-90);

		//Setting up the drop-down menu for websites
		ChoiceBox<String> webBox=new ChoiceBox<String>();
		webBox.getStyleClass().add("choiceBox");
		webBox.getItems().addAll("Deviant Art","E926","Fur Affinity","Inkbunny","SoFurry","Tumblr","Twitter","Weasyl");
		webBox.setValue("Fur Affinity");
		tagBox.setTooltip(new Tooltip("Select the website for links"));
		webBox.setLayoutX(imgDimensions[0]+90);
		webBox.setLayoutY(imgDimensions[1]-90);
		
		//Returning the drop-down
		newPane.getChildren().addAll(tagBox,webBox);
		
		//Setting up the text field for tags
		TextField tagField=new TextField("Enter tag(s) here");
		tagField.getStyleClass().add("tagField");
		tagField.setLayoutX(imgDimensions[0]-10);
		tagField.setLayoutY(imgDimensions[1]-76);

		//Setting up the add and remove buttons for tags
		Button addBtn=new Button("Add");
		addBtn.getStyleClass().add("blueButton");
		addBtn.setLayoutX(imgDimensions[0]+10);
		addBtn.setLayoutY(imgDimensions[1]-30);

		Button removeBtn=new Button("Remove");
		removeBtn.getStyleClass().add("blueButton");
		removeBtn.setLayoutX(imgDimensions[0]+60);
		removeBtn.setLayoutY(imgDimensions[1]-30);

		//Setting up the button for deleting an image
		Button deleteBtn=new Button("Delete image");
		deleteBtn.getStyleClass().add("deleteButton");		
		deleteBtn.setLayoutX(imgDimensions[0]+130);
		deleteBtn.setLayoutY(imgDimensions[1]-30);

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
						Pane p = getPaneForArt(art, newWindow, imgDimensions, tagDimensions);
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
					tagsScroll.setContent(new Text(art.displayTags("TAGS",",\n")));
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

						Pane p = getPaneForArt(art, newWindow, imgDimensions, tagDimensions);
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
					tagsScroll.setContent(new Text(art.displayTags("TAGS",",\n")));
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
		return(newPane);
	}
	
	//Returns the optimal dimensions for the big image scene based on the aspect ratio of art being displayed
	public int[] getDetailImageSize(Art a){
		//Art max is 1000x800, 5:4
		int[] rtn=new int[2];
		//Getting ratio of image
		int[] aspects=a.getAspect();
		float ratio=(float)aspects[1]/(float)aspects[0];
		
		if(ratio<0.8){//Image is wide
			rtn[0]=1000;
			rtn[1]=(int)(ratio*1000);
		}else{//Image is tall
			rtn[1]=800;
			rtn[0]=(int)(800/ratio);
		}
		return(rtn);
	}
	
	//Returns the amount of space to be allocated to the tags
	public int getTagSize(Art a){
		int numLinks=a.getNumTags("LINKS");
		if(numLinks<3)
			return(240);
		return(numLinks*80);
	}
	
	
	
	
	public static void main(String[] args){
		launch(args);
	}
}
