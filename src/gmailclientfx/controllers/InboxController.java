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
import gmailclientfx.models.FetchMessageCallable;
import gmailclientfx.models.MyMessage;
import gmailclientfx.models.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.UIDFolder.FetchProfileItem;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import org.apache.commons.mail.util.MimeMessageParser;

/**
 * FXML Controller class
 *
 * @author zeljko94
 */

public class InboxController implements Initializable {
    Thread fetchInboxThread;
    ObservableList<MyMessage> allMessages;
    
    @FXML
    GridPane inboxGridPane;
    
    @FXML
    Button btnObrisiOznacene;
    
    @FXML
    TableView<MyMessage> inboxTable;
    
    @FXML
    TableColumn<MyMessage, Integer> stupacId;
    
    @FXML
    TableColumn<Object, String> stupacOznaci;
    
    @FXML
    TableColumn<MyMessage, String> stupacNaslov;
    
    @FXML
    TableColumn<MyMessage, String> stupacPosiljatelj;
    
    @FXML
    TableColumn<MyMessage, String> stupacDatum;
    
    public void clearPane()
    {
        if(inboxGridPane != null) inboxGridPane.getChildren().clear();
    }
    
    public void fetchInbox()
    {
    ObservableList<MyMessage> data = FXCollections.observableArrayList();
    stupacId.setCellValueFactory(new PropertyValueFactory<MyMessage, Integer>("TblId"));
    stupacNaslov.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Subject"));
    stupacPosiljatelj.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Sender"));
    stupacDatum.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("DateReceived"));

    Platform.runLater(() -> {inboxTable.setItems(data);});
    inboxTable.setOnMousePressed(new EventHandler<MouseEvent>()
            {
        @Override
        public void handle(MouseEvent event) {
            if(event.isPrimaryButtonDown() && event.getClickCount() == 2)
            {
                MyMessage selectedMsg = inboxTable.getSelectionModel().getSelectedItem();
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
    Folder inbox = store.getFolder("INBOX");
    inbox.open(Folder.READ_WRITE);
    int getSeenCount = inbox.getMessageCount();
    FetchProfile fp = new FetchProfile();
    fp.add(FetchProfile.Item.ENVELOPE);
    fp.add(FetchProfileItem.FLAGS);
    fp.add(FetchProfileItem.CONTENT_INFO);
    fp.add("X-mailer");
    
    MimeMessage[] seenMessages = (MimeMessage[]) inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
    for(int i=0; i<seenMessages.length; i++)
    {
        MyMessage msg = GmailClient.fetchMessage(seenMessages[i], i+1, "INBOX");
        data.add(msg);
    }
    
    MimeMessage[] unseenMessages = (MimeMessage[]) inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    for(int i=0; i<unseenMessages.length; i++)
    {
        MyMessage msg = GmailClient.fetchMessage(unseenMessages[i], i+1, "INBOX");
        data.add(msg);
    }
    

    
    inbox.close(false);
    store.close();
} catch (Exception ex) {
    Logger.getLogger(InboxController.class.getName()).log(Level.SEVERE, null, ex);
}
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {    
        allMessages = FXCollections.observableArrayList();
        fetchInboxThread = new Thread()
        {
            public void run()
            {
                //while(true)
                //{
                    fetchInbox();
                //}
            }
        };
        fetchInboxThread.start();
    }      
}
