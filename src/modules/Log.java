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
package modules;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.time.LocalDate;
import java.util.Scanner;

public class Log {  
    private int ID;
    private LocalDate date;
    private String body;
    
    /**
     * Logs File
     */
    private File logsFile;
    private FileWriter fw;
    
    /**
     * Log File Name
     */
    private final static String logFileName = "logs.txt";

    
    /**
     * Getters
     */
    public int getID() {
        return ID;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    /**
     * Constructor for loading logs
     * @param ID
     * @param date
     * @param body
     */
    public Log(int ID, LocalDate date, String body){
        this.ID = ID;
        this.date = date;
        this.body = body;
    }
    
    /**
     * Constructor for creating logs
     * @param action Log type can be "etudiant" OR "departement" OR "filiere"
     * @param type Can be "delete" OR "insert" OR "update"
     * @param object Log variable
     */
    public Log(String action, String type, String object){
        ID = getFileLinesCount() + 1;
        date = LocalDate.now();
        
        try{
            logsFile = new File(Log.logFileName);
            
            fw = new FileWriter(logsFile, true);
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        
        appendLog(this.buildLogString(action, type, object));
    }
   
    
    private String buildLogString(String action, String type, String object){
        
        String suffixBody = "";
        switch(action){
            case "delete":
               suffixBody = "à été supprimé";
               break;
            case "update":
               suffixBody = "à été modifié";
               break;
            case "insert":
               suffixBody = "à été ajouté";
               break;
        }
        
        String prefixBody = "";
        switch(type){
            case "etudiant":
               prefixBody = "L'étudiant";
               break;
            case "departement":
               prefixBody = "Département";
               break;
            case "filiere":
               prefixBody = "Filière";
               break;
            case "annee":
                prefixBody = "L'année scolaire";
                break;
        }
        
        return "["+this.ID+"] " + this.date + " " + prefixBody + " " + object + " " + suffixBody;
    }
    
    private void appendLog(String log){
        try{
            fw.write(log);
            fw.write("\n");
            fw.close();
            
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
   
    public static Log[] loadLogs(){
        try{
            File f = new File(Log.logFileName);
            
            Scanner r = new Scanner(f);
            
            Log[] logs = new Log[Log.getFileLinesCount() - 1];
            
            int i = 0;
            while(r.hasNextLine()){
                String line = r.nextLine();
                
                int ID = Integer.parseInt(line.split(" ")[0].substring(1, line.split(" ")[0].length() - 1));
                LocalDate date = LocalDate.parse(line.split(" ")[1]);
                String body = "";
                
                for (int j = 2; j < line.split(" ").length; j++) {
                    body += line.split(" ")[j] + " ";
                }
                
                logs[i] = new Log(ID, date, body);
                i++;
            }
            r.close();
            
            return logs;
        } catch(Exception ex){
            
        }
        
        return null;
    }

    public static int getFileLinesCount(){
        
        try
        {
            FileReader  input = new FileReader(Log.logFileName);
            LineNumberReader count = new LineNumberReader(input);
        
            while (count.skip(Long.MAX_VALUE) > 0)
            {
              // Loop just in case the file is > Long.MAX_VALUE or skip() decides to not read the entire file
            }

            return count.getLineNumber() + 1;                                
            
        } catch(Exception ex){
            System.out.println("");
        }
        return 0;
    }
}
