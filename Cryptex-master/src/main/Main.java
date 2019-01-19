package main;

import javafx.scene.image.*;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	//g
    @Override
    //Setup MainScreen
    public void start(Stage MainScreen) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/MainScreen.fxml"));
        Scene scene = new Scene(root);
        
        //Disable window resizing
        MainScreen.setResizable(false);
        
        //Set the icon for the MainScreen window
        MainScreen.getIcons().add(new Image("/resources/images/CryptexIcon.png"));
        		
        //Set the title of the MainScreen window
        MainScreen.setTitle("Cyptex");
        
        //Set the screen and display it
        MainScreen.setScene(scene);
        MainScreen.show();
    }
    //ab
    public static void main(String[] args) {
        launch(args);
    }
}
