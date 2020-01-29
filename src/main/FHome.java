/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import components.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import utilities.DBManager;
import utilities.DBQueryHelper;
import utilities.DBUtilities;

/**
 *
 * @author WILL
 */
public class FHome extends javax.swing.JFrame {
    
    /**
     * Global vars
     */
    private JPanel selectedMenuPanel;
    private LZTable myTable;
    /**
     * Specify current table 
     */
    public static String currentTable;
    public static int currentAnnee = 2020;
    /**
     * Timer for notifications
     */
    Timer timer;
    int timerSeconds = 3;

    /**
     * Creates new form FHome
     */
    public FHome() {
        initComponents();
        
        DBManager.setConnection(); // Set database connecion
        
        notificationPanel.setVisible(false);
        
        myTable = new LZTable(); // intialize LZTable
        this.dynamicTable.add(new LZScrollPane(myTable)); // Add LZTable to LZScrollPane
        
        updateCounters(); // Update counters
        
        fillComboAnnees();
        
        comboAnnees.setSelectedIndex(comboAnnees.getItemCount() - 1); // Set last school year
        
        currentAnnee = Integer.parseInt(comboAnnees.getSelectedItem().toString()); // Set currentAnnee gloabal variable
        
        viewChanger(this.tableView);
        
        FHome form = this; // Store actual form to use in mouse adapter interface

        bAccueil.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                
                viewChanger(form.logsView);
                
                // Change Title & Icon
                setTitleIcon("Dernière activités", "history.png");
                
                currentTable = null;
                
            }
            
        });
        
        bEtu.addMouseListener(new MouseAdapter() {
      
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                
                // Change selected table
                currentTable = "etudiant";
                
                // Change Title & Icon
                setTitleIcon("Étudiants", "users.png");
                
                // Change to table view
                viewChanger(form.tableView);
                
                // fill table data
                fillDataTable();     
              
            }
                
        });
        
        bDep.addMouseListener(new MouseAdapter() {
      
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                
                // Change selected table
                currentTable = "departement";
                
                // Change Title & Icon
                setTitleIcon("Départements", "department.png");
                
                // Change to table view
                viewChanger(form.tableView);
                
                // fill table data
                fillDataTable();  
            }
            
        });
        
        bFil.addMouseListener(new MouseAdapter() {
      
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                
                // Change selected table
                currentTable = "filiere";
                
                // Change Title & Icon
                setTitleIcon("Filières", "list.png");
                
                // Change to table view
                viewChanger(form.tableView);
                
                // fill table data
                fillDataTable();  
            }
        });
        
        /**
         * Update counters when changing selected item in ComboBox
         */
        comboAnnees.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboAnnees.getModel().getSize() > 0)
                    currentAnnee = Integer.parseInt(((LZComboBox)e.getSource()).getSelectedItem().toString());
                updateCounters(); // Update counters
                if( currentTable != null )
                    fillDataTable();
            }
            
        });
        
        /**
         * Add new school year
         */
        bAddAnnee.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                
                DBQueryHelper.insert("annee_scolaire", new String[][] { { null } });
                showNotification("L'année scolarité ajoutée avec success!"); // Show showNotification
                fillComboAnnees();
                //comboAnnees.setSelectedIndex(comboAnnees.getItemCount() - 1);
            }   
        });
        
        /**
         * Delete selected school year
         */
