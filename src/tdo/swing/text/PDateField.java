/*
 * PDateField.java
 *
 * Created on 30 Октябрь 2006 г., 14:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.text;

import java.awt.event.*;
/**
 * <p>Title: Filis Application</p>
 * <p>Description: Freq Sensor's Support</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: IS</p>
 * @author VNS
 * @version 1.0
 */

public class PDateField {
  public PDateField() {
  }
  PDateEditorSupport owner;
  private char fieldID;
  private int maskPos;
  private int maskLen;
  private int position;
  private int length;
  private int order;

  private int newPosition;
  private int newLength;

  public PDateField(char fieldID, String editMask, char fieldSeparator,
                    int order) {
    setEditMask(fieldID, editMask, fieldSeparator);
    this.fieldID = fieldID;
    this.order = order;
  }
  public int getLength() {
    return this.length;
  }
  public void setLength(int length) {
    this.length = length;
  }
  public int getPosition() {
    return this.position;
  }
  public void setPosition(int position) {
    this.position = position;
  }
//
  public int getNewLength() {
    return this.newLength;
  }
  public void setNewLength(int newLength) {
    this.newLength = newLength;
  }
  public int getNewPosition() {
    return this.newPosition;
  }
  public void setNewPosition(int newPosition) {
    this.newPosition = newPosition;
  }

//
  public void setOwner( PDateEditorSupport owner ) {
    this.owner = owner;
  }
  public void setEditMask(char fieldID, String editMask, char fieldSeparator) {
    maskPos  = editMask.indexOf(fieldID);
    int n = editMask.indexOf(fieldSeparator,maskPos);
    if ( n == -1 ) {
      n = editMask.indexOf(' ',maskPos);
      if ( n == -1 )
        maskLen = editMask.length() - maskPos;
    } else
      maskLen  = editMask.indexOf(fieldSeparator,maskPos) - maskPos;

  }

  public boolean isCaretIn( int caretPos ) {
    return (caretPos >= position && caretPos <= position + length) ?
        true : false;
  }


  public PDateField previous() {
    if ( this.order == 0 )
      return this;
    return owner.getDateField()[ this.order-1 ];
  }
  public PDateField next() {
    if ( isLast()  )
      return this;
    return owner.getDateField()[ this.order+1 ];
  }

  public int getOldValue() {

    String s = owner.component.getText().substring(maskPos,maskPos + maskLen).trim();
    if ( s.equals("") )
      return 0;
    return Integer.parseInt(s);
  }
  public int getNewValue() {
    String s = String.copyValueOf(owner.getNewTextChars(),newPosition, newLength).trim();
    if ( s.equals("") )
      return 0;
    return Integer.parseInt(s);
  }

  public boolean isLast() {
    return (this.order == owner.getDateField().length - 1) ? true : false;
  }

  public boolean check(KeyEvent e) {
    if ( newLength > maskLen ) {
      if ( isLast() )
        return false;
      PDateField nextField = owner.getDateField()[order+1];
      //Создадим новый текст, который должен бы появиться при действии по умолчанию
      int sStart = owner.component.getSelectionStart();
      int sEnd   = owner.component.getSelectionEnd();
      char[] newTextChars = owner.getNewTextChars();
      newTextChars = new char[owner.getTextChars().length - sEnd +
                                         sStart + 1 - nextField.length];
      owner.setNewTextChars(newTextChars);
      int n = 0;
      for (int i=0; i < owner.getTextChars().length; i++ ) {
        if ( i == nextField.getPosition() ) {
          owner.getNewTextChars()[n++] = e.getKeyChar();
          continue;
        }
        if ( i > nextField.getPosition() &&
             i < nextField.position + nextField.length )
          continue;
        if ( i < sStart || i > sEnd - 1) {
          owner.getNewTextChars()[n++] = owner.getTextChars()[i];
        }
      }
      owner.adjustNewFields(); // проставит значения свойств newPosition и newLength
      owner.setNewTextRequired(true);
      owner.setNewCaretPosition(nextField.getPosition()+1);
      return false;
    }
    int code = e.getKeyChar();
    int caret = owner.component.getCaretPosition();
//    owner.newCaretPosition = caret;
    if ( ! ( Character.isDigit( (char)code ) ||
             code==owner.getFieldSeparator() ) ) {
      return false;
    }

    if ( code == owner.getFieldSeparator() ) {
//        PDateFieldSupport
      if ( owner.getLast().fieldID == this.fieldID ) {
        return false;
      }
      owner.setConsumed(true);
      owner.selectNext();
      return true;
    }
//    owner.getNewTextChars()[owner.component.getCaretPosition()] = e.getKeyChar();
//    owner.newCaretPosition = ++caret;
    boolean result = true;
    switch ( this.fieldID ) {
      case 'd' :
        int val = this.getNewValue();
        if ( val > 31 )
          result = false;
        break;
      case 'M' :
        val = this.getNewValue();
        if ( val > 12 )
          result = false;
        break;
    }
    return result;
  }

}