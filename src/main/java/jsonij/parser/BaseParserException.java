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

public class BaseParserException extends Exception {

    protected int line;
    protected int position;
    protected String message;

    public BaseParserException() {
    }

    public BaseParserException(String message) {
        this(-1, -1, message);
    }

    public BaseParserException(int line, int position, String message) {
        this.line = line;
        this.position = position;
        this.message = message;
    }

    @Override
    public String getMessage() {
        String output = "Parsing Exception";
        if (line != -1 || position != -1) {
            output = String.format("%s (%s,%s): %s", output, line, position, message);
        } else {
            output = String.format("%s: %s", output, message);
        }
        return output;
    }
}
