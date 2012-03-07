/*
 * JComboBoxBinder.java
 *
 * Created on 5 ������ 2006 �., 12:48
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
     * ������� ��������� ������ JListBinder. <p>
     */
    public JComboBoxBinder() {
    }
    /**
     * �������������� ��������� ��������� ������. <p>
     * ����� ���������� ������� PositionManager ����� �������� ���������� ������ � ����������
     * ������� positionManager,component,columnName. <p>
     *    ������� ��������� ������ ListSelectionHandler �  ��������� ��� ���������� JList ��� ���������
     * ������� ListSelectionEvent. <p>
     * ��������� �������� �������� selectionMode ������ selectionModel � ��������� ���������� ������ �
     * ����� ������������ �������������� ��� �������� JList �� ������ ����������� ���������. ����������� �����
     * �������� �������� selectionMode ������ selectionModel ������ ListSelectionModel.SINGLE_SELECTION.
     *   ��������� �������� �������� model (��� ListModel)  � ��������� ���������� ������ �
     * ����� ������������ �������������� ��� �������� JList �� ������ ����������� ���������. ����������� �����
     * �������� �������� model ������ new PListModel(dataTable,columnName).<p>
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
     * ������� ������ �� ListSelectionListener �� JList. <p>
     * ����� initBinder ������������� ��������� ���������� JList ���������
     * ������� ListSelectionEvent. ��� �������� (remove) ������� ������� ���������� �������
     * � ������ �� ���� listener. <p>
     * ��������������� �������� �������� selectionMode ��� selectionModel � �������� �������� model,
     * � ��������, ������� ���� �� ���������� JList. <p>
     */
    @Override
    public void removing() {
        //((JComboBox)this.getComponent()).removeListSelectionListener(handler);
        //((JComboBox)this.getComponent()).getSelectionModel().setSelectionMode(selectionMode);
        ((JComboBox)this.getComponent()).setModel(model);
        ((JComboBox)this.getComponent()).removeActionListener(this);
    }
    /**
     * ������������� ����� �������� ����������� ���� ������ ( selectedIndex ) ������
     * ������� ���� �������� ������ dataTable.<p>
     * ������ �������� ������ ����� �������� ��������� rowIndex.<p>
     * ������������� �������� selectedIndex � ������ ��������� ��� �������.<p>
     * 
     * 
     * @param dataTable - ��� BaseDataTable. ������� ������, ��������� � PositionManager.
     * @param rowIndex - ��� int. ������ ��������� ����.
     * @param columnIndex - ��� int. ������ ������� ��������� ����, ������� ������������� �������
     * ��� ������ ������ JList.
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
