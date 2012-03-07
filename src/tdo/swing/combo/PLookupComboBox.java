/*
 * PLookupComboBox.java
 *
 * Created on 5 Ноябрь 2006 г., 17:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.combo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import tdo.DataColumn;
import tdo.Table;
import tdo.event.ActiveRowEvent;
import tdo.event.ActiveRowListener;

/**
 *
 * @author Valera
 */
public class PLookupComboBox extends JComboBox{
     protected int selectedItemIndex;    
     protected Object oldSelectedObject;
     
     private Table targetDataTable;
     private String targetColumnName;    // Относится к targetDataTable 
     
     private Table lookupDataTable;     
     private String displayColumnName;   // Относится к lookupDataTable
     private String lookupColumnName;    // Относится к lookupDataTable
     

     /**
     * Creates a <code>JComboBox</code> that takes its items from an
     * existing <code>ComboBoxModel</code>.  Since the
     * <code>ComboBoxModel</code> is provided, a combo box created using
     * this constructor does not create a default combo box model and
     * may impact how the insert, remove and add methods behave.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the 
     * 		displayed list of items
     * @see DefaultComboBoxModel
     */
    public PLookupComboBox(ComboBoxModel aModel) {
        super(aModel);
        pInit();
    }

    
    /** 
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified array.  By default the first item in the array
     * (and therefore the data model) becomes selected.
     *
     * @param items  an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public PLookupComboBox(final Object items[]) {
        super(items);
        pInit();
        
       // setModel(new DefaultComboBoxModel(items));
  
    }

    /**
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified Vector.  By default the first item in the vector
     * (and therefore the data model) becomes selected.
     *
     * @param items  an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    private PLookupComboBox(Vector<?> items) {
        super(items);
        pInit();
       // setModel(new DefaultComboBoxModel(items));
  
    }

    /**
     * Creates a <code>JComboBox</code> with a default data model.
     * The default data model is an empty list of objects.
     * Use <code>addItem</code> to add items.  By default the first item
     * in the data model becomes selected.
     *
     * @see DefaultComboBoxModel
     */
    public PLookupComboBox() {
        super();
        //setModel(new DefaultComboBoxModel());
        pInit();
        //this.addItemListener()

    }

    public PLookupComboBox(Table lookupDataTable, String displayColumnName) {
        super();
        pInit();        
        PComboBoxModel cbm = new PComboBoxModel(lookupDataTable, displayColumnName);
        setModel(cbm);        
//        cbm.addListDataListener(this.eventHandler);

        this.lookupDataTable = lookupDataTable;
        this.displayColumnName = displayColumnName.toUpperCase();
     
        
    }
  
    EventHandler eventHandler = new EventHandler();
    protected void pInit() {
        eventHandler = new EventHandler();
        this.addItemListener(eventHandler);
        this.addActionListener(eventHandler);
    }
    
    public Table getLookupDataTable() {
        return this.lookupDataTable;
    }
    
    public void setLookupDataTable(Table lookupDataTable) {
        
        if ( this.lookupDataTable != null && this.lookupDataTable.equals(lookupDataTable))
            return;
        this.lookupDataTable = lookupDataTable;
        if ( this.lookupDataTable != null ) {
            PComboBoxModel cbm = new PComboBoxModel(lookupDataTable, displayColumnName);
            setModel(cbm);        
        }
    }
    
    public String getDisplayColumnName() {
        return this.displayColumnName;
    }
    
    public void setDisplayColumnName( String displayColumnName ) {
        if ( this.displayColumnName.toUpperCase().equals(displayColumnName) )
            return;
        this.displayColumnName = displayColumnName.toUpperCase();
        if ( this.lookupDataTable != null ) {
            PComboBoxModel cbm = new PComboBoxModel(lookupDataTable, displayColumnName);
            setModel(cbm);                    
        }
    }

   public String getLookupColumnName() {
        return this.lookupColumnName;
    }
    
    public void setLookupColumnName( String lookupColumnName ) {
        if ( this.lookupColumnName != null && this.lookupColumnName.toUpperCase().equals(lookupColumnName) )
            return;
        this.lookupColumnName = lookupColumnName.toUpperCase();
    }    
    
    public Table getTargetDataTable() {
        return this.targetDataTable;
    }
    
    public void setTargetDataTable(Table targetDataTable) {
        
        if ( this.targetDataTable != null && this.targetDataTable.equals(targetDataTable))
            return;
        if ( this.targetDataTable != null )
            this.targetDataTable.removeActiveRowListener(this.eventHandler);
        this.targetDataTable = targetDataTable;
        if ( this.targetDataTable != null )        
            this.targetDataTable.addActiveRowListener(this.eventHandler);
    }

    public String getTargetColumnName() {
        return this.targetColumnName;
    }
    
