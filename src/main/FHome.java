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
    private static String currentTable;
    private static int currentAnnee;
    /**
     * Timer for notifications
     */
    private Timer timer;
    private int timerSeconds = 2;

    /**
     * Creates new form FHome
     */
    public FHome() {
        initComponents();

        DBManager.setConnection(); // Set database connecion
        
        notificationPanel.setVisible(false); // Hide norification panel for first time
        
        myTable = new LZTable(); // intialize LZTable
        this.dynamicTable.add(new LZScrollPane(myTable)); // Add LZTable to LZScrollPane
        
        fillComboAnnees();
        
        comboAnnees.setSelectedIndex(comboAnnees.getItemCount() - 1); // Set last school year
        if(comboAnnees.getModel().getSize() > 0) 
            currentAnnee = Integer.parseInt(comboAnnees.getSelectedItem().toString()); // Set currentAnnee gloabal variable
        
        updateCounters(); // Update counters
        
        viewChanger(this.logsView);
        
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
                comboAnnees.setSelectedIndex(comboAnnees.getItemCount() - 1);
                
            }   
        });
        
        /**
         * Delete selected school year
         */
        bDeleteAnnee.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.

                DBQueryHelper.delete("annee_scolaire", 
                        new String[][] { 
                            { 
                                "annee", 
                                comboAnnees.getSelectedItem().toString() 
                            } 
                        },
                        "AND");
                fillComboAnnees(); // Fill comboAnnee
                comboAnnees.setSelectedIndex(comboAnnees.getItemCount() - 1); // Set last year as selected year
                showNotification("L'année scolarité supprimée avec success!"); // Show showNotification
                
                // Logs view
                viewChanger(logsView);
                // Change Title & Icon
                setTitleIcon("Dernière activités", "history.png");
            }   
        });
        
        /**
         * Delete table records
         */
        bDelete.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = myTable.getSelectedRow();
                String ID = myTable.getModel().getValueAt(row, 0).toString();
                if(DBQueryHelper.delete(currentTable, new String[][] { { DBUtilities.getPrimaryColumnName(currentTable), ID } }, "AND")){
                    switch(currentTable){
                        case "etudiant":
                            showNotification("L'étudiant supprimé avec succès!");
                            break;
                        case "filiere":
                            showNotification("Filière supprimé avec succès!");
                            break;
                        case "departement":
                            showNotification("Département supprimée avec succès!");
                            break;
                    }
                    fillDataTable();
                    updateCounters();
                }
            }
        });
        
        /**
         * INSERT Views
         */
        bInsertView.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                switch(currentTable){
                    case "etudiant":
                        viewChanger(form.insertEtudiantView);
                        fillComboFiliere();
                        break;
                    case "filiere":
                        viewChanger(form.insertFiliereView);
                        fillComboDept();
                        break; 
                    case "departement":
                        viewChanger(form.insertDepartementView);
                        break;
                }  
                
            }
            
        });
        
        /**
         * UPDATE Views
         */
        bUpdateView.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if( myTable.getSelectedRowCount() > 0 ){
                    switch(currentTable){
                        case "etudiant":
                            viewChanger(form.updateEtudiantView);
                            tID_UPDATE.setText(getTableData()[0]);
                            tNom_UPDATE.setText(getTableData()[1]);
                            tPrenom_UPDATE.setText(getTableData()[2]);
                            tCIN_UPDATE.setText(getTableData()[3]);
                            tAdresse_UPDATE.setText(getTableData()[4]);
                            tEmail_UPDATE.setText(getTableData()[5]);
                            fillComboFiliere();
                            comboFiliere_UPDATE.setSelectedItem(getTableData()[6]);
                            break;
                        case "filiere":
                            viewChanger(form.updateFiliereView);
                            tIDFiliere_UPDATE.setText(getTableData()[0]);
                            tIntituleFiliere_UPDATE.setText(getTableData()[1]);
                            fillComboDept();
                            comboDept_UPDATE.setSelectedItem(getTableData()[2]);
                            break;
                        case "departement":
                            viewChanger(form.updateDepartementView);
                            tIDDept_UPDATE.setText(getTableData()[0]);
                            tIntituleDEPT_UPDATE.setText(getTableData()[1]);
                            tChef_UPDATE.setText(getTableData()[2]);
                            break;    

                    }    
                }  else showNotification("Veillez choissisez un enregistrement d'abord.");
            }
            
        });
        
        /**
         * Search Views
         */
        bSearchView.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                switch(currentTable){
                    case "etudiant":
                        viewChanger(form.searchEtudiantView);
                        fillComboFiliere();
                        break;
                    case "filiere":
                        viewChanger(form.searchFiliereView);
                        fillComboDept();
                        break;
                    case "departement":
                        viewChanger(form.searchDepartementView);
                        break;    
                    
                }  
            }
        });
        
        // Center form to screen
        this.setLocationRelativeTo(null);
        
    }
    
    private void updateCounters(){
        
        lCountEtud.setText(DBQueryHelper.getCount("etudiant", currentAnnee));
        lCountDept.setText(DBQueryHelper.getCount("departement", currentAnnee));
        lCountFill.setText(DBQueryHelper.getCount("filiere", currentAnnee));
  
    }
    
    private void fillComboAnnees(){
        comboAnnees.removeAllItems();
        String[] rows = DBQueryHelper.getDataByColumn("annee_scolaire", "annee");
        for(int i = 0; i < rows.length; i++) {
            comboAnnees.addItem(rows[i]);
        }
    }
    
    private String[] getTableData(){
        
        if( myTable.getRowCount() > 0 ){
            String[] data = new String[myTable.getColumnCount()];

            int row = myTable.getSelectedRow();

            for (int i = 0; i < myTable.getColumnCount(); i++) {

                data[i] = myTable.getModel().getValueAt(row, i).toString();

            }
            return data;
        }
        
        return null;
    }
    
    private void fillDataTable(){ 

        DefaultTableModel tableModel = new DefaultTableModel(
                DBQueryHelper.getRows(currentTable, currentAnnee),
                DBUtilities.getColumns(currentTable, DBUtilities.UPPER_CASE));
        
        myTable.setModel(tableModel);
        
    }
    
    private void showNotification(String message){
        notificationPanel.setVisible(true); // Show notification panel
        lNotification.setText(message);  // Set notification message

        timer = new Timer(this.timerSeconds * 1000, new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {
                
                int t = timerSeconds;
                timerSeconds--;
                if (timerSeconds == 0) {
                    notificationPanel.setVisible(false);
                    timer.stop();
                    timerSeconds = t;
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
    
    private void fillComboFiliere(){
        comboFiliere_INSERT.removeAllItems();
        comboFiliere_UPDATE.removeAllItems();
        comboFiliere_SEARCH.removeAllItems();

        String[] rows = DBQueryHelper.getDataByColumn("filiere", "intitule_fill");
        for(int i = 0; i < rows.length; i++) {
            comboFiliere_INSERT.addItem(rows[i]);
            comboFiliere_UPDATE.addItem(rows[i]);
            comboFiliere_SEARCH.addItem(rows[i]);
        }         
    }
    
    private void fillComboDept(){
        comboDept_INSERT.removeAllItems();
        comboDept_UPDATE.removeAllItems();

        String[] rows = DBQueryHelper.getDataByColumn("departement", "intitule_dept");
        for(int i = 0; i < rows.length; i++) {
            comboDept_INSERT.addItem(rows[i]);
            comboDept_UPDATE.addItem(rows[i]);
        }   
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
        bInsertView = new components.LZButton();
        bUpdateView = new components.LZButton();
        bSearchView = new components.LZButton();
        bDelete = new components.LZButton();
        logsView = new javax.swing.JPanel();
        lZInputLabel1 = new components.LZInputLabel();
        confirmationPanel = new javax.swing.JPanel();
        container = new javax.swing.JPanel();
        lZInputLabel4 = new components.LZInputLabel();
        bDeleteAnnee = new components.LZButton();
        lZButton13 = new components.LZButton();
        lZInputLabel3 = new components.LZInputLabel();
        insertEtudiantView = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        tAdresse_INSERT = new components.LZTextField();
        lZInputLabel10 = new components.LZInputLabel();
        lZInputLabel9 = new components.LZInputLabel();
        tCIN_INSERT = new components.LZTextField();
        tNom_INSERT = new components.LZTextField();
        lZInputLabel8 = new components.LZInputLabel();
        tEmail_INSERT = new components.LZTextField();
        tPrenom_INSERT = new components.LZTextField();
        lZTextField1 = new components.LZTextField();
        lZInputLabel7 = new components.LZInputLabel();
        lZInputLabel6 = new components.LZInputLabel();
        lZInputLabel5 = new components.LZInputLabel();
        lZInputLabel11 = new components.LZInputLabel();
        comboFiliere_INSERT = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        lZButton10 = new components.LZButton();
        lZButton12 = new components.LZButton();
        updateEtudiantView = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        tAdresse_UPDATE = new components.LZTextField();
        lZInputLabel12 = new components.LZInputLabel();
        lZInputLabel13 = new components.LZInputLabel();
        tCIN_UPDATE = new components.LZTextField();
        tNom_UPDATE = new components.LZTextField();
        lZInputLabel14 = new components.LZInputLabel();
        tEmail_UPDATE = new components.LZTextField();
        tPrenom_UPDATE = new components.LZTextField();
        tID_UPDATE = new components.LZTextField();
        lZInputLabel15 = new components.LZInputLabel();
        lZInputLabel16 = new components.LZInputLabel();
        lZInputLabel17 = new components.LZInputLabel();
        lZInputLabel18 = new components.LZInputLabel();
        comboFiliere_UPDATE = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        lZButton14 = new components.LZButton();
        lZButton15 = new components.LZButton();
        searchEtudiantView = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        tAdresse_SEARCH = new components.LZTextField();
        lZInputLabel19 = new components.LZInputLabel();
        lZInputLabel20 = new components.LZInputLabel();
        tCIN_SEARCH = new components.LZTextField();
        tNom_SEARCH = new components.LZTextField();
        lZInputLabel21 = new components.LZInputLabel();
        tEmail_SEARCH = new components.LZTextField();
        tPrenom_SEARCH = new components.LZTextField();
        tIDEtu_SEARCH = new components.LZTextField();
        lZInputLabel22 = new components.LZInputLabel();
        lZInputLabel23 = new components.LZInputLabel();
        lZInputLabel24 = new components.LZInputLabel();
        lZInputLabel25 = new components.LZInputLabel();
        comboFiliere_SEARCH = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        lZButton16 = new components.LZButton();
        lZButton17 = new components.LZButton();
        insertFiliereView = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        tIntituleFiliere_INSERT = new components.LZTextField();
        lZInputLabel28 = new components.LZInputLabel();
        lZTextField2 = new components.LZTextField();
        lZInputLabel29 = new components.LZInputLabel();
        lZInputLabel30 = new components.LZInputLabel();
        comboDept_INSERT = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        lZButton18 = new components.LZButton();
        lZButton19 = new components.LZButton();
        updateFiliereView = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        tIntituleFiliere_UPDATE = new components.LZTextField();
        lZInputLabel31 = new components.LZInputLabel();
        tIDFiliere_UPDATE = new components.LZTextField();
        lZInputLabel32 = new components.LZInputLabel();
        lZInputLabel33 = new components.LZInputLabel();
        comboDept_UPDATE = new javax.swing.JComboBox<>();
        jPanel10 = new javax.swing.JPanel();
        lZButton20 = new components.LZButton();
        lZButton21 = new components.LZButton();
        searchFiliereView = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel11 = new javax.swing.JPanel();
        tIntituleFiliere_SEARCH = new components.LZTextField();
        lZInputLabel34 = new components.LZInputLabel();
        tIDFill_SEARCH = new components.LZTextField();
        lZInputLabel35 = new components.LZInputLabel();
        lZInputLabel36 = new components.LZInputLabel();
        comboDept_SEARCH = new javax.swing.JComboBox<>();
        jPanel12 = new javax.swing.JPanel();
        lZButton22 = new components.LZButton();
        lZButton23 = new components.LZButton();
        insertDepartementView = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jPanel13 = new javax.swing.JPanel();
        tChef_INSERT = new components.LZTextField();
        lZInputLabel37 = new components.LZInputLabel();
        lZTextField4 = new components.LZTextField();
        lZInputLabel38 = new components.LZInputLabel();
        lZInputLabel39 = new components.LZInputLabel();
        tIntituleDEPT_INSERT = new components.LZTextField();
        jPanel14 = new javax.swing.JPanel();
        lZButton24 = new components.LZButton();
        lZButton25 = new components.LZButton();
        updateDepartementView = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jPanel15 = new javax.swing.JPanel();
        tChef_UPDATE = new components.LZTextField();
        lZInputLabel40 = new components.LZInputLabel();
        tIDDept_UPDATE = new components.LZTextField();
        lZInputLabel41 = new components.LZInputLabel();
        lZInputLabel42 = new components.LZInputLabel();
        tIntituleDEPT_UPDATE = new components.LZTextField();
        jPanel16 = new javax.swing.JPanel();
        lZButton26 = new components.LZButton();
        lZButton27 = new components.LZButton();
        searchDepartementView = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel17 = new javax.swing.JPanel();
        tChef_SEARCH = new components.LZTextField();
        lZInputLabel43 = new components.LZInputLabel();
        tIDDept_SEARCH = new components.LZTextField();
        lZInputLabel44 = new components.LZInputLabel();
        lZInputLabel45 = new components.LZInputLabel();
        tIntituleDept_SEARCH = new components.LZTextField();
        jPanel18 = new javax.swing.JPanel();
        lZButton28 = new components.LZButton();
        lZButton29 = new components.LZButton();
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
        bShowConfirmation = new javax.swing.JLabel();

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
        tableView.add(dynamicTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 220));

        tableButtons.setBackground(new java.awt.Color(255, 255, 255));

        bInsertView.setBackground(new java.awt.Color(38, 46, 60));

        bUpdateView.setBackground(new java.awt.Color(39, 187, 216));
        bUpdateView.setText("Modifier");

        bSearchView.setBackground(new java.awt.Color(28, 104, 150));
        bSearchView.setText("Rechercher");

        bDelete.setText("Supprimer");

        javax.swing.GroupLayout tableButtonsLayout = new javax.swing.GroupLayout(tableButtons);
        tableButtons.setLayout(tableButtonsLayout);
        tableButtonsLayout.setHorizontalGroup(
            tableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableButtonsLayout.createSequentialGroup()
                .addComponent(bInsertView, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(bUpdateView, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(bDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71)
                .addComponent(bSearchView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        tableButtonsLayout.setVerticalGroup(
            tableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bInsertView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bUpdateView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bSearchView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        tableView.add(tableButtons, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 710, 60));

        logsView.setBackground(new java.awt.Color(255, 255, 255));
        logsView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lZInputLabel1.setText("Dernière activités");
        logsView.add(lZInputLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 110, -1, -1));

        confirmationPanel.setBackground(new java.awt.Color(255, 255, 255));
        confirmationPanel.setLayout(new javax.swing.BoxLayout(confirmationPanel, javax.swing.BoxLayout.LINE_AXIS));

        container.setBackground(new java.awt.Color(255, 255, 255));

        lZInputLabel4.setText("Voulez vous vraiment supprimé cette anné scolaire ?");
        lZInputLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        bDeleteAnnee.setText("Supprimer");

        lZButton13.setBackground(new java.awt.Color(38, 46, 60));
        lZButton13.setText("Annuler");
        lZButton13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZInputLabel3.setText("Toutes les donnés concernant cette année scolaire sera supprimé.");
        lZInputLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        javax.swing.GroupLayout containerLayout = new javax.swing.GroupLayout(container);
        container.setLayout(containerLayout);
        containerLayout.setHorizontalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 723, Short.MAX_VALUE)
            .addGroup(containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(containerLayout.createSequentialGroup()
                    .addGap(0, 104, Short.MAX_VALUE)
                    .addGroup(containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lZInputLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(containerLayout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addComponent(lZInputLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(containerLayout.createSequentialGroup()
                            .addGap(110, 110, 110)
                            .addComponent(bDeleteAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(30, 30, 30)
                            .addComponent(lZButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 104, Short.MAX_VALUE)))
        );
        containerLayout.setVerticalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 314, Short.MAX_VALUE)
            .addGroup(containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(containerLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lZInputLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(17, 17, 17)
                    .addComponent(lZInputLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(27, 27, 27)
                    .addGroup(containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(bDeleteAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lZButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        confirmationPanel.add(container);

        insertEtudiantView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tAdresse_INSERT.setText("Adresse");

        lZInputLabel10.setText("Adresse :");

        lZInputLabel9.setText("CIN :");

        tCIN_INSERT.setText("CIN");

        tNom_INSERT.setText("Nom");

        lZInputLabel8.setText("Filière :");

        tEmail_INSERT.setText("Email");

        tPrenom_INSERT.setText("Prénom");

        lZTextField1.setText("L'identifiant");
        lZTextField1.setEenabled(false);

        lZInputLabel7.setText("Nom :");

        lZInputLabel6.setText("ID :");

        lZInputLabel5.setText("Email : ");

        lZInputLabel11.setText("Prénom :");

        comboFiliere_INSERT.setEditable(true);
        comboFiliere_INSERT.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        comboFiliere_INSERT.setForeground(new java.awt.Color(85, 85, 85));
        comboFiliere_INSERT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(comboFiliere_INSERT, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tNom_INSERT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tPrenom_INSERT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tCIN_INSERT, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tAdresse_INSERT, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tEmail_INSERT, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZInputLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lZTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tCIN_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZInputLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tNom_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tAdresse_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lZInputLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lZInputLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tPrenom_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tEmail_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZInputLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboFiliere_INSERT)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel1);

        insertEtudiantView.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        lZButton10.setBackground(new java.awt.Color(38, 46, 60));
        lZButton10.setText("Annuler");
        lZButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton12.setBackground(new java.awt.Color(28, 104, 150));
        lZButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bInsertMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lZButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 461, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        insertEtudiantView.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        updateEtudiantView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(null);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tAdresse_UPDATE.setText("Adresse");

        lZInputLabel12.setText("Adresse :");

        lZInputLabel13.setText("CIN :");

        tCIN_UPDATE.setText("CIN");

        tNom_UPDATE.setText("Nom");

        lZInputLabel14.setText("Filière :");

        tEmail_UPDATE.setText("Email");

        tPrenom_UPDATE.setText("Prénom");

        tID_UPDATE.setText("L'identifiant");
        tID_UPDATE.setEenabled(false);

        lZInputLabel15.setText("Nom :");

        lZInputLabel16.setText("ID :");

        lZInputLabel17.setText("Email : ");

        lZInputLabel18.setText("Prénom :");

        comboFiliere_UPDATE.setEditable(true);
        comboFiliere_UPDATE.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        comboFiliere_UPDATE.setForeground(new java.awt.Color(85, 85, 85));
        comboFiliere_UPDATE.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboFiliere_UPDATE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFiliere_UPDATEActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(comboFiliere_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tID_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tNom_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tPrenom_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tCIN_UPDATE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tAdresse_UPDATE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tEmail_UPDATE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZInputLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tID_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tCIN_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZInputLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tNom_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tAdresse_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lZInputLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lZInputLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tPrenom_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tEmail_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZInputLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboFiliere_UPDATE)
                .addContainerGap())
        );

        jScrollPane2.setViewportView(jPanel3);

        updateEtudiantView.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        lZButton14.setBackground(new java.awt.Color(38, 46, 60));
        lZButton14.setText("Annuler");
        lZButton14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton15.setBackground(new java.awt.Color(39, 187, 216));
        lZButton15.setText("Modifier");
        lZButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bUpdateMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(lZButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 461, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        updateEtudiantView.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        searchEtudiantView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane3.setBorder(null);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        tAdresse_SEARCH.setText("Adresse");

        lZInputLabel19.setText("Adresse :");

        lZInputLabel20.setText("CIN :");

        tCIN_SEARCH.setText("CIN");

        tNom_SEARCH.setText("Nom");

        lZInputLabel21.setText("Filière :");

        tEmail_SEARCH.setText("Email");

        tPrenom_SEARCH.setText("Prénom");

        tIDEtu_SEARCH.setText("L'identifiant");

        lZInputLabel22.setText("Nom :");

        lZInputLabel23.setText("ID :");

        lZInputLabel24.setText("Email : ");

        lZInputLabel25.setText("Prénom :");

        comboFiliere_SEARCH.setEditable(true);
        comboFiliere_SEARCH.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        comboFiliere_SEARCH.setForeground(new java.awt.Color(85, 85, 85));
        comboFiliere_SEARCH.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboFiliere_SEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFiliere_SEARCHActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(comboFiliere_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIDEtu_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tNom_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tPrenom_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tCIN_SEARCH, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tAdresse_SEARCH, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tEmail_SEARCH, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZInputLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tIDEtu_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tCIN_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZInputLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZInputLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tNom_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tAdresse_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lZInputLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lZInputLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tPrenom_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tEmail_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZInputLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboFiliere_SEARCH)
                .addContainerGap())
        );

        jScrollPane3.setViewportView(jPanel5);

        searchEtudiantView.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        lZButton16.setBackground(new java.awt.Color(38, 46, 60));
        lZButton16.setText("Annuler");
        lZButton16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton17.setBackground(new java.awt.Color(39, 187, 216));
        lZButton17.setText("Rechercher");
        lZButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bSearchMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(lZButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 451, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        searchEtudiantView.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        insertFiliereView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane4.setBorder(null);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        tIntituleFiliere_INSERT.setText("Intitulé");

        lZInputLabel28.setText("Filière :");

        lZTextField2.setText("L'identifiant");
        lZTextField2.setEenabled(false);

        lZInputLabel29.setText("Intitulé :");

        lZInputLabel30.setText("ID :");

        comboDept_INSERT.setEditable(true);
        comboDept_INSERT.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        comboDept_INSERT.setForeground(new java.awt.Color(85, 85, 85));
        comboDept_INSERT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(comboDept_INSERT, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIntituleFiliere_INSERT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(443, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(lZInputLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZInputLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(tIntituleFiliere_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZInputLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboDept_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jScrollPane4.setViewportView(jPanel7);

        insertFiliereView.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        lZButton18.setBackground(new java.awt.Color(38, 46, 60));
        lZButton18.setText("Annuler");
        lZButton18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton19.setBackground(new java.awt.Color(28, 104, 150));
        lZButton19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bInsertMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(lZButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 461, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        insertFiliereView.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        updateFiliereView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane5.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane5.setBorder(null);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        tIntituleFiliere_UPDATE.setText("Intitulé");

        lZInputLabel31.setText("Filière :");

        tIDFiliere_UPDATE.setText("L'identifiant");
        tIDFiliere_UPDATE.setEenabled(false);

        lZInputLabel32.setText("Intitulé :");

        lZInputLabel33.setText("ID :");

        comboDept_UPDATE.setEditable(true);
        comboDept_UPDATE.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        comboDept_UPDATE.setForeground(new java.awt.Color(85, 85, 85));
        comboDept_UPDATE.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(comboDept_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel33, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIDFiliere_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel32, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIntituleFiliere_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(443, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(lZInputLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(tIDFiliere_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZInputLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(tIntituleFiliere_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZInputLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboDept_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jScrollPane5.setViewportView(jPanel9);

        updateFiliereView.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        lZButton20.setBackground(new java.awt.Color(38, 46, 60));
        lZButton20.setText("Annuler");
        lZButton20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton21.setBackground(new java.awt.Color(39, 187, 216));
        lZButton21.setText("Modifier");
        lZButton21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bUpdateMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(lZButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 461, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        updateFiliereView.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        searchFiliereView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane6.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane6.setBorder(null);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        tIntituleFiliere_SEARCH.setText("Intitulé");

        lZInputLabel34.setText("Filière :");

        tIDFill_SEARCH.setText("L'identifiant");

        lZInputLabel35.setText("Intitulé :");

        lZInputLabel36.setText("ID :");

        comboDept_SEARCH.setEditable(true);
        comboDept_SEARCH.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        comboDept_SEARCH.setForeground(new java.awt.Color(85, 85, 85));
        comboDept_SEARCH.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(comboDept_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel36, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIDFill_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIntituleFiliere_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(443, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(lZInputLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(tIDFill_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZInputLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(tIntituleFiliere_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZInputLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboDept_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jScrollPane6.setViewportView(jPanel11);

        searchFiliereView.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        lZButton22.setBackground(new java.awt.Color(38, 46, 60));
        lZButton22.setText("Annuler");
        lZButton22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton23.setBackground(new java.awt.Color(39, 187, 216));
        lZButton23.setText("Rechercher");
        lZButton23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bSearchMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(lZButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 448, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        searchFiliereView.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        insertDepartementView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });
        insertDepartementView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane7.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane7.setBorder(null);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        tChef_INSERT.setText("Chef de département");

        lZInputLabel37.setText("Chef de département : ");

        lZTextField4.setText("L'identifiant");
        lZTextField4.setEenabled(false);

        lZInputLabel38.setText("Intitulé :");

        lZInputLabel39.setText("ID :");

        tIntituleDEPT_INSERT.setText("Intitulé");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lZInputLabel39, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZTextField4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tChef_INSERT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel37, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIntituleDEPT_INSERT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(443, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(lZInputLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZInputLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(tIntituleDEPT_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZInputLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tChef_INSERT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jScrollPane7.setViewportView(jPanel13);

        insertDepartementView.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        lZButton24.setBackground(new java.awt.Color(38, 46, 60));
        lZButton24.setText("Annuler");
        lZButton24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton25.setBackground(new java.awt.Color(28, 104, 150));
        lZButton25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bInsertMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(lZButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 461, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        insertDepartementView.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        updateDepartementView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane8.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane8.setBorder(null);

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        tChef_UPDATE.setText("Chef de département");

        lZInputLabel40.setText("Chef de département : ");

        tIDDept_UPDATE.setText("L'identifiant");

        lZInputLabel41.setText("Intitulé :");

        lZInputLabel42.setText("ID :");

        tIntituleDEPT_UPDATE.setText("Intitulé");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lZInputLabel42, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIDDept_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel41, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tChef_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIntituleDEPT_UPDATE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(443, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(lZInputLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(tIDDept_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZInputLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(tIntituleDEPT_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZInputLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tChef_UPDATE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jScrollPane8.setViewportView(jPanel15);

        updateDepartementView.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        lZButton26.setBackground(new java.awt.Color(38, 46, 60));
        lZButton26.setText("Annuler");
        lZButton26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton27.setBackground(new java.awt.Color(39, 187, 216));
        lZButton27.setText("Modifier");
        lZButton27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bUpdateMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(lZButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 437, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        updateDepartementView.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

        searchDepartementView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane9.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane9.setBorder(null);

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));

        tChef_SEARCH.setText("Chef de département");

        lZInputLabel43.setText("Chef de département : ");

        tIDDept_SEARCH.setText("L'identifiant");

        lZInputLabel44.setText("Intitulé :");

        lZInputLabel45.setText("ID :");

        tIntituleDept_SEARCH.setText("Intitulé");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lZInputLabel45, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIDDept_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel44, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tChef_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lZInputLabel43, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tIntituleDept_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(443, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(lZInputLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(tIDDept_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lZInputLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(tIntituleDept_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lZInputLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tChef_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jScrollPane9.setViewportView(jPanel17);

        searchDepartementView.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 230));

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        lZButton28.setBackground(new java.awt.Color(38, 46, 60));
        lZButton28.setText("Annuler");
        lZButton28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CancelClickEvent(evt);
            }
        });

        lZButton29.setBackground(new java.awt.Color(39, 187, 216));
        lZButton29.setText("Rechercher");
        lZButton29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bSearchMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(lZButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lZButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 444, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lZButton28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lZButton29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        searchDepartementView.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 710, 50));

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
        contentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dynamicTitle.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        dynamicTitle.setForeground(new java.awt.Color(85, 85, 85));
        dynamicTitle.setText("Dernière activités");
        contentPanel.add(dynamicTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 0, 678, 40));

        dynamicIcon.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        dynamicIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dynamicIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ressources/dynamicIcons/history.png"))); // NOI18N
        contentPanel.add(dynamicIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 0, 40, 40));

        viewChangerPanel.setLayout(new java.awt.BorderLayout());
        contentPanel.add(viewChangerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 51, 710, 280));

        notificationPanel.setBackground(new java.awt.Color(255, 120, 173));

        lNotification.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout notificationPanelLayout = new javax.swing.GroupLayout(notificationPanel);
        notificationPanel.setLayout(notificationPanelLayout);
        notificationPanelLayout.setHorizontalGroup(
            notificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notificationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lNotification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(610, Short.MAX_VALUE))
        );
        notificationPanelLayout.setVerticalGroup(
            notificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notificationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lNotification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        contentPanel.add(notificationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 710, 40));

        rightPanel.add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 214, 760, 380));

        lZInputLabel2.setText("Année scolaire");
        lZInputLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rightPanel.add(lZInputLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 11, 123, 34));

        comboAnnees.setEditable(true);
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

        bShowConfirmation.setFont(new java.awt.Font("Segoe UI Black", 1, 28)); // NOI18N
        bShowConfirmation.setForeground(new java.awt.Color(153, 153, 153));
        bShowConfirmation.setText("-");
        bShowConfirmation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bShowConfirmation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bShowConfirmationMousePressed(evt);
            }
        });
        rightPanel.add(bShowConfirmation, new org.netbeans.lib.awtextra.AbsoluteConstraints(312, 9, -1, -1));

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

    private void CancelClickEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CancelClickEvent
        // TODO add your handling code here:
        viewChanger(this.tableView);
    }//GEN-LAST:event_CancelClickEvent

    private void bShowConfirmationMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bShowConfirmationMousePressed
        // TODO add your handling code here:
        viewChanger(confirmationPanel);
        setTitleIcon("Suppression d'une année scolaire.", "warning.png");
    }//GEN-LAST:event_bShowConfirmationMousePressed

    private void comboFiliere_UPDATEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFiliere_UPDATEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboFiliere_UPDATEActionPerformed

    private void comboFiliere_SEARCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFiliere_SEARCHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboFiliere_SEARCHActionPerformed

    private void bInsertMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bInsertMousePressed
        // TODO add your handling code here:
        switch(currentTable){
            case "etudiant":
                if(DBQueryHelper.insert(currentTable, new String[][]{
                    {
                        null,
                        tNom_INSERT.getText(),
                        tPrenom_INSERT.getText(),
                        tCIN_INSERT.getText(),
                        tAdresse_INSERT.getText(),
                        tEmail_INSERT.getText(),
                        comboFiliere_INSERT.getSelectedItem().toString()
                    }
                })){
                    showNotification("L'étudiant ajouté avec succés!");
                    fillDataTable();
                }
                break;
            case "filiere":
                if(DBQueryHelper.insert(currentTable, new String[][]{
                    {
                        null,
                        tIntituleFiliere_INSERT.getText(),
                        comboDept_INSERT.getSelectedItem().toString()
                    }
                })){
                    showNotification("Filière ajouté avec succés!");
                    fillDataTable();
                }
                break;
            case "departement":
                boolean insert_dept = DBQueryHelper.insert(currentTable, new String[][]{
                    {
                        null,
                        tIntituleDEPT_INSERT.getText(),
                        tChef_INSERT.getText()
                    }
                });
                boolean insert_annee_dept = DBQueryHelper.insert("annee_departement", new String[][]{
                    {
                        tIntituleDEPT_INSERT.getText(),
                        String.valueOf(currentAnnee)
                    }
                });
                
                if( insert_dept && insert_annee_dept ){
                    showNotification("Département ajouté avec succés!");
                    fillDataTable();
                }
                
                break;
        }
        
        updateCounters();
    }//GEN-LAST:event_bInsertMousePressed

    private void bUpdateMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bUpdateMousePressed
        // TODO add your handling code here:
        switch(currentTable){
            case "etudiant":
                if(DBQueryHelper.updateRow(currentTable, new String[]
                    {
                        tID_UPDATE.getText(),
                        tNom_UPDATE.getText(),
                        tPrenom_UPDATE.getText(),
                        tCIN_UPDATE.getText(),
                        tAdresse_UPDATE.getText(),
                        tEmail_UPDATE.getText(),
                        comboFiliere_UPDATE.getSelectedItem().toString()
                    })){   
                        showNotification("L'étudiant à été modifié avec succès!");
                    }
                break;
            case "filiere":
                if(DBQueryHelper.updateRow(currentTable, new String[]
                    {
                        tIDFiliere_UPDATE.getText(),
                        tIntituleFiliere_UPDATE.getText(),
                        comboDept_UPDATE.getSelectedItem().toString()
                    }
                    )){
                        showNotification("Filière à été modifié avec succès!");
                    }
                break;
            case "departement":
                if(DBQueryHelper.updateRow(currentTable, new String[]
                    {
                        tIDDept_UPDATE.getText(),
                        tIntituleDEPT_UPDATE.getText(),
                        tChef_UPDATE.getText()
                    }
                    )){
                        showNotification("L'étudiant à été modifié avec succès!");
                    }
                break;
        }  
        
        fillDataTable();
    }//GEN-LAST:event_bUpdateMousePressed

    private void bSearchMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bSearchMousePressed
        // TODO add your handling code here:
        DefaultTableModel tableModel = null;
        
        switch(currentTable) {
            case "etudiant":
                tableModel = new DefaultTableModel(DBQueryHelper.getRows(
                    currentTable, 
                    new String[]{ 
                        tIDEtu_SEARCH.getText(), 
                        tNom_SEARCH.getText(), 
                        tPrenom_SEARCH.getText(), 
                        tCIN_SEARCH.getText(),
                        tAdresse_SEARCH.getText(),
                        tEmail_SEARCH.getText(),
                        comboFiliere_SEARCH.getSelectedItem().toString()
                    }, 
                    currentAnnee ), 
                    DBUtilities.getColumns(currentTable, DBUtilities.UPPER_CASE)
                );
                break;
            case "filiere":
                tableModel = new DefaultTableModel(DBQueryHelper.getRows(
                    currentTable, 
                    new String[]{ 
                        tIDFill_SEARCH.getText(), 
                        tIntituleFiliere_SEARCH.getText(), 
                        comboDept_SEARCH.getSelectedItem().toString()
                    }, 
                    currentAnnee ), 
                    DBUtilities.getColumns(currentTable, DBUtilities.UPPER_CASE)
                );
                break;
            case "departement":
                tableModel = new DefaultTableModel(DBQueryHelper.getRows(
                    currentTable, 
                    new String[]{ 
                        tIDDept_SEARCH.getText(),
                        tIntituleDept_SEARCH.getText(),
                        tChef_SEARCH.getText()
                    }, 
                    currentAnnee ), 
                    DBUtilities.getColumns(currentTable, DBUtilities.UPPER_CASE)
                );
                break;
        }
        
        myTable.setModel(tableModel);
        viewChanger(tableView);
    }//GEN-LAST:event_bSearchMousePressed

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
    private components.LZButton bDelete;
    private components.LZButton bDeleteAnnee;
    private javax.swing.JPanel bDep;
    private javax.swing.JPanel bEtu;
    private javax.swing.JPanel bFil;
    private components.LZButton bInsertView;
    private components.LZButton bSearchView;
    private javax.swing.JLabel bShowConfirmation;
    private components.LZButton bUpdateView;
    private components.LZComboBox comboAnnees;
    private javax.swing.JComboBox<String> comboDept_INSERT;
    private javax.swing.JComboBox<String> comboDept_SEARCH;
    private javax.swing.JComboBox<String> comboDept_UPDATE;
    private javax.swing.JComboBox<String> comboFiliere_INSERT;
    private javax.swing.JComboBox<String> comboFiliere_SEARCH;
    private javax.swing.JComboBox<String> comboFiliere_UPDATE;
    private javax.swing.JPanel components;
    private javax.swing.JPanel confirmationPanel;
    private javax.swing.JPanel container;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel dynamicIcon;
    private javax.swing.JPanel dynamicTable;
    private javax.swing.JLabel dynamicTitle;
    private javax.swing.JPanel insertDepartementView;
    private javax.swing.JPanel insertEtudiantView;
    private javax.swing.JPanel insertFiliereView;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
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
    private components.LZButton lZButton12;
    private components.LZButton lZButton13;
    private components.LZButton lZButton14;
    private components.LZButton lZButton15;
    private components.LZButton lZButton16;
    private components.LZButton lZButton17;
    private components.LZButton lZButton18;
    private components.LZButton lZButton19;
    private components.LZButton lZButton20;
    private components.LZButton lZButton21;
    private components.LZButton lZButton22;
    private components.LZButton lZButton23;
    private components.LZButton lZButton24;
    private components.LZButton lZButton25;
    private components.LZButton lZButton26;
    private components.LZButton lZButton27;
    private components.LZButton lZButton28;
    private components.LZButton lZButton29;
    private components.LZButton lZButton5;
    private components.LZButton lZButton6;
    private components.LZButton lZButton7;
    private components.LZButton lZButton8;
    private components.LZInputLabel lZInputLabel1;
    private components.LZInputLabel lZInputLabel10;
    private components.LZInputLabel lZInputLabel11;
    private components.LZInputLabel lZInputLabel12;
    private components.LZInputLabel lZInputLabel13;
    private components.LZInputLabel lZInputLabel14;
    private components.LZInputLabel lZInputLabel15;
    private components.LZInputLabel lZInputLabel16;
    private components.LZInputLabel lZInputLabel17;
    private components.LZInputLabel lZInputLabel18;
    private components.LZInputLabel lZInputLabel19;
    private components.LZInputLabel lZInputLabel2;
    private components.LZInputLabel lZInputLabel20;
    private components.LZInputLabel lZInputLabel21;
    private components.LZInputLabel lZInputLabel22;
    private components.LZInputLabel lZInputLabel23;
    private components.LZInputLabel lZInputLabel24;
    private components.LZInputLabel lZInputLabel25;
    private components.LZInputLabel lZInputLabel28;
    private components.LZInputLabel lZInputLabel29;
    private components.LZInputLabel lZInputLabel3;
    private components.LZInputLabel lZInputLabel30;
    private components.LZInputLabel lZInputLabel31;
    private components.LZInputLabel lZInputLabel32;
    private components.LZInputLabel lZInputLabel33;
    private components.LZInputLabel lZInputLabel34;
    private components.LZInputLabel lZInputLabel35;
    private components.LZInputLabel lZInputLabel36;
    private components.LZInputLabel lZInputLabel37;
    private components.LZInputLabel lZInputLabel38;
    private components.LZInputLabel lZInputLabel39;
    private components.LZInputLabel lZInputLabel4;
    private components.LZInputLabel lZInputLabel40;
    private components.LZInputLabel lZInputLabel41;
    private components.LZInputLabel lZInputLabel42;
    private components.LZInputLabel lZInputLabel43;
    private components.LZInputLabel lZInputLabel44;
    private components.LZInputLabel lZInputLabel45;
    private components.LZInputLabel lZInputLabel5;
    private components.LZInputLabel lZInputLabel6;
    private components.LZInputLabel lZInputLabel7;
    private components.LZInputLabel lZInputLabel8;
    private components.LZInputLabel lZInputLabel9;
    private components.LZTextField lZTextField1;
    private components.LZTextField lZTextField2;
    private components.LZTextField lZTextField4;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel logsView;
    private javax.swing.JPanel notificationPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel searchDepartementView;
    private javax.swing.JPanel searchEtudiantView;
    private javax.swing.JPanel searchFiliereView;
    private components.LZTextField tAdresse_INSERT;
    private components.LZTextField tAdresse_SEARCH;
    private components.LZTextField tAdresse_UPDATE;
    private components.LZTextField tCIN_INSERT;
    private components.LZTextField tCIN_SEARCH;
    private components.LZTextField tCIN_UPDATE;
    private components.LZTextField tChef_INSERT;
    private components.LZTextField tChef_SEARCH;
    private components.LZTextField tChef_UPDATE;
    private components.LZTextField tEmail_INSERT;
    private components.LZTextField tEmail_SEARCH;
    private components.LZTextField tEmail_UPDATE;
    private components.LZTextField tIDDept_SEARCH;
    private components.LZTextField tIDDept_UPDATE;
    private components.LZTextField tIDEtu_SEARCH;
    private components.LZTextField tIDFiliere_UPDATE;
    private components.LZTextField tIDFill_SEARCH;
    private components.LZTextField tID_UPDATE;
    private components.LZTextField tIntituleDEPT_INSERT;
    private components.LZTextField tIntituleDEPT_UPDATE;
    private components.LZTextField tIntituleDept_SEARCH;
    private components.LZTextField tIntituleFiliere_INSERT;
    private components.LZTextField tIntituleFiliere_SEARCH;
    private components.LZTextField tIntituleFiliere_UPDATE;
    private components.LZTextField tNom_INSERT;
    private components.LZTextField tNom_SEARCH;
    private components.LZTextField tNom_UPDATE;
    private components.LZTextField tPrenom_INSERT;
    private components.LZTextField tPrenom_SEARCH;
    private components.LZTextField tPrenom_UPDATE;
    private javax.swing.JPanel tableButtons;
    private javax.swing.JPanel tableView;
    private javax.swing.JPanel updateDepartementView;
    private javax.swing.JPanel updateEtudiantView;
    private javax.swing.JPanel updateFiliereView;
    private javax.swing.JPanel viewChangerPanel;
    // End of variables declaration//GEN-END:variables
}
