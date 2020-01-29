/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;
import java.awt.Color;
import javax.swing.*;

/**
 *
 * @author WILL
 */
public class LZTable extends JTable {
    
    public LZTable(){
        this.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        this.setFont(new java.awt.Font("Segoe UI", 0, 14));
        
        this.setGridColor(new java.awt.Color(255, 255, 255));

        this.setRowHeight(40);

        this.setRowMargin(0);

        this.setSelectionBackground(new java.awt.Color(255, 120, 172));
                
        this.setBackground(Color.white);
        
        this.setShowGrid(false);
        
        this.setFillsViewportHeight(true);
    }
    
    public LZTable(String[] cols, Object[][] rows){

        this.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        this.setFont(new java.awt.Font("Segoe UI", 0, 14));
        
        this.setModel(new javax.swing.table.DefaultTableModel(rows, cols) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });

        this.setGridColor(new java.awt.Color(255, 255, 255));

        this.setRowHeight(40);

        this.setRowMargin(0);

        this.setSelectionBackground(new java.awt.Color(255, 120, 172));
                
        this.setBackground(Color.white);

        this.setShowGrid(false); 
        
        
    }
    
    
}
