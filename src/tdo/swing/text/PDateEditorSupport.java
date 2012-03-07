/*
 * PDateEditorSupport.java
 *
 * Created on 30 Октябрь 2006 г., 14:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tdo.swing.text;

//import java.awt.TextComponent;
import java.awt.AWTEvent;
import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;
/**
 *
 * <p>Title: Filis Application</p>
 * <p>Description: Freq Sensor's Support</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: IS</p>
 * @author VNS
 * @version 1.0
 *
 * Класс обеспечивает свойства и методы для облегчения редактирования
 * и обработки данных типа <code>java.sql.Timestamp</code> в компонентах-
 * наследниках класса <code>java.awt.TextComponent</code>. <p>
 * При разработке класса учитывались следующие особенности обработки
 * клавиатурного ввода в <code>java.awt</code> и, в частности,
 * классом <code>java.awt.TextComponent</code> и его наследниками:

 * <OL>
 *   <LI>В обработке ввода символа принимают участие три обработчика событий
 *       компонента <code>java.awt.TextComponent</code>, причем в следующем
 *       порядке:
 *       <UL>
 *          <LI>KeyPressed;
 *          </LI>
 *          <LI>KeyTyped;
 *          </LI>
 *          <LI>KeyReleased;
 *          </LI>
 *       </UL>
 *   </LI>
 *   <LI>
 *      В обработчика <code>KeyTyped</code> и именно здесь
 *      имеется возможность отказаться от ввода символа вызовом метода
 *      <code>consume()</code>, поскольку реально еще символ не размещен
 *      в поле <code>text</code> компонента <code>TextСomponent</code>.
 *   </LI>
 *   <LI>
 *      Код, реализующий обработчик <code>keyTyped</code>
 *      может установить, что введенный символ корректен, но после его
 *      ввода требуется изменить позицию каретки <i>НЕ</i> в позиции по
 *      умолчанию, т.е. в текущем положении каретки а в другом - вычисленном
 *      программно.<p>
 *   </LI>
 *   <LI>
 *      Код, реализующий обработчик <code>keyTyped</code> может установить,
 *      что введенный символ корректен, но разместить
 *      его необходимо <i>НЕ</i> в позиции по умолчанию, т.е. в текущем положении
 *      каретки а в другом - вычисленном программно.<p>
 *      Для указания нового положения используется свойство
 *      <code>newCaretPosition</code> как указано выше. Однако, для размещения
 *      введенного символа в другой позиции необходимо воспользоваться
 *      методом <code>setText</code> класса <code>TextComponent</code>. Такая
 *      операция должна выполняться в обработчике <code>KeyReleased</code>.
 *    </LI>
 *   <LI>
 *
 *   </LI>
 * </OL>
 * Помимо перечисленных выше особенностей существуют и другие, например :
 * <OL>
 *   <LI>
 *      Клавиши <code>Delete , BackSpace, Esc , Enter </code> также возбуждают
 *      событие требующее <code>keyTyped</code>.
 *   </LI>
 * </OL>
 */
public class PDateEditorSupport implements java.awt.event.TextListener
{
  TextComponent component;
  private java.awt.AWTEvent initEvent = null;
  private char initChar;
  private String initText = "  .  .  ";

  private PDateField dateField[] = new PDateField[3];
  private char[] mask;
  private char fieldSeparator = '.';

  private char[] textChars;
  private char[] newTextChars;
  private int newCaretPosition = -1;
  private boolean consumed = false;
  private boolean newTextRequired = false;

//    private int caretPosition = 0;

  PDateField dayField;
  PDateField monthField;
  PDateField yearField;


