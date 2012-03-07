/*
 * JTextFieldBinder.java
 *
 * Created on 29 Октябрь 2006 г., 11:14
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
     * Инициализирует созданный экземпляр класса. <p>
     *      Метод вызывается классом PositionManager после создания экземпляра класса и назначения
     * свойств positionManager,component,columnName. <p>
     *      Назначает себя (this) слушателем  события ActionEvent. <p>
     */
    @Override
    protected void initBinder() {
        JTextField f = (JTextField)this.getComponent();
        f.addActionListener(this);
    }
    
    /**
     * Удаляет ссылку на ActionListener из JTextField. <p>
     * Метод initBinder автоматически назначает компоненте JTextField слушателя
     * события ActionEvent. При удалении (remove) данного объекта необходимо удалить
     * и ссылку на этот listener. <p>
     */
    @Override
    public void removing() {
        ((JTextField)this.getComponent()).removeActionListener(this);
    }
    /**
     * Обновление состояния компонента в результате изменения позиции активной записи
     * BaseDataTable. <p>
     * В компоненте типа JTextField устанавливается значение свойства text. <p>
     * 
     * 
     * 
     * @param value - тип Object. Новое значение из нового активного ряда rowIndex PDatatable,
     * в ячейке, определяемой параметром column.
     * @param column - тип DataColumn. Колонка, связанная с компонентом.
     * @param rowIndex - тип int. Новый активный ряд BaseDataTable.
     */
    @Override
    protected void componentChange( Object value,  DataColumn column , int rowIndex ) {
        String s = DataUtil.toString(column,value);
        ((JTextField)this.getComponent()).setText(s);
        
    }
    
    /**
     * Изменяет значение в колонке dataTable, заданной параметрами columnIndex и rowIndex.<p>
     * Преобразует значение свойства text компонента в тип соответствующей колоки таблицы dataTable
     * и передает новое значение в вызов одноименного метода PositionManager.setValueAt.
     * 
     * 
     * @param dataTable - тип BaseDataTable. Таблица данных, связанная с PositionManager.
     * @param rowIndex - тип int. Индекс активного ряда.
     * @param columnIndex - тип int. Индекс колонки активного ряда, связанной со свойством text компонента
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
