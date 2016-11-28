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
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author zeljko94
 */
public class User
{
    private int id;
    private String email;
    private String refresh_token;
    
    public User(String email, String rf)
    {
        this.email = email;
        this.refresh_token = rf;
    }
    
    public static int getUserId(String email)
    {
            int id = 0;
        try {
            
            DBConnection db = new DBConnection();
            List<Object> params = Arrays.asList(email);
            db.select("SELECT * FROM users WHERE email=?", params);
            while(db.getResultSet().next())
            {
                id = db.getResultSet().getInt("id");
            }
            db.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    
    public static User getUserByEmail(String email)
    {
        User user = null;
        try {
            DBConnection db = new DBConnection();
            
            List<Object> params = Arrays.asList(email);
            db.select("SELECT * FROM users WHERE email=?", params);
            while(db.getResultSet().next())
            {
                user = new User(db.getResultSet().getString("email"),
                        db.getResultSet().getString("refresh_token"));
                user.setId(db.getResultSet().getInt("id"));
            }
            db.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return user;
    }
    
    public static void unesiUseraUBazu(User u)
    {
        DBConnection db = new DBConnection();
        
        List<Object> params = Arrays.asList(u.getEmail(), u.getRefreshToken());
        db.insert("INSERT INTO users(email, refresh_token) VALUES(?, ?)", params);
        
        db.disconnect();
    }
    
    
    public int getId(){ return this.id; }
    public String getEmail(){ return this.email; }
    public String getRefreshToken(){ return this.refresh_token; }
    
    public void setId(int id){ this.id = id; }
    public void setEmail(String email){ this.email = email; }
    public void setRefreshToken(String rf){ this.refresh_token = rf; }
    
}
