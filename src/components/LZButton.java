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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public final class LZButton extends JButton{
    private Color hoverColor;
    private Color beforeHoverColor;
    
    public LZButton(){
        this.setBorderPainted(false);
        this.setForeground(Color.white);
        this.setText("Ajouter");
        this.setBackground(new Color(237, 121, 149));
        this.setFocusable(false);
        this.setFont(new CustomFont(16));
        this.setSize(120, 35); 
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton $this = this;
        MouseAdapter mouseAdapter = new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
                hoverColor = $this.getBackground();
                beforeHoverColor = $this.getBackground();
                $this.setBackground(CustomColors.BLACK);
            }  

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
                $this.setBackground(hoverColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                $this.setBackground(beforeHoverColor);
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