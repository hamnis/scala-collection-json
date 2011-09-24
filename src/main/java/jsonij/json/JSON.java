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
import jsonij.parser.ParserException;

/**
 * JSON document class.
 *
 * This class defines the representation of each of the JSON types and provides
 * methods to parse and create JSON instances from various sources. All JSON
 * types extend Value. The following table documents which class to use when
 * representing each of the JSON types.
 *
 * <table>
 *  <tr>
 *      <td style="border:1px solid black;"><strong>JSON Type</strong></td>
 *      <td style="border:1px solid black;"><strong>Class</strong></td>
 *  </tr>
 *  <tr>
 *      <td style="border:1px solid black;">Object</td>
 *      <td style="border:1px solid black;">JSON.Object<JSON.String,Value></td>
 *  </tr>
 *  <tr>
 *      <td style="border:1px solid black;">Array</td>
 *      <td style="border:1px solid black;">JSON.Array<Value></td>
 *  </tr>
 *  <tr>
 *      <td style="border:1px solid black;">String</td>
 *      <td style="border:1px solid black;">JSON.String</td>
 *  </tr>
 *  <tr>
 *      <td style="border:1px solid black;">Numeric</td>
 *      <td style="border:1px solid black;">JSON.Numeric</td>
 *  </tr>
 *  <tr>
 *      <td style="border:1px solid black;">True</td>
 *      <td style="border:1px solid black;">JSON.TRUE</td>
 *  </tr>
 *  <tr>
 *      <td style="border:1px solid black;">False</td>
 *      <td style="border:1px solid black;">JSON.FALSE</td>
 *  </tr>
 *  <tr>
 *      <td style="border:1px solid black;">Null</td>
 *      <td style="border:1px solid black;">JSON.NULL</td>
 *  </tr>
 * </table>
 * 
 * @author openecho
 * @version 1.0.0
 */
public class JSON {

    /**
     * Root Value for the JSON Document.
     */
    Value root;
    /**
     * Static Instance holding the JSON True instance. Reference this instead of constructing new JSON.True instances.
     */
    public static final True TRUE;
    /**
     * Static Instance holding the JSON False instance. Reference this instead of constructing new JSON.False instances.
     */
    public static final False FALSE;
    /**
     * Static Instance holding the JSON Null instance. Reference this instead of constructing new JSON.Null instances.
     */
    public static final Null NULL;

    static final JSONParser parser;

    static {
        TRUE = new JSON.True();
        FALSE = new JSON.False();
        NULL = new JSON.Null();
        parser = new JSONParser();
    }

    /**
     * Default JSON constructor. Requires the JSON root Value.
     * @param root
     */
    public JSON(Value root) {
        if (root == null) {
            throw new NullPointerException("JSON Root Cannot be Null.");
        }
        if (root.internalType() != Value.TYPE.ARRAY && root.internalType() != Value.TYPE.OBJECT) {
            throw new RuntimeException("JSON can only be constructed from Arrays and Objects.");
        }
        this.root = root;
    }

    /**
     * Accessor for the root value for this JSON Document.
     * @return The root Value.
     */
    public Value getRoot() {
        return root;
    }

    /**
     * Package protected mutator for this JSON document.
     * @param root The root to set for the JSON document.
     */
    void setRoot(Value root) {
        if (root == null) {
            throw new NullPointerException("JSON Root Cannot be Null.");
        }
        if (root.internalType() != Value.TYPE.ARRAY && root.internalType() != Value.TYPE.OBJECT) {
            throw new RuntimeException("JSON can only be constructed from Arrays and Objects.");
        }
        this.root = root;
    }

    /**
     * Size inspector for the root JSON Value. If the root is an object or an array
     * this will return the dimension.
     * @return The size of the root JSON Value.
     * @see openecho.json.Value#size()
     */
    public int size() {
        return getRoot().size();
    }

    /**
     * Null check for the root JSON Value.
     * @return Boolean if the root value is JSON.Null.
     * @see openecho.json.Value#isNull()
     */
    public boolean isNull() {
        return getRoot().isNull();
    }
    
    /**
     * Boolean accessor for the root JSON Value.
     * @return Boolean Value for the root JSON Value.
     * @see openecho.json.Value#getBoolean()
     */
    public boolean getBoolean() {
        return getRoot().getBoolean();
    }

    /**
     * Integer accessor for the root JSON Value.
     * @return int Value for the root JSON Value.
     * @see openecho.json.Value#getInt()
     */
    public int getInt() {
        return getRoot().getInt();
    }

    /**
     * Double accessor for the root JSON Value.
     * @return double Value for the root JSON Value.
     * @see openecho.json.Value#getDouble()
     */
    public double getDouble() {
        return getRoot().getDouble();
    }

    /**
     * String accessor for the root JSON Value.
     * @return java.lang.String Value for the root JSON Value.
     * @see openecho.json.Value#getString()
     */
    public java.lang.String getString() {
        return getRoot().getString();
    }

    /**
     * Value accessor by index for the root JSON Value.
     * @param i The index to access.
     * @return Value Value for the root JSON Value.
     * @see openecho.json.Value#get(int i)
     */
    public Value get(int i) {
        return getRoot().get(i);
    }

    /**
     * Value accessor by String for the root JSON Value. This method only functions
     * if the root value is a JSON Object.
     * @param key The key to access.
     * @return Value Value for the root JSON Value.
     * @see openecho.json.Value#get(java.lang.String key)
     */
    public Value get(java.lang.String key) {
        return getRoot().get(key);
    }

