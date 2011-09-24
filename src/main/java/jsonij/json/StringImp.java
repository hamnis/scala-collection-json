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

import java.io.Serializable;

/**
 * JSON String Implementation.
 * 
 * @author openecho
 * @version 1.0.0
 */
public class StringImp extends Value implements Serializable, CharSequence {

    /**
     * Container for the String.
     */
    protected String value;

    /**
     * Constructor using java.lang.String.
     *
     * @param orig The original String for this Value
     */
    public StringImp(String orig) {
        value = orig;
    }

    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#internalType()
     */
    @Override
    protected TYPE internalType() {
        return TYPE.STRING;
    }

    /* (non-Javadoc)
     * @see java.lang.CharSequence#charAt(int)
     */
    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    /* (non-Javadoc)
     * @see java.lang.CharSequence#length()
     */
    @Override
    public int length() {
        return value.length();
    }

    /* (non-Javadoc)
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof String || o instanceof JSON.String) {
            return o.toString().equals(value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value;
    }

    @Override
    public int nestedSize() {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#toJSON()
     */
    @Override
    public String toJSON() {
        StringBuilder outputStringBuilder = new StringBuilder();
        outputStringBuilder.append('"');
        char c;
        for (int i = 0; i < value.length(); i++) {
            c = value.charAt(i);
            switch (c) {
                case '"':
                    outputStringBuilder.append("\\\"");
                    break;
                case '\\':
                    outputStringBuilder.append("\\\\");
                    break;
                case '/':
                    outputStringBuilder.append("\\/");
                    break;
                case '\b':
                    outputStringBuilder.append("\\b");
                    break;
                case '\f':
                    outputStringBuilder.append("\\f");
                    break;
                case '\n':
                    outputStringBuilder.append("\\n");
                    break;
                case '\r':
                    outputStringBuilder.append("\\r");
                    break;
                case '\t':
                    outputStringBuilder.append("\\t");
                    break;
                default:
                    int a = (int) c;
                    if(a == 32 || a == 33 || ((a >= 35) && (a <= 91)) || ((a >= 93) && (a <= 127))) {
                        outputStringBuilder.append(c);
                    } else {
                        String hex = Integer.toHexString(a);
                        outputStringBuilder.append("\\u");
                        for(int j=hex.length();j<4;j++) {
                            outputStringBuilder.append("0");
                        }
                        outputStringBuilder.append(hex);
                    }
                    break;
            }
        }
        outputStringBuilder.append('"');
        return String.format(outputStringBuilder.toString());
    }
}