  public PDateEditorSupport() {
  }
  public PDateEditorSupport(TextComponent component, String editMask) {
    this.component = component;
    textChars = this.component.getText().toCharArray();
    newTextChars = this.component.getText().toCharArray();

    this.mask   = editMask.toCharArray();
    this.fieldSeparator = defineFieldSeparator();

    int dayIndex  = editMask.indexOf('d');
    int monIndex  = editMask.indexOf('M');
    int yearIndex = editMask.indexOf('y');
    int min = Math.min(dayIndex,monIndex);
    min = Math.min(min, yearIndex);
    if ( dayIndex == min) {
      dayIndex = 0;
      if ( monIndex < yearIndex ) {
        monIndex  = 1;
        yearIndex = 2;
      } else {
        monIndex  = 2;
        yearIndex = 1;
      }
    } else
    if ( monIndex == min) {
      monIndex = 0;
      if ( dayIndex < yearIndex ) {
        dayIndex  = 1;
        yearIndex = 2;
      } else {
        dayIndex  = 2;
        yearIndex = 1;
      }

    }
    if ( yearIndex == min) {
      yearIndex = 0;
      if ( monIndex < dayIndex ) {
        monIndex  = 1;
        yearIndex = 2;
      } else {
        monIndex  = 2;
        yearIndex = 1;
      }

    }



    dayField   = new PDateField('d',editMask, fieldSeparator, dayIndex);
    dayField.setOwner(this);
    monthField = new PDateField('M',editMask, fieldSeparator, monIndex);
    monthField.setOwner(this);

    yearField  = new PDateField('y',editMask, fieldSeparator, yearIndex);
    yearField.setOwner(this);

    dateField[dayIndex]   =  dayField;
    dateField[monIndex]   =  monthField;
    dateField[yearIndex]  =  yearField;

  }
  protected char defineFieldSeparator() {
    char s = '.';
    int n = 0;
    for ( int i = 0; i < mask.length; i++ ) {
      if ( mask[i] == 'd' || mask[i] == 'M' || mask[i] == 'y' ) {
        s = mask[i];
        n = i;
        break;
      }
    }

    for ( int i = n; i < mask.length; i++ ) {
      if ( mask[i] != s  ) {
        s = mask[i];
        break;
      }
    }

    return s;

  }
  public void setTextChars(char[] textChars) {
    this.textChars = textChars;
  }

  public void setNewTextChars(char[] newTextChars) {
    this.newTextChars = newTextChars;
  }

  public void setEditMask(String editMask) {
    this.mask   = editMask.toCharArray();
    dayField.setEditMask(   'd', editMask, fieldSeparator);
    monthField.setEditMask( 'M', editMask, fieldSeparator);
    yearField.setEditMask(  'y', editMask, fieldSeparator);
  }
  public char getFieldSeparator() {
    return this.fieldSeparator;
  }

  public void setFieldSeparator(char fieldSeparator) {
   this.fieldSeparator = fieldSeparator;
  }

  public char[] getTextChars() {
    return this.textChars;
  }
  public char[] getNewTextChars() {
    return this.newTextChars;
  }
  public PDateField getLast() {
    return this.dateField[dateField.length -1];
  }
/*  public PDateField last() {
    return this.dateField[dateField.length -1];
  }
*/
  public PDateField getFirst() {
    return this.dateField[0];
  }

  public PDateField getNext() {
    PDateField f = getField();
    if ( f == null )
      return getLast();
    else
      return f.next();
  }
  public PDateField getPrevious() {
    PDateField f = getField();
    if ( f == null )
      return getFirst();
    else
    if ( component.getCaretPosition() >= component.getText().length() )
      return f.next();
    else
      return f.previous();

  }

  public void selectCurrent() {
    PDateField f = getField();
    if ( f == null )
      component.selectAll();
    else
      component.select(f.getPosition(),f.getPosition()+f.getLength());
  }
  public void selectPrevious() {
    PDateField f = getPrevious();
    if ( f == null )
      component.selectAll();
    else
      component.select(f.getPosition(),f.getPosition()+f.getLength());
  }
  public void selectNext() {
    PDateField f = getNext();
    if ( f == null )
      component.selectAll();
    else
      component.select(f.getPosition(),f.getPosition()+f.getLength());
  }
  public void selectFirst() {
    PDateField f = getFirst();
    if ( f == null )
      component.selectAll();
    else
      component.select(f.getPosition(),f.getPosition()+f.getLength());
  }
  public void selectLast() {
    PDateField f = getLast();
    if ( f == null )
      component.selectAll();
    else
      component.select(f.getPosition(),f.getPosition()+f.getLength());
  }

