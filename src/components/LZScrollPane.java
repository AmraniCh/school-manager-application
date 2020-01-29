/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

/**
 *
 * @author WILL
 */
public class LZScrollPane extends JScrollPane {
    
    public LZScrollPane(Component comp){
        
        super(comp);
        
        this.setBackground(Color.white);
        
        this.setBorder(BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        
    }
    
}
