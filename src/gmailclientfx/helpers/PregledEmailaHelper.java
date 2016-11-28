/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.helpers;

import gmailclientfx.models.MyMessage;

public class PregledEmailaHelper {
    private static MyMessage msg;
    
    public static void setMsg(MyMessage m){ msg = m; }
    public static MyMessage getMsg(){ return msg; }
}
