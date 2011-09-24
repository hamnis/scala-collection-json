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
public class DoubleInternalNumber extends InternalNumber implements Serializable {
    
    double value;
    
    public DoubleInternalNumber(double value) {
        this.value = value;
    }

    public byte byteValue() {
        return new Double(value).byteValue();
    }

    public double doubleValue() {
        return new Double(value).doubleValue();
    }

    public float floatValue() {
        return new Double(value).floatValue();
    }

    public int intValue() {
        return new Double(value).intValue();
    }

    public long longValue() {
        return new Double(value).longValue();
    }

    public short shortValue() {
        return new Double(value).shortValue();
    }

    @Override
    public Number getNumber() {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#toJSON()
     */
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
        final DoubleInternalNumber other = (DoubleInternalNumber) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (int) ( Double.doubleToLongBits(this.value) ^ ( Double.doubleToLongBits(this.value) >>> 32 ) );
        return hash;
    }
}
