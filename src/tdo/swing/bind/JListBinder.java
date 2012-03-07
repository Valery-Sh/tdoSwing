/*
 * JListBinder.java
 *
 * Created on 1 Ноябрь 2006 г., 16:21
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
     * Создает экземпляр класса JListBinder. <p>
     */
    public JListBinder() {
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
     * Удаляет ссылку на ListSelectionListener из JList. <p>
     * Метод initBinder автоматически назначает компоненте JList слушателя
     * события ListSelectionEvent. При удалении (remove) данного объекта необходимо удалить
     * и ссылку на этот listener. <p>
     * Восстанавливает значение свойства selectionMode для selectionModel и значение свойства model,
     * в значения, которые были до связывания JList. <p>
     */
    @Override
    public void removing() {
        ((JList)this.getComponent()).removeListSelectionListener(handler);
        ((JList)this.getComponent()).getSelectionModel().setSelectionMode(selectionMode);
        ((JList)this.getComponent()).setModel(listModel);
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
     * Внутренний класс класса JListBinder. <p>
     * Служит обработчиком события ListSelectionEvent для класса JList.<p>
     * Реализует интерфейс ListSelectionListener, переопределяя его метод valueChanged. <P>
     * Производит попытку установить новый активный ряд для dataTable, для чего вызывает
     * метод moveTo класса PositionManager. Если попытка завершилась неудачно, т.е. метод
     * PositionManager.moveTo(...) вернул булевое значение false, то selectedIndex списка восстанавливается
     * на активную запись.
     */
    public class ListSelectionHandler implements ListSelectionListener {
        /**
         * Реализация метода valueChanged интерфейса ListSelectionListener.<p>
         * Производит попытку установить новый активный ряд для dataTable, для чего вызывает
         * метод moveTo класса PositionManager. Если попытка завершилась неудачно, т.е. метод
         * PositionManager.moveTo(...) вернул булевое значение false, то selectedIndex списка восстанавливается
         * на активную запись.
         * @param e типа ListSelectionEvent.
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
