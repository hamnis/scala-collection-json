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

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import static jsonij.json.Constants.OPEN_OBJECT;
import static jsonij.json.Constants.CLOSE_OBJECT;
import static jsonij.json.Constants.QUOTATION_MARK;
import static jsonij.json.Constants.NAME_SEPARATOR;

public class ObjectImp<K extends JSON.String, V extends Value> extends Value implements java.util.Map<K, V> {

    /**
     * Holds the Mapping Values for the Object
     */
    protected LinkedHashMap<K, V> mapValue;
    /**
     * Holds the Key order so values can be extracted in the order they were added.
     */
    protected ArrayList<K> valueOrder;

    /**
     * Default Constructor
     */
    public ObjectImp() {
        mapValue = new LinkedHashMap<K, V>();
        valueOrder = new ArrayList<K>();
    }

    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#internalType()
     */
    @Override
    protected TYPE internalType() {
        return TYPE.OBJECT;
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        mapValue.clear();
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return mapValue.containsKey((K) key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return mapValue.containsValue((V) value);
    }

    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return mapValue.entrySet();
    }

    /* (non-Javadoc)
     * @see java.util.List#get(java.lang.Integer)
     */
    @Override
    public V get(int i) {
        return mapValue.get((K) valueOrder.get(i));
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
        return mapValue.get((K) key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return mapValue.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<K> keySet() {
        return mapValue.keySet();
    }

    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) {
        valueOrder.add(key);
        return mapValue.put(key, value);
    }

    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K k : m.keySet()) {
            valueOrder.add(k);
        }
        mapValue.putAll(m);
    }

    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public V remove(Object key) {
        valueOrder.remove((K) key);
        return mapValue.remove((K) key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        return mapValue.size();
    }

    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    @Override
    public Collection<V> values() {
        return mapValue.values();
    }

    @Override
    public int nestedSize() {
        int c = 0;
        for (V v : values()) {
            c += v.nestedSize();
        }
        return size() + c;
    }

    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#toJSON()
     */
    @Override
    public String toJSON() {
        K k = null;
        Value v = null;
        Iterator keyIterator = valueOrder.iterator();
        if (keyIterator.hasNext()) {
            StringBuilder json = new StringBuilder();
            json.append((char) OPEN_OBJECT);
            k = (K) keyIterator.next();
            v = get(k);
            json.append((char) QUOTATION_MARK).append(k.toString()).append((char) QUOTATION_MARK).append((char) NAME_SEPARATOR).append(v.toJSON());
            while (keyIterator.hasNext()) {
                k = (K) keyIterator.next();
                v = get(k);
                json.append((char) ',').append((char) QUOTATION_MARK).append(k.toString()).append((char) QUOTATION_MARK).append((char) NAME_SEPARATOR).append(v.toJSON());
            }
            json.append((char) CLOSE_OBJECT);
            return json.toString();
        } else {
            return "{}";
        }
    }
}
