/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author WILL
 */
public class DBManager {
    
    public static final String DB_NAME = "annuaire_ens";
    /**
     * [connection] object initialize with setConnection() function
     */
    public static Connection connection;
   
    /**
     * Setting connection to database
     */
    public static void setConnection()
    {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/"+DB_NAME,
                    "root",
                    ""
            );
            
            System.out.print("Database is connected!");
            
        }catch(ClassNotFoundException | SQLException e){ 
            System.out.println("Failed connect to database!" + e.getMessage()); 
        }        
    }
    
    
}