    public void setTargetColumnName( String targetColumnName ) {
        if ( this.targetColumnName != null && this.targetColumnName.toUpperCase().equals(targetColumnName) )
            return;
        this.targetColumnName = targetColumnName.toUpperCase();
    }
    
    public void setTargetValue() {
        if ( this.targetDataTable == null || this.targetColumnName == null )
            return;
        DataColumn column = targetDataTable.getColumns().get(this.targetColumnName);
        if ( column == null )
            return;
        int selIndex = this.getSelectedIndex();
        if ( selIndex < 0 )
            return;
        if ( this.getModel() instanceof PComboBoxModel ) {
            if (this.lookupColumnName == null  )
                return; //наверное надо выбросить Exception
        }
        
        Object obj = getLookupValue(selIndex);
        
        try {
            setTargetValue(obj);
        } catch ( Exception e ) {
           setSelectedItem(this.oldSelectedObject);
        }
    }
    
    protected void setTargetValue(Object obj) {
        int rowIndex = this.targetDataTable.getActiveRowIndex();
        if ( rowIndex < 0 )
            return;
        DataColumn column = targetDataTable.getColumns().get(this.targetColumnName);
        
        String s = null;
        if ( obj != null )
            s = obj.toString();
        //Object o = column.valueOf(s);
        
        this.targetDataTable.getRow(rowIndex).setValue(obj, this.targetColumnName);
    }
    protected Object getLookupValue(int selIndex) {
        if ( this.getModel() instanceof PComboBoxModel ) {
            return this.lookupDataTable.getRow(selIndex).getValue(this.lookupColumnName);
        } else {
            return null;
        }
    }
    
 // Flag to ensure the we don't get multiple ActionEvents on item selection.
    
    @Override
    public int getSelectedIndex() {
        return super.getSelectedIndex();
    }
    @Override
    public Object getSelectedItem() {
        return super.getSelectedItem();
    }
    
    @Override
    public void setSelectedIndex(int anIndex) {
        int size = dataModel.getSize();

        if ( anIndex == -1 ) {
            setSelectedItem( null );
        } else if ( anIndex < -1 || anIndex >= size ) {
            throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");
        } else {
            setSelectedItem(dataModel.getElementAt(anIndex));
        }
    }
    
    
    @Override
    public void setSelectedItem(Object anObject) {
        //System.out.println("setSelectedItem(Object anObject)");
        super.setSelectedItem(anObject);
        Object oldSelection = selectedItemReminder;
        if (oldSelection != null && oldSelection.equals(anObject)) {
            this.eventHandler.actionPerformed(null);
        }       
    }

    public class EventHandler implements ActionListener, ItemListener, ActiveRowListener {
        public void actionPerformed(ActionEvent e) {
            setTargetValue(); // Тест на Exception
        }
        /**
         * Метод, реализующий интерфейс ItemListener.<p>
         * Метод срабатывает при выполнении setSelectedItem(..). При этом, если до этого момента
         * не было ничего выбрано, то e.getStateChange() == e.SELECTED. <p>
         * Метод выполняется раньше метода actionPerformed().
         */
        public void itemStateChanged(ItemEvent e) {
            int i = 0;
            String s;
            if ( e.getStateChange() == e.DESELECTED ) {
                // Срабатывает, только, если selectedItemReminder != null
                // Но всегда до изменения selectedItem
                i++;
                oldSelectedObject = e.getItem(); // это, на самом деле значение защ. поля selectedItemReminder
                s = oldSelectedObject == null ? "nil" : oldSelectedObject.toString();
            }
            if ( e.getStateChange() == e.SELECTED ) {
                // Срабатывает, только, если selectedItemReminder != null
                // Но всегда после изменения selectedItem
                i++;
                s = oldSelectedObject == null ? "nil" : oldSelectedObject.toString();
            }
        }
        
        public void activeRowChange( ActiveRowEvent e) {
            int rowNo = targetDataTable.getActiveRowIndex();
            if ( rowNo < 0 )
                return;
            
            Object targetobj = null;
            DataColumn column = null;
            if ( targetColumnName != null ) {
                targetobj = targetDataTable.getRow(rowNo).getValue(targetColumnName);
                column = targetDataTable.getColumns().get(targetColumnName);
                if ( lookupDataTable != null ) {
                    boolean found = false;
                    for ( int i=0; i < lookupDataTable.getRowCount(); i++ ) {
                        Object lookupobj = lookupDataTable.getRow(i).getValue(lookupColumnName);
                        if ( lookupobj.equals(targetobj) ) {
                            setSelectedIndex(i);
                            found = true;
                            break;
                        }
                    }//for
                    if ( ! found ) {
                        setSelectedIndex(-1);
                    }
                }//if
            }//if
        }

        public void activeRowChanging(ActiveRowEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }//classEventHandler
}//class PLookupComboBox
