/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.border.MatteBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 *
 * @author WILL
 */
public class LZTextField extends JTextField {
    private boolean Eenabled = true;

    public boolean isEenabled() {
        return Eenabled;
    }

    public void setEenabled(boolean Eenabled) {
        if(Eenabled == false){
            this.setBackground(CustomColors.DISABLED);
            this.setEnabled(Eenabled);
        }
        else{
            this.setEnabled(Eenabled);
            this.setBackground(Color.white);
        }
        
        this.Eenabled = Eenabled;
    }
    
    
    
    public LZTextField(){
        
        this.setForeground(new Color(85, 85, 85));
        
        this.setPreferredSize(new Dimension(250, 32));
        
        this.setFont(new CustomFont(16));
        
        this.setBackground(Color.white);
        
        this.setEnabled(this.Eenabled);
        
        this.setBorder( new MatteBorder(0, 0, 2, 0, CustomColors.BLACK));
        
        JTextField $this = this;
        
        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e); //To change body of generated methods, choose Tools | Templates.
                $this.setText("");
            }
        };
        
        this.addFocusListener(fa);
        

    }
    
}
