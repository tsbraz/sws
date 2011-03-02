/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2009 SACI Informática Ltda.
 */

package saci.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitária para conversão de dados
 * 
 * @author Thiago
 */
public class Types {

    private static final String INTEGER_REGEXP = "[^0-9]";
    private static String datePattern = "yyyy-MM-dd";
    private static final Map<Class<?>, Boolean> printableMap = new HashMap<Class<?>, Boolean>();

    static {
        printableMap.put(String.class, true);
        printableMap.put(Boolean.class, true);
        printableMap.put(boolean.class, true);
        printableMap.put(Byte.class, true);
        printableMap.put(byte.class, true);
        printableMap.put(Short.class, true);
        printableMap.put(short.class, true);
        printableMap.put(Integer.class, true);
        printableMap.put(int.class, true);
        printableMap.put(Float.class, true);
        printableMap.put(float.class, true);
        printableMap.put(Long.class, true);
        printableMap.put(long.class, true);
        printableMap.put(Double.class, true);
        printableMap.put(double.class, true);
        printableMap.put(BigDecimal.class, true);
        printableMap.put(BigInteger.class, true);
        printableMap.put(Date.class, true);
    }

    /**
     * Verifica se a classe passada por parametro é "imprimível" (tipo
     * primitivo, String, Date, BigDecimal e BigInteger)
     * 
     * @param value
     *            a classe que deve ser testada
     * @return
     */
    public static boolean isPrintable(Class<?> value) {
        return printableMap.containsKey(value);
    }

    /**
     * Altera o padrão de formatação de data, padrão yyyy-MM-dd
     * 
     * @param pattern
     *            o novo padrão para formatação de datas
     */
    public static void setDatePattern(String pattern) {
        datePattern = pattern;
    }

