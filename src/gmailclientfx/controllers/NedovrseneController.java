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
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.ConditionalFeature.FXML;
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
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import org.apache.commons.mail.util.MimeMessageParser;

/**
 * FXML Controller class
 *
 * @author zeljko94
 */
public class NedovrseneController implements Initializable {
    
    @FXML
    GridPane nedovrseneGridPane;
    
    @FXML
    Button btnObrisiOznacene;
    
    @FXML
    TableView<MyMessage> nedovrseneTable;
    
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
        // TODO
        Thread fetchNedovrseneThread = new Thread()
        {
            public void run()
            {
                fetchNedovrsene();
            }
        };
        fetchNedovrseneThread.start();
    }    
    
    public void clearPane()
    {
        if(nedovrseneGridPane != null) nedovrseneGridPane.getChildren().clear();
    }
    
    public void fetchNedovrsene()
    {
        ObservableList<MyMessage> data = FXCollections.observableArrayList();
        stupacId.setCellValueFactory(new PropertyValueFactory<MyMessage, Integer>("TblId"));
        stupacNaslov.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Subject"));
        stupacTo.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("Sender"));
        stupacDatum.setCellValueFactory(new PropertyValueFactory<MyMessage, String>("DateReceived"));

        nedovrseneTable.setItems(data);
        nedovrseneTable.setOnMousePressed(new EventHandler<MouseEvent>()
            {
        @Override
        public void handle(MouseEvent event) {
            if(event.isPrimaryButtonDown() && event.getClickCount() == 2)
            {
                MyMessage selectedMsg = nedovrseneTable.getSelectionModel().getSelectedItem();
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
            
            Folder nedovrsene = null;
            Folder[] folders = store.getDefaultFolder().list("*");
            for(Folder f : folders)
            {
                IMAPFolder imapFolder = (IMAPFolder) f;
                for(String att : imapFolder.getAttributes())
                {
                    if("\\Drafts".equals(att))
                    {
                        nedovrsene = f;
                    }
                }
            }
            nedovrsene.open(Folder.READ_WRITE);
            
            MimeMessage[] seenMessages = (MimeMessage[]) nedovrsene.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
            for(int i=0; i<seenMessages.length; i++)
            {
                MyMessage msg = GmailClient.fetchMessage(seenMessages[i], i+1, "DRAFT");
                data.add(msg);
            }
            
            MimeMessage[] unseenMessages = (MimeMessage[]) nedovrsene.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            for(int i=0; i<unseenMessages.length; i++)
            {
                MyMessage msg = GmailClient.fetchMessage(unseenMessages[i], i+1, "DRAFT");
                data.add(msg);
            }
            nedovrsene.close(false);
            store.close();
        } catch (Exception ex) {
            Logger.getLogger(NedovrseneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
