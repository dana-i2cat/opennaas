/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper.capability.example;

import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 
 * @author ahammaa
 */

public class IntSet extends AbstractSet<Integer>
		implements java.io.Serializable {

	private static final long	serialVersionUID	= 1L;

	/**
	 * The bitset that contains the non-negative intergers in this set (include 0).
	 */
	private final BitSet		bitSet;

	/**
	 * Creates a new, empty set of {@code int} values.
	 */
	public IntSet() {
		this(new BitSet());
	}

	/**
	 * Creates a new set backed by the provided bitset.
	 * 
	 * @see #wrap(BitSet)
	 */
	private IntSet(BitSet bitSet) {
		this.bitSet = bitSet;
	}

	/**
	 * Creates a new set containing all of the specified {@code int} values.
	 */
	public IntSet(Collection<Integer> ints) {
		this();
		for (Integer i : ints)
			bitSet.set(i);
	}

	public boolean add(Integer i) {
		return add(i.intValue());
	}

	public boolean add(int i) {
		if (i < 0)
			throw new IllegalArgumentException(
					"Cannot store negative values in an IntSet");
		boolean isPresent = bitSet.get(i);
		bitSet.set(i);
		return !isPresent;
	}

	/**
	 * Adds to this set all of the elements that are contained in the specified {@code IntSet} if not already present, using an {@code IntSet}
	 * -optimized process.
	 */
	public boolean addAll(IntSet ints) {
		int oldSize = size();
		bitSet.or(ints.bitSet);
		return oldSize != size();
	}

	public boolean contains(Integer i) {
		return contains(i.intValue());
	}

	public boolean contains(int i) {
		return i >= 0 && bitSet.get(i);
	}

	public boolean isEmpty() {
		return bitSet.isEmpty();
	}

	public Iterator<Integer> iterator() {
		return new BitSetIterator();
	}

	public boolean remove(Integer i) {
		return remove(i.intValue());
	}

	public boolean remove(int i) {
		if (i < 0)
			return false;
		boolean isPresent = bitSet.get(i);
		if (isPresent)
			bitSet.set(i, false);
		return isPresent;
	}

	/**
	 * Removes from this set all of the elements that are contained in the specified {@code IntSet} using an {@code IntSet}-optimized process.
	 */
	public boolean removeAll(IntSet ints) {
		int oldSize = size();
		bitSet.andNot(ints.bitSet);
		return oldSize != size();
	}

	/**
	 * Retains only the elements in this set that are contained in the specified {@code IntSet} using an {@code IntSet}-optimized process.
	 */
	public boolean retainAll(IntSet ints) {
		int oldSize = size();
		bitSet.and(ints.bitSet);
		return oldSize != size();
	}

	public int size() {
		return bitSet.cardinality();
	}

	/**
	 * Wraps the provided {@code BitSet} as a {@link Set} returning the result. Any changes to the set will be reflected in {@code b} and vice-versa.
	 */
	public static Set<Integer> wrap(BitSet b) {
		return new IntSet(b);
	}

	/**
	 * An iterator over the integers in the backing {@code BitSet}.
	 */
	private class BitSetIterator implements Iterator<Integer> {

		int	next	= -1;
		int	cur		= -1;

		public BitSetIterator() {
			advance();
		}

		private void advance() {
			if (next < -1)
				return;
			next = bitSet.nextSetBit(next + 1);
			// Keep track of when we finally go off the end
			if (next == -1)
				next = -2;
		}

		public boolean hasNext() {
			return next >= 0;
		}

		public Integer next() {
			if (!hasNext())
				throw new NoSuchElementException();
			cur = next;
			advance();
			return cur;
		}

		public void remove() {
			if (cur == -1)
				throw new IllegalStateException("Item already removed");
			bitSet.set(cur, false);
			cur = -1;
		}
	}
}
