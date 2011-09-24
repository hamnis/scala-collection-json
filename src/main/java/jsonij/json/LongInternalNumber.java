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

/**
 *
 * @author jmarsden
 */
public class LongInternalNumber extends InternalNumber implements Serializable {

    long value;

    public LongInternalNumber(long value) {
        this.value = value;
    }

    public byte byteValue() {
        return new Long(value).byteValue();
    }

    public double doubleValue() {
        return new Long(value).doubleValue();
    }

    public float floatValue() {
        return new Long(value).floatValue();
    }

    public int intValue() {
        return new Long(value).intValue();
    }

    public long longValue() {
        return new Long(value).longValue();
    }

    public short shortValue() {
        return new Long(value).shortValue();
    }

    @Override
    public Number getNumber() {
        return new Long(value);
    }

    @Override
    public String toJSON() {
        return "" + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LongInternalNumber other = (LongInternalNumber) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (int) ( this.value ^ ( this.value >>> 32 ) );
        return hash;
    }
}
