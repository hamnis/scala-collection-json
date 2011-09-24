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
package jsonij.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author openecho
 */
public abstract class BaseReaderParser implements ReaderParser {

    protected int peekValue;
    protected boolean hasPeeked;
    protected Position position;

    public BaseReaderParser() {
        this(null);
    }

    public BaseReaderParser(Reader reader) {
        this.peekValue = -1;
        this.hasPeeked = false;
        this.position = new Position();
    }
    
    /**
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    protected Position setPosition(Position position) {
        return this.position = position;
    }

    /**
     * Only here for POJO reasons.
     * @return hasPeeked()
     * @see hasPeeked()
     */
    public boolean isHasPeeked() {
        return hasPeeked();
    }

    public boolean hasPeeked() {
        return hasPeeked;
    }

    public void setHasPeeked(boolean hasPeeked) {
        this.hasPeeked = hasPeeked;
    }

    public int getLineNumber() {
        return getPosition().getLineNumber();
    }

    public int getPositionNumber() {
        return getPosition().getPostionNumber();
    }

    public int peek() throws IOException, ParserException {
        if (!hasPeeked) {
            peekValue = readNext();
            hasPeeked = true;
        }
        return peekValue;
    }

    public int read() throws IOException, ParserException {
        if (hasPeeked) {
            hasPeeked = false;
            return peekValue;
        }
        return readNext();
    }

    /**
     * Reads from the reader.
     *
     * @param targetReader The reader to be read from.
     * @return The read byte if found otherwise -1 if the end of the stream is reached.
     * @throws IOException Java IO Exception.
     */
    protected abstract int readNext() throws IOException, ParserException;

    public void close() {

    }


    protected void handleNewLine() throws IOException {
        getPosition().newLine();
    }

    @Override
    public String toString() {
        String state = "";
        try {
            state = String.format("Next Char %s", (char) peek());
        } catch (IOException e) {
            state = String.format("Unknown State: %s", e.toString());
        } catch (BaseParserException e) {
            state = String.format("Unknown State: %s", e.toString());
        }
        return String.format("Reader %s: %s", getPosition(), state);
    }
}