//        bDeleteAnnee.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
//
//                DBQueryHelper.delete("annee_scolaire", 
//                        new String[][] { 
//                            { 
//                                "annee", 
//                                comboAnnees.getSelectedItem().toString() 
//                            } 
//                        },
//                        "AND");
//                fillComboAnnees(); // Fill comboAnnee
//                comboAnnees.setSelectedIndex(comboAnnees.getItemCount() - 1); // Set last year as selected year
//                showNotification("L'année scolarité supprimée avec success!"); // Show showNotification
//                viewChanger(logsView);
//            }   
//        });
        
    }
    
    private void updateCounters(){
        
        lCountEtud.setText(DBQueryHelper.getCount("etudiant", currentAnnee));
        lCountDept.setText(DBQueryHelper.getCount("departement", currentAnnee));
        lCountFill.setText(DBQueryHelper.getCount("filiere", currentAnnee));
  
    }
    
    private void fillComboAnnees(){
        comboAnnees.removeAllItems();
        String[] rows = DBQueryHelper.getDataByColumn("annee_scolaire", "annee");
        for (int i = 0; i < rows.length; i++) {
            comboAnnees.addItem(rows[i]);
        }
    }
    
    private void fillDataTable(){ 

        DefaultTableModel tableModel = new DefaultTableModel(
                DBQueryHelper.getRows(currentTable, currentAnnee),
                DBUtilities.getColumns(currentTable, DBUtilities.UPPER_CASE));
        
        myTable.setModel(tableModel);
        
    }
    
    private void showNotification(String message){
        notificationPanel.setVisible(true);
        lNotification.setText(message);  

        timer = new Timer(500, new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {
                //...Update the progress bar...

                timerSeconds--;
                if (timerSeconds == 0) {
                    notificationPanel.setVisible(false);
                    timer.stop();
                    timerSeconds = 3;
                    //...Update the GUI...
                }
            }
        
        });
        
        timer.start();

    }
    
    /**
     * @param panel  
     * Changing the color of panel when click or hover
     */
    private void setPanelColor(JPanel panel){
        panel.setBackground(CustomColors.SECONDARY);
    }
    
    /**
     * @param panel 
     * Reset panel color to the default color 
     */
    private void resetPanelColor(JPanel panel){
        panel.setBackground(new Color(233, 78, 135));
    }
    
    private void viewChanger(JPanel view){
        
        this.viewChangerPanel.removeAll();
        this.viewChangerPanel.add(view);
        this.viewChangerPanel.revalidate();
        this.viewChangerPanel.repaint();
        
    }
    
    private void setTitleIcon(String title, String iconName){
        
        this.dynamicTitle.setText(title);
        this.dynamicIcon.setIcon(new ImageIcon(getClass().getResource("/ressources/dynamicIcons/"+iconName)));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        components = new javax.swing.JPanel();
        kButton1 = new keeptoo.KButton();
        kGradientPanel4 = new keeptoo.KGradientPanel();
        lZButton5 = new components.LZButton();
        lZButton6 = new components.LZButton();
        lZButton7 = new components.LZButton();
        lZButton8 = new components.LZButton();
        tableView = new javax.swing.JPanel();
        dynamicTable = new javax.swing.JPanel();
        tableButtons = new javax.swing.JPanel();
        lZButton9 = new components.LZButton();
        lZButton10 = new components.LZButton();
        lZButton11 = new components.LZButton();
        lZButton12 = new components.LZButton();
        logsView = new javax.swing.JPanel();
        lZInputLabel1 = new components.LZInputLabel();
        leftPanel = new javax.swing.JPanel();
        bAccueil = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        bEtu = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bFil = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        bDep = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        kGradientPanel1 = new keeptoo.KGradientPanel();
        lCountEtud = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        kGradientPanel2 = new keeptoo.KGradientPanel();
        lCountDept = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        kGradientPanel3 = new keeptoo.KGradientPanel();
        lCountFill = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        bClose = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        dynamicTitle = new javax.swing.JLabel();
        dynamicIcon = new javax.swing.JLabel();
        viewChangerPanel = new javax.swing.JPanel();
        notificationPanel = new javax.swing.JPanel();
        lNotification = new components.LZInputLabel();
        lZInputLabel2 = new components.LZInputLabel();
        comboAnnees = new components.LZComboBox();
        bAddAnnee = new javax.swing.JLabel();
        bDeleteAnnee = new javax.swing.JLabel();

        components.setBackground(new java.awt.Color(255, 255, 255));
        components.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        kButton1.setText("kButton1");
        components.add(kButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, -1, -1));

        javax.swing.GroupLayout kGradientPanel4Layout = new javax.swing.GroupLayout(kGradientPanel4);
        kGradientPanel4.setLayout(kGradientPanel4Layout);
        kGradientPanel4Layout.setHorizontalGroup(
            kGradientPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );
        kGradientPanel4Layout.setVerticalGroup(
            kGradientPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        components.add(kGradientPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, -1));

        lZButton5.setBackground(new java.awt.Color(28, 104, 150));
        lZButton5.setText("Rechercher");
        components.add(lZButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 270, 120, -1));

        lZButton6.setBackground(new java.awt.Color(38, 46, 60));
        components.add(lZButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, 120, -1));

        lZButton7.setBackground(new java.awt.Color(39, 187, 216));
        lZButton7.setText("Modifier");
        components.add(lZButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 170, 120, -1));

        lZButton8.setText("Supprimer");
        components.add(lZButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 220, 120, -1));

        tableView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dynamicTable.setBackground(new java.awt.Color(255, 204, 102));
        dynamicTable.setLayout(new java.awt.BorderLayout());
        tableView.add(dynamicTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 730, 220));

        tableButtons.setBackground(new java.awt.Color(255, 255, 255));

        lZButton9.setBackground(new java.awt.Color(38, 46, 60));

        lZButton10.setBackground(new java.awt.Color(39, 187, 216));
        lZButton10.setText("Modifier");

        lZButton11.setBackground(new java.awt.Color(28, 104, 150));
        lZButton11.setText("Rechercher");

        lZButton12.setText("Supprimer");

        javax.swing.GroupLayout tableButtonsLayout = new javax.swing.GroupLayout(tableButtons);
        tableButtons.setLayout(tableButtonsLayout);
        tableButtonsLayout.setHorizontalGroup(
            tableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableButtonsLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lZButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(lZButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addComponent(lZButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(lZButton11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );
        tableButtonsLayout.setVerticalGroup(
            tableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        tableView.add(tableButtons, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 730, 60));

        logsView.setBackground(new java.awt.Color(255, 255, 255));

        lZInputLabel1.setText("Dernière activités");

        javax.swing.GroupLayout logsViewLayout = new javax.swing.GroupLayout(logsView);
        logsView.setLayout(logsViewLayout);
        logsViewLayout.setHorizontalGroup(
            logsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logsViewLayout.createSequentialGroup()
                .addGap(239, 239, 239)
                .addComponent(lZInputLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(249, Short.MAX_VALUE))
        );
        logsViewLayout.setVerticalGroup(
            logsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logsViewLayout.createSequentialGroup()
                .addContainerGap(169, Short.MAX_VALUE)
                .addComponent(lZInputLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(153, 153, 153))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(0, 0));
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        leftPanel.setBackground(new java.awt.Color(233, 78, 135));
        leftPanel.setPreferredSize(new java.awt.Dimension(240, 580));

        bAccueil.setBackground(new java.awt.Color(233, 78, 135));
        bAccueil.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        bAccueil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MenuItemHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MenuItemHoverExit(evt);
            }
        });
        bAccueil.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Accueil");
        bAccueil.add(jLabel4);
        jLabel4.setBounds(60, 0, 180, 60);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ressources/staticIcons/home.png"))); // NOI18N
        bAccueil.add(jLabel1);
        jLabel1.setBounds(10, 0, 40, 60);

        bEtu.setBackground(new java.awt.Color(233, 78, 135));
        bEtu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        bEtu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MenuItemHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MenuItemHoverExit(evt);
            }
        });
        bEtu.setLayout(null);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Étudiants");
        bEtu.add(jLabel5);
        jLabel5.setBounds(60, 0, 180, 60);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ressources/staticIcons/users.png"))); // NOI18N
        bEtu.add(jLabel2);
        jLabel2.setBounds(10, 0, 40, 60);

        bFil.setBackground(new java.awt.Color(233, 78, 135));
        bFil.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        bFil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MenuItemHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MenuItemHoverExit(evt);
            }
        });
        bFil.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Filières");
        bFil.add(jLabel6);
        jLabel6.setBounds(60, 0, 180, 60);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ressources/staticIcons/list.png"))); // NOI18N
        bFil.add(jLabel3);
        jLabel3.setBounds(10, 0, 40, 60);

        bDep.setBackground(new java.awt.Color(233, 78, 135));
        bDep.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        bDep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MenuItemHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MenuItemHoverExit(evt);
            }
        });
        bDep.setLayout(null);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Départements");
        bDep.add(jLabel7);
        jLabel7.setBounds(60, 0, 180, 60);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ressources/staticIcons/department.png"))); // NOI18N
        bDep.add(jLabel8);
        jLabel8.setBounds(10, 0, 40, 60);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 32)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Annuaire ENS");

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));
        jSeparator1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jSeparator1.setOpaque(true);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Made With ♥ By EL AMRANI CHAKIR");

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bAccueil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bEtu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bFil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bDep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77))
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(bAccueil, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bEtu, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bDep, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bFil, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addContainerGap())
        );

        getContentPane().add(leftPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 600));

        rightPanel.setBackground(new java.awt.Color(255, 255, 255));
        rightPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        kGradientPanel1.setBackground(new java.awt.Color(255, 255, 255));
        kGradientPanel1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        kGradientPanel1.setkBorderRadius(15);
        kGradientPanel1.setkEndColor(new java.awt.Color(255, 255, 255));
        kGradientPanel1.setkGradientFocus(400);
        kGradientPanel1.setkStartColor(new java.awt.Color(124, 103, 255));
        kGradientPanel1.setPreferredSize(new java.awt.Dimension(179, 128));

        lCountEtud.setFont(new java.awt.Font("Segoe UI", 0, 52)); // NOI18N
        lCountEtud.setForeground(new java.awt.Color(255, 255, 255));
        lCountEtud.setText("5");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Étudiants");

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lCountEtud, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCountEtud)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rightPanel.add(kGradientPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 65, 200, 131));

        kGradientPanel2.setBackground(new java.awt.Color(255, 255, 255));
        kGradientPanel2.setkBorderRadius(15);
        kGradientPanel2.setkEndColor(new java.awt.Color(255, 255, 255));
        kGradientPanel2.setkGradientFocus(300);
        kGradientPanel2.setkStartColor(new java.awt.Color(255, 196, 44));

        lCountDept.setFont(new java.awt.Font("Segoe UI", 0, 52)); // NOI18N
        lCountDept.setForeground(new java.awt.Color(255, 255, 255));
        lCountDept.setText("13");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Départements");

        javax.swing.GroupLayout kGradientPanel2Layout = new javax.swing.GroupLayout(kGradientPanel2);
        kGradientPanel2.setLayout(kGradientPanel2Layout);
        kGradientPanel2Layout.setHorizontalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lCountDept, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addContainerGap())
        );
        kGradientPanel2Layout.setVerticalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCountDept)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rightPanel.add(kGradientPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 65, 200, 131));

        kGradientPanel3.setBackground(new java.awt.Color(255, 255, 255));
        kGradientPanel3.setkBorderRadius(15);
        kGradientPanel3.setkEndColor(new java.awt.Color(255, 255, 255));
        kGradientPanel3.setkStartColor(new java.awt.Color(156, 63, 229));

        lCountFill.setFont(new java.awt.Font("Segoe UI", 0, 52)); // NOI18N
        lCountFill.setForeground(new java.awt.Color(255, 255, 255));
        lCountFill.setText("9");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Filières");

        javax.swing.GroupLayout kGradientPanel3Layout = new javax.swing.GroupLayout(kGradientPanel3);
        kGradientPanel3.setLayout(kGradientPanel3Layout);
        kGradientPanel3Layout.setHorizontalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lCountFill, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        kGradientPanel3Layout.setVerticalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCountFill)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rightPanel.add(kGradientPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 65, 200, 131));

        bClose.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        bClose.setForeground(new java.awt.Color(153, 153, 153));
        bClose.setText("X");
        bClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bCloseMousePressed(evt);
            }
        });
        rightPanel.add(bClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(732, 0, -1, -1));

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));

        dynamicTitle.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        dynamicTitle.setForeground(new java.awt.Color(85, 85, 85));
        dynamicTitle.setText("Dernière activités");

        dynamicIcon.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        dynamicIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dynamicIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ressources/dynamicIcons/history.png"))); // NOI18N

        viewChangerPanel.setLayout(new java.awt.BorderLayout());

        notificationPanel.setBackground(new java.awt.Color(255, 120, 173));

        lNotification.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout notificationPanelLayout = new javax.swing.GroupLayout(notificationPanel);
        notificationPanel.setLayout(notificationPanelLayout);
        notificationPanelLayout.setHorizontalGroup(
            notificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notificationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lNotification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(615, Short.MAX_VALUE))
        );
        notificationPanelLayout.setVerticalGroup(
            notificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notificationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lNotification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(dynamicIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dynamicTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(notificationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewChangerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dynamicIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dynamicTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewChangerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        rightPanel.add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 214, 760, -1));

        lZInputLabel2.setText("Année scolaire");
        lZInputLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rightPanel.add(lZInputLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 11, 123, 34));

        comboAnnees.setFocusable(false);
        rightPanel.add(comboAnnees, new org.netbeans.lib.awtextra.AbsoluteConstraints(154, 10, 101, 36));

        bAddAnnee.setFont(new java.awt.Font("Segoe UI Black", 1, 28)); // NOI18N
        bAddAnnee.setForeground(new java.awt.Color(153, 153, 153));
        bAddAnnee.setText("+");
        bAddAnnee.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bAddAnnee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMouseExited(evt);
            }
        });
        rightPanel.add(bAddAnnee, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 9, -1, -1));

        bDeleteAnnee.setFont(new java.awt.Font("Segoe UI Black", 1, 28)); // NOI18N
        bDeleteAnnee.setForeground(new java.awt.Color(153, 153, 153));
        bDeleteAnnee.setText("-");
        bDeleteAnnee.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bDeleteAnnee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMouseExited(evt);
            }
        });
        rightPanel.add(bDeleteAnnee, new org.netbeans.lib.awtextra.AbsoluteConstraints(312, 9, -1, -1));

        getContentPane().add(rightPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 760, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MenuItemHoverExit(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuItemHoverExit
        // TODO add your handling code here:
        JPanel panel = (JPanel) evt.getSource();  
        
        // Check if the components has selected ? if yes no change bg color : if no do nothing
        if( !panel.equals(selectedMenuPanel) ) resetPanelColor((JPanel) evt.getSource());
    }//GEN-LAST:event_MenuItemHoverExit

    private void MenuItemHover(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuItemHover
        // TODO add your handling code here:
        setPanelColor((JPanel) evt.getSource());
    }//GEN-LAST:event_MenuItemHover

    private void bCloseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCloseMousePressed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_bCloseMousePressed

    private void btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMouseEntered
        // TODO add your handling code here:
        ((JLabel) evt.getSource()).setForeground(new Color(255, 120, 172));

    }//GEN-LAST:event_btnMouseEntered

    private void btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMouseExited
        // TODO add your handling code here:
        ((JLabel) evt.getSource()).setForeground(new Color(85, 85, 85));
    }//GEN-LAST:event_btnMouseExited

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FHome().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bAccueil;
    private javax.swing.JLabel bAddAnnee;
    private javax.swing.JLabel bClose;
    private javax.swing.JLabel bDeleteAnnee;
    private javax.swing.JPanel bDep;
    private javax.swing.JPanel bEtu;
    private javax.swing.JPanel bFil;
    private components.LZComboBox comboAnnees;
    private javax.swing.JPanel components;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel dynamicIcon;
    private javax.swing.JPanel dynamicTable;
    private javax.swing.JLabel dynamicTitle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private keeptoo.KButton kButton1;
    private keeptoo.KGradientPanel kGradientPanel1;
    private keeptoo.KGradientPanel kGradientPanel2;
    private keeptoo.KGradientPanel kGradientPanel3;
    private keeptoo.KGradientPanel kGradientPanel4;
    private javax.swing.JLabel lCountDept;
    private javax.swing.JLabel lCountEtud;
    private javax.swing.JLabel lCountFill;
    private components.LZInputLabel lNotification;
    private components.LZButton lZButton10;
    private components.LZButton lZButton11;
    private components.LZButton lZButton12;
    private components.LZButton lZButton5;
    private components.LZButton lZButton6;
    private components.LZButton lZButton7;
    private components.LZButton lZButton8;
    private components.LZButton lZButton9;
    private components.LZInputLabel lZInputLabel1;
    private components.LZInputLabel lZInputLabel2;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel logsView;
    private javax.swing.JPanel notificationPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel tableButtons;
    private javax.swing.JPanel tableView;
    private javax.swing.JPanel viewChangerPanel;
    // End of variables declaration//GEN-END:variables
}
