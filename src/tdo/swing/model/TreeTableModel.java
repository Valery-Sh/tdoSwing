/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tdo.swing.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import tdo.DataRow;
import tdo.TreeDataTable;

/**
 *
 * @author Valery
 */
public class TreeTableModel implements TreeModel {
    protected TreeDataTable table;
    public TreeTableModel() {
    }
    public TreeTableModel(TreeDataTable table) {
        this.table = table;

    }

    public Object getRoot() {
        return table.getRow(0);
    }

    public Object getChild(Object parent, int index) {
        DataRow prow = (DataRow)parent;
        DataRow crow = null;
        int pindex = prow.getIndex();
        int i = pindex + 1;
        if ( pindex > table.getRowCount() )
            return null;
        int cindex = 0;

        while ( i < table.getRowCount() ) {
            DataRow r = table.getRow(i);
            int rdepth = r.getState().getDepth();
            int pdepth = prow.getState().getDepth();

            if ( rdepth == pdepth+1 ) {
                if ( i == index ) {
                    return r;
                }
            } else if ( rdepth >= pdepth  ) {
                return null;
            }
            i++;
        }
        return null;
    }

    public int getChildCount(Object parent) {
        DataRow prow = (DataRow)parent;
        int pindex = prow.getIndex();
        int i = pindex + 1;
        if ( pindex > table.getRowCount() )
            return 0;
        int count = 0;
        while ( i < table.getRowCount() ) {
            DataRow r = table.getRow(i);
            int rdepth = r.getState().getDepth();
            int pdepth = prow.getState().getDepth();

            if ( rdepth == pdepth+1 ) {
                count++;
            } else if ( rdepth >= pdepth  ) {
                return count;
            }
            i++;
        }
        return count;

    }

    public boolean isLeaf(Object node) {
        DataRow prow = (DataRow)node;
        return prow.getState().getDepth() == -1 ? true : false;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getIndexOfChild(Object parent, Object child) {
        DataRow prow = (DataRow)parent;
        DataRow crow = null;
        int pindex = prow.getIndex();
        int i = pindex + 1;
        if ( pindex > table.getRowCount() )
            return -1;

        while ( i < table.getRowCount() ) {
            DataRow r = table.getRow(i);
            int rdepth = r.getState().getDepth();
            int pdepth = prow.getState().getDepth();

            if ( rdepth == pdepth+1 ) {
                if ( r == child ) {
                    return i;
                }
            } else if ( rdepth >= pdepth  ) {
                return -1;
            }
            i++;
        }
        return -1;

    }
    List<TreeModelListener> treeModelListeners;
    public void addTreeModelListener(TreeModelListener l) {
        if ( this.treeModelListeners == null ) {
            this.treeModelListeners = new ArrayList<TreeModelListener>();
        }
        this.treeModelListeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        if ( this.treeModelListeners != null ) {
            this.treeModelListeners.remove(l);
        }

    }

}
