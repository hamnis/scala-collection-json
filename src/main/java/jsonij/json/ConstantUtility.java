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

import static jsonij.json.Constants.MINUS;
import static jsonij.json.Constants.PLUS;
import static jsonij.json.Constants.DIGITS;
import static jsonij.json.Constants.HEXDIGITS;

import static jsonij.json.Constants.SPACE;
import static jsonij.json.Constants.TAB;
import static jsonij.json.Constants.CARRIAGE_RETURN;
import static jsonij.json.Constants.NEW_LINE;

public class ConstantUtility {

    public static boolean isDigit(int r) {
        return r >= DIGITS[0] && r <= DIGITS[9];
    }

    public static boolean isDigit(char c) {
        return isDigit((int) c);
    }

    public static boolean isNumeric(int r) {
        if (r == PLUS) {
            // TODO: Make about invalid JSON
            return true;
        }
        return r == MINUS || isDigit(r);
    }

    public static boolean isNumeric(char c) {
        return isNumeric((int) c);
    }

    public static boolean isHexDigit(int r) {
        return ( r >= HEXDIGITS[0] && r <= HEXDIGITS[9] )
                || ( r >= HEXDIGITS[10] && r <= HEXDIGITS[15] )
                || ( r >= HEXDIGITS[16] && r <= HEXDIGITS[21] );
    }

    public static boolean isHexDigit(char c) {
        return isHexDigit((int) c);
    }

    public static boolean isWhiteSpace(int r) {
        return r == SPACE || r == TAB;
    }

    public static boolean isWhiteSpace(char c) {
        return isWhiteSpace((int) c);
    }

    public static boolean isNewLine(int r) {
        return r == CARRIAGE_RETURN || r == NEW_LINE;
    }

    public static boolean isNewLine(char c) {
        return isNewLine((int) c);
    }

    public static boolean isReturn(int r) {
        return r == CARRIAGE_RETURN;
    }

    public static boolean isReturn(char c) {
        return isReturn((int) c);
    }

    public static boolean isLineFeed(int r) {
        return r == NEW_LINE;
    }

    public static boolean isLineFeed(char c) {
        return isLineFeed((int) c);
    }

    public static boolean isValidStringChar(int r) {
        return //( r >= 93 && r <= 1114111 ) || ( r >= 35 && r <= 91 ) || ( r == 32 ) || ( r == 33 );
                r >= 32 && r != 34 && r != 92;
    }
}
