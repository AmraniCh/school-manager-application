/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.time.LocalDate;
import javax.swing.JPanel;

/**
 *
 * @author WILL
 */
public class LZLogPanelItem extends JPanel{
    private LocalDate date;
    private String body;
    
    public LZLogPanelItem(LocalDate date, String body){
        this.date = date;
        this.body = body;
        
        this.setBackground(Color.white);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(new LZInputLabel(date.toString()));
        this.add(new LZInputLabel(body));
        this.setSize(400, 14);
    }
    
}
