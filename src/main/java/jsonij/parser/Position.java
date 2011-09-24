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

public class Position {

    //protected int readCount;
    protected int lineNumber;
    protected int postionNumber;

    public Position() {
        //this.readCount = 0;
        this.lineNumber = 1;
        this.postionNumber = 0;
    }
/*
    public int getReadCount() {
        return readCount;
    }

    public int setReadCount(int readCount) {
        return this.readCount = readCount;
    }

    public int tickReadCount() {
        return this.readCount++;
    }
*/
    public int getLineNumber() {
        return lineNumber;
    }

    public int setLineNumber(int lineNumber) {
        return this.lineNumber = lineNumber;
    }

    public int newLine() {
        postionNumber = 0;
        return lineNumber++;
    }

    public int getPostionNumber() {
        return postionNumber;
    }

    public int setPostionNumber(int postionNumber) {
        return this.postionNumber = postionNumber;
    }

    final public int movePosition() {
        return postionNumber++;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", getLineNumber(), getPostionNumber());
    }
}