    /**
     * Converts JSON Document into a valid JSON String.
     * @return The JSON String.
     */
    public java.lang.String toJSON() {
        return getRoot().toJSON();
    }

    @Override
    public java.lang.String toString() {
        return java.lang.String.format("JSON@%s:%s",hashCode(),getRoot().toString());
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if(o == null) {
            return false;
        }
        if(!getClass().equals(o.getClass())) {
            return false;
        }
        return getRoot().equals(((JSON)o).getRoot());
    }

    @Override
    public int hashCode() {
        Value root = getRoot();
        if(root == null) {
            return 0;
        } else {
            return root.hashCode();
        }
    }
    
    

    /**
     * Parse Method that parses from a String.
     * @param document The document.
     * @return Parsed JSON instance.
     * @throws ParserException JSON Parser Exception.
     * @throws IOException IO Exception.
     */
    public static JSON parse(java.lang.String document) throws ParserException, IOException {
        return new JSON(parser.parse(document));
    }

    /**
     * Parse Method that parses from a Reader. The messageReader should be reset.
     * @param documentReader The Reader parameter.
     * @return Parsed JSON instance.
     * @throws ParserException JSON Parser Exception.
     * @throws IOException IO Exception.
     */
    public static JSON parse(Reader documentReader) throws ParserException, IOException {
        return new JSON(parser.parse(documentReader));
    }

    /**
     * JSON Object. Wrapper for ObjectImp.
     * @param <K> The key internalType (must extend JSON.String).
     * @param <V> The element (must extend Value).
     */
    public static class Object<K extends JSON.String, V extends Value> extends ObjectImp<K, V> {
    }

    /**
     * JSON Array. Wrapper for ArrayImp.
     * @param <E> The element that the Array will hold (must extend Value).
     */
    public static class Array<E extends Value> extends ArrayImp<E> {
    }

    /**
     * JSON Numeric. Wrapper for NumericImp.
     */
    public static class Numeric extends NumericImp {
        /**
         * Default Constructor.
         * @param n The java.lang.Number Value.
         */
        public Numeric(int value) {
            super(value);
        }

        public Numeric(long value) {
            super(value);
        }

        public Numeric(double value) {
            super(value);
        }

        public Numeric(Number numberValue) {
            super(numberValue);
        }
    }

    /**
     * JSON String. Wrapper for StringImp.
     */
    public static class String extends StringImp {
        /**
         * Default Constructor.
         * @param s The java.lang.String value.
         */
        public String(java.lang.String s) {
            super(s);
        }
        
        public static String getValue(java.lang.String string) {
            return new JSON.String(string);
        }
    }

    /**
     * JSON Boolean. Common parent to True and False.
     */
    public static abstract class Boolean extends Value {
        
        public static Boolean getValue(boolean bool) {
            if(bool) {
                return JSON.TRUE;
            } else {
                return JSON.FALSE;
            }
        }
    }

    /**
     * JSON True Implementation.
     */
    public static class True extends Boolean {
        /**
         * The String for True.
         */
        public static final java.lang.String VALUE;

        static {
            VALUE = "true";
        }

        /**
         * Default Constructor. Use JSON.TRUE to get an instance of this Object.
         */
        private True() {
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#internalType()
         */
        @Override
        protected TYPE internalType() {
            return TYPE.TRUE;
        }

        /* (non-Javadoc)
         * @see openecho.json.JSON.Boolean#getBoolean()
         */
        @Override
        public boolean getBoolean() {
            return true;
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#nestedSize()
         */
        @Override
        public int nestedSize() {
            return 0;
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#toJSON()
         */
        @Override
        public java.lang.String toJSON() {
            return VALUE;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            int hash = 5 + toJSON().hashCode();
            return hash;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof JSON.True) {
                return true;
            }
            return false;
        }
    }

    /**
     * JSON False Implementation.
     */
    public static class False extends Boolean {
        /**
         * The String for False.
         */
        public static final java.lang.String VALUE;

        static {
            VALUE = "false";
        }

        /**
         * Default Constructor. Use JSON.FALSE to get an instance of this Object.
         */
        private False() {
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#internalType()
         */
        @Override
        protected TYPE internalType() {
            return TYPE.FALSE;
        }

        /* (non-Javadoc)
         * @see openecho.json.JSON.Boolean#getBoolean()
         */
        @Override
        public boolean getBoolean() {
            return false;
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#nestedSize()
         */
        @Override
        public int nestedSize() {
            return 0;
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#toJSON()
         */
        @Override
        public java.lang.String toJSON() {
            return VALUE;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            int hash = 3 + toJSON().hashCode();
            return hash;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof JSON.False) {
                return true;
            }
            return false;
        }
    }

    /**
     * JSON Null Implementation.
     */
    public static class Null extends Value {

        /**
         * The String for null.
         */
        public static final java.lang.String VALUE;

        static {
            VALUE = "null";
        }

        /**
         * Default Constructor. Use JSON.NULL to get an instance of this Object.
         */
        private Null() {
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#internalType()
         */
        @Override
        protected TYPE internalType() {
            return TYPE.NULL;
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#nestedSize()
         */
        @Override
        public int nestedSize() {
            return 0;
        }

        /* (non-Javadoc)
         * @see openecho.json.Value#toJSON()
         */
        @Override
        public java.lang.String toJSON() {
            return VALUE;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            int hash = 7 + toJSON().hashCode();
            return hash;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof JSON.Null) {
                return true;
            }
            return false;
        }
    }
}
