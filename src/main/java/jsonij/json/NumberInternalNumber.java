/*
 * Copyright 2011 jmarsden.
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
 */
package jsonij.json;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author jmarsden
 */
public class NumberInternalNumber extends InternalNumber implements Serializable {
    
    protected Number value;
    
    public NumberInternalNumber(Number value) {
        this.value = value;
    }

    public byte byteValue() {
        return value.byteValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public float floatValue() {
        return value.floatValue();
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public short shortValue() {
        return value.shortValue();
    }

    @Override
    public Number getNumber() {
        return value;
    }

    @Override
    public String toJSON() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NumberInternalNumber other = (NumberInternalNumber) obj;
        if (this.value != other.value && ( this.value == null || !this.value.equals(other.value) )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + ( this.value != null ? this.value.hashCode() : 0 );
        return hash;
    }
    
    
}
