/*
 * JListBinder.java
 *
 * Created on 1 ������ 2006 �., 16:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.bind;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tdo.DataColumn;
import tdo.Table;
import tdo.bind.AbstractBinder;
import tdo.swing.list.PListModel;
import tdo.swing.util.JListScrolling;

/**
 *
 * @author valery
 */
public class JListBinder extends AbstractBinder {
    protected ListSelectionHandler handler;
    int selectionMode;
    ListModel listModel;
    /**
     * ������� ��������� ������ JListBinder. <p>
     */
    public JListBinder() {
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
        handler = new ListSelectionHandler();
        selectionMode = ((JList)this.getComponent()).getSelectionModel().getSelectionMode();
        ((JList)this.getComponent()).getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ((JList)this.getComponent()).addListSelectionListener(handler);
        
        if ( this.getColumnName() == null ) {
            return;
        }
        Table dataTable = this.getPositionManager().getTable();
        this.listModel = ((JList)this.getComponent()).getModel();
        PListModel vlistModel;
        if ( dataTable != null ) {
            vlistModel = new PListModel(dataTable,this.getColumnName());
            //vListModel.addListDataListener()
            ((JList)this.getComponent()).setModel(vlistModel);
        }
    }
    
    @Override
    public void dataTableChanged( Table newDataTable) {
        PListModel vlistModel;
        if ( newDataTable != null ) {
            vlistModel = new PListModel(newDataTable,this.getColumnName());
            ((JList)this.getComponent()).setModel(vlistModel);
        } else {
            ((JList)this.getComponent()).setModel(listModel);
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
        ((JList)this.getComponent()).removeListSelectionListener(handler);
        ((JList)this.getComponent()).getSelectionModel().setSelectionMode(selectionMode);
        ((JList)this.getComponent()).setModel(listModel);
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
        JList f = (JList)this.getComponent();
        ((PListModel)f.getModel()).fireContentsChanged(f.getModel(),rowIndex,rowIndex);
        f.setSelectedIndex(rowIndex);
        JListScrolling.makeRowVisible(f,rowIndex);
        
    }
    
    @Override
    protected void rowInserted(int rowIndex) {
        JList f = (JList)this.getComponent();
        ((PListModel)f.getModel()).fireContentsChanged(f.getModel(),rowIndex,rowIndex);
    }
    @Override
    protected void rowDeleted(int rowIndex) {
        JList f = (JList)this.getComponent();
        ((PListModel)f.getModel()).fireContentsChanged(f.getModel(),rowIndex,rowIndex);
    }
    
    /**
     * ���������� ����� ������ JListBinder. <p>
     * ������ ������������ ������� ListSelectionEvent ��� ������ JList.<p>
     * ��������� ��������� ListSelectionListener, ������������� ��� ����� valueChanged. <P>
     * ���������� ������� ���������� ����� �������� ��� ��� dataTable, ��� ���� ��������
     * ����� moveTo ������ PositionManager. ���� ������� ����������� ��������, �.�. �����
     * PositionManager.moveTo(...) ������ ������� �������� false, �� selectedIndex ������ �����������������
     * �� �������� ������.
     */
    public class ListSelectionHandler implements ListSelectionListener {
        /**
         * ���������� ������ valueChanged ���������� ListSelectionListener.<p>
         * ���������� ������� ���������� ����� �������� ��� ��� dataTable, ��� ���� ��������
         * ����� moveTo ������ PositionManager. ���� ������� ����������� ��������, �.�. �����
         * PositionManager.moveTo(...) ������ ������� �������� false, �� selectedIndex ������ �����������������
         * �� �������� ������.
         * @param e ���� ListSelectionEvent.
         */
        public void valueChanged( ListSelectionEvent e ) {
            Table dataTable = getPositionManager().getTable();
            if ( dataTable == null )
                return;
            JList f = (JList)getComponent();
            int rowIndex = f.getSelectedIndex();
            if ( ! getPositionManager().moveTo(rowIndex) ) {
                f.setSelectedIndex(dataTable.getActiveRowIndex());
            }
        }
    }//class ListSelectionHandler
    
    public class ListDataHandler implements ListDataListener {
        public void contentsChanged(ListDataEvent e) {
            
        }
        public void intervalAdded(ListDataEvent e) {
            
        }
        public void intervalRemoved(ListDataEvent e) {
            
        }
        
    }

//    public void activeRowChanging(ActiveRowEvent e) {
//    }
}//class JListBinder