  public void setConsumed( boolean consumed) {
    this.consumed = consumed;
  }
  /**
   * Проверка корректности ввода символа. <p>
   * Кроме собственно проверки, метод определяет необходимость ....
   *
   * Следующие обстоятельства необходимо иметь ввиду при использовании метода:
   * <OL>
   *   <LI>В обработке ввода символа принимают участие три обработчика событий
   *       компонента <code>java.awt.TextComponent</code>, причем в следующем
   *       порядке:
   *       <UL>
   *          <LI>KeyPressed;
   *          </LI>
   *          <LI>KeyTyped;
   *          </LI>
   *          <LI>KeyReleased;
   *          </LI>
   *       </UL>
   *   </LI>
   *   <LI>
   *      Метод вызывается из обработчика <code>KeyTyped</code>. Именно здесь
   *      имеется возможность отказаться от ввода символа вызовом метода
   *      <code>consume()</code>, поскольку реально еще символ не размещен
   *      в поле <code>text</code> компонента <code>TextXomponent</code>.
   *   </LI>
   *   <LI>
   *      Чтобы заставить компонент вызвать метод <code>consume()</code>
   *      метод вазвращает значение результата обработки <code>false</code>.
   *      Кроме того, значению свойства <code>PDateEditorSupport.consume</code>
   *      присваивается тоже значение, что и возвращаемому результата. Именно
   *      оно должно использоваться в обработчике <code>KeyReleased</code>
   *      для определения того, выдан ли <code>consume()</code> или нет. <p>
   *   </LI>
   *   <LI>
   *      Метод может установить, что введенный символ корректен, но после его
   *      ввода требуется изменить позицию каретки <i>НЕ</i> в позиции по
   *      умолчанию, т.е. в текущем положении каретки а в другом - вычисленном
   *      методом.<p>
   *      Для указания нового положения используется свойство
   *      <code>newCaretPosition</code> объекта типа
   *      <code>PDateEditSupport</code>. Его значение, равное -1 указывает,
   *      что новое положение не задается, а это, в свою очередь указывает
   *      обработчику <code>KeyReleased</code>, что не следует устанавливать
   *      новое значение <code>caretPosition</code>.
   *   </LI>
   *   <LI>
   *      Метод может установить, что введенный символ корректен, но разместить
   *      его необходимо <i>НЕ</i> в позиции по умолчанию, т.е. в текущем положении
   *      каретки а в другом - вычисленном методом.<p>
   *      Для указания нового положения используется свойство
   *      <code>newCaretPosition</code> как указано выше. Однако, для размещения
   *      введенного символа в другой позиции необходимо воспользоваться
   *      методом <code>setText</code> класса <code>TextComponent</code>. Такая
   *      операция должна выполняться в обработчике <code>KeyReleased</code>.
   *      Класс <code>PDateEditorSupport</code> содержит булевое свойство
   *      свойство <code>newTextRequired</code>. Его значение равное
   *      <code>true</code> указывает обработчику <code>KeyReleased</code> на
   *      необходимость установки нового значения свойства <code>text</code>.
   *    </LI>
   *   <LI>
   *
   *   </LI>
   * </OL>
   * @param e событие типа <code>KeyEvent</code>, приведшее к вызову метода.
   * @return тип <code>boolean</code>
   */
  public boolean checkDateField(KeyEvent e) {

    PDateField field = getField();
    consumed = false;
    newTextRequired = false;
    if ( field == null ) {
      setConsumed(true);
      return false;
    }
    //Создадим новый текст, который должен бы появиться при действии по умолчанию
    int sStart = component.getSelectionStart();
    int sEnd   = component.getSelectionEnd();
    if ( sEnd == textChars.length && sStart < sEnd && sStart == 0) {
      if ( ! ( Character.isDigit( e.getKeyChar() ) ||
               e.getKeyChar() == getFieldSeparator() ) ) {
        this.setConsumed(true);
        return false;
      }

      PDateField f = dateField[0];
      newTextChars = new char[textChars.length - f.getLength() + 1];
      int n = 0;
      for (int i=0; i < textChars.length; i++ ) {
        if ( i == f.getPosition() ) {
          newTextChars[n++] = e.getKeyChar();
          continue;
        }
        if ( i > f.getPosition() && i < f.getPosition() + f.getLength() ) {
          continue;
        }
        newTextChars[n++] = textChars[i];
      }
      adjustNewFields(); // проставит значения свойств newPosition и newLength
      this.setNewCaretPosition(1);
      this.setNewTextRequired(true);
      this.setConsumed(true);
      return false;

    }
    newTextChars = new char[textChars.length - sEnd + sStart + 1];
    int n = 0;
    for (int i=0; i < textChars.length; i++ ) {
      if ( i == sEnd )
        newTextChars[n++] = e.getKeyChar();
      if ( i < sStart || i > sEnd - 1) {
        newTextChars[n++] = textChars[i];
      }
    }
    adjustNewFields(); // проставит значения свойств newPosition и newLength
                       // для всех полей с учетом нового текста
    boolean result = field.check(e);
    setConsumed( ! result );
    return result;
  }
  public boolean isConsumed() {
    return true;
  }
  public String getNewText() {
    return String.copyValueOf(this.newTextChars);
  }
  public int getNewCaretPosition() {
    return this.newCaretPosition;
  }
  public void setNewCaretPosition( int newCaretPosition) {
    this.newCaretPosition = newCaretPosition;
  }
  public boolean isNewTextRequired() {
    return this.newTextRequired;
  }
  public void setNewTextRequired(boolean newTextRequired) {
    this.newTextRequired = newTextRequired;
  }
  public PDateField getField() {
    return getField(this.component.getCaretPosition());
  }
  public PDateField[] getDateField() {
    return this.dateField;
  }
  public PDateField getField(int pos) {
    if ( dayField.isCaretIn(pos) )
      return dayField;
    if ( monthField.isCaretIn(pos) )
      return monthField;
    if ( yearField.isCaretIn(pos) )
      return yearField;
    return getLast();
  }
  public PDateField getField(char fieldType) {
    PDateField result = null;
    if ( fieldType == 'd' )
      result = dayField;
    else
    if ( fieldType == 'M' )
      result = monthField;
    else
    if ( fieldType == 'y' )
      result = yearField;
    return result;
  }

