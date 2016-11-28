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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import org.apache.commons.mail.util.MimeMessageParser;

/**
 * FXML Controller class
 *
 * @author zeljko94
 */
public class SpamController implements Initializable {
    
    @FXML
    GridPane spamGridPane;
    
    @FXML
    Button btnBrisiOznacene;
    
    @FXML
    TableView<MyMessage> spamTable;
    
    @FXML
    TableColumn<MyMessage, Integer> stupacId;
    
    @FXML
    TableColumn<MyMessage, String> stupacOznaci;
    
    @FXML
    TableColumn<MyMessage, String> stupacNaslov;
    
    @FXML
    TableColumn<MyMessage, String> stupacFrom;
    
    @FXML
    TableColumn<MyMessage, String> stupacDatum;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Thread fetchSpamThread = new Thread()
        {
            public void run()
            {
                fetchSpam();
            }
        };
        fetchSpamThread.start();
    }    
    
    
    public void clearPane()
    {
        if(spamGridPane != null) spamGridPane.getChildren().clear();
    }
    
    public void fetchSpam()
    {
        ObservableList<MyMessage> data = FXCollections.observableArrayList();
        stupacId.setCellValueFactory(new PropertyValueFactory<MyMessage, Integer>("TblId"));
        stupacNaslov.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Subject"));
        stupacFrom.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Sender"));
        stupacDatum.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("DateReceived"));

        spamTable.setItems(data);
        spamTable.setOnMousePressed(new EventHandler<MouseEvent>()
            {
        @Override
        public void handle(MouseEvent event) {
            if(event.isPrimaryButtonDown() && event.getClickCount() == 2)
            {
                MyMessage selectedMsg = spamTable.getSelectionModel().getSelectedItem();
                PregledEmailaHelper.setMsg(selectedMsg);
                try {
                    Parent root;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("gmailclientfx/views/pregledEmaila.fxml"));
                    root = loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    PregledEmailaController pgec = loader.getController();
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                    {
                        @Override
                        public void handle(WindowEvent event) {
                                    pgec.bodyWebViewEngine.load(null);
                                    System.out.println("Closing form!");
                        }
                        
                    });
                    stage.setTitle(PregledEmailaHelper.getMsg().getSubject() + " - " + PregledEmailaHelper.getMsg().getSender());
                    stage.show();
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
            Folder spam = null;
            Folder[] folders = store.getDefaultFolder().list("*");
            for(Folder f : folders)
            {
                IMAPFolder imapFolder = (IMAPFolder) f;
                for(String att : imapFolder.getAttributes())
                {
                    if("\\Junk".equals(att))
                    {
                    spam = f;
                    }
                }
            }
            spam.open(Folder.READ_WRITE);
            
            MimeMessage[] seenMessages = (MimeMessage[]) spam.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
            for(int i=0; i<seenMessages.length; i++)
            {
                MyMessage msg = GmailClient.fetchMessage(seenMessages[i], i+1, "SPAM");
                data.add(msg);
            }
            MimeMessage[] unseenMessages = (MimeMessage[]) spam.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            for(int i=0; i<unseenMessages.length; i++)
            {
                MyMessage msg = GmailClient.fetchMessage(unseenMessages[i], i+1, "SPAM");
                data.add(msg);
            }
            spam.close(false);
            store.close();
        } catch (Exception ex) {
            Logger.getLogger(SpamController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
