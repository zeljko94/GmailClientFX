/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.core;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.ListDraftsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Profile;
import com.sun.mail.smtp.SMTPTransport;
import static gmailclientfx.core.OAuth2Authenticator.connectToSmtp;
import gmailclientfx.models.MyAttachment;
import gmailclientfx.models.MyMessage;
import gmailclientfx.models.User;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.apache.commons.mail.util.MimeMessageParser;

/**
 *
 * @author zeljko94
 */
public class GmailClient
{
    private static String EMAIL;
    private static BigInteger USER_ID;
    private static String ACCESS_TOKEN;
    private static String REFRESH_TOKEN;
    private static String CLIENT_ID = "925801090673-0bmdqhi0g8u7hk8ltss1d8s0pa7ra8sn.apps.googleusercontent.com";
    private static String CLIENT_SECRET = "rXwvCYdj4g-Kj3gjt1N0k2uu";
    private static List<String> SCOPES = Arrays.asList("https://mail.google.com/",
                                        "https://www.googleapis.com/auth/userinfo.profile");
    private static GoogleAuthorizationCodeFlow FLOW;
    private static GoogleCredential CREDENTIAL;
    private static JacksonFactory JSON_FACTORY = new JacksonFactory();
    private static HttpTransport TRANSPORT = new NetHttpTransport();
    private static Gmail GMAIL;
    private static Profile PROFILE;
    
    private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    
    private static List<MyAttachment> ATTACHMENTS;
    
