/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailclientfx.models;

import java.awt.Color;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import sun.awt.shell.ShellFolder;

/**
 *
 * @author zeljko94
 */
public class MyAttachment
{
    private java.io.File FILE;
    private Image IMG;
    private ImageIcon IMG_ICON;
    private JLabel LABEL;
    private String TEXT;
    
    public MyAttachment(String path) throws FileNotFoundException
    {
        FILE = new java.io.File(path);
        ShellFolder sf = ShellFolder.getShellFolder(FILE);
        ImageIcon imgIcon = new ImageIcon(sf.getIcon(true));
        Image img = imgIcon.getImage();
        IMG = img.getScaledInstance(50, 50, Image.SCALE_REPLICATE);
        IMG_ICON = new ImageIcon(IMG);

        TEXT = FILE.getName();
        LABEL = new JLabel(TEXT, IMG_ICON, JLabel.LEFT);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
        LABEL.setIcon(IMG_ICON);
        LABEL.setVerticalTextPosition(JLabel.BOTTOM);
        LABEL.setHorizontalTextPosition(JLabel.CENTER);
        LABEL.setBorder(border);
    }
    
    @Override
    public boolean equals(Object o)
    {
        MyAttachment att = (MyAttachment) o;
        if(FILE.equals(att.FILE) &&
           TEXT.equals(att.TEXT)) return true;
        else return false;
    }
    
    public static boolean containsByPath(List<MyAttachment> list, String path)
    {
        for(MyAttachment a : list)
        {
            if(a.getFile().getPath().equals(path)) return true;
        }
        return false;
    }
    
    public java.io.File getFile(){ return FILE; }
    public Image getImg(){ return IMG; }
    public ImageIcon getImgIcon(){ return IMG_ICON; }
    public JLabel getLabel(){ return LABEL; }
    public String getText(){ return TEXT; }
}
