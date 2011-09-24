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

import java.util.Locale;
import jsonij.parser.ParserException;

/**
 *
 * @author openecho
 */
public class JSONParserException extends ParserException {

    public static final String MESSAGE_BUNDLE = "JSONMessageBundle";

    /**
     * Basic Constructor.
     *
     * @param key Exception key
     * @param args Additional Arguments for Exception
     */
    public JSONParserException(String key, Object... args) {
        super(key, -1, -1, null, args);
    }

    /**
     * Constructor Including Line Number and Position Number of Exception
     *
     * @param key Exception Key
     * @param line Exception Line
     * @param position Exception Position
     * @param args Additional Arguments for Exception
     */
    public JSONParserException(String key, int line, int position, Object... args) {
        super(key, line, position, null, args);
    }

    /**
     * Constructor Including Line Number, Position Number and Locale of Exception.
     *
     * @param key Exception Key
     * @param line Exception Line
     * @param position Exception Position
     * @param locale Valid Locale for the exception
     * @param args Additional Arguments for Exception
     */
    public JSONParserException(String key, int line, int position, Locale locale, Object... args) {
        super(key, line, position, locale, args);
    }

    @Override
    public String getBundleName() {
        return MESSAGE_BUNDLE;
    }
}
