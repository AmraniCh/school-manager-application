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
package utilities;

import java.sql.ResultSet;

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
            System.out.println(query);
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
   
    
 
}
