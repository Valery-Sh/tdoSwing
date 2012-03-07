/*
 * DataUtil.java
 * 
 * Created on 21.09.2007, 11:59:50
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tdo.swing.util;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.*;
import java.util.Date;
import tdo.DataColumn;
import tdo.DataColumn.PDBBigIntColumn;
import tdo.DataColumn.PDBDateColumn;
import tdo.DataColumn.PDBDecimalColumn;
import tdo.DataColumn.PDBDoubleColumn;
import tdo.DataColumn.PDBIntegerColumn;
import tdo.DataColumn.PDBNumberColumn;
import tdo.DataColumn.PDBRealColumn;
import tdo.DataColumn.PDBSmallIntColumn;
import tdo.DataColumn.PDBTimeColumn;
import tdo.DataColumn.PDBTimestampColumn;
import tdo.DataColumn.PDBTinyIntColumn;

/**
 *
 * @author valery
 */
public class DataUtil {
    
    public static String toString( DataColumn column, Object value ) {
        return toString(column,value, null);
    }
    
    public static String toString(DataColumn column,Object value, String pattern ) {
        if ( column instanceof PDBNumberColumn ) {
            return toDecimalString( column, value, pattern, '.',' ');
        } else if ( column instanceof PDBDateColumn ) {
            return toDateString(column,value,pattern);
        }  else if ( column instanceof PDBTimestampColumn ) {
            return toTimestampString(column,value,pattern);
        }  else if ( column instanceof PDBTimeColumn ) {
            return "0";
        }
        return value == null ? "" : value.toString();
        //return valueToString( value, pattern,getDecimalSeparator(),getGroupingSeparator());
    }
    
    public static Object valueOf(DataColumn column,String value) {
        return valueOf(column,value,'.');
    }
    
  
    public static Object valueOf(DataColumn column,
                                    String value,
                                    char decimalSeparator ) 
    {
        if ( column instanceof PDBNumberColumn ) {
            return decimalValueOf( column, value, decimalSeparator);
        } else if ( column instanceof PDBDateColumn ) {
            return dateValueOf(column,value);
        }  else if ( column instanceof PDBTimestampColumn ) {
            return timestampValueOf(column,value);
        }  else if ( column instanceof PDBTimeColumn ) {
            return timeValueOf(column,value);
        }

        return value;
    }

    public static Object decimalValueOf(DataColumn column,String value,
                                char decimalSeparator) {
        Object r = null;
        
        String plain = "0";
        if ( value != null )
            plain = parseDecimal(value,decimalSeparator);
        
        if ( column instanceof PDBBigIntColumn ) {
            r = Long.valueOf(plain);
        } else if ( column instanceof PDBDecimalColumn ) {
            r = new BigDecimal(plain);
        }  else if ( column instanceof PDBDoubleColumn ) {
            r = Double.valueOf(plain);
        }  else if ( column instanceof PDBRealColumn ) {
            r = Float.valueOf(plain);
        } else if ( column instanceof PDBIntegerColumn ) {
            r = Integer.valueOf(plain);
        } else if ( column instanceof PDBSmallIntColumn ) {
            r = Short.valueOf(plain);
        }  else if ( column instanceof PDBTinyIntColumn ) {
            r = Byte.valueOf(plain);
        }  
        
        return r;
    }
    public static Timestamp timestampValueOf(DataColumn column,String value,
                            DateFormat df) {
        Date d = dateValueOf(column,value,df);
        return new Timestamp(d.getTime());
    }
    public static Timestamp timestampValueOf(DataColumn column,String value) {
        return timestampValueOf(column,value,new SimpleDateFormat("dd.MM.yy"));
    }
    public static Timestamp timestampValueOf(DataColumn column,String value,
            String pattern) {
        return timestampValueOf(column,value,pattern);
    }
    
    public static Date dateValueOf(DataColumn column,String value,
                            DateFormat df) {
        java.util.Date dt;
        try {
            dt = df.parse(value.trim());
//            dt = new Date(dt.getTime());
        } catch (ParseException e) {
            dt = new Date( 0 );
        }
        return dt;
    }
    public static Date dateValueOf(DataColumn column,String value) {
        return dateValueOf(column,value,new SimpleDateFormat("dd.MM.yy"));        
    }

    public static Date dateValueOf(DataColumn column,String value,
                            String pattern) {
        java.util.Date dt;
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern(pattern != null ? pattern : "dd.MM.yy");
        try {
            dt = df.parse(value.trim());
//            dt = new Date(dt.getTime());
        } catch (ParseException e) {
            dt = new Date( 0 );
        }
        return dt;
    }
    
    public static Time timeValueOf(DataColumn column, String value) {
       if ( value == null )
          return new Time(0);
       return Time.valueOf(value);
    }

    public static String toDecimalString(DataColumn column,
                                    Object value,
                                    String pattern,
                                    char decimalSeparator,
                                    char groupingSeparator) {
        Object obj = value;
        
        if ( pattern != null ) {
            if ( value == null )
                obj = column.blankValueInstance(); // cannot be null
            DecimalFormat df = new DecimalFormat();
            df.applyPattern(pattern);
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setDecimalSeparator(decimalSeparator);
            dfs.setGroupingSeparator(groupingSeparator);
            df.setDecimalFormatSymbols(dfs);
            
            return df.format(obj);
            
            //return getDecimalFormat().format(((Long)obj).longValue() );
        }
        if ( value == null )  {
            if ( column.isNullable() )
                return null;
            else
                return "0";
        }
        
        return value.toString();
    }

    public static String toDateString(DataColumn column,Object value, String pattern ) {
        Object o = value;
        if ( o == null )
            o = new Date(0);
        
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        
        if ( pattern != null ) {
            df.applyPattern(pattern);
        }
        
        if ( ((Date)o).getTime() == 0 ) {
            String s = df.toPattern();
            s = s.replace('d',' ');
            s = s.replace('M',' ');
            return  s.replace('y',' ');
        }
        
        return df.format((Date)o );
        
    }
    
    public static String toTimestampString(DataColumn column,Object value, String pattern ) {
        Object o = value;
        if ( o == null )
            o = new Timestamp(0);
        
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        
        if ( pattern != null ) {
            df.applyPattern(pattern);
        }
        
        if ( ((Timestamp)o).getTime() == 0 ) {
            String s = df.toPattern();
            s = s.replace('d',' ');
            s = s.replace('M',' ');
            return  s.replace('y',' ');
        }
        
        return df.format((Timestamp)o );
        
    }
    

    
    public static String toPlainString(BigDecimal bd) {
        String s = bd.toString();
        return toPlainString(s);
    }
    
    public static String toPlainString(String s) {
        StringBuffer sb;
        String unscale;
        int scale;
        int pointPos;
        int expPos = s.indexOf('E');
        
        if ( expPos >= 0 ) {
            int sc = expPos+1;
            char scaleSign = s.charAt(sc);
            if ( s.charAt(sc) == '+' || s.charAt(sc) == '-') {
                sc++;
            }
            scale = Integer.parseInt( s.substring(sc) ); // scale value
            if ( scaleSign == '-')
                scale = -scale;
            
            if ( scale == 0 )
                return s.substring(0,expPos);
            
            pointPos = s.indexOf('.');  // dec point position
            
            
            sb = new StringBuffer(s.substring(0, expPos).length() - 1);
            
            char[] c = s.toCharArray();
            int p = 0;
            boolean hasSign  = ( c[0] == '+' || c[0] == '-' );
            int signPos = ( c[0] == '+' || c[0] == '-' ) ? 0 : -1;
            while ( p < expPos ) {
                if ( p != pointPos && p != signPos )
                    sb.append(c[p]);
                p++;
            }//while
            
            int last = expPos;
            if ( pointPos < 0 ) {
                pointPos = expPos; // no dec point => assume it's position after last digit
                expPos++;
            }
            
            StringBuilder sbr = new StringBuilder(); //result buffer
            
            int newPointPos = pointPos + scale; // may be negative
            
            if ( newPointPos <= 0 ) {
                sbr.append('0');
                sbr.append('.');
                
                if ( newPointPos < 0 ) {
                    for ( int i =0; i < Math.abs(newPointPos); i++ ) {
                        sbr.append('0');
                        
                    }
                }
                sbr.append(sb.toString());
            } else {
                sbr.append(sb.toString());
                
                if ( newPointPos < expPos - 1 )
                    sbr.insert(newPointPos,'.');
                else {
                    for ( int i = expPos; i <= newPointPos; i++)
                        sbr.append('0');
                }
            }
            if ( signPos == 0 )
                sbr.insert( 0,c[0]);
            
            return sbr.toString();
        }
        return s;
    }

