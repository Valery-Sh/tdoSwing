/*
 * JListScrolling.java
 *
 * Created on 1 Ноябрь 2006 г., 18:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.util;


import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JList;
import javax.swing.JTable;



public abstract class JListScrolling
{
    private JListScrolling()
    {
    }


    public static Rectangle getRowBounds(JList list, int row)
    {
        checkRow(list, row);

        Rectangle result = list.getCellBounds(row, row);
        Insets i = list.getInsets();

        result.x = i.left;
        result.width = list.getWidth() - i.left - i.right;

        return result;
    }

    public static Rectangle getRowBounds(JList list, int first, int last)
    {
        checkRows(list, first, last);

        Rectangle result = list.getCellBounds(first, -1);
        result = result.union(list.getCellBounds(last, -1));
        Insets i = list.getInsets();

        result.x = i.left;
        result.width = list.getWidth() - i.left - i.right;

        return result;
    }

    private static void checkRows(JTable table, int first, int last)
    {
        if (first < 0)
            throw new IndexOutOfBoundsException(first+" < 0");
        if (first > last)
            throw new IndexOutOfBoundsException(first+" > "+last);
        if (last >= table.getRowCount())
            throw new IndexOutOfBoundsException(last+" >= "+table.getRowCount());
    }    
    public static void makeRowVisible(JList list, int row)
    {
        Scrolling.scrollVertically(list, getRowBounds(list, row));
    }

    public static void makeRowsVisible(JList list, int first, int last)
    {
        Scrolling.scrollVertically(list, getRowBounds(list, first, last));
    }


    public static void makeRowsVisible(JList list, int first, int last, int bias)
    {
        Scrolling.scrollVertically(list, getRowBounds(list, first, last), bias);
    }


    private static void checkRow(JList list, int row)
    {
        
        if (row < 0)
            throw new IndexOutOfBoundsException(row+" < 0");
        if (row >= list.getModel().getSize())
            throw new IndexOutOfBoundsException(row+" >= "+list.getModel().getSize());
    }

    private static void checkRows(JList list, int first, int last)
    {
        if (first < 0)
            throw new IndexOutOfBoundsException(first+" < 0");
        if (first > last)
            throw new IndexOutOfBoundsException(first+" > "+last);
        if (last >= list.getModel().getSize() )
            throw new IndexOutOfBoundsException(last+" >= "+list.getModel().getSize());
    }

}
