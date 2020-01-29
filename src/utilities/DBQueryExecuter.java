/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author WILL
 */
public class DBQueryExecuter {
    private static PreparedStatement ps;
    
    /**
     * @param query
     * @return Return ResultSet 
     */
    public static ResultSet excuteQuery(String query)
    {
        try {
            ps = DBManager.connection.prepareStatement(query);  
            return ps.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return null;
        }   
    }
    
    /**
     * @param query
     * @param params 
     * @return Return ResultSet 
     */
    public static ResultSet excuteQueryParams(String query, String[] params)
    {
        try {
            // initialize prepared statement 
            ps = DBManager.connection.prepareStatement(query);
            
            // fetching trough params object string and set params to the query
            for( int i = 0; i < params.length; i++ ){
                ps.setString(i + 1, params[i]);
            }
           
            return ps.executeQuery();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return null;
        }   
    }
    
}
