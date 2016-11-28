/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.controllers;

import com.sun.javafx.tk.FileChooserType;
import gmailclientfx.core.GmailClient;
import gmailclientfx.models.MyAttachment;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JFileChooser;

/**
 *
 * @author zeljko94
 */
public class NewMessageViewController implements Initializable{
    
    ArrayList<String> ATTACHMENTS;
    
    @FXML
    public GridPane newMessageGridPane;
    
    @FXML
    Label lblTo;
    
    @FXML
    TextField txtTo;
    
    @FXML
    Label lblNaslov;
    
    @FXML
    TextField txtNaslov;
    
    @FXML
    Label lblPoruka;
    
    @FXML
    TextArea txtPoruka;
    
    @FXML
    Button btnPosalji;
    
    @FXML
    Button btnDodajPrivitak;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ATTACHMENTS = new ArrayList<String>();
    }
    
    public void clearPane()
    {
        if(newMessageGridPane != null) newMessageGridPane.getChildren().clear();
    }
    
    public void showAlertBox(String title, String msg)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();    
    }
    
    public void btnPosalji_click()
    {
        try {
            //showAlertBox("Button click", "Button posalji click");
            
            String to = txtTo.getText();
            String subject = txtNaslov.getText();
            String body = txtPoruka.getText();
            
            GmailClient.sendMessage(to, subject, body, ATTACHMENTS);
        } catch (Exception ex) {
            Logger.getLogger(NewMessageViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void btnDodajPrivitak_click()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Dodaj privitak");
        List<File> files = (List<File>) fc.showOpenMultipleDialog(new Stage());
        
        for(File f : files)
        {
            if(!ATTACHMENTS.contains(f.getPath())) ATTACHMENTS.add(f.getPath());
        }
    }
}
