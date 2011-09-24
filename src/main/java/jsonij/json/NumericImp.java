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
import java.math.BigInteger;

public class NumericImp extends Value implements Serializable {

    protected InternalNumber internalNumberData;
    
    public NumericImp(int value) {
        internalNumberData = new LongInternalNumber(value);
    }
    
    public NumericImp(long value) {
        internalNumberData = new LongInternalNumber(value);
    }
    
    public NumericImp(double value) {
        internalNumberData = new DoubleInternalNumber(value);
    }
     
    public NumericImp(Number numberValue) {
        internalNumberData = new NumberInternalNumber(numberValue);
    }
    
    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#internalType()
     */
    @Override
    protected TYPE internalType() {
        return TYPE.NUMERIC;
    }

    public byte byteValue() {
        return internalNumberData.byteValue();
    }

    public double doubleValue() {
        return internalNumberData.doubleValue();
    }

    public float floatValue() {
        return internalNumberData.floatValue();
    }

    public int intValue() {
        return internalNumberData.intValue();
    }

    public long longValue() {
        return internalNumberData.longValue();
    }

    public short shortValue() {
        return internalNumberData.shortValue();
    }

    @Override
    public InternalNumber getNumber() {
        return internalNumberData;
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
        return internalNumberData.toJSON();
    }
}
