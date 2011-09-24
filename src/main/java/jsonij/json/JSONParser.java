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

import static jsonij.json.Constants.BACKSPACE;
import static jsonij.json.Constants.BACKSPACE_CHAR;
import static jsonij.json.Constants.OPEN_ARRAY;
import static jsonij.json.Constants.OPEN_OBJECT;
import static jsonij.json.Constants.CARRIAGE_RETURN;
import static jsonij.json.Constants.CARRIAGE_RETURN_CHAR;
import static jsonij.json.Constants.DECIMAL_POINT;
import static jsonij.json.Constants.DIGITS;
import static jsonij.json.Constants.QUOTATION_MARK;
import static jsonij.json.Constants.CLOSE_ARRAY;
import static jsonij.json.Constants.CLOSE_OBJECT;
import static jsonij.json.Constants.ESCAPE;
import static jsonij.json.Constants.EXPS;
import static jsonij.json.Constants.FALSE_STR;
import static jsonij.json.Constants.FORM_FEED;
import static jsonij.json.Constants.FORM_FEED_CHAR;
import static jsonij.json.Constants.HEX_CHAR;
import static jsonij.json.Constants.MINUS;
import static jsonij.json.Constants.NAME_SEPARATOR;
import static jsonij.json.Constants.NEW_LINE;
import static jsonij.json.Constants.NEW_LINE_CHAR;
import static jsonij.json.Constants.NULL_STR;
import static jsonij.json.Constants.PLUS;
import static jsonij.json.Constants.QUOTATION;
import static jsonij.json.Constants.REVERSE_SOLIDUS;
import static jsonij.json.Constants.SOLIDUS_CHAR;
import static jsonij.json.Constants.TAB;
import static jsonij.json.Constants.TAB_CHAR;
import static jsonij.json.Constants.TRUE_STR;
import static jsonij.json.Constants.VALUE_SEPARATOR;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import jsonij.parser.ParserException;
import jsonij.parser.ReaderParser;

/**
 * 
 * 
 * @author J.W.Marsden
 */
public class JSONParser {

    protected Locale locale;

    public JSONParser() {
        locale = Locale.ENGLISH;
    }

    /**
     * @return the locale
     */
    public final Locale getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public final Locale setLocale(Locale locale) {
        return this.locale = locale;
    }

    public final Value parse(java.lang.String targetString) throws IOException, ParserException {
        if (targetString == null) {
            throw new NullPointerException();
        }
        if (targetString.trim().equals("")) {
            throw new JSONParserException("invalidEmpty");
        }
        final JSONReader target = new StringJSONReader(targetString);
        int r = target.peek();
        if (r == -1) {
            throw new JSONParserException("invalidEmpty");
        }
        Value value = null;
        if (r == OPEN_OBJECT) {
            value = parseObject(target);
        } else if (r == OPEN_ARRAY) {
            value = parseArray(target);
        } else {
            throw new JSONParserException("invalidExpecting2", (char) OPEN_OBJECT, (char) OPEN_ARRAY, (char) r);
        }
        if (target.peek() != -1) {
            //throw new JSONParserException("invalidExtraJunk", (char) OPEN_OBJECT, (char) OPEN_ARRAY);
        }
        return value;
    }

    public final Value parse(Reader targetReader) throws IOException, ParserException {
        if (targetReader == null) {
            throw new NullPointerException();
        }
        final JSONReader target = new ReaderJSONReader(targetReader);
        int r = target.peek();
        if (r == -1) {
            throw new JSONParserException("invalidEmpty");
        }
        Value value = null;
        if (r == OPEN_OBJECT) {
            value = parseObject(target);
        } else if (r == OPEN_ARRAY) {
            value = parseArray(target);
        } else {
            throw new JSONParserException("invalidExpecting2", (char) OPEN_OBJECT, (char) OPEN_ARRAY, (char) r);
        }
        if (target.peek() != -1) {
            // TODO: Extra Junk. Add Warning.
        }
        return value;
    }

    public final Value parseValue(java.lang.String targetString) throws IOException, ParserException {
        if (targetString == null) {
            throw new NullPointerException();
        }
        final JSONReader target = new StringJSONReader(targetString);
        return parseValue(target);
    }
    
    public final StatefullValue parseStatefullValue(java.lang.String targetString, int startIndex) throws IOException, ParserException {
        if (targetString == null) {
            throw new NullPointerException();
        }
        final StringJSONReader target = new StringJSONReader(targetString, startIndex);   
        StatefullValue result = new StatefullValue(target, parseValue(target));
        return result;
    }

