package guicontrollers;

//Import Libraries

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MainScreenController {
    private static Stage window;
    //Declare components from FXML file
    @FXML private AnchorPane screenIn;
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private JFXTextField fullName;
    @FXML private JFXTextField username1;
    @FXML private JFXPasswordField password1;
    @FXML private JFXPasswordField passwordConfirm;
    @FXML private JFXButton buttonIn;
    @FXML private JFXButton buttonUp;
    @FXML private Label error;
    @FXML private Label error2;
    @FXML private JFXProgressBar loadingBar;

    //String containing users name
    private static String temp;

    //Variables to check which screen is currently up
    private int countUp = 0;
    private int countIn = 0;
    private String state = "in";
    private String state1 = "in";

    //File path for Login information
    String infoPath = "info.txt";

    //LoginScreen.fxml path
    String loginScreenPath = "/resources/fxml/LoadingScreen.fxml";

    //CoinMainScreen.fxl path
    String coinMainPath = "/resources/images/CoinMainScreen.fxml";

    //CryptexIcon.png path
    String iconPath = "/resources/images/CryptexIcon.png";

    //Create info.txt file to store login information
    private File file = new File(infoPath);

    @FXML public void upClicked() throws IOException {
        TranslateTransition ft=new TranslateTransition(new Duration(500), screenIn);
        ft.setToX((screenIn.getWidth()));
        ft.play();
        state1 = "up";
        countIn = 0;
        error2.setText("");
        username.setText("");
        password.setText("");
        if((countUp == 1) || (state.equals("up") && countIn != 0)){
            if(fullName.getText() != null && !fullName.getText().isEmpty() && username1.getText() != null && !username1.getText().isEmpty() && password1.getText() != null && !password1.getText().isEmpty() && passwordConfirm.getText() != null && !passwordConfirm.getText().isEmpty()){
                if(!username1.getText().contains(" ") && !password1.getText().contains(" ") && !passwordConfirm.getText().contains(" ")){
                    if(password1.getText().equals(passwordConfirm.getText())){
                        if(!checkFound()){
                            TranslateTransition ft2=new TranslateTransition(new Duration(500), screenIn);
                            ft2.setToX(0);
                            ft2.play();
                            error.setText("Signed Up Successfully! Please Sign In!");
                            countUp = 0;
                            countIn = 1;
                            FileWriter out = new FileWriter(file, true);
                            out.write(username1.getText()+","+password1.getText()+","+ fullName.getText()+"\n");
                            out.close();
                            fullName.setText("");
                            username1.setText("");
                            password1.setText("");
                            passwordConfirm.setText("");
                        }
                        else{
                            error2.setText("Username already exists!");
                        }
                    }
                    else{
                        error2.setText("Passwords do not match!");
                    }
                }
                else{
                    error2.setText("Username and Password cannot contain spaces!");
                }
            }
            else{
                error2.setText("Fields cannot be empty!");
            }
        }
        else{
            countUp++;
        }
        state = "up";
    }

    @FXML public void inClicked(ActionEvent event) throws IOException {
        TranslateTransition ft=new TranslateTransition(new Duration(500), screenIn);
        ft.setToX(0);
        ft.play();
        state1 = "in";
        countUp = 0;
        error.setText("");
        fullName.setText("");
        username1.setText("");
        password1.setText("");
        passwordConfirm.setText("");
        if((countIn == 1) || state.equals("in")){
            if(checkMatch()){
                temp = username.getText();
                Parent load1 = FXMLLoader.load(getClass().getResource(loginScreenPath));
                Scene loadScene = new Scene(load1);
                window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(loadScene);
                window.getIcons().add(new Image(iconPath));
                window.show();
            }
            else{
                error.setText("Invalid Username/Password!");
            }
        }
        else{
            countIn++;
        }
        state = "in";
    }
    @FXML public void keyPressed(KeyEvent event){
        switch(event.getCode()){
            case ENTER:
                if(state1.equals("in")) {
                    buttonIn.fire();
                }
                else{
                    buttonUp.fire();
                }
        }
    }

    private boolean checkFound(){
        boolean found = false;
        String tempUsername;
        String tempPassword;
        String tempName;
        try{
            Scanner x = new Scanner(new File(infoPath));
            x.useDelimiter("[,\n]");

            while(x.hasNext() && !found){
                tempUsername = x.next();
                tempPassword = x.next();
                tempName = x.next();
                if(tempUsername.equals(username1.getText())){
                    found = true;
                }
            }
        }
        catch (Exception ignored){

        }
        return found;
    }

    private boolean checkMatch(){
        String mUsername = "";
        String mPassword = "";
        String mName = "";
        try{
            Scanner y = new Scanner(new File(infoPath));
            y.useDelimiter("[,\n]");
            while(y.hasNext()){
                mUsername = y.next();
                mPassword = y.next();
                mName = y.next();
                if(mUsername.equals(username.getText())){
                    return mPassword.equals(password.getText());
                }
            }
        }
        catch(Exception ignored){

        }
        return  false;
    }

    public static String getTemp() {
        return temp;
    }

    public static Stage getWindow() {
        return window;
    }
}
