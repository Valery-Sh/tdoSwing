/*
 * PComboBoxModel.java
 *
 * Created on 5 Ноябрь 2006 г., 12:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.combo;

import javax.swing.ComboBoxModel;
import tdo.Table;

/**
 *
 * @author Valera
 */
public class PComboBoxModel extends tdo.swing.list.PListModel implements ComboBoxModel {
    private Object selectedObject;
    private Object displaySelectedObject;    
    /** Creates a new instance of PComboBoxModel */
    public PComboBoxModel() {
        this(null,null);
    }
    public PComboBoxModel(Table dataTable) {
        this(dataTable,null);
    }
    public PComboBoxModel(Table dataTable, String columnName) {
        super(dataTable,columnName);
    }
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
   // public void setSelectedItem(Object anObject) {
        //selectedObject = anObject;
        //fireContentsChanged(this, -1, -1);
        
    public void setSelectedItem(Object anObject) {
//System.out.println("MODEL setSelectedItem" );            
        if ((selectedObject != null && !selectedObject.equals( anObject )) ||
                selectedObject == null && anObject != null) {
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
        } else {
            if ( anObject ==  null) {
                selectedObject = anObject;
                fireContentsChanged(this, -1, -1);
                
            }
        }
    }
        
    
    
    
    // implements javax.swing.ComboBoxModel
    public Object getSelectedItem() {
        return selectedObject;
    }
    public Object getDisplaySelectedItem() {
        return displaySelectedObject;
    }
    public void setDisplaySelectedItem(Object displaySelectedObject ) {
        this.displaySelectedObject = displaySelectedObject;
    }
    
    
    
    
}//class
