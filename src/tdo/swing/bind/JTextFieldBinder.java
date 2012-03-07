/*
 * JTextFieldBinder.java
 *
 * Created on 29 ������� 2006 �., 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.bind;

import javax.swing.JTextField;
import tdo.DataColumn;
import tdo.Table;
import tdo.bind.AbstractBinder;
import tdo.event.TableEvent;
import tdo.swing.util.DataUtil;

/**
 *
 * @author Valera
 */
public class JTextFieldBinder extends AbstractBinder{
    /** Creates a new instance of JTextFieldBinder */
    public JTextFieldBinder() {
    }
    
    /**
     * �������������� ��������� ��������� ������. <p>
     *      ����� ���������� ������� PositionManager ����� �������� ���������� ������ � ����������
     * ������� positionManager,component,columnName. <p>
     *      ��������� ���� (this) ����������  ������� ActionEvent. <p>
     */
    @Override
    protected void initBinder() {
        JTextField f = (JTextField)this.getComponent();
        f.addActionListener(this);
    }
    
    /**
     * ������� ������ �� ActionListener �� JTextField. <p>
     * ����� initBinder ������������� ��������� ���������� JTextField ���������
     * ������� ActionEvent. ��� �������� (remove) ������� ������� ���������� �������
     * � ������ �� ���� listener. <p>
     */
    @Override
    public void removing() {
        ((JTextField)this.getComponent()).removeActionListener(this);
    }
    /**
     * ���������� ��������� ���������� � ���������� ��������� ������� �������� ������
     * BaseDataTable. <p>
     * � ���������� ���� JTextField ��������������� �������� �������� text. <p>
     * 
     * 
     * 
     * @param value - ��� Object. ����� �������� �� ������ ��������� ���� rowIndex PDatatable,
     * � ������, ������������ ���������� column.
     * @param column - ��� DataColumn. �������, ��������� � �����������.
     * @param rowIndex - ��� int. ����� �������� ��� BaseDataTable.
     */
    @Override
    protected void componentChange( Object value,  DataColumn column , int rowIndex ) {
        String s = DataUtil.toString(column,value);
        ((JTextField)this.getComponent()).setText(s);
        
    }
    
    /**
     * �������� �������� � ������� dataTable, �������� ����������� columnIndex � rowIndex.<p>
     * ����������� �������� �������� text ���������� � ��� ��������������� ������ ������� dataTable
     * � �������� ����� �������� � ����� ������������ ������ PositionManager.setValueAt.
     * 
     * 
     * @param dataTable - ��� BaseDataTable. ������� ������, ��������� � PositionManager.
     * @param rowIndex - ��� int. ������ ��������� ����.
     * @param columnIndex - ��� int. ������ ������� ��������� ����, ��������� �� ��������� text ����������
     * JTextField.
     */
    @Override
    protected void setValue( Table dataTable, int rowIndex, int columnIndex) {
        if ( dataTable == null )
            return;
        DataColumn column = dataTable.getColumns().get(columnIndex);
        Object obj = DataUtil.valueOf(column,((JTextField)this.getComponent()).getText());
        this.getPositionManager().setValue(obj, rowIndex, columnIndex);
        
    }
    @Override
    public void tableChanged(TableEvent e ) {
        super.tableChanged(e);
    }

//    public void activeRowChanging(ActiveRowEvent e) {
//    }
}//class