     public static void authorizeUser() throws IOException
    {
        
        FLOW = new GoogleAuthorizationCodeFlow.Builder(TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
                //.setApprovalPrompt("select_account")
                .setAccessType("offline")
                .build();
        
        String url = FLOW.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        String txtURI = url + "&prompt=select_account";
        String code = "";
        
        if(Desktop.isDesktopSupported())
        {
            try {
                Desktop.getDesktop().browse(new URI(txtURI));
                
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Verifikacija");
                dialog.setHeaderText(null);
                dialog.setContentText("Unesite verifikacijski kod: ");
                
                Optional<String> result = dialog.showAndWait();
                if(result.isPresent())
                {
                    code = result.get();
                }
            } catch (URISyntaxException ex) {
                Logger.getLogger(GmailClient.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Greska prilikom logiranja!");
            }
        }
        
        GoogleTokenResponse tokenResponse = FLOW.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        CREDENTIAL = new GoogleCredential.Builder()
            .setTransport(TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
            .addRefreshListener(new CredentialRefreshListener() {
              @Override
              public void onTokenResponse(Credential credential, TokenResponse tokenResponse) {
                // Handle success.
              }

              @Override
              public void onTokenErrorResponse(Credential credential,
                  TokenErrorResponse tokenErrorResponse) {
                  // Handle error.
              }
            })
            .build();

        CREDENTIAL.setFromTokenResponse(tokenResponse);
        GMAIL = new Gmail.Builder(TRANSPORT, JSON_FACTORY, CREDENTIAL).setApplicationName("JavaGmailSend").build();
        PROFILE = GMAIL.users().getProfile("me").execute();
        EMAIL = PROFILE.getEmailAddress();
        USER_ID = PROFILE.getHistoryId();
        ACCESS_TOKEN = CREDENTIAL.getAccessToken();
        REFRESH_TOKEN = CREDENTIAL.getRefreshToken();
        /*Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verifikacija");
        alert.setHeaderText(null);
        alert.setContentText("Uspješna verifikacija!");
        alert.showAndWait();*/
    }
     
     
     public static MyMessage fetchMessage(MimeMessage m, int tblIndex, String lbl)
        {
            MyMessage myMsg = null;
        try {
            MimeMessage msg = new MimeMessage(m);
            MimeMessageParser parser = new MimeMessageParser(msg);
            parser.parse();
            
            String naslov = parser.getSubject();
            String from = parser.getFrom();
            List<Address> to = parser.getTo();//msg.getRecipients(javax.mail.Message.RecipientType.TO);
            String toStr = to.toString().replace("[", "").replace("]", "");
            if(toStr.equals("")) toStr = GmailClient.getEmail();
            String body = "";
            if(parser.hasHtmlContent()) body = parser.getHtmlContent();
            else body = parser.getPlainContent();
            String date = msg.getSentDate().toString();
            String label = lbl;
            
            myMsg = new MyMessage(User.getUserId(GmailClient.getEmail()),
                    naslov,
                    from,
                    toStr,
                    body,
                    date,
                    label);
            myMsg.setTblId(tblIndex);
        } catch (MessagingException ex) {
            Logger.getLogger(GmailClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GmailClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myMsg;
    }
     
    public static void refreshAccessToken() throws IOException
    {
        CREDENTIAL = new GoogleCredential.Builder()
        .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
        .setJsonFactory(JSON_FACTORY).setTransport(TRANSPORT).build()
        .setRefreshToken(REFRESH_TOKEN);
        CREDENTIAL.refreshToken();
        GMAIL = new Gmail.Builder(TRANSPORT, JSON_FACTORY, CREDENTIAL).setApplicationName("JavaGmailSend").build();
        PROFILE = GMAIL.users().getProfile("me").execute();
        EMAIL = PROFILE.getEmailAddress();
        USER_ID = PROFILE.getHistoryId();
        ACCESS_TOKEN = CREDENTIAL.getAccessToken();
        REFRESH_TOKEN = CREDENTIAL.getRefreshToken();
       /* Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verifikacija!");
        alert.setHeaderText(null);
        alert.setContentText("Uspješna verifikacija!");
        alert.showAndWait();*/
    }
    
    public static MimeMessage getMimeMessage(Gmail service, String userId, String messageId)
      throws IOException, MessagingException {
    com.google.api.services.gmail.model.Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

    byte[] emailBytes = Base64.decodeBase64(message.getRaw());

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

    return email;
  }
    
    public static List<com.google.api.services.gmail.model.Message> listMessagesWithLabels(Gmail service, String userId, List<String> labelIds) throws IOException, MessagingException
    {
        ListMessagesResponse response = service.users().messages().list(userId).setLabelIds(labelIds).execute();

        List<com.google.api.services.gmail.model.Message> messages = new ArrayList<com.google.api.services.gmail.model.Message>();
        while (response.getMessages() != null)
        {
            messages.addAll(response.getMessages());
          if (response.getNextPageToken() != null)
          {
            String pageToken = response.getNextPageToken();
            response = service.users().messages().list(userId).setLabelIds(labelIds)
                .setPageToken(pageToken).execute();
          }
          else
          {
            break;
          }
    }
    /*
    for (com.google.api.services.gmail.model.Message message : messages)
    {
        int FK_userId = User.getUserId(getEmail());
        String subject = getMimeMessage(GMAIL, EMAIL, message.getId()).getSubject();
        String sender = InternetAddress.toString(getMimeMessage(GMAIL, EMAIL, message.getId()).getFrom());
        String body = getMimeMessage(GMAIL, EMAIL, message.getId()).getContent().toString();
        
        String date = getMimeMessage(GMAIL, EMAIL, message.getId()).getSentDate().toString();
        String label = labelIds.get(0);
        
        MyMessage newMyMessage = new MyMessage(FK_userId, subject, sender, body, date, label);
       // MyMessage.unesiUBazu(newMyMessage);
        Object content = getMimeMessage(GMAIL, EMAIL, message.getId()).getContent();
        if (content instanceof String)
        {
            String b = (String)content;
        }
        else if (content instanceof Multipart)
        {
            Multipart mp = (Multipart)content;
            System.out.println(mp.getBodyPart(0));
        }
    }*/
    return messages;
  }
    
    
    public static void listDrafts(Gmail service, String userId) throws IOException {
    ListDraftsResponse response = service.users().drafts().list(userId).execute();
    List<Draft> drafts = response.getDrafts();
    for (Draft draft : drafts) {
      System.out.println(draft.toPrettyString());
    }
  }
    
    public static Draft getDraft(Gmail service, String userId, String draftId)
      throws IOException {
    Draft draft = service.users().drafts().get(userId, draftId).execute();
    Message message = draft.getMessage();

    return draft;
  }
    public static void sendMessage(String to, String subject, String body, List<String> attachments) throws Exception
    {
                // authenticate with gmail smtp server
        SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com",
                                            587,
                                            EMAIL,
                                            ACCESS_TOKEN,
                                            true);
        
        // kreiraj MimeMessage objekt
        MimeMessage msg = new MimeMessage(OAuth2Authenticator.getSession());
        
        // dodaj headere
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        
        
        msg.setFrom(new InternetAddress(EMAIL));
        msg.setRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(to));
        msg.setSubject(subject, "UTF-8");
        msg.setReplyTo(InternetAddress.parse(EMAIL, false));
        
        // tijelo poruke
        BodyPart msgBodyPart = new MimeBodyPart();
        msgBodyPart.setText(body);
        
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(msgBodyPart);
        msg.setContent(multipart);
        
        // dodaj privitke
        if(attachments.size() > 0)
        {
            for (String attachment : attachments) {
                msgBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                msgBodyPart.setDataHandler(new DataHandler(source));
                msgBodyPart.setFileName(source.getName());
                multipart.addBodyPart(msgBodyPart);
            }
            msg.setContent(multipart);
        }
        smtpTransport.sendMessage(msg, InternetAddress.parse(to));
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Poruka poslana!");
        alert.setHeaderText(null);
        alert.setContentText("Email uspješno poslan!");
        alert.showAndWait();
    }
    
    
    //GETTERS
    public static String getEmail(){ return EMAIL; }
    public static BigInteger getUserId(){ return USER_ID; }
    public static String getAccesToken(){ return ACCESS_TOKEN; }
    public static String getRefreshToken(){ return REFRESH_TOKEN; }
    public static String getClientId(){ return CLIENT_ID; }
    public static String getClientSecret(){ return CLIENT_SECRET; }
    public static List<String> getScopes(){ return SCOPES; }
    public static GoogleAuthorizationCodeFlow getFlow(){ return FLOW; }
    public static GoogleCredential getCredential(){ return CREDENTIAL; }
    public static JacksonFactory getJsonFactory(){ return JSON_FACTORY; }
    public static HttpTransport getTransport(){ return TRANSPORT; }
    public static Gmail getGmail(){ return GMAIL; }
    public static Profile getProfile(){ return PROFILE;}
    // SETTERS
    public static void setEmail(String e){ EMAIL = e; }   
    public static void setUserId(BigInteger id){ USER_ID = id; }
    public static void setAccessToken(String token){ ACCESS_TOKEN = token; }   
    public static void setRefreshToken(String token){ REFRESH_TOKEN = token; }   
    public static void setClientId(String id){ CLIENT_ID = id; }   
    public static void setClientSecret(String secret){ CLIENT_SECRET = secret; }   
    public static void setScopes(List<String> scopes){ SCOPES = scopes; }
    public static void setFlow(GoogleAuthorizationCodeFlow f){ FLOW = f; }
    public static void setCredential(GoogleCredential cred){ CREDENTIAL = cred; }
    public static void setJsonFactory(JacksonFactory js){ JSON_FACTORY = js; }
    public static void setTransport(HttpTransport t){ TRANSPORT = t; }
    public static void setGmail(Gmail g){ GMAIL = g; }
    public static void setProfile(Profile p ){ PROFILE = p; }
    
}