    /**
     * Executa um trim na string, se for nulo retorna ""
     * 
     * @param o
     * @return executa um trim na string, se for nulo retorna ""
     */
    public static String trim(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    /**
     * Verifica se a string a nula ou vazia
     * 
     * @param s
     *            a string a ser testada
     * @return
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Transforma a string passada por parametro para Boolean
     * 
     * @param b
     * @return
     */
    public static Boolean parseBoolean(String b) {
        if (!isNullOrEmpty(b)) {
            return new Boolean(b.equals("S") || b.equals("true"));
        } else {
            return null;
        }
    }

    /**
     * Transforma a string passada por parametro para Byte
     * 
     * @param i
     * @return
     */
    public static Byte parseByte(String i) {
        i = prepareInteger(i);
        if (!isNullOrEmpty(i)) {
            return new Byte(i);
        } else {
            return null;
        }
    }

    /**
     * Transforma a string passada por parametro para Short
     * 
     * @param i
     * @return
     */
    public static Short parseShort(String i) {
        i = prepareInteger(i);
        if (!isNullOrEmpty(i)) {
            return new Short(i);
        } else {
            return null;
        }
    }

    public static Character parseChar(String s) {
        if (!isNullOrEmpty(s)) {
            return new Character(s.charAt(0));
        }
        return null;
    }

    /**
     * Transforma a string passada por parametro para Integer
     * 
     * @param i
     * @return
     */
    public static Integer parseInt(String i) {
        i = prepareInteger(i);
        if (!isNullOrEmpty(i)) {
            return new Integer(i);
        } else {
            return null;
        }
    }

    /**
     * Transforma a string passada por parametro para BigInteger
     * 
     * @param d
     * @return
     */
    public static BigInteger parseBigInteger(String d) {
        d = prepareInteger(d);
        if (!isNullOrEmpty(d)) {
            BigInteger v = new BigInteger(d);
            return v;
        } else {
            return null;
        }
    }

    /**
     * Transforma a string passada por parametro para Long
     * 
     * @param l
     * @return
     */
    public static Long parseLong(String l) {
        l = prepareInteger(l);
        if (!isNullOrEmpty(l)) {
            return new Long(l);
        } else {
            return null;
        }
    }

    /**
     * Transforma a string passada por parametro para Float
     * 
     * @param d
     * @return
     */
    public static Float parseFloat(String d) {
        d = prepareDecimal(d);
        if (!isNullOrEmpty(d)) {
            Float v = new Float(d);
            return v;
        } else {
            return null;
        }
    }

    /**
     * Transforma a string passada por parametro para Double
     * 
     * @param d
     * @return
     */
    public static Double parseDouble(String d) {
        d = prepareDecimal(d);
        if (!isNullOrEmpty(d)) {
            Double v = new Double(d);
            return v;
        } else {
            return null;
        }
    }

    /**
     * Transforma a string passada por parametro para BigDecimal
     * 
     * @param d
     * @return
     */
    public static BigDecimal parseBigDecimal(String d) {
        d = prepareDecimal(d);
        if (!isNullOrEmpty(d)) {
            BigDecimal v = new BigDecimal(d);
            return v;
        } else {
            return null;
        }
    }

    static String prepareInteger(String s) {
        if (!isNullOrEmpty(s)) {
            boolean neg = s.startsWith("-");
            s = s.replaceAll(INTEGER_REGEXP, "");
            return neg ? "-" + s : s;
        }
        return null;
    }

    static String prepareDecimal(String s) {
        if (!isNullOrEmpty(s)) {
            boolean neg = s.startsWith("-");
            int decimalIndex = Math.max(s.lastIndexOf("."), s.lastIndexOf(","));
            String s1 = s.substring(0, decimalIndex).replaceAll(INTEGER_REGEXP, "");
            String s2 = s.substring(decimalIndex).replaceAll(INTEGER_REGEXP, "");
            s = s1 + "." + s2;
            return neg ? "-" + s : s;
        }
        return null;
    }
    
    /**
     * Transforma a string passada por parametro para Date
     * 
     * @see Types#setDatePattern(java.lang.String)
     * @param d
     * @return
     */
    public static Date parseDate(String d) {
        if (!isNullOrEmpty(d)) {
            ParsePosition pp = new ParsePosition(0);
            return new SimpleDateFormat(datePattern).parse(d, pp);
        } else {
            return null;
        }
    }

    /**
     * Transforma a data passada por parametro para String
     * 
     * @see Types#setDatePattern(java.lang.String)
     * @param d
     * @return
     */
    public static String parseString(Date d) {
        if (d != null) {
            return new SimpleDateFormat(datePattern).format(d);
        } else {
            return null;
        }
    }

    /**
     * Retorna o Byte no tipo primitivo byte, caso nulo, retorna 0.
     * 
     * @param d
     * @return
     */
    public static byte byteValue(Byte d) {
        return d != null ? d.byteValue() : 0;
    }

    /**
     * Retorna o Short no tipo primitivo short, caso nulo, retorna 0.
     * 
     * @param d
     * @return
     */
    public static short shortValue(Short d) {
        return d != null ? d.shortValue() : 0;
    }

    /**
     * Retorna o Integer no tipo primitivo int, caso nulo, retorna 0.
     * 
     * @param d
     * @return
     */
    public static int intValue(Integer d) {
        return d != null ? d.intValue() : 0;
    }

    /**
     * Retorna o Long no tipo primitivo long, caso nulo, retorna 0.
     * 
     * @param d
     * @return
     */
    public static long longValue(Long d) {
        return d != null ? d.longValue() : 0;
    }

    /**
     * Retorna o Float no tipo primitivo float, caso nulo, retorna 0.
     * 
     * @param d
     * @return
     */
    public static float floatValue(Float d) {
        return d != null ? d.floatValue() : 0;
    }

    /**
     * Retorna o Double no tipo primitivo double, caso nulo, retorna 0.
     * 
     * @param d
     * @return
     */
    public static double doubleValue(Double d) {
        return d != null ? d.doubleValue() : 0;
    }

    /**
     * Retorna o Boolean no tipo primitivo boolean, caso nulo, retorna false.
     * 
     * @param d
     * @return
     */
    public static boolean booleanValue(Boolean d) {
        return d != null ? d.booleanValue() : false;
    }

    /**
     * Retorna o objeto passado por parametro em um objeto da classe do
     * parametro <i>to</i>.
     * <p>
     * Transforma os seguintes tipos:
     * <p>
     * String, Byte, Short, Integer, Long, Float, Double, BigInteger,
     * BigDecimal, Date, Boolean
     * <p>
     * RuntimeException caso não seja nenhuma das classes reconhecidas
     * 
     * @param <T>
     * @param o
     *            O objeto que deve ser transformado
     * @param to
     *            A classe na qual o objeto deve ser transformado
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o, Class<T> to) {
        String s = o == null ? "" : o.toString();
        T result = null;
        if (to.equals(String.class)) {
            return (T) s;
        } else if (to.equals(Byte.class) || to.equals(Byte.TYPE)) {
            result = castByte(to, s);
        } else if (to.equals(Short.class) || to.equals(Short.TYPE)) {
            result = castShort(to, s);
        } else if (to.equals(Integer.class) || to.equals(Integer.TYPE)) {
            result = castInteger(to, s);
        } else if (to.equals(Long.class) || to.equals(Long.TYPE)) {
            result = castLong(to, s);
        } else if (to.equals(Float.class) || to.equals(Float.TYPE)) {
            result = castFloat(to, s);
        } else if (to.equals(Double.class) || to.equals(Double.TYPE)) {
            result = castDouble(to, s);
        } else if (to.equals(BigInteger.class)) {
            return (T) parseBigInteger(s);
        } else if (to.equals(BigDecimal.class)) {
            return (T) parseBigDecimal(s);
        } else if (to.equals(Date.class)) {
            result = castDate(o, s);
        } else if (to.equals(Boolean.class) || to.equals(Boolean.TYPE)) {
            result = castBoolean(to, s);
        } else if (to.equals(InputStream.class) || to.equals(byte[].class)) {
            result = castInputStream(o, to.equals(byte[].class));
        } else {
            throw new RuntimeException("Unknown cast type " + to.getName());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castInputStream(Object o, boolean isByteArray) {
        T result = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            IOUtil.echo((InputStream)o, bout);
            if (isByteArray) {
                result = (T) bout.toByteArray();
            } else {
                result = (T) new ByteArrayInputStream(bout.toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castBoolean(Class<T> to, String s) {
        T result;
        result = (T) parseBoolean(s);
        if (result == null && to.isPrimitive()) {
            result = (T) Boolean.FALSE;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castDate(Object o, String s) {
        T result;
        if (o instanceof Date) {
            result = (T) o;
        } else {
            result = (T) parseDate(s);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castDouble(Class<T> to, String s) {
        T result;
        result = (T) parseDouble(s);
        if (result == null && to.isPrimitive()) {
            result = (T) new Double((short) 0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castFloat(Class<T> to, String s) {
        T result;
        result = (T) parseFloat(s);
        if (result == null && to.isPrimitive()) {
            result = (T) new Float((short) 0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castLong(Class<T> to, String s) {
        T result;
        result = (T) parseLong(s);
        if (result == null && to.isPrimitive()) {
            result = (T) new Long((short) 0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castInteger(Class<T> to, String s) {
        T result;
        result = (T) parseInt(s);
        if (result == null && to.isPrimitive()) {
            result = (T) new Integer((short) 0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castShort(Class<T> to, String s) {
        T result;
        result = (T) parseShort(s);
        if (result == null && to.isPrimitive()) {
            result = (T) new Short((short) 0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castByte(Class<T> to, String s) {
        T result;
        result = (T) parseByte(s);
        if (result == null && to.isPrimitive()) {
            result = (T) new Byte((byte) 0);
        }
        return result;
    }

}
