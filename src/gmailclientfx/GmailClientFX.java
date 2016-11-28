/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx;


import gmailclientfx.core.DBConnection;
import gmailclientfx.core.GmailClient;
import gmailclientfx.core.OAuth2Authenticator;
import java.awt.Color;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.sound.midi.ControllerEventListener;

/**
 * Klasa koja služi za pokretanje aplikacije.
 * 
 * @author zeljko94
 */
public class GmailClientFX extends Application {
    
    /**
     * Metoda za pokretanje glavne forme aplikacije
     * @param primaryStage
     * @throws IOException 
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        Parent root = FXMLLoader.load(getClass().getResource("views/Login.fxml"));
        
        Scene scene = new Scene(root);
        OAuth2Authenticator.initialize();
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show(); 
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
