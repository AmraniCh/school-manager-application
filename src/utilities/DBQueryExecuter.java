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

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
