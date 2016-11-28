/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.core;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zeljko94
 */
public class DBConnection {
    private String host;
    private String user;
    private String pass;
    private String db;
    
    protected Connection conn;
    protected PreparedStatement st;
    protected ResultSet rs;
    
    public DBConnection()
    {
        this.host = "localhost";
        this.user = "root";
        this.pass = "";
        this.db = "gmailclient";
        this.connect();
    }
   
    public DBConnection(String host, String user, String pass, String db)
    {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.db = db;
        this.connect();
    }
    
    public void connect()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            try {
                this.conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.db + "?user=" +this.user + "&password=" + this.pass);
            } catch (SQLException ex) {
                System.out.println("Povezivanje sa bazom nije uspjelo.");
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch (ClassNotFoundException ex)
        {
            System.out.println("Sustav nije uspio pronaci klasu za konekciju na mysql.");
        }
    }
    
    public void disconnect()
    {
        try {
           if(this.conn != null) this.conn.close();
           if(this.rs != null) this.rs.close();
           if(this.st != null) this.st.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void bindParams(List<Object> params)
    {
        if(params == null) return;
        
        for(int i=0; i<params.size(); i++)
        {
            try {
                String type = params.get(i).getClass().getSimpleName().toString().toLowerCase();
                
                switch(type)
                {
                    case "string":
                        this.st.setString(i+1, params.get(i).toString());
                        break;
                        
                    case "integer":
                        this.st.setInt(i+1, Integer.parseInt(params.get(i).toString()));
                        break;
                        
                    case "double":
                        break;
                        
                    case "float":
                        this.st.setFloat(i+1, Float.parseFloat(params.get(i).toString()));
                        break;
                    
                    case "date":
                        Date date = (Date)params.get(i);
                        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                        this.st.setDate(i+1, sqlDate);
                        break;
                        
                        
                    default:
                        break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
        
    public ResultSet select(String sql, List<Object> params)
    {
        try {
            this.st = (PreparedStatement) this.conn.prepareStatement(sql);
            bindParams(params);
            this.rs = this.st.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return rs;
    }
    
    public void insert(String sql, List<Object> params)
    {
        try {
            this.st = (PreparedStatement) this.conn.prepareStatement(sql);
            bindParams(params);
            this.st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void update(String sql, List<Object> params)
    {
        try {
            this.st = (PreparedStatement) this.conn.prepareStatement(sql);
            bindParams(params);
            this.st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void delete(String sql, List<Object> params)
    {
        try {
            this.st = (PreparedStatement) this.conn.prepareStatement(sql);
            bindParams(params);
            this.st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
   
    // GETTERS
    public String getHost(){ return this.host; }
    public String getUser(){ return this.user; }
    public String getPass(){ return this.pass; }
    public String getDB(){ return this.db; }
    public ResultSet getResultSet(){ return this.rs; }
    public PreparedStatement getStatement(){ return this.st; }
    public Connection getConnection(){ return this.conn; }
    
    // SETTERS
    public void setHost(String host){ this.host = host; }
    public void setUser(String user){ this.user = user; }
    public void setPass(String pass){ this.pass = pass; }
    public void setDB(String db){ this.db = db; }
    public void setResultSet(ResultSet rs){ this.rs = rs; }
    public void setStatement(PreparedStatement st){ this.st = st; }
    public void setConnection(Connection conn){ this.conn = conn; }
}

