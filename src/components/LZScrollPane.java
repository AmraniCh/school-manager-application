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
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

public class LZScrollPane extends JScrollPane {

    public LZScrollPane() {
        init();
    }
    
    public LZScrollPane(Component comp){
        super(comp);
        init();
    }
    
    private void init(){
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
    }
    
}