    /**
     * Parse a JSON Value from the target.
     *
     * @param target Reader to read from.
     * @return The JSON Value instance just parsed(getMessages().getString("invalidValue")
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final Value parseValue(JSONReader target) throws IOException, ParserException {
        Value value = null;
        int p = target.peek();
        if (p == QUOTATION) {
            value = parseString(target);
        } else if (p == OPEN_OBJECT) {
            value = parseObject(target);
        } else if (p == OPEN_ARRAY) {
            value = parseArray(target);
        } else if (ConstantUtility.isNumeric(p)) {
            value = parseNumeric(target);
        } else if (p == TRUE_STR.charAt(0)) {
            value = parseTrue(target);
        } else if (p == FALSE_STR.charAt(0)) {
            value = parseFalse(target);
        } else if (p == NULL_STR.charAt(0)) {
            value = parseNull(target);
        } else {
            throw new JSONParserException("invalidUnexpected", target.getLineNumber(), target.getPositionNumber(), (char) target.peek());
        }
        return value;
    }

    /**
     * Parses a JSON Object Value from the reader.
     *
     * @param target The reader to read the value from
     * @return The Object Value
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final JSON.Object<JSON.String, Value> parseObject(JSONReader target) throws IOException, ParserException {
        if (target.peek() != OPEN_OBJECT) {
            throw new JSONParserException("invalidObjectExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) OPEN_OBJECT, (char) target.peek());
        }
        target.read();
        JSON.Object<JSON.String, Value> value = new JSON.Object<JSON.String, Value>();
        if (target.peek() != CLOSE_OBJECT) {
            JSON.String attributeName = (JSON.String) parseString(target);
            if (target.peek() == NAME_SEPARATOR) {
                target.read();
            } else {
                throw new JSONParserException("invalidObjectExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) NAME_SEPARATOR, (char) target.peek());
            }
            Value attributeValue = parseValue(target);
            value.put(attributeName, attributeValue);
            while (target.peek() == VALUE_SEPARATOR) {
                target.read();
                attributeName = (JSON.String) parseString(target);
                if (value.containsKey(attributeName)) {
                    throw new JSONParserException("invalidKeyAlreadyUsed", target.getLineNumber(), target.getPositionNumber(), attributeName);
                }
                if (target.peek() == NAME_SEPARATOR) {
                    target.read();
                } else {
                    throw new JSONParserException("invalidObjectExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) NAME_SEPARATOR, (char) target.peek());
                }
                attributeValue = parseValue(target);
                value.put(attributeName, attributeValue);
            }
        }
        if (target.peek() == CLOSE_OBJECT) {
            target.read();
        } else {
            throw new JSONParserException("invalidArrayExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) CLOSE_OBJECT, (char) target.peek());
        }
        return value;
    }

    /**
     * Parses a JSON Array Value from the reader.
     *
     * @param target The reader to read the value from
     * @return The Array Value
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final JSON.Array<Value> parseArray(JSONReader target) throws IOException, ParserException {
        if (target.peek() != OPEN_ARRAY) {
            throw new JSONParserException("invalidArrayExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) OPEN_ARRAY, (char) target.peek());
        }
        target.read();
        JSON.Array<Value> value = new JSON.Array<Value>();
        if (target.peek() != CLOSE_ARRAY) {
            Value arrayValue = parseValue(target);
            value.add(arrayValue);
            while (target.peek() == VALUE_SEPARATOR) {
                target.read();
                arrayValue = parseValue(target);
                value.add(arrayValue);
            }
        }
        if (target.peek() == CLOSE_ARRAY) {
            target.read();
        } else {
            throw new JSONParserException("invalidArrayExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) CLOSE_ARRAY, (char) target.peek());
        }
        return value;
    }

    /**
     * Parses a JSON String Value from the reader.
     *
     * @param target The reader to read the value from
     * @return The String Value
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final JSON.String parseString(JSONReader target) throws IOException, ParserException {
        JSON.String value = null;
        if (target.peek() != QUOTATION) {
            throw new JSONParserException("invalidStringExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) QUOTATION, (char) target.peek());
        }
        StringBuilder valueStringBuilder = new StringBuilder();
        ReaderParser targetString = target.getStringReader();
        targetString.read();
        int p;
        while (true) {
            p = targetString.peek();
            if (p == QUOTATION) {
                break;
            } else if (p == REVERSE_SOLIDUS) {
                targetString.read();
                p = targetString.peek();
                switch (p) {
                    case QUOTATION:
                        valueStringBuilder.append((char) QUOTATION_MARK);
                        targetString.read();
                        break;
                    case REVERSE_SOLIDUS:
                        valueStringBuilder.append((char) ESCAPE);
                        targetString.read();
                        break;
                    case SOLIDUS_CHAR:
                        valueStringBuilder.append((char) SOLIDUS_CHAR);
                        targetString.read();
                        break;
                    case BACKSPACE_CHAR:
                        valueStringBuilder.append((char) BACKSPACE);
                        targetString.read();
                        break;
                    case FORM_FEED_CHAR:
                        valueStringBuilder.append((char) FORM_FEED);
                        targetString.read();
                        break;
                    case NEW_LINE_CHAR:
                        valueStringBuilder.append((char) NEW_LINE);
                        targetString.read();
                        break;
                    case CARRIAGE_RETURN_CHAR:
                        valueStringBuilder.append((char) CARRIAGE_RETURN);
                        targetString.read();
                        break;
                    case TAB_CHAR:
                        valueStringBuilder.append((char) TAB);
                        targetString.read();
                        break;
                    case HEX_CHAR:
                        targetString.read();
                        StringBuilder unicodeStringBuilder = new StringBuilder();
                        for (int i = 0; i < 4; i++) {
                            if (ConstantUtility.isHexDigit(targetString.peek())) {
                                unicodeStringBuilder.append((char) targetString.read());
                            } else {
                                throw new JSONParserException("invalidStringHex", target.getLineNumber(), target.getPositionNumber(), targetString.peek());
                            }
                        }
                        int unicodeInt = Integer.parseInt(unicodeStringBuilder.toString().toUpperCase(), 16);
                        if (Character.isHighSurrogate((char) unicodeInt)) {
                            String highSurrogateString = unicodeStringBuilder.toString();
                            int highSurrogate = unicodeInt;
                            unicodeStringBuilder = new StringBuilder();
                            if (targetString.peek() == REVERSE_SOLIDUS) {
                                targetString.read();
                            } else {
                                throw new JSONParserException("invalidStringMissingSurrogate", target.getLineNumber(), target.getPositionNumber(), REVERSE_SOLIDUS, targetString.peek());
                            }
                            if (targetString.peek() == HEX_CHAR) {
                                targetString.read();
                            } else {
                                throw new JSONParserException("invalidStringMissingSurrogate", target.getLineNumber(), target.getPositionNumber(), HEX_CHAR, targetString.peek());
                            }
                            for (int i = 0; i < 4; i++) {
                                if (ConstantUtility.isHexDigit(targetString.peek())) {
                                    unicodeStringBuilder.append((char) targetString.read());
                                } else {
                                    throw new JSONParserException("invalidStringHex", target.getLineNumber(), target.getPositionNumber(), targetString.peek());
                                }
                            }
                            String lowSurrogateString = unicodeStringBuilder.toString();
                            int lowSurrogate = Integer.parseInt(lowSurrogateString.toUpperCase(), 16);
                            if (Character.isSurrogatePair((char) highSurrogate, (char) lowSurrogate)) {
                                char[] c = Character.toChars(Character.toCodePoint((char) highSurrogate, (char) lowSurrogate));
                                valueStringBuilder.append(new String(c));
                            } else {
                                throw new JSONParserException("invalidStringSurrogates", target.getLineNumber(), target.getPositionNumber(), highSurrogateString, lowSurrogateString);
                            }
                        } else {     
                            if (ConstantUtility.isValidStringChar(unicodeInt)) {
                                valueStringBuilder.append((char) unicodeInt);
                            } else {
                                throw new JSONParserException("invalidStringValue", target.getLineNumber(), target.getPositionNumber(), unicodeInt, unicodeStringBuilder.toString());
                            }
                        }
                        break;
                    default:
                        throw new JSONParserException("invalidStringEscape", target.getLineNumber(), target.getPositionNumber(), targetString.peek());
                }
            } else {
                if (ConstantUtility.isValidStringChar(p)) {
                    valueStringBuilder.append((char) targetString.read());
                } else {
                    throw new JSONParserException("invalidStringValue", target.getLineNumber(), target.getPositionNumber(), targetString.peek(), (char) targetString.peek());
                }
            }
        }
        if (targetString.peek() != QUOTATION) {
            throw new JSONParserException("invalidStringExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) QUOTATION, (char) targetString.peek());
        } else {
            targetString.read();
        }
        targetString.close();
        value = new JSON.String(valueStringBuilder.toString());
        return value;
    }

    /**
     * Parses a JSON Numeric Value from the reader.
     *
     * @param target The reader to read the value from
     * @return The Value of the numeric
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final JSON.Numeric parseNumeric(JSONReader target) throws IOException, ParserException {
        JSON.Numeric value = null;
        StringBuilder numericStringBuilder = new StringBuilder();
        boolean minusFlag = false;
        boolean decimalFlag = false;
        boolean exponetFlag = false;
        int beforeDecimalCount = 0;
        int afterDecimalCount = 0;
        if (target.peek() == MINUS) {
            minusFlag = true;
            target.read();
            numericStringBuilder.append((char) MINUS);
            if (!( target.peek() >= DIGITS[0] && target.peek() <= DIGITS[9] )) {
                throw new JSONParserException("invalidNumericExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) target.peek());
            }
        }
        if (target.peek() == DIGITS[0]) {
            numericStringBuilder.append((char) target.read());
            beforeDecimalCount++;
        } else if (target.peek() >= DIGITS[1] && target.peek() <= DIGITS[9]) {
            numericStringBuilder.append((char) target.read());
            beforeDecimalCount++;
            while (ConstantUtility.isDigit(target.peek())) {
                numericStringBuilder.append((char) target.read());
                beforeDecimalCount++;
            }
        } else {
            throw new JSONParserException("invalidNumericExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) target.peek());
        }
        if (target.peek() == DECIMAL_POINT) {
            target.read();
            decimalFlag = true;
            numericStringBuilder.append((char) DECIMAL_POINT);
            if (!( target.peek() >= DIGITS[0] && target.peek() <= DIGITS[9] )) {
                throw new JSONParserException("invalidNumericExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) target.peek());
            }
            while (ConstantUtility.isDigit(target.peek())) {
                numericStringBuilder.append((char) target.read());
                afterDecimalCount++;
            }
        }
        if (target.peek() == EXPS[0] || target.peek() == EXPS[1]) {
            target.read();
            exponetFlag = true;
            numericStringBuilder.append((char) EXPS[1]);
            if (target.peek() == MINUS) {
                target.read();
                numericStringBuilder.append((char) MINUS);
            } else if (target.peek() == PLUS) {
                target.read();
                numericStringBuilder.append((char) PLUS);
            } else {
                numericStringBuilder.append((char) PLUS);
            }
            if (!( target.peek() >= DIGITS[0] && target.peek() <= DIGITS[9] )) {
                throw new JSONParserException("invalidNumericExpecting1", target.getLineNumber(), target.getPositionNumber(), (char) target.peek());
            }
            while (ConstantUtility.isDigit(target.peek())) {
                numericStringBuilder.append((char) target.read());
            }
        }
        if (!decimalFlag && !exponetFlag) {
            if (beforeDecimalCount < 18) {
                value = new JSON.Numeric(Long.parseLong(numericStringBuilder.toString()));
            } else {
                value = new JSON.Numeric(new BigInteger(numericStringBuilder.toString()));
            }
        } else {
            if (beforeDecimalCount + afterDecimalCount < 18) {
                value = new JSON.Numeric(Double.parseDouble(numericStringBuilder.toString()));
            } else {
                value = new JSON.Numeric(new BigDecimal(numericStringBuilder.toString()));
            }
        }
        return value;
    }

    /**
     * Parses a JSON True Value from the reader.
     *
     * @param target The reader to read the value from
     * @return The False Value
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final JSON.True parseTrue(JSONReader target) throws IOException, ParserException {
        JSON.True value = null;
        for (int i = 0; i < TRUE_STR.length(); i++) {
            if (target.peek() == TRUE_STR.charAt(i)) {
                target.read();
            } else {
                throw new JSONParserException("invalidValue", target.getLineNumber(), target.getPositionNumber(), TRUE_STR, (char) target.peek());
            }
        }
        value = JSON.TRUE;
        return value;
    }

    /**
     * Parses a JSON False Value from the reader.
     *
     * @param target The reader to read the value from
     * @return The False Value
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final JSON.False parseFalse(JSONReader target) throws IOException, ParserException {
        JSON.False value = null;
        for (int i = 0; i < FALSE_STR.length(); i++) {
            if (target.peek() == FALSE_STR.charAt(i)) {
                target.read();
            } else {
                throw new JSONParserException("invalidValue", target.getLineNumber(), target.getPositionNumber(), FALSE_STR, (char) target.peek());
            }
        }
        value = JSON.FALSE;
        return value;
    }

    /**
     * Parses a JSON Null Value from the reader.
     *
     * @param target The reader to read the value from
     * @return The Null Value
     * @throws IOException IO Exception
     * @throws ParserException JSON Parser Exception
     */
    public final JSON.Null parseNull(JSONReader target) throws IOException, ParserException {
        JSON.Null value = null;
        for (int i = 0; i < NULL_STR.length(); i++) {
            if (target.peek() == NULL_STR.charAt(i)) {
                target.read();
            } else {
                throw new JSONParserException("invalidValue", target.getLineNumber(), target.getPositionNumber(), NULL_STR, (char) target.peek());
            }
        }
        value = JSON.NULL;
        return value;
    }
    
    public static class StatefullValue {
        final StringJSONReader reader;
        final Value value;
        
        public StatefullValue(StringJSONReader reader, Value value) {
            this.reader = reader;
            this.value = value;
        }

        public StringJSONReader getReader() {
            return reader;
        }

        public Value getValue() {
            return value;
        }
    }
}
