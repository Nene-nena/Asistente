/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.star.wizards.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MiRender extends DefaultTableCellRenderer
{
   public Component getTableCellRendererComponent(JTable table,
      Object value,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column)
   {
      super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
   if(row==-1){
      
     
     this.setSize(1,1); 
      
   }else{
      if(!isSelected){
      if(row%2==0){
        
         this.setBackground(Color.WHITE);
         
   }else{
         this.setBackground(new Color(14938620));
         
   }}}
  
      return this;
   }
}