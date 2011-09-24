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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 * @author openecho
 * @version 1.0.0
 */
public class ArrayImp<E extends Value> extends Value implements java.util.List<E> {

    protected List<E> arrayValue;

    public ArrayImp() {
        arrayValue = new ArrayList<E>();
    }

    /* (non-Javadoc)
     * @see com.realitypipe.json.Value#internalType()
     */
    @Override
    protected TYPE internalType() {
        return TYPE.ARRAY;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(E e) {
        return arrayValue.add(e);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, E element) {
        arrayValue.add(index, element);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return arrayValue.addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return arrayValue.addAll(index, c);
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    @Override
    public void clear() {
        arrayValue.clear();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return arrayValue.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return arrayValue.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    @Override
    public E get(int index) {
        return arrayValue.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object o) {
        return arrayValue.indexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return arrayValue.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return arrayValue.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object o) {
        return arrayValue.lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<E> listIterator() {
        return arrayValue.listIterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return arrayValue.listIterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return arrayValue.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    @Override
    public E remove(int index) {
        return arrayValue.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return arrayValue.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return arrayValue.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E element) {
        return arrayValue.set(index, element);
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    @Override
    public int size() {
        return arrayValue.size();
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return arrayValue.subList(fromIndex, toIndex);
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray() {
        return arrayValue.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return arrayValue.toArray(a);
    }

    @Override
    public int nestedSize() {
        int c = 0;
        for (E e : this) {
            c += e.nestedSize();
        }
        return size() + c;
    }

    @Override
    public String toJSON() {
        Iterator<E> valueIterator = iterator();
        if(valueIterator.hasNext()) {
            StringBuilder jsonStringBuilder = new StringBuilder();
            jsonStringBuilder.append('[');
            Value value = valueIterator.next();
            jsonStringBuilder.append(value.toJSON());
            while(valueIterator.hasNext()) {
                value = valueIterator.next();
                jsonStringBuilder.append(',').append(value.toJSON());
            }
            jsonStringBuilder.append(']');
            return jsonStringBuilder.toString();
        } else {
            return "[]";
        }
    }

    @Override
    public String toString() {
        return toJSON();
    }

}
