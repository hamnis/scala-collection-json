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

import java.lang.reflect.Array;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Internationised Parser Exception.
 * 
 * @author openecho
 * @version 1.0.0
 */
public abstract class ParserException extends BaseParserException {
    /**
     * Exception Key
     */
    protected String key;
    /**
     * Exception Locale
     */
    protected Locale locale;

    /**
     * Basic Constructor.
     *
     * @param key Exception key
     * @param args Additional Arguments for Exception
     */
    public ParserException(String key, Object... args) {
        this(key, -1, -1, null, args);
    }

    /**
     * Constructor Including Line Number and Position Number of Exception
     *
     * @param key Exception Key
     * @param line Exception Line
     * @param position Exception Position
     * @param args Additional Arguments for Exception
     */
    public ParserException(String key, int line, int position, Object... args) {
        this(key, line, position, null, args);
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
    public ParserException(String key, int line, int position, Locale locale, Object... args) {
        this.line = line;
        this.position = position;
        this.key = key;

        /**
         * TODO:
         * Fix this. Dont force it to be English!
         */
        this.locale = ((locale == null) ? Locale.ENGLISH : locale);
        if (this.locale != null) {
            try {
                String messageFormat = ResourceBundle.getBundle(getBundleName()).getString(this.key);
                this.message = String.format(messageFormat, args);
            } catch (Exception ex) {
                StringBuilder argumentStringBuilder = new StringBuilder();
                Object argValue;
                int argCount;
                if (( argCount = Array.getLength(args) ) > 0) {
                    for (int i = 0; i < argCount - 1; i++) {
                        argValue = args[i];
                        if (argValue != null) {
                            argumentStringBuilder.append(args.toString()).append(',');
                        } else {
                            argumentStringBuilder.append("null").append(',');
                        }
                    }
                    argValue = args[argCount - 1];
                    if (argValue != null) {
                        argumentStringBuilder.append(argValue.toString());
                    } else {
                        argumentStringBuilder.append("null");
                    }
                }
                String messageFormat = "Message Format Not Found (%s#%s[%s]): %s";
                this.message = String.format(messageFormat, getBundleName(), this.key, argumentStringBuilder.toString(), ex);
            }
        } else {
            this.message = String.format("Undefined Exception %s %s", key, locale);
        }
    }

    public abstract String getBundleName();
}
