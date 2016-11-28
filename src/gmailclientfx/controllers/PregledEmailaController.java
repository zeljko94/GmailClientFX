
package gmailclientfx.controllers;

import gmailclientfx.helpers.PregledEmailaHelper;
import gmailclientfx.models.MyMessage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PregledEmailaController implements Initializable {
    @FXML
    GridPane pregledEmailaGridPane;
    
    @FXML
    Label lblFrom;
    
    @FXML
    Label lblTo;
    
    @FXML
    Label lblNaslov;
    
    @FXML
    Label lblPrivitci;
    
    @FXML
    Label lblDatum;
    
    @FXML
    TextField txtFrom;
    
    @FXML
    TextField txtTo;
    
    @FXML
    TextField txtNaslov;
    
    @FXML
    WebView bodyWebView;
    
    @FXML
    WebEngine bodyWebViewEngine;
    
    @FXML
    Button btnOdgovori;
    
    @FXML
    Button btnProslijedi;
    
    @FXML
    Button btnObrisi;
            
    @FXML
    HBox HBoxPrivitci;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtFrom.setEditable(false);
        txtTo.setEditable(false);
        txtNaslov.setEditable(false);
        bodyWebViewEngine = bodyWebView.getEngine();
        
        MyMessage msg = PregledEmailaHelper.getMsg();
        
        txtFrom.setText(msg.getSender());
        txtTo.setText(msg.getRecipients());
        txtNaslov.setText(msg.getSubject());
        bodyWebViewEngine.loadContent(msg.getBody());
        lblDatum.setText(msg.getDateReceived());
    }    
    
}
