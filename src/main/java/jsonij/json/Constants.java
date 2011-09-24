/**
 * Copyright (C) 2010-2011 J.W.Marsden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package jsonij.json;

/**
 * JSON Constant Definition.
 * 
 * @author openecho
 * @version 1.0.0
 */
public interface Constants {

    /***********************
     * File Constants
     ***********************/
    public static final int BACKSPACE = '\b';
    public static final int TAB = '\t';
    public static final int FORM_FEED = '\f';
    public static final int NEW_LINE = '\n';
    public static final int CARRIAGE_RETURN = '\r';
    public static final int SPACE = ' ';
    public static final int ESCAPE = '\\';
    public static final int QUOTATION_MARK = '\"';
    /***********************
     * JSON Constants
     ***********************/
    public static final int OPEN_ARRAY = '[';
    public static final int OPEN_OBJECT = '{';
    public static final int CLOSE_ARRAY = ']';
    public static final int CLOSE_OBJECT = '}';
    public static final int VALUE_SEPARATOR = ',';
    public static final int NAME_SEPARATOR = ':';
    /************************
     * JSON Numeric Constants
     ************************/
    public static final char[] DIGITS =
            new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final int DECIMAL_POINT = '.';
    public static final int MINUS = '-';
    public static final int PLUS = '+';
    public static final char[] EXPS = new char[]{'e', 'E'};
    /************************
     * JSON String Constants
     ************************/
    public static final int QUOTATION = '"';
    public static final int REVERSE_SOLIDUS = '\\';
    public static final int SOLIDUS_CHAR = '/';
    public static final int BACKSPACE_CHAR = 'b';
    public static final int FORM_FEED_CHAR = 'f';
    public static final int NEW_LINE_CHAR = 'n';
    public static final int CARRIAGE_RETURN_CHAR = 'r';
    public static final int TAB_CHAR = 't';
    public static final int HEX_CHAR = 'u';
    public static final char[] HEXDIGITS =
            new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'};
    /************************
     * Other JSON Types
     ************************/
    public static final String TRUE_STR = "true";
    public static final String FALSE_STR = "false";
    public static final String NULL_STR = "null";
    /************************
     * JPath Constants
     ************************/
    public static final int LEFT_SQUARE_BRACKET = '[';
    public static final int RIGHT_SQUARE_BRACKET = ']';
    public static final int LEFT_PARENTHESIS = '(';
    public static final int RIGHT_PARENTHESIS = ')';
    public static final int ALL_CHAR = '*';
    public static final int LAST_CHAR = '$';
    public static final int EXPRESSION_CHAR = '?';
    public static final int PERIOD_CHAR = '.';
    public static final int CURRENT_ELEMENT_CHAR = '@';
    public static final int COMMA_CHAR = ',';
    public static final int COLON_CHAR = ':';
    public static final int LESS = '<';
    public static final int GREATER = '>';
    public static final int EQUAL = '=';
    public static final int AND = '&';
    public static final int OR = '|';
    public static final String LAST_PREDICATE = "last()";
    public static final String POSITION_PREDICATE = "position()";

}
