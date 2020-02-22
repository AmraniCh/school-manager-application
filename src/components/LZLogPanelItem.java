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
package components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.time.LocalDate;
import javax.swing.JPanel;

public class LZLogPanelItem extends JPanel{
    private LocalDate date;
    private String body;
    
    public LZLogPanelItem(LocalDate date, String body){
        this.date = date;
        this.body = body;
        
        this.setBackground(Color.white);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(new LZInputLabel(date.toString()));
        this.add(new LZInputLabel(body));
        this.setSize(400, 14);
    }
    
}
