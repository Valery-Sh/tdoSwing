/*
 * JComboBoxBinder.java
 *
 * Created on 5 Ноябрь 2006 г., 12:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tdo.swing.bind;


import java.awt.event.ActionEvent;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import tdo.DataColumn;
import tdo.Table;
import tdo.bind.AbstractBinder;
import tdo.swing.combo.PComboBoxModel;
import tdo.swing.util.DataUtil;

/**
 *
 * @author valery
 */
public class JComboBoxBinder extends AbstractBinder {
    //        protected ListSelectionHandler handler;
    //        int selectionMode;
    ComboBoxModel model;
    /**
     * Создает экземпляр класса JListBinder. <p>
     */
    public JComboBoxBinder() {
    }
    /**
     * Инициализирует созданный экземпляр класса. <p>
     * Метод вызывается классом PositionManager после создания экземпляра класса и назначения
     * свойств positionManager,component,columnName. <p>
     *    Создает экземпляр класса ListSelectionHandler и  назначает его компоненте JList как слушателя
     * события ListSelectionEvent. <p>
     * Сохраняет значение свойства selectionMode модели selectionModel в локальной переменной класса с
     * целью последующего воостановления при удалении JList из списка связываемых компонент. Присваивает новое
     * значение свойству selectionMode модели selectionModel равное ListSelectionModel.SINGLE_SELECTION.
     *   Сохраняет значение свойства model (тип ListModel)  в локальной переменной класса с
     * целью последующего воостановления при удалении JList из списка связываемых компонент. Присваивает новое
     * значение свойству model равное new PListModel(dataTable,columnName).<p>
     * 
     */
    @Override
    protected void initBinder() {
        //((JList)this.getComponent()).
        //handler = new ListSelectionHandler();
        // selectionMode = ((JComboBox)this.getComponent()).getSelectionModel().getSelectionMode();
        //((JComboBox)this.getComponent()).getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //((JComboBox)this.getComponent()).addListSelectionListener(handler);
        
        if ( this.getColumnName() == null ) {
            return;
        }
        Table dataTable = this.getPositionManager().getTable();
        this.model = ((JComboBox)this.getComponent()).getModel();
        PComboBoxModel vModel;
        if ( dataTable != null ) {
            vModel = new PComboBoxModel(dataTable,this.getColumnName());
            //vListModel.addListDataListener()
            ((JComboBox)this.getComponent()).setModel(vModel);
            vModel.fireContentsChanged(vModel, -1, -1);
        }
        JComboBox f = (JComboBox)this.getComponent();
        f.addActionListener(this);
    }
    
    @Override
    public void dataTableChanged( Table newDataTable) {
        PComboBoxModel vModel;
        if ( newDataTable != null ) {
            vModel = new PComboBoxModel(newDataTable,this.getColumnName());
            ((JComboBox)this.getComponent()).setModel(vModel);
        } else {
            ((JComboBox)this.getComponent()).setModel(model);
        }
    }
    
    
    /**
     * Удаляет ссылку на ListSelectionListener из JList. <p>
     * Метод initBinder автоматически назначает компоненте JList слушателя
     * события ListSelectionEvent. При удалении (remove) данного объекта необходимо удалить
     * и ссылку на этот listener. <p>
     * Восстанавливает значение свойства selectionMode для selectionModel и значение свойства model,
     * в значения, которые были до связывания JList. <p>
     */
    @Override
    public void removing() {
        //((JComboBox)this.getComponent()).removeListSelectionListener(handler);
        //((JComboBox)this.getComponent()).getSelectionModel().setSelectionMode(selectionMode);
        ((JComboBox)this.getComponent()).setModel(model);
        ((JComboBox)this.getComponent()).removeActionListener(this);
    }
    /**
     * Устанавливает новое значение выделенного ряда списка ( selectedIndex ) равным
     * индексу ряда активной записи dataTable.<p>
     * Индекс активной записи равен значению параметра rowIndex.<p>
     * Уснанавливает значение selectedIndex и делает выбранный ряд видимым.<p>
     * 
     * 
     * @param dataTable - тип BaseDataTable. Таблица данных, связанная с PositionManager.
     * @param rowIndex - тип int. Индекс активного ряда.
     * @param columnIndex - тип int. Индекс колонки активного ряда, которая предоставляет объекты
     * для модели данных JList.
     */
    @Override
    protected void componentChange( Object value, DataColumn column, int rowIndex  ) {
        JComboBox f = (JComboBox)this.getComponent();
        ((PComboBoxModel)f.getModel()).fireContentsChanged(f.getModel(),rowIndex,rowIndex);
//        ((PComboBoxModel)f.getModel()).setSelectedItem(column.toString(value));
        ((PComboBoxModel)f.getModel()).setSelectedItem(DataUtil.toString(column,value));        
        
        //if (  rowIndex != f.getSelectedIndex() ) {
        //        f.setSelectedIndex(rowIndex);
        //        JListScrolling.makeRowVisible(f,rowIndex);
        //}
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox f = (JComboBox)this.getComponent();
        Table dataTable = this.getPositionManager().getTable();
        int rowIndex = f.getSelectedIndex();
        if ( rowIndex < 0 || rowIndex >=dataTable.getRowCount() ) {
            return;
        }
        
        this.getPositionManager().moveTo(rowIndex);
    }
    
    @Override
    protected void rowInserted(int rowIndex) {
        JComboBox f = (JComboBox)this.getComponent();
        ((PComboBoxModel)f.getModel()).fireIntervalAdded(f.getModel(),rowIndex,rowIndex);
    }
    @Override
    protected void rowDeleted(int rowIndex) {
        JComboBox f = (JComboBox)this.getComponent();
        Table dataTable = this.getPositionManager().getTable();
        ((PComboBoxModel)f.getModel()).fireIntervalRemoved(f.getModel(),rowIndex,rowIndex);
        if ( dataTable.getRowCount() == 0 ) {
            f.getModel().setSelectedItem(null);
        }
    }

//    public void activeRowChanging(ActiveRowEvent e) {
//    }
    
}//class JComboBoxBinder
