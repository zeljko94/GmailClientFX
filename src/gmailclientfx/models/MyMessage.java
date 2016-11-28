/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.models;

import gmailclientfx.core.DBConnection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author zeljko94
 */
public class MyMessage {
    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleIntegerProperty user_id = new SimpleIntegerProperty();
    private SimpleStringProperty subject = new SimpleStringProperty();
    private SimpleStringProperty sender = new SimpleStringProperty();
    private SimpleStringProperty body = new SimpleStringProperty();
    private SimpleStringProperty date_received = new SimpleStringProperty();
    private SimpleStringProperty label = new SimpleStringProperty();
    private SimpleIntegerProperty tblId = new SimpleIntegerProperty();
    private SimpleStringProperty recipients = new SimpleStringProperty();
    
    public MyMessage( int uid, String subject, String sender, String recipients, String body, String date, String label)
    {
        this.user_id = new SimpleIntegerProperty(uid);
        this.subject = new SimpleStringProperty(subject);
        this.sender = new SimpleStringProperty(sender);
        this.body = new SimpleStringProperty(body);
        this.date_received = new SimpleStringProperty(date);
        this.label = new SimpleStringProperty(label);
        this.recipients =  new SimpleStringProperty(recipients);
    }
    
    public static ObservableList<MyMessage> dohvatiInboxIzBaze(String email)
    {
            ObservableList<MyMessage> messages = FXCollections.observableArrayList();
        try {
            
            DBConnection db = new DBConnection();
            List<Object> params = Arrays.asList(email);
            db.select("SELECT * FROM messages m, users u WHERE m.user_id=u.id AND u.email=? AND m.label='INBOX'", params);
            
            while(db.getResultSet().next())
            {
                MyMessage message = new MyMessage(db.getResultSet().getInt("user_id"),
                                                  db.getResultSet().getString("subject"),
                                                  db.getResultSet().getString("sender"),
                                                  db.getResultSet().getString("recipients"),
                                                  db.getResultSet().getString("body"),
                                                  db.getResultSet().getString("date_received"), 
                                                  db.getResultSet().getString("label"));
                message.setId(db.getResultSet().getInt("id"));
                messages.add(message);
            }
            db.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(MyMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  messages;
    }
    
    public static void unesiUBazu(MyMessage message)
    {
        DBConnection db = new DBConnection();
        List<Object> params = Arrays.asList(message.getUserId(),
                                            message.getSubject(),
                                            message.getSender(),
                                            message.getRecipients(),
                                            message.getBody(),
                                            message.getDateReceived(),
                                            message.getLabel());
        db.insert("INSERT INTO messages(user_id, subject, sender, recipients, body, date_received, label)" + 
                  "VALUES(?,?,?,?,?,?)", params);
        db.disconnect();
    }
    
    // GETTERS
    public int getId(){return this.id.get();}
    public int getUserId(){return this.user_id.get();}
    public String getSubject(){return this.subject.get();}
    public String getSender(){return this.sender.get();}
    public String getBody(){return this.body.get();}
    public String getDateReceived(){return this.date_received.get();}
    public String getLabel(){return this.label.get();}
    public Integer getTblId(){ return this.tblId.get(); }
    public String getRecipients(){ return this.recipients.get(); }
    // SETTERS
    public void setId(int id){ this.id.set(id); }
    public void setUserId(int id){ this.user_id.set(id); }
    public void setSubject(String s){ this.subject.set(s); }
    public void setSender(String s){ this.sender.set(s); }
    public void setBody(String b){ this.body.set(b); }
    public void setDateReceived(String t){ this.date_received.set(t); }
    public void setLabel(String lbl){ this.label.set(lbl); }
    public void setTblId(Integer id){ this.tblId.set(id); }
    public void setRecipients(String rec){ this.recipients.set(rec); }
}
