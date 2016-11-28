/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.controllers;

import gmailclientfx.models.MyAttachment;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author zeljko94
 */
public class HomeController implements Initializable {
    String activeWindow = "";
    private static List<MyAttachment> ATTACHMENTS;
    
    @FXML
    GridPane homeGridPane;
    
    @FXML
    Label lblNova;
    
    @FXML
    Label lblInbox;
    
    @FXML
    Label lblPoslane;
    
    @FXML
    Label lblNedovrseneLabel;
    
    @FXML
    Label lblObrisane;
    
    @FXML
    Label lblSpam;
    
    @FXML
    MenuBar homeMenuBar;
    
    @FXML
    HBox footer;
    
    @FXML
    Separator separator1;
    
    
    // -------------------- kontroleri --------------------------
    
    @FXML
    NewMessageViewController newMessageController;
    
    @FXML
    InboxController inboxController;
    
    @FXML
    PoslaneController poslaneController;
    
    @FXML
    NedovrseneController nedovrseneController;
    
    @FXML
    ObrisaneController obrisaneController;
    
    @FXML
    SpamController spamController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
   
    public void spremiDraft()
    {
        if(activeWindow == "nova" && 
           (!newMessageController.txtNaslov.getText().equals("") ||
            !newMessageController.txtPoruka.getText().equals("") ||
            !newMessageController.txtTo.getText().equals("")))
        {
            // spremi draft
            System.out.println("Spremam draft!!");
        }
    }
    
    public void lblNova_click()
    {
        
        spremiDraft();
        activeWindow = "nova";
        if(inboxController != null) inboxController.clearPane();
        if(poslaneController != null) poslaneController.clearPane();
        if(newMessageController != null) newMessageController.clearPane();
        if(nedovrseneController != null) nedovrseneController.clearPane();
        if(obrisaneController != null) obrisaneController.clearPane();
        if(spamController != null) spamController.clearPane();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/newMessageView.fxml"));
            GridPane pane = loader.load();
            newMessageController = loader.getController();
            homeGridPane.add(pane, 1, 1);
            Stage stage = (Stage)homeGridPane.getScene().getWindow();
            stage.setTitle("NOVA PORUKA");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void lblInbox_click()
    {
        spremiDraft();
        activeWindow = "inbox";
        if(newMessageController != null) newMessageController.clearPane();
        if(poslaneController != null) poslaneController.clearPane();
        if(inboxController != null) inboxController.clearPane();
        if(nedovrseneController != null) nedovrseneController.clearPane();
        if(obrisaneController != null) obrisaneController.clearPane();
        if(spamController != null) spamController.clearPane();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/Inbox.fxml"));
            GridPane pane = loader.load();
            inboxController = loader.getController();
            homeGridPane.add(pane, 1, 1);
            Stage stage = (Stage)homeGridPane.getScene().getWindow();
            stage.setTitle("INBOX");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public void lblPoslane_click()
    {
        spremiDraft();
        activeWindow = "poslane";
        if(newMessageController != null) newMessageController.clearPane();
        if(inboxController != null) inboxController.clearPane();
        if(poslaneController != null) poslaneController.clearPane();
        if(nedovrseneController != null) nedovrseneController.clearPane();
        if(obrisaneController != null) obrisaneController.clearPane();
        if(spamController != null) spamController.clearPane();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/Poslane.fxml"));
            GridPane pane = loader.load();
            poslaneController = loader.getController();
            homeGridPane.add(pane, 1, 1);
            Stage stage = (Stage)homeGridPane.getScene().getWindow();
            stage.setTitle("POSLANE / SENT");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    public void lblNedovrsene_click()
    {
        spremiDraft();
        activeWindow = "nedovrsene";
        if(newMessageController != null) newMessageController.clearPane();
        if(inboxController != null) inboxController.clearPane();
        if(poslaneController != null) poslaneController.clearPane();
        if(nedovrseneController != null) nedovrseneController.clearPane();
        if(obrisaneController != null) obrisaneController.clearPane();
        if(spamController != null) spamController.clearPane();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/Nedovrsene.fxml"));
            GridPane pane = loader.load();
            nedovrseneController = loader.getController();
            homeGridPane.add(pane, 1, 1);
            Stage stage = (Stage)homeGridPane.getScene().getWindow();
            stage.setTitle("NEDOVRSENE / DRAFTS");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void lblObrisane_click()
    {
        spremiDraft();
        activeWindow = "obrisane";
        if(newMessageController != null) newMessageController.clearPane();
        if(inboxController != null) inboxController.clearPane();
        if(poslaneController != null) poslaneController.clearPane();
        if(nedovrseneController != null) nedovrseneController.clearPane();
        if(obrisaneController != null) obrisaneController.clearPane();
        if(spamController != null) spamController.clearPane();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/Obrisane.fxml"));
            GridPane pane = loader.load();
            obrisaneController = loader.getController();
            homeGridPane.add(pane, 1, 1);
            Stage stage = (Stage)homeGridPane.getScene().getWindow();
            stage.setTitle("OBRISANE / DELETED");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void lblSpam_click()
    {
        spremiDraft();
        activeWindow = "smece";
        if(newMessageController != null) newMessageController.clearPane();
        if(inboxController != null) inboxController.clearPane();
        if(poslaneController != null) poslaneController.clearPane();
        if(nedovrseneController != null) nedovrseneController.clearPane();
        if(obrisaneController != null) obrisaneController.clearPane();
        if(spamController != null) spamController.clearPane();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/Spam.fxml"));
            GridPane pane = loader.load();
            spamController = loader.getController();
            homeGridPane.add(pane, 1, 1);
            Stage stage = (Stage)homeGridPane.getScene().getWindow();
            stage.setTitle("SPAM");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    // --------------------------------- NewMessageViewControler--------------------------------------------
    
    
     
    
}
