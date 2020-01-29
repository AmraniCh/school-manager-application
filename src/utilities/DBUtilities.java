/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.sql.ResultSet;

/**
 *
 * @author WILL
 */
public class DBUtilities {
    private static ResultSet rs;
    /**
     * Column names characters format
     */
    public static final int LOWER_CASE = 0;
    public static final int UPPER_CASE = 1;
    public static final int DEFAULT_CASE = -1;
   
    
    /**
     * @param tableName
     * @return Return rows count
     */
    public static int getRowsCount(String tableName){
        
        try {
            rs = DBQueryExecuter.excuteQuery("SELECT COUNT(*) FROM "+tableName);
            while( rs.next() ){
                return Integer.parseInt(rs.getString("COUNT(*)"));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return -1;
    }
    
    /**
     * @param tableName
     * @return Return columns count
     */
    public static int getColumnsCount(String tableName){   
        try {
            rs = DBQueryExecuter.excuteQuery("SELECT COUNT(*) FROM information_schema.columns WHERE table_name = '"+tableName+"' AND table_schema = '"+DBManager.DB_NAME+"'; ");
            while( rs.next() ){
                return Integer.parseInt(rs.getString("COUNT(*)"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        return -1;
    }
    
    /**
     * @param tableName target table
     * @param format The characters format of returned columns names,
     * Is a integer constant for {@code DBUtilities},
     * It can be LOWER_CASE
     * or UPPER_CASE
     * or DEFAULT_CASE for the same format from database.
     * @return Return columns names as string array with specific format
     */
    public static String[] getColumns(String tableName, int format)
    {
        try {            
            String[] cols = new String[DBUtilities.getColumnsCount(tableName)];
                
            rs = DBQueryExecuter.excuteQuery("SELECT * FROM information_schema.columns WHERE table_name = '"+tableName+"' AND table_schema = '"+DBManager.DB_NAME+"'; ");
            
            int i = 0;
            while( rs.next() ){
                
                if( format == DBUtilities.UPPER_CASE )
                    cols[i] = rs.getString("COLUMN_NAME").toUpperCase();
                
                if( format == DBUtilities.LOWER_CASE )
                    cols[i] = rs.getString("COLUMN_NAME").toLowerCase();
                
                if( format == DBUtilities.DEFAULT_CASE )
                    cols[i] = rs.getString("COLUMN_NAME");
                
                i++;
                
            }
            
            return cols;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return null;
        }
    }
    
    /**
     * @param tableName
     * @return Return the name of primary key column 
     */
    public static String getPrimaryColumnName(String tableName){
        try {
            rs = DBQueryExecuter.excuteQueryParams(
                    "SHOW KEYS FROM "+tableName+" WHERE Key_name = ?", 
                    new String[] { "PRIMARY" });
            
            while( rs.next() ){
                return rs.getString("Column_name");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
 
}
