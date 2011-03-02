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

/**
 *
 * @author Thiago
 */
public class StringUtil {

    public static String xmlFilter(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#39;");
                    break;
                default:
                    result.append(value.charAt(i));
            }
        }
        return result.toString();
    }

    public static String leftPad(String originalText, int length, char fillChar) {
        StringBuilder sb = new StringBuilder();
        length = length - originalText.length();
        for (int i = 0; i < length; i++) {
            sb.append(fillChar);
        }
        sb.append(originalText);
        return sb.toString();
    }

    public static String rightPad(String originalText, int length, char fillChar) {
        StringBuilder sb = new StringBuilder();
        length = length - originalText.length();
        sb.append(originalText);
        for (int i = 0; i < length; i++) {
            sb.append(fillChar);
        }
        return sb.toString();
    }
    
    public static String join(String[] text, String delimiter) {
        StringBuilder sb = new StringBuilder();
        boolean insertDelimiter = false;
        for (String s : text) {
            if (insertDelimiter) {
                sb.append(delimiter);
            }
            sb.append(s);
            insertDelimiter = true;
        }
        return sb.toString();
    }

    public static String surround(String text, String delimiter) {
        return delimiter + text + delimiter;
    }

    public static String[] surroundAll(String[] text, String delimiter) {
        for (int i = 0; i < text.length; i++) {
            text[i] = surround(text[i], delimiter);
        }
        return text;
    }
    
}
