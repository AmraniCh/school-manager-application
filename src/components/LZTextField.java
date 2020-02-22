/* 
 * Copyright 2020 EL AMRANI CHAKIR - LAZZARD - 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package components;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.border.MatteBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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
        this.setForeground(CustomColors.BLACK);
        this.setPreferredSize(new Dimension(250, 32));
        this.setFont(new CustomFont(16));
        this.setBackground(Color.white);        
        this.setEnabled(this.Eenabled);   
        this.setBorder( new MatteBorder(0, 0, 2, 0, CustomColors.BLACK));
        
        JTextField $this = this;
        
        /**
         * Placeholder simulation
         */
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
