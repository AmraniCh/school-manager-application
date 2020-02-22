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
import javax.swing.JComboBox;
import javax.swing.border.*;

public class LZComboBox extends JComboBox{
    
    public LZComboBox(){
        this.setBorder( new MatteBorder(0, 0, 2, 0, CustomColors.SECONDARY));
        this.setFont(new CustomFont(16));       
        this.setEditable(true);      
        this.setBackground(Color.white);      
    }
    
}
