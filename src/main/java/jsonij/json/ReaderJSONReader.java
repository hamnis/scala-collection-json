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
import java.io.Reader;
import jsonij.parser.Position;
import jsonij.parser.ReaderParser;

/**
 * java.io.Reader implementation of a JSONReader. 
 * @author J.W.Marsden
 */
public class ReaderJSONReader extends JSONReader {

    protected Reader reader;
    ReaderJSONStringReader stringReader;

    public ReaderJSONReader(Reader reader) {
        if (reader == null) {
            throw new NullPointerException("Null Reader Exception.");
        }
        this.reader = reader;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    /**
     * Reads from the reader.
     *
     * @param targetReader The reader to be read from.
     * @return The read byte if found otherwise -1 if the end of the stream is reached.
     * @throws IOException Java IO Exception.
     */
    @Override
    public int readNext() throws IOException {
        int r = -1;
        while (reader.ready() && ( r = reader.read() ) != -1) {
            getPosition().movePosition();
            do {
                if (ConstantUtility.isReturn(r)) {
                    handleNewLine();
                    r = reader.read();
                    if (r != -1 && ConstantUtility.isNewLine(r)) {
                        continue;
                    }
                } else if (ConstantUtility.isNewLine(r)) {
                    handleNewLine();
                    r = reader.read();
                    if (r != -1 && ConstantUtility.isReturn(r)) {
                        continue;
                    }
                }
            } while (ConstantUtility.isReturn(r) || ConstantUtility.isNewLine(r));
            if (!ConstantUtility.isWhiteSpace(r)) {
                break;
            }
        }
        return r;
    }

    public ReaderParser getStringReader() {
        if (stringReader == null) {
            stringReader = new ReaderJSONStringReader();
        } else if (stringReader.isActive()) {
            // TODO: Do we want an exception here?
        }
        stringReader.setActive(true);
        return stringReader;
    }

    protected class ReaderJSONStringReader implements ReaderParser {

        protected boolean active;

        public ReaderJSONStringReader() {
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
            if (hasPeeked) {
                if (!active) {
                    return -1;
                }
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
            if (reader.ready() && ( r = reader.read() ) != -1) {
                position.movePosition();
            }
            return r;
        }

        public Position getPosition() {
            return ReaderJSONReader.this.getPosition();
        }

        public boolean isHasPeeked() {
            return ReaderJSONReader.this.isHasPeeked();
        }

        public boolean hasPeeked() {
            return ReaderJSONReader.this.hasPeeked();
        }

        public void setHasPeeked(boolean hasPeeked) {
            ReaderJSONReader.this.setHasPeeked(hasPeeked);
        }

        public int getLineNumber() {
            return ReaderJSONReader.this.getLineNumber();
        }

        public int getPositionNumber() {
            return ReaderJSONReader.this.getPositionNumber();
        }
    }
}