  ////////// TextListener /////////////////////////////
  public void textValueChanged(TextEvent e ) {
    this.textValueChanged();
  }
  public void textValueChanged() {
    if ( initEvent != null && initEvent instanceof KeyEvent &&
         ((KeyEvent)initEvent).getKeyChar() !=' ' ) {
      initEvent = null;
      textChars = initText.toCharArray();
      this.adjustFields(textChars);
      checkInitFields();
      if ( this.isNewTextRequired() )
        component.setText(getNewText());
      else {
        component.setText(initText);
        component.selectAll();
      }
//      this.component.setText( Character.toString( initChar ));
    }

    textChars = this.component.getText().toCharArray();
    newTextChars = this.component.getText().toCharArray();
    adjustFields();
  }

  private boolean checkInitFields() {
    this.setNewTextRequired(false);
    if ( ! ( Character.isDigit( initChar ) ||
             initChar == getFieldSeparator() ) ) {
      this.setConsumed(true);
      return false;
    }

    PDateField f = dateField[0];
    newTextChars = new char[textChars.length - f.getLength() + 1];
    int n = 0;
    for (int i=0; i < textChars.length; i++ ) {
      if ( i == f.getPosition() ) {
        newTextChars[n++] = initChar;
        continue;
      }
      if ( i > f.getPosition() && i < f.getPosition() + f.getLength() ) {
        continue;
      }
      newTextChars[n++] = textChars[i];
    }
    adjustNewFields(); // проставит значения свойств newPosition и newLength
    this.setNewCaretPosition(1);
    this.setNewTextRequired(true);
    this.setConsumed(true);
    return true;
  }

  protected void adjustFields() {
    adjustFields(textChars);
  }

  protected void adjustFields(char[] text) {

    int pos = 0;
    int len = 0;
    int n = 0;
    int i = 0;

    while ( i < text.length ) {
      if ( i == text.length - 1 ) {
        len++;
      }
      if (text[i] == fieldSeparator || i == text.length - 1) {
        dateField[n].setPosition(pos);
        dateField[n].setLength(len);
        n++;
        len = 0;
        pos = i + 1;
      } else {
        len++;
      }
      i++;

    }//while

  }

  public void adjustNewFields() {
    adjustNewFields(newTextChars);
  }

  protected void adjustNewFields(char[] text) {

    int pos = 0;
    int len = 0;
    int n = 0;
    int i = 0;

    while ( i < text.length ) {
      if ( i == text.length - 1 ) {
        len++;
      }
      if (text[i] == fieldSeparator || i == text.length - 1) {
        dateField[n].setNewPosition(pos);
        dateField[n].setNewLength(len);
        n++;
        len = 0;
        pos = i + 1;
      } else {
        len++;
      }
      i++;

    }//while

  }
  public void setInitEvent( AWTEvent initEvent) {
    this.initEvent = initEvent;
  }
  public char getInitChar() {
    return this.initChar;
  }
  public void setInitChar( char initChar ) {
    this.initChar = initChar;
  }
  public String getInitText() {
    return this.initText;
  }
  public void setInitText( String initText ) {
    this.initText = initText;
  }

}