/*
 * JTableBinder.java
 *
 * Created on 3 Ноябрь 2006 г., 10:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tdo.swing.bind;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import tdo.DataColumn;
import tdo.DataRow;
import tdo.Table;
import tdo.bind.AbstractBinder;
import tdo.event.PendingEditingListener;
import tdo.swing.model.ModelProvider;
import tdo.swing.util.JTableScrolling;

/**
 *
 * @author valery
 */
public class JTableBinder extends AbstractBinder {

    protected TableSelectionHandler handler;
    int selectionMode;
    TableModel tableModel;
    TableEventHandler dataTableHandler;

    /**
     * Создает экземпляр класса JListBinder. <p>
     */
    public JTableBinder() {
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
        handler = new TableSelectionHandler();
        selectionMode = ((JTable) this.getComponent()).getSelectionModel().getSelectionMode();
        ((JTable) this.getComponent()).getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //((JTable)this.getComponent()).addListSelectionListener(handler);
        ((JTable) this.getComponent()).getSelectionModel().addListSelectionListener(handler);

        dataTableHandler = new TableEventHandler();
        Table dataTable = this.getPositionManager().getTable();
        this.tableModel = ((JTable) this.getComponent()).getModel();
        TableModel model;
        if (dataTable != null) {
            ModelProvider mp = new ModelProvider();
            model = mp.getTableModel(dataTable);
            ((JTable) this.getComponent()).setModel(model);
            dataTable.addPendingEditingListener(dataTableHandler);
        }
    }

    @Override
    public void dataTableChanged(Table dataTable) {
        TableModel model;
        if (dataTable != null) {
            ModelProvider mp = new ModelProvider();
            model = mp.getTableModel(dataTable);
            ((JTable) this.getComponent()).setModel(model);
            dataTable.addPendingEditingListener(dataTableHandler);
        } else {
            ((JTable) this.getComponent()).setModel(tableModel);
        }
    }

    @Override
    public void dataTableChanging(Table oldDataTable, Table newDataTable) {
        if (oldDataTable != null) {
            oldDataTable.removePendingEditingListener(dataTableHandler);
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
        ((JTable) this.getComponent()).getSelectionModel().removeListSelectionListener(handler);
        ((JTable) this.getComponent()).getSelectionModel().setSelectionMode(selectionMode);
        ((JTable) this.getComponent()).setModel(tableModel);
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
    protected void componentChange(Object value, DataColumn column, int rowIndex) {
        JTable f = (JTable) this.getComponent();
        //if (  rowIndex != f.getSelectedIndex() ) {
        f.setRowSelectionInterval(rowIndex, rowIndex);
        //JTableScrolling.makeRowVisible(f, rowIndex);
        f.scrollRectToVisible(f.getCellRect(rowIndex, 0, true));        
    //}
    }

    @Override
    protected void componentChanging(Object value, DataColumn column, int rowIndex) {
        JTable f = (JTable) this.getComponent();
        int editingRow = f.getEditingRow();
        //editingColumn
        if (editingRow >= 0 && editingRow == rowIndex) {
            Table dt = this.getPositionManager().getTable();
            int editingColumn = f.getEditingColumn();
            String editingColumnName = (String) f.getColumnModel().getColumn(editingColumn).getIdentifier();

            DataRow r = this.getPositionManager().getTable().getRow(rowIndex);
            Object v = r.getValue(editingColumnName);
            r.beginEdit();
            this.getPositionManager().setValue(v, rowIndex, editingColumnName);
        }
    }

    @Override
    protected void rowInserted(int rowIndex) {
        JTable f = (JTable) this.getComponent();
        //if (  rowIndex != f.getSelectedIndex() ) {
        f.setRowSelectionInterval(rowIndex, rowIndex);
        JTableScrolling.makeRowVisible(f, rowIndex);

    }

    @Override
    protected void rowDeleted(int rowIndex) {

        JTable f = (JTable) this.getComponent();
        Table dataTable = this.getPositionManager().getTable();
        int arow = dataTable.getActiveRowIndex();
        if (arow >= 0) {
            f.setRowSelectionInterval(arow, arow);
            JTableScrolling.makeRowVisible(f, arow);
        }

    }

    /**
     * Внутренний класс класса JListBinder. <p>
     * Служит обработчиком события ListSelectionEvent для класса JList.<p>
     * Реализует интерфейс ListSelectionListener, переопределяя его метод valueChanged. <P>
     * Производит попытку установить новый активный ряд для dataTable, для чего вызывает
     * метод moveTo класса PositionManager. Если попытка завершилась неудачно, т.е. метод
     * PositionManager.moveTo(...) вернул булевое значение false, то selectedIndex списка восстанавливается
     * на активную запись.
     */
    public class TableSelectionHandler implements ListSelectionListener {

        /**
         * Реализация метода valueChanged интерфейса ListSelectionListener.<p>
         * Производит попытку установить новый активный ряд для dataTable, для чего вызывает
         * метод moveTo класса PositionManager. Если попытка завершилась неудачно, т.е. метод
         * PositionManager.moveTo(...) вернул булевое значение false, то selectedIndex списка восстанавливается
         * на активную запись.
         * @param e типа ListSelectionEvent.
         */
        public void valueChanged(ListSelectionEvent e) {
            Table dataTable = getPositionManager().getTable();
            if (dataTable == null) {
                return;
            }
            JTable f = (JTable) getComponent();
            int rowIndex = f.getSelectedRow();

            if (rowIndex < 0 || rowIndex >= dataTable.getRowCount()) {
                return;
            }
            if (!getPositionManager().moveTo(rowIndex)) {
                //if ( f.getCellEditor())
                if (rowIndex == f.getEditingRow()) {
                    TableCellEditor tce = f.getCellEditor();
                    if (tce != null) {
                        tce.stopCellEditing();
                    }
                }
                rowIndex = dataTable.getActiveRowIndex();
                f.setRowSelectionInterval(rowIndex, rowIndex);
            //JTableScrolling.makeRowVisible(f,rowIndex);

            }

        }

/*        private TableCellEditor getCellEditor(int rowIndex) {
            JTable f = (JTable) getComponent();
            if (f.getCellEditor() == null) {
                return null;
            }
            for (int i = 0; i < f.getColumnCount(); i++) {
                Object o = f.getCellEditor(rowIndex, i);
                if (o != null) {
                    return (TableCellEditor) o;
                }
            }
            return null;
        }
 */
    }//class ListSelectionHandler

    public class TableEventHandler implements PendingEditingListener {

        public void stopPendingEditing() {
            //boolean b = true;
            JTable t = (JTable) getComponent();

            TableCellEditor tce = t.getCellEditor();

            if (tce != null) {
                tce.stopCellEditing();
            }
        //return b;
        }
    }//class TableEventHandler
}//class JListBinder
