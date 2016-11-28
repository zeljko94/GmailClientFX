/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.controllers;

import gmailclientfx.helpers.PregledEmailaHelper;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import gmailclientfx.core.GmailClient;
import gmailclientfx.core.OAuth2Authenticator;
import gmailclientfx.models.MyMessage;
import gmailclientfx.models.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Folder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import org.apache.commons.mail.util.MimeMessageParser;

/**
 * FXML Controller class
 *
 * @author zeljko94
 */
public class PoslaneController implements Initializable {
    @FXML
    GridPane poslaneGridPane;
    
    @FXML
    Button btnBrisiOznacene;
    
    @FXML
    TableView<MyMessage> poslaneTable;
    
    @FXML
    TableColumn<MyMessage, Integer> stupacId;
    
    @FXML
    TableColumn<MyMessage, String> stupacOznaci;
    
    @FXML
    TableColumn<MyMessage, String> stupacNaslov;
    
    @FXML
    TableColumn<MyMessage, String> stupacTo;
    
    @FXML
    TableColumn<MyMessage, String> stupacDatum;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Thread fetchPoslaneThread = new Thread()
        {
          public void run()
          {
              fetchPoslane();
          }
        };
        fetchPoslaneThread.start();
    }    
    
    public void clearPane()
    {
        if(poslaneGridPane != null) poslaneGridPane.getChildren().clear();
    }
    
    public void  fetchPoslane()
    {
    ObservableList<MyMessage> data = FXCollections.observableArrayList();
    stupacId.setCellValueFactory(new PropertyValueFactory<MyMessage, Integer>("TblId"));
    stupacNaslov.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Subject"));
    stupacTo.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Sender"));
    stupacDatum.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("DateReceived"));

    poslaneTable.setItems(data);
    poslaneTable.setOnMousePressed(new EventHandler<MouseEvent>()
            {
        @Override
        public void handle(MouseEvent event) {
            if(event.isPrimaryButtonDown() && event.getClickCount() == 2)
            {
                MyMessage selectedMsg = poslaneTable.getSelectionModel().getSelectedItem();
                PregledEmailaHelper.setMsg(selectedMsg);
                try {
                    Parent root;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/pregledEmaila.fxml"));
                    root = loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(PregledEmailaHelper.getMsg().getSubject() + " - " + PregledEmailaHelper.getMsg().getSender());
                    stage.show();
                    PregledEmailaController pgec = loader.getController();
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                    {
                        @Override
                        public void handle(WindowEvent event) {
                                    pgec.bodyWebViewEngine.load(null);
                                    System.out.println("Closing form!");
                        }
                        
                    });
                } catch (IOException ex) {
                    Logger.getLogger(InboxController.class.getName()).log(Level.SEVERE, null, ex);
                }             
            }
        }
                
            });
        try {
            IMAPStore store = OAuth2Authenticator.connectToImap("imap.gmail.com",
                    993,
                    GmailClient.getEmail(),
                    GmailClient.getAccesToken(),
                    true);
            
            Folder poslane = null;
            Folder[] folders = store.getDefaultFolder().list("*");
            for(Folder f : folders)
            {
                IMAPFolder imapFolder = (IMAPFolder) f;
                for(String att : imapFolder.getAttributes())
                {
                    if("\\Sent".equals(att))
                    {
                    poslane = f;
                    }
                }
            }
            poslane.open(Folder.READ_WRITE);
            
            MimeMessage[] seenMessages = (MimeMessage[]) poslane.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
            for(int i=0; i<seenMessages.length; i++)
            {
                MyMessage msg = GmailClient.fetchMessage(seenMessages[i], i+1, "SENT");
                data.add(msg);
            }
            
            MimeMessage[] unseenMessages = (MimeMessage[]) poslane.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            for(int i=0; i<unseenMessages.length; i++)
            {
                MyMessage msg = GmailClient.fetchMessage(unseenMessages[i], i+1, "SENT");
                data.add(msg);
            }
            poslane.close(false);
            store.close();
            } catch (Exception ex) {
                Logger.getLogger(PoslaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
