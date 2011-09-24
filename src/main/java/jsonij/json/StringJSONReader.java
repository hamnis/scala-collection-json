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

import java.io.IOException;
import jsonij.parser.ParserException;
import jsonij.parser.Position;
import jsonij.parser.ReaderParser;

/**
 * java.lang.String implementation of a JSONReader. 
 * @author J.W.Marsden
 */
public class StringJSONReader extends JSONReader {

    final protected String jsonString;
    protected int index;
    final protected int length;
    final protected StringJSONStringReader stringReader;

    public StringJSONReader(String jsonString) {
        if (jsonString == null) {
            throw new NullPointerException("Null String Exception.");
        }
        this.jsonString = jsonString;
        length = jsonString.length();
        stringReader = new StringJSONStringReader();
        index = 0;
    }
    
    public StringJSONReader(String jsonString, int index) {
        if (jsonString == null) {
            throw new NullPointerException("Null String Exception.");
        }
        this.jsonString = jsonString;
        length = jsonString.length();
        stringReader = new StringJSONStringReader();
        this.index = index;
        position.setPostionNumber(index);
    }

    @Override
    public int readNext() throws IOException, ParserException {
        int r = -1;
        while (index < length) {
            r = jsonString.charAt(index++);
            position.movePosition();
            do {
                if (ConstantUtility.isReturn(r)) {
                    handleNewLine();
                    if (index < length) {
                        r = jsonString.charAt(index++);
                        if (ConstantUtility.isNewLine(r)) {
                            continue;
                        } 
                    } else {
                        break;
                    }
                } else if (ConstantUtility.isNewLine(r)) {
                    handleNewLine();
                    if (index < length) {
                        r = jsonString.charAt(index++);
                        if (ConstantUtility.isReturn(r)) {
                            continue;
                        } 
                    } else {
                        break;
                    }
                }
            } while (ConstantUtility.isReturn(r) || ConstantUtility.isNewLine(r));
            if (!ConstantUtility.isWhiteSpace(r)) {
                break;
            }
        }
        return r;
    }

    public int getIndex() {
        return index;
    }

    public ReaderParser getStringReader() {
        stringReader.setActive(true);
        return stringReader;
    }

    protected class StringJSONStringReader implements ReaderParser {

        public boolean active;

        public StringJSONStringReader() {
            active = true;
        }

        /**
         * @return the active
         */
        public boolean isActive() {
            return active;
        }

        /**
         * @param active the active to set
         */
        protected void setActive(boolean active) {
            this.active = active;
        }

        public int peek() throws IOException {
            if (!hasPeeked) {
                if (!active) {
                    return -1;
                }
                peekValue = readNext();
                hasPeeked = true;
            }
            return peekValue;
        }

        public int read() throws IOException {
            if (!active) {
                return -1;
            }
            if (hasPeeked) {
                hasPeeked = false;
                return peekValue;
            }
            return readNext();
        }

        public void close() {
            active = false;
        }

        protected int readNext() throws IOException {
            int r = -1;
            if (index < length) {
                r = jsonString.charAt(index++);
                position.movePosition();
            }
            return r;
        }

        public Position getPosition() {
            return StringJSONReader.this.getPosition();
        }

        public boolean isHasPeeked() {
            return StringJSONReader.this.isHasPeeked();
        }

        public boolean hasPeeked() {
            return StringJSONReader.this.hasPeeked();
        }

        public void setHasPeeked(boolean hasPeeked) {
            StringJSONReader.this.setHasPeeked(hasPeeked);
        }

        public int getLineNumber() {
            return StringJSONReader.this.getLineNumber();
        }

        public int getPositionNumber() {
            return StringJSONReader.this.getPositionNumber();
        }
    }
}