    /**
     * Преобразование объекта в строковое представление десятичного числа.<p>
     * <UL>
     *    <li>Преобразует значение параметра value в String, используя toString() метод.</li>
     *    <li>Создается новая строка из символов полученных на предыдущем шаге. Она содержит правильное строковое представление
     *        десятичного числа, т.е. содержит знак числа ( если есть в исходной строке ), цифры и десятичную точку, если она есть в исходной строке.
     *        Если исходная строка имеет нулевую длину, либо не содержит ни одной цифры, то итговая строка имеет вид "0.0".
     *    </li>
     * </UL>
     * Необходимо отметить, что метод не вызывает исключительных ситуаций и, если исходная строка
     * не может быть представлена числовым значением, то возвращает "0.0".
     * TODO: Возможно необходимо пересмотреть такое поведение.
     * @param value - исходный объект;
     * @param decimalSeparator - знак, используемый как разделитель целой и дробной частей;
     * @param groupingSeparator - знак используемый для разделения групп.
     * @return - преобразованное строковое значение объекта value.
     */
    public static String parseDecimal( Object value,
            char decimalSeparator) {
        if ( value == null )
            return "0";
        
        String s = value.toString().trim();
        int ln = s.length();
        if ( ln == 0 )
            return "0"; // ##p Возможно надо не точка, а decimal separator
        if ( (s.equals(".") || s.equals(decimalSeparator )) && s.length() == 1 )
            return "0";
        
        StringBuilder sbTarget = new StringBuilder(s);
        int i = s.indexOf(decimalSeparator);
        if ( i >=0 )
            sbTarget.replace(i, i+1, "." );
        s = sbTarget.toString();
        try {
            s = toPlainString(new BigDecimal(s));
        } catch(Exception e) {
            s = "0";
        }
        return s;
 
    }

    /**
     *
     * @param precision
     * @param scale
     */
    public static String createDecimalDisplayFormat(int precision, int scale) {
        int n;// = 0;
        int r;// = 0;
        
        int sz = scale == 0 ? precision : precision - scale - 1;
        StringBuffer sb = new StringBuffer();
        //        if ( scale == 0  ) {
        n = sz / 4;
        r = sz - n * 4;
        if ( n == 0 ) {
            for ( int i = 1; i <= r; i++ )
                sb.append(  i < r ? '#' : '0' );
        } else {
            if ( r > 0 ) {
                for ( int i = 1; i <= r; i++ ) {
                    sb.append( i < r ? '#' : ',');
                }
                
            }
            for ( int i = 1; i <= n; i++ ) {
                appendDecGroup(sb, i == n);
            }
        }
        if ( scale > 0 ) {
            for ( int i = 1; i <= scale; i++ ) {
                if ( i == 1 ) {
                    sb.append('.');
                }
                sb.append( '0');
            }
            
        }
        return sb.toString();
    }
    
    private static void appendDecGroup( StringBuffer sb, boolean isLast) {
        sb.append('#');
        sb.append('#');
        if ( ! isLast ) {
            sb.append('#');
            sb.append(',');
        } else {
            sb.append('0');
        }
    }

}//class DataUtil
