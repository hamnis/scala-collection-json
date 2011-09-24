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
import java.util.Iterator;
import java.util.Set;

/**
 * Value is extended by every JSON internalType implementation. This class provides generic
 * access to all values to make life a little easier when traversing the JSON Document.
 * 
 * @author openecho
 * @version 1.0.0
 */
public abstract class Value implements Serializable, Comparable<Value> {

    /**
     * Current Value Type
     */
    protected TYPE valueType;

    /**
     * Type enumeration.
     */
    public enum TYPE {

        /**
         * OBJECT used to denote JSON Object Types.
         */
        OBJECT,
        /**
         * ARRAY used to denote JSON Array Types.
         */
        ARRAY,
        /**
         * STRING used to denote JSON String Types.
         */
        STRING,
        /**
         * NUMERIC used to denote JSON Numeric Types.
         */
        NUMERIC,
        /**
         * TRUE used to denote JSON True Types.
         */
        TRUE,
        /**
         * False used to denote JSON False Types.
         */
        FALSE,
        /**
         * Null used to denote JSON Null Types.
         */
        NULL
    }

    /**
     * Constructor for Value. As a Value constructs it must have a internalType or it
     * cannot construct.
     */
    public Value() {
        this.valueType = internalType();
        if (this.valueType == null) {
            throw new NullPointerException("type() method must be implemented and return a valid type.");
        }
    }

    /**
     * Internal Method to find the internalType for the Object. Must be implemented and not return null.
     *
     * @return The internalType of this JSON Value.
     */
    protected abstract TYPE internalType();

    /**
     * Accessor for the Value TYPE.
     *
     * @return TYPE The value valueType.
     */
    public TYPE getValueType() {
        return valueType;
    }

    /**
     * Accessor for the Value TYPE.
     *
     * @return TYPE The value valueType.
     */
    public TYPE type() {
        return valueType;
    }

    /**
     * Retrieves the size of the Value. If string internalType, this will return the
     * length of the String. If this internalType is an Array or an Object then it
     * will return the number of elements in the Object. If this Value is
     * not a String, Array or Object then this will return -1.
     * @return size of the Value or -1 if this Value has no size.
     */
    public int size() {
        switch (valueType) {
            case STRING:
                return ( (JSON.String) this ).length();
            case ARRAY:
                return ( (JSON.Array<?>) this ).size();
            case OBJECT:
                return ( (JSON.Object<?, ?>) this ).size();
            default:
                return -1;
        }
    }

    /**
     * Finds the nested elements under this Value. This is effectively the count
     * of all JSON Values attached to this Value. This number does not include this
     * value itself.
     * @return int The count of all Values attached to this Value.
     */
    public abstract int nestedSize();

    public boolean isNull() {
        switch (valueType) {
            case NULL:
                return true;
            default:
                return !getBoolean();
        }
    }

    /**
     * Finds the boolean representation for the Value. If the value is JSON.TRUE
     * or JSON.FALSE then those booleans are returned. If the value is Numeric
     * then true is returned for all values that are not zero. If the value is
     * a String then an empty String returns 0. If the value is an Object or Array
     * then all sizes that are not zero return true.
     * @return boolean The boolean for the Value.
     */
    public boolean getBoolean() {
        boolean result = false;
        switch (valueType) {
            case TRUE:
                result = true;
                break;
            case FALSE:
                result = false;
                break;
            case NUMERIC:
                result = ( (JSON.Numeric) this ).intValue() != 0;
                break;
            case STRING:
                result = ( (JSON.String) this ).length() != 0;
                break;
            case ARRAY:
                result = ( (JSON.Array<?>) this ).size() != 0;
                break;
            case OBJECT:
                result = ( (JSON.Object<?, ?>) this ).size() != 0;
                break;
        }
        return result;
    }

    /**
     * Finds the int representation for the Value. Returns 1 when the value is
     * JSON True and 0 when the value is JSON False. When the value is Numeric
     * it will return the intValue from Number. If the value is a String then
     * an attempt is made to parse the String value into an integer and return it.
     * All other types return -1.
     * @return int The int for the Value.
     */
    public int getInt() {
        int result = -1;
        switch (valueType) {
            case TRUE:
                result = 1;
                break;
            case FALSE:
                result = 0;
                break;
            case NUMERIC:
                result = ( (JSON.Numeric) this ).intValue();
                break;
            case STRING:
                result = Integer.parseInt(( this ).toString());
                break;
        }
        return result;
    }

