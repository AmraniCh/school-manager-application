/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author WILL
 */
public class DBQueryHelper {
    private static PreparedStatement ps;
    private static ResultSet rs;
    
    /**
     * @param tableName
     * @param where
     * @param operator Optional "AND" or "OR"
     * @return 
     */
    public static boolean delete(String tableName, String[][] where, String operator)
    {
        try{
            String query = "DELETE FROM " + tableName;

            if( where != null && operator != null ){
                query += " WHERE ";

                String[] values = new String[where.length]; // Store key values
                
                for(int i = 0; i < where.length; i++)
                {

                    for( int j = 0; j < where[i].length; j++ )
                    {
                        query += where[i][j] + " = ?";
                        values[i] = String.valueOf(where[i][j + 1]);
                        break;
                    }
                    if( where.length == i + 1 ) // Check if last item
                        break;

                    query += " AND "; 
                }                
          
                ps = DBManager.connection.prepareStatement(query);
                // Setting stored values to prepared statement query
                for (int i = 0; i < values.length; i++) {
                    ps.setString(i + 1, values[i]);
                }  
                
            }
            else{
                ps = DBManager.connection.prepareStatement(query);
            }

            if( ps.executeUpdate() > 0)
                return true;
            
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * @param tableName
     * @param rows Inserted rows
     * @return Return true if affected rows > 0, false if affected rows equal to 0
     */
    public static boolean insert(String tableName, String[][] rows)
    {
        
        try{
            
            String query = "INSERT INTO " + tableName + " VALUES(";
            
            String[][] params = new String[rows.length][DBUtilities.getColumnsCount(tableName)];
            
            for (int i = 0; i < rows.length; i++) 
            {
                for (int j = 0; j < rows[i].length; j++) 
                {   
                    query += "?";
                    
                    params[i][j] = rows[i][j];

                    if( rows[i].length != j + 1 )
                        query += ", ";
                    else { query += ")"; break; }
                } 
                if( i + 1 != rows.length ){
                    query += ",("; 
                }
            }
            
            
            ps = DBManager.connection.prepareStatement(query);
            
            for (int i = 0; i < params.length; i++) {
                for (int j = 0; j < rows[i].length; j++) {
                    ps.setString(j + 1, params[i][j]);
                }
//                if( i + 1 != params.length ){
//                    ps.addBatch();
//                }
            }
           
            if( ps.executeUpdate() > 0 )
                return true;
            
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        return false;
    }
    
    /**
     * @param tableName
     * @return Return columns names as object
     */
    public static String[][] getRows(String tableName, Integer anneScolaire)
    {
        try {            
            int colsCount = DBUtilities.getColumnsCount(tableName);
            
            switch(tableName){
                case "etudiant":
                    rs = DBQueryExecuter.excuteQuery("select e.* " +
                        "from etudiant e inner join filiere f " +
                        "on e.filiere = f.intitule_fill " +
                        "inner join departement d " +
                        "on f.departement_intitule = d.intitule_dept " +
                        "inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'");
                    break;
                case "filiere":
                    rs = DBQueryExecuter.excuteQuery("select f.* " +
                        "from filiere f inner join departement d " +
                        "on f.departement_intitule = d.intitule_dept " +
                        "inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'");
                    break;
                case "departement":
                    rs = DBQueryExecuter.excuteQuery("select d.* " +
                        "from departement d inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'");
                    break;
            }
            
            rs.last();
            
            String[][] rows = new String[rs.getRow()][colsCount];
            
            int i = 0;
            rs.beforeFirst();
            while( rs.next() ){
   
                for (int j = 0; j < colsCount; j++) {
                    rows[i][j] = rs.getString(j + 1);
                }
                
                i++;  
            }
         
            return rows;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return null;
        }
    }
    
    /**
     * @param tableName
     * @param where String object present rows data,
     * It should be arranged
     * @param anneScolaire
     * @return Return rows data as array of strings 
     */
    public static String[][] getRows(String tableName, String[] where, Integer anneScolaire)
    {
        try {            
            int colsCount = DBUtilities.getColumnsCount(tableName);
            
            String query = null;
            switch(tableName){
                case "etudiant":
                    query = "select e.* " +
                        "from etudiant e inner join filiere f " +
                        "on e.filiere = f.intitule_fill " +
                        "inner join departement d " +
                        "on f.departement_intitule = d.intitule_dept " +
                        "inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'";
                    break;
                case "filiere":
                    query = "select f.* " +
                        "from filiere f inner join departement d " +
                        "on f.departement_intitule = d.intitule_dept " +
                        "inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'";
                    break;
                case "departement":
                    query = "select d.* " +
                        "from departement d inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'";
                    break;
            }
            
            query += " AND ";
            
            String[] params = new String[colsCount];
            
            for(int i = 0; i < colsCount; i++)
            {
                params[i] = where[i];
                query+= DBUtilities.getColumns(tableName, DBUtilities.DEFAULT_CASE)[i] + " = " + "?";
                
                if( i + 1 != DBUtilities.getColumnsCount(tableName) )
                    query += " OR ";
            }
            
            rs = DBQueryExecuter.excuteQueryParams(query, params);
            
            rs.last();
            
            String[][] rows = new String[rs.getRow()][DBUtilities.getColumnsCount(tableName)];
            
            int i = 0;
            rs.beforeFirst();
            while( rs.next() ){
                
                for (int j = 0; j < colsCount; j++) {
                    rows[i][j] = rs.getString(j + 1);
                }
                
                i++;  
            }
         
            return rows;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return null;
        }
    }
    
    public static String getCount(String tableName, Integer anneScolaire){
        
        try{
            
            String query = null;
            switch(tableName){
                case "etudiant":
                    query = "select COUNT(*) " +
                        "from etudiant e inner join filiere f " +
                        "on e.filiere = f.intitule_fill " +
                        "inner join departement d " +
                        "on f.departement_intitule = d.intitule_dept " +
                        "inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'";
                    break;
                case "filiere":
                    query = "select COUNT(*) " +
                        "from filiere f inner join departement d " +
                        "on f.departement_intitule = d.intitule_dept " +
                        "inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'";
                    break;
                case "departement":
                    query = "select COUNT(*) " +
                        "from departement d inner join annee_departement a " +
                        "on d.intitule_dept = a.departement " +
                        "where a.annee = '"+anneScolaire+"'";
                    break;
            }
            
            rs = DBQueryExecuter.excuteQuery(query);
            
            while(rs.next()){
                return rs.getString(1);
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        return "";
    }
    
    /**
     * @param tableName
     * @param data as string array
     * @return Return Boolean
     */
    public static boolean updateRow(String tableName, String[] data)
    {
        
        try{
            
            String query = "UPDATE " + tableName + " SET ";
            
            String[] cols = DBUtilities.getColumns(tableName, DBUtilities.DEFAULT_CASE);
            
            for (int i = 0; i < data.length; i++) {
                
                query += cols[i] + " = ?";
                
                if( i + 1 != data.length )
                  query += " , ";  
                
            }
            
            query += " WHERE " + cols[0] + " = '" + data[0] + "'";
            
            // Set PreparedStatement query
            ps = DBManager.connection.prepareStatement(query);
            
            // Set PreparedStatement query parameters
            for (int i = 0; i < data.length; i++) {
                ps.setString(i + 1, data[i]);
            }
            
            if(ps.executeUpdate() > 0)
                return true;
            
        } catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * 
     * @param tableName
     * @param columnName target column
     * @return Return row data as string array
     */
    public static String[] getDataByColumn(String tableName, String columnName)
    {
        try{
          
            String query = "SELECT " + columnName + " FROM " + tableName;

            rs = DBQueryExecuter.excuteQuery(query);
            
            rs.last();  
            
            String[] data = new String[rs.getRow()];

            rs.beforeFirst();
            
            int i = 0;
            while( rs.next() ){
              data[i] = rs.getString(1);
              i++;
            }
            
            return data;
        } catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * @param tableName
     * @param where
     * @param operator Optional "AND" or "OR"
     * @return 
     */
    /*
    public static String[] getRow(String tableName, String[][] where, String operator)
    {
        try {
            String query = "SELECT * FROM " + tableName;
            
            if( where != null && operator != null ){
                
                query += " WHERE ";
                String[] values = new String[where.length]; // Store key values
                
                for(int i = 0; i < where.length; i++)
                {

                    for( int j = 0; j < where[i].length; j++ )
                    {
                        query += where[i][j] + " = ?";
                        values[i] = String.valueOf(where[i][j + 1]);
                        break;
                    }
                    if( where.length == i + 1 ) // Check if last item
                        break;

                    query += " AND "; 
                }

                ps = DBManager.connection.prepareStatement(query);
                // Setting stored values to prepared statement query
                for (int i = 0; i < values.length; i++) {
                    ps.setString(i + 1, values[i]);
                }  
                
            } else{
                ps = DBManager.connection.prepareStatement(query);
            }
            
            rs = ps.executeQuery();
            
            int cols = DBUtilities.getColumnsCount(tableName);
            String[] record = new String[cols];
            
            if( rs.next() ){
                for (int j = 0; j < cols; j++) {
                    record[j] = rs.getString( j + 1 );   
                }
            }
            
            return record;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }*/
}
