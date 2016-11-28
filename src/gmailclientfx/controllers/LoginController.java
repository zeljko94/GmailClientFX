/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.controllers;

import gmailclientfx.core.GmailClient;
import gmailclientfx.models.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.validator.EmailValidator;

/**
 * FXML Controller class
 *
 * @author zeljko94
 */
public class LoginController implements Initializable {

    @FXML
    GridPane loginGridPane;
    
    @FXML
    Label lblLogo;
    
    @FXML
    Label lblEmail;
    
    @FXML
    Label lblStatusMsg;
    
    @FXML
    TextField txtEmail;
    
    @FXML
    Button btnLogin;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
   
  
    public void btnLogin_click(ActionEvent e)
    {
        if(!txtEmail.getText().equals(""))
        {
            String email = txtEmail.getText();
            if(EmailValidator.getInstance().isValid(email))
            {
                User user = User.getUserByEmail(email);
                
                if(!(user == null))
                {
                    try {
                        
                        GmailClient.setRefreshToken(user.getRefreshToken());
                        GmailClient.refreshAccessToken();
                        
                        Parent loginStage = txtEmail.getParent();
                        loginStage.getScene().getWindow().hide();
                        Parent root;
                        root = FXMLLoader.load(getClass().getClassLoader().getResource("gmailclientfx/views/Home.fxml"));
                        Stage stage = new Stage();
                        stage.setTitle("Home");
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent e) {
                                Platform.exit();
                                System.exit(0);
                                
                            }
                        });
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                else
                {
                    try {
                        GmailClient.authorizeUser();
                        String noviEmail = GmailClient.getEmail();
                        String noviRefreshToken = GmailClient.getRefreshToken();
                        User u = User.getUserByEmail(noviEmail);
                        
                        
                        if(u == null)
                        {
                            User.unesiUseraUBazu(new User(noviEmail, noviRefreshToken));
                        }
                        
                        Parent loginStage = txtEmail.getParent();
                        loginStage.getScene().getWindow().hide();
                        Parent root;
                        root = FXMLLoader.load(getClass().getClassLoader().getResource("gmailclientfx/views/Home.fxml"));
                        Stage stage = new Stage();
                        stage.setTitle("Home");
                        stage.setScene(new Scene(root));
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent e) {
                                Platform.exit();
                                System.exit(0);
                                
                            }
                        });
                        stage.show();
                        
                    } catch (IOException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else
            {
                lblStatusMsg.setText("Uneseni email nije validan!");
            }
        }
        else
        {
            lblStatusMsg.setText("Unesite email!");
        }
    }
    
}
