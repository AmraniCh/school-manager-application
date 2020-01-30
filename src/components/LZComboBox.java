/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.border.*;

/**
 *
 * @author WILL
 */
public class LZComboBox extends JComboBox{
    
    public LZComboBox(){
        
        this.setBorder( new MatteBorder(0, 0, 2, 0, CustomColors.SECONDARY));
        
        this.setFont(new CustomFont(16));
        
        this.setEditable(true);
        
        this.setBackground(Color.white);
        
    }
    
}
