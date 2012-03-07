/*
 * JLabelBinder.java
 *
 * Created on 1 ������ 2006 �., 15:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.bind;

import javax.swing.JLabel;
import tdo.DataColumn;
import tdo.Table;
import tdo.bind.AbstractBinder;
import tdo.event.TableEvent;
import tdo.event.TableEvent.TableEventCause;
import tdo.swing.util.DataUtil;

/**
 *
 * @author valery
 */
public class JLabelBinder extends AbstractBinder {
    
    /** Creates a new instance of JLabelBinder */
    public JLabelBinder() {
    }
    
    /**
     * ��������� ��������� ���������� � ���������� ��������� ������� �������� ������
     * PDataTable. <p>
     * � ���������� JLabel ��������������� �������� �������� text. <p>
     * 
     * @param value - ��� Object. ����� �������� �� ������ ��������� ���� rowIndex PDatatable,
     * � ������, ������������ ���������� column.
     * @param column - ��� DataColumn. �������, ��������� � �����������.
     * @param rowIndex - ��� int. ����� �������� ��� PDataTable.
     */
    @Override
    protected void componentChange( Object value,  DataColumn column, int rowIndex  ) {
        if ( column != null ) {
            String s = DataUtil.toString(column,value);                    
            ((JLabel)this.getComponent()).setText(s);
        } 
    }
    
    @Override
    protected void rowUpdated(int rowIndex) {
       String columnName = this.getColumnName();
       Table dt = this.getPositionManager().getTable();
       Object value = dt.getRow(rowIndex).getValue(columnName);
       String s = DataUtil.toString(dt.getColumns().get(columnName),value);
       ((JLabel)this.getComponent()).setText(s);
            
    }

    @Override
    protected void rowInserted(int rowIndex) {
       rowUpdated(rowIndex);
    }
    
    @Override
    public void tableChanged(TableEvent e ) {
        super.tableChanged(e);
        Table dataTable = this.getPositionManager().getTable();
        
        if ( e.getCause() == TableEventCause.insert ) {
              if ( e.getChangedRow() == dataTable.getActiveRowIndex() )
                 rowInserted(e.getChangedRow());
        }
        if ( e.getCause() == TableEventCause.newrow ) {

            rowInserted(e.getChangedRow());
        }        
    }

//    public void activeRowChanging(ActiveRowEvent e) {
//    }
}