    /**
     * Finds the double representation for the Value. Returns 1D when the value is
     * JSON True and 0D when the value is JSON False. When the value is Numeric
     * it will return the doubleValue from Number. If the value is a String then
     * an attempt is made to parse the String value into an Double and return it.
     * All other types return -1D.
     * @return double The double value for the Value.
     */
    public double getDouble() {
        double result = -1D;
        switch (valueType) {
            case TRUE:
                result = 1D;
                break;
            case FALSE:
                result = 0D;
                break;
            case NUMERIC:
                result = ( (JSON.Numeric) this ).doubleValue();
                break;
            case STRING:
                result = Double.parseDouble(( this ).toString());
                break;
        }
        return result;
    }

    /**
     * Finds the Number representation for the Value. Returns 1D when the value is
     * JSON True and 0D when the value is JSON False. When the value is Numeric
     * it will return the Number. If the value is a String then an attempt is made
     * to parse the String value into an Double and return it. All other types return -1D.
     * @return double The double value for the Value.
     */
    public Number getNumber() {
        Number result = -1D;
        switch (valueType) {
            case TRUE:
                result = 1D;
                break;
            case FALSE:
                result = 0D;
                break;
            case NUMERIC:
                result = ( (JSON.Numeric) this ).getNumber();
                break;
            case STRING:
                result = Double.parseDouble(( this ).toString());
                break;
        }
        return result;
    }

    /**
     * Finds the String representation for the Value. When the value is true or
     * false it will return "true" or "false" respectively. If the value is
     * Numeric it will return the toString() version of the Number instance
     * or the String itself. All other values return null.
     * @return String The String value for the Value.
     */
    public String getString() {
        String result = null;
        switch (valueType) {
            case TRUE:
                result = "true";
                break;
            case FALSE:
                result = "false";
                break;
            case NUMERIC:
                result = ( (JSON.Numeric) this ).toString();
                break;
            case STRING:
                result = ( this ).toString();
                break;
        }
        return result;
    }

    /**
     * Extracts a Value instance at an Index. This method only returns values when
     * the internalType is OBJECT or ARRAY. All other cases will return null.
     * @param i The index to get the value for.
     * @return Value The Value at the index or null.
     */
    public Value get(int i) {
        Value result = null;
        switch (valueType) {
            case ARRAY:
                result = ( (JSON.Array<?>) this ).get(i);
                break;
            case OBJECT:
                result = ( (JSON.Object<?, ?>) this ).get(i);
                break;
        }
        return result;
    }

    /**
     * Tests if there is a Value at the specified key. This method only responds
     * when the Value is of internalType OBJECT. All other cases will return false.
     * @param key Key to extract the value from.
     * @return true when there is a value at the key.
     */
    public boolean has(String key) {
        boolean result = false;
        switch (valueType) {
            case OBJECT:
                result = ( (JSON.Object<?, ?>) this ).containsKey(new JSON.String(key));
                break;
        }
        return result;
    }

    public Set<JSON.String> valueKeySet() {
        Set<JSON.String> keys = null;
        switch (valueType) {
            case OBJECT:
                keys = ( (JSON.Object<JSON.String, Value>) this ).mapValue.keySet();
                break;
        }
        return keys;
    }

    /**
     * Gets a Value at a key for the current Value. This method only responds
     * when the Value is of internalType OBJECT. All other cases will return null.
     * @param key Key to extract the value from.
     * @return Value The value at the given Key.
     */
    public Value get(String key) {
        Value result = null;
        switch (valueType) {
            case OBJECT:
                result = ( (JSON.Object<?, ?>) this ).get(new JSON.String(key));
                break;
        }
        return result;
    }

    /**
     * Converts the current Value into a JSON String that represents it.
     * @return The JSON Value as JSON String.
     */
    public abstract String toJSON();

