
package components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;


public final class LZButton extends JButton{
      private Color hoverColor;
      private Color before;
      
//    public String EText = "LZButton";
//    public Color EBackgrondColor = new Color(237, 121, 149);
//    public Color EForeColor = Color.white;
//    
//    private Rectangle2D rect;
//
//    public String getEText() {
//        return EText;
//    }
//    
//    public void setEText(String EText) {
//        this.EText = EText;
//    }
//
//    public Color getEForeColor() {
//        return EForeColor;
//    }
//
//    public void setEForeColor(Color EForeColor) {
//        this.EForeColor = EForeColor;
//    }
//
//    public Color getEBackgrondColor() {
//        return EBackgrondColor;
//    }
//
//    public void setEBackgrondColor(Color EBackgrondColor) {
//        this.EBackgrondColor = EBackgrondColor;
//    }
    


    public LZButton(){
        
        this.setBorderPainted(false);
        this.setForeground(Color.white);
        this.setText("Ajouter");
        this.setBackground(new Color(237, 121, 149));
        this.setFocusable(false);
        this.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        this.setSize(120, 35); 
      
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton $this = this;
        
        
        MouseAdapter mouseAdapter = new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
                hoverColor = $this.getBackground();
                before = $this.getBackground();
                $this.setBackground(new Color(85, 85, 85));
            }  

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
                $this.setBackground(hoverColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                $this.setBackground(before);
            }
            
            
            
           
        };
         
        addMouseListener(mouseAdapter);
        
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
    }
    
    
    
    
}