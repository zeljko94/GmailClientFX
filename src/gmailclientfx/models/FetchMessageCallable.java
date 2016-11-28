/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.models;

import gmailclientfx.core.GmailClient;
import java.util.concurrent.Callable;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.util.MimeMessageParser;

/**
 *
 * @author zeljko94
 */
public class FetchMessageCallable implements Callable<MyMessage>
    {
        private MimeMessage msg;
        private int tblIndex;
        private String lbl;
        
        public MimeMessage getMsg(){ return this.msg; }
        public int getTblIndex(){ return this.tblIndex; }
        public String getLbl(){ return this.lbl; }
        
        public void setMsg(MimeMessage m) { this.msg = m;}
        public void setTblIndex(int i){ this.tblIndex = i; }
        public void setLbl(String l){ this.lbl = l; }
        
        public FetchMessageCallable(MimeMessage m, int i, String lbl)
        {
            this.msg = m;
            this.tblIndex = i;
            this.lbl = lbl;
        }
        
        @Override
        public MyMessage call() throws Exception {
            return fetchMessage(msg, tblIndex, lbl);
        }
        
        public MyMessage fetchMessage(MimeMessage m, int tblIndex, String lbl) throws MessagingException, Exception
        {
        MimeMessage msg = new MimeMessage(m);
                MimeMessageParser parser = new MimeMessageParser(msg);
                parser.parse();

                String naslov = parser.getSubject();
                String from = parser.getFrom();
                Address[] to = msg.getRecipients(Message.RecipientType.TO);
                String toStr = "";
                if(to.length > 1)
                {
                    for(int k=0; k<to.length; k++)
                    {
                        if(k == to.length-1) toStr += to[k].toString();
                        else toStr += to[k].toString() + ",";
                    }
                }
                else
                {
                    toStr = to[0].toString();
                }
                String body = parser.getHtmlContent();
                if(body.equals("")) body = parser.getPlainContent();
                String date = msg.getSentDate().toString();
                String label = lbl; 

                MyMessage myMsg = new MyMessage(User.getUserId(GmailClient.getEmail()),
                                            naslov,
                                            from,
                                            toStr,
                                            body,
                                            date,
                                            label);
                myMsg.setTblId(tblIndex);
                return myMsg;
    }
        
    }
