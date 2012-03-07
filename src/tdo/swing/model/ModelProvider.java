/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tdo.swing.model;

import java.util.HashMap;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import tdo.Table;
import tdo.event.TableEvent;
import tdo.event.TableEvent.TableEventCause;
import tdo.event.TableListener;


public class ModelProvider {

    public TableModel getTableModel(Table table) {
        return new TableTableModel(table);
    }
    public static TableModel createTableModel(Table table) {
        return new TableTableModel(table);
    }

    public static class TableTableModel extends AbstractTableModel {

        private Table table;
        private HashMap<TableModelListener,TableTableModelListener> tmMap;
        public TableTableModel(Table table) {
            this.table = table;
            tmMap = new HashMap<TableModelListener,TableTableModelListener>();
        }

        public int getRowCount() {
            return this.table.getRowCount();
        }

        public int getColumnCount() {
            return this.table.getColumns().getCount();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return this.table.getColumns().get(columnIndex).getName();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return this.table.getColumns().get(columnIndex).getType();

        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.table.isCellEditable(rowIndex, columnIndex);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return this.table.getRow(rowIndex).getValue(columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            this.table.getRow(rowIndex).beginEdit();
            this.table.getRow(rowIndex).setValue(aValue, columnIndex);
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            super.addTableModelListener(l);
            TableTableModelListener ttl = new TableTableModelListener(this,l);
            table.addDataModelListener(ttl);
            this.tmMap.put(l, ttl);

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            super.removeTableModelListener(l);
            TableTableModelListener ttl = this.tmMap.get(l);
            table.removeDataModelListener(ttl);
            tmMap.remove(l);
        }
    }//class

    public static class TableTableModelListener implements TableListener {

        TableModelListener modelListener;
        TableModel tableModel;
        public TableTableModelListener(TableModel tableModel,TableModelListener l) {
            this.modelListener = l;
            this.tableModel = tableModel;
        }

        public void tableChanged(TableEvent e) {
            TableModelEvent tme = new TableModelEvent(tableModel,
                    e.getChangedRow(),
                    e.getChangedRow(),
                    e.getChangedColumns(),
                    getType(e.getCause()));
            modelListener.tableChanged(tme);
        }


        protected int getType(TableEventCause cause) {
            if (cause == TableEventCause.insert) {
                return TableModelEvent.INSERT;
            }
            if (cause == TableEventCause.update) {
                return TableModelEvent.UPDATE;
            }
            if (cause == TableEventCause.delete) {
                return TableModelEvent.DELETE;
            }
            return -1;


        }
    }//class

}//class ModelProvider