    /**
     * Default toString for a JSON Value. Returns the JSON string for the Value.
     * @return
     */
    @Override
    public String toString() {
        return toJSON();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Value o = (Value) obj;
        boolean equalsResult = false;
        if (internalType() == Value.TYPE.OBJECT) {
            if (o.internalType() == Value.TYPE.OBJECT) {
                if (o.size() == size()) {
                    equalsResult = true;
                    Iterator<JSON.String> keySetIterator = valueKeySet().iterator();                    
                    Value thisValue = null;
                    Value otherValue = null;
                    while(keySetIterator.hasNext()) {
                        JSON.String key = keySetIterator.next();
                        thisValue = get(key.getString());
                        otherValue = o.get(key.getString());
                        if(otherValue != null) {
                            if(!thisValue.equals(otherValue)) {
                                equalsResult = false;
                                break;
                            }
                        }
                    }
                } else {
                    equalsResult = false;
                }
            } else {
                equalsResult = toString().equals(o.toString());
            }
        } else if (internalType() == Value.TYPE.ARRAY) {
            if (o.internalType() == Value.TYPE.ARRAY) {
                if (o.size() == size()) {
                    equalsResult = true;
                    for (int i = 0; i < size(); i++) {
                        if (!get(i).equals(o.get(i))) {
                            equalsResult = false;
                            break;
                        }
                    }
                } else {
                    equalsResult = false;
                }
            } else {
                equalsResult = toString().equals(o.toString());
            }
        } else if (internalType() == Value.TYPE.NULL) {
            equalsResult = ( isNull() && o.isNull() );
        } else if (internalType() == Value.TYPE.TRUE || internalType() == Value.TYPE.FALSE) {
            equalsResult = ( getBoolean() == o.getBoolean() );
        } else if (internalType() == Value.TYPE.NUMERIC) {
            Number thisNumber = getNumber();
            Number otherNumber = o.getNumber();
            if (thisNumber != null && otherNumber != null) {
                equalsResult = thisNumber.equals(otherNumber);
            }
        } else if (internalType() == Value.TYPE.STRING) {
            equalsResult = getString().equals(o.getString());
        }
        return equalsResult;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + ( this.valueType != null ? this.valueType.hashCode() : 0 );
        if (internalType() == Value.TYPE.OBJECT) {
            for (int i = 0; i < size(); i++) {
                hash = 13 * hash + ( get(i).hashCode() );
            }
        } else if (internalType() == Value.TYPE.ARRAY) {
            for (int i = 0; i < size(); i++) {
                hash = 13 * hash + ( get(i).hashCode() );
            }
        } else if (internalType() == Value.TYPE.NULL) {
            hash = 13 * hash + ( JSON.NULL.hashCode() );
        } else if (internalType() == Value.TYPE.TRUE) {
            hash = 13 * hash + ( JSON.TRUE.hashCode() );
        } else if (internalType() == Value.TYPE.FALSE) {
            hash = 13 * hash + ( JSON.FALSE.hashCode() );
        } else if (internalType() == Value.TYPE.NUMERIC) {
            hash = 13 * hash + ( getNumber().hashCode() );
        } else if (internalType() == Value.TYPE.STRING) {
            hash = 13 * hash + ( getString().hashCode() );
        }
        return hash;
    }

    @Override
    public int compareTo(Value o) {
        int compare = 0;
        if (internalType() == Value.TYPE.OBJECT) {
            if (internalType() == Value.TYPE.NULL || internalType() == Value.TYPE.TRUE || internalType() == Value.TYPE.FALSE) {
                compare = 1;
            } else {
                compare = toJSON().compareTo(o.toJSON());
            }
        } else if (internalType() == Value.TYPE.ARRAY) {
            if (internalType() == Value.TYPE.NULL || internalType() == Value.TYPE.TRUE || internalType() == Value.TYPE.FALSE || internalType() == Value.TYPE.OBJECT) {
                compare = 1;
            } else {
                compare = toJSON().compareTo(o.toJSON());
            }
        } else if (internalType() == Value.TYPE.NULL) {
            if (o.internalType() == Value.TYPE.NULL) {
                compare = 0;
            } else {
                compare = -1;
            }
        } else if (internalType() == Value.TYPE.TRUE) {
            if (o.internalType() == Value.TYPE.NULL) {
                compare = 1;
            } else if (o.internalType() == Value.TYPE.TRUE) {
                compare = 0;
            } else {
                compare = -1;
            }
        } else if (internalType() == Value.TYPE.FALSE) {
            if (o.internalType() == Value.TYPE.NULL) {
                compare = 1;
            } else if (o.internalType() == Value.TYPE.TRUE) {
                compare = 1;
            } else if (o.internalType() == Value.TYPE.FALSE) {
                compare = 0;
            } else {
                compare = -1;
            }
        } else if (internalType() == Value.TYPE.NUMERIC) {
            double thisDouble = getDouble();
            double thatDouble = o.getDouble();
            if (thisDouble < thatDouble) {
                compare = -1;
            } else if (thisDouble == thatDouble) {
                compare = 0;
            } else if (thisDouble > thatDouble) {
                compare = 1;
            }

        } else if (internalType() == Value.TYPE.STRING) {
            compare = getString().compareTo(o.getString());
        }
        return compare;
    }
}
