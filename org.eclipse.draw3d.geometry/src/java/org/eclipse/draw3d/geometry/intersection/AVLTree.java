/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.geometry.intersection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Pointer based implementation of an AVL tree (see
 * http://en.wikipedia.org/wiki/AVL_tree). This implementation does not allow
 * duplicates.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @param <T> the data type contained in the tree
 * @since 20.11.2009
 */
public class AVLTree<T> implements Iterable<T> {

	private class AVLNode {

		private T data;

		private int height;

		private AVLNode left;

		private AVLNode next;

		private AVLNode parent;

		private AVLNode previous;

		private AVLNode right;

		public AVLNode(AVLNode i_parent, T i_data) {

			parent = i_parent;
			data = i_data;
			height = 0;

			m_nodeMap.put(i_data, this);
		}

		private int getBalance() {

			int l = left != null ? left.height : -1;
			int r = right != null ? right.height : -1;

			return r - l;
		}

		public T getData() {

			return data;
		}

		public AVLNode getNext() {

			return this.next;
		}

		public AVLNode getPrevious() {

			return this.previous;
		}

		public boolean insert(T i_data) {

			int c = compare(i_data, this.data);
			if (c < 0) {
				if (this.left == null) {
					this.left = new AVLNode(this, i_data);
					this.height = 1; // height was either 0 or 1

					if (this.previous != null) {
						this.previous.next = this.left;
						this.left.previous = this.previous;
					} else
						m_first = this.left;

					this.previous = this.left;
					this.left.next = this;

					return true;
				} else {
					boolean success = this.left.insert(i_data);
					if (success) {
						updateHeight();
						rebalanceAfterInsert();
					}
					return success;
				}
			} else if (c > 0) {
				if (this.right == null) {
					this.right = new AVLNode(this, i_data);
					this.height = 1; // height was either 0 or 1

					if (this.next != null) {
						this.next.previous = this.right;
						this.right.next = this.next;
					} else
						m_last = this.right;

					this.next = this.right;
					this.right.previous = this;

					return true;
				} else {
					boolean success = this.right.insert(i_data);
					if (success) {
						updateHeight();
						rebalanceAfterInsert();
					}
					return success;
				}
			} else
				return false;
		}

		public boolean isLeft() {

			if (this.parent == null)
				return false;

			return this.parent.left == this;
		}

		public boolean isRight() {

			if (this.parent == null)
				return false;

			return this.parent.right == this;
		}

		public AVLNode query(Object i_query, Comparator<Object> i_comparator) {

			int c = i_comparator.compare(i_query, data);
			if (c < 0 && left != null)
				return left.query(i_query, i_comparator);
			else if (c > 0 && right != null)
				return right.query(i_query, i_comparator);
			else if (c == 0)
				return this;

			return null;
		}

		public AVLNode queryNext(Object i_query, Comparator<Object> i_comparator) {

			if (i_comparator.compare(i_query, data) < 0) {
				AVLNode result = null;
				if (left != null)
					result = left.queryNext(i_query, i_comparator);

				if (result != null)
					return result;

				return this;
			} else {
				if (right != null)
					return right.queryNext(i_query, i_comparator);

				return null;
			}
		}

		public AVLNode queryPrevious(Object i_query,
			Comparator<Object> i_comparator) {

			if (i_comparator.compare(i_query, data) > 0) {
				AVLNode result = null;
				if (right != null)
					result = right.queryPrevious(i_query, i_comparator);

				if (result != null)
					return result;

				return this;
			} else {
				if (left != null)
					return left.queryPrevious(i_query, i_comparator);

				return null;
			}
		}

		private void rebalanceAfterInsert() {

			int b = getBalance();
			if (b < -1) {
				if (left.getBalance() < 0) {
					rotateCW();
				} else {
					left.rotateCCW();
					rotateCW();
				}
			} else if (b > 1) {
				if (right.getBalance() > 0) {
					rotateCCW();
				} else {
					right.rotateCW();
					rotateCCW();
				}
			}
		}

		private void rebalanceAfterRemove() {

			int b = getBalance();
			if (b < -1) {
				if (left.getBalance() <= 0) {
					rotateCW();
				} else {
					left.rotateCCW();
					rotateCW();
				}
			} else if (b > 1) {
				if (right.getBalance() >= 0) {
					rotateCCW();
				} else {
					right.rotateCW();
					rotateCCW();
				}
			}
		}

		public boolean remove(T i_data) {

			int c = compare(i_data, this.data);
			if (c < 0) {
				if (this.left == null) {
					return false;
				} else {
					boolean success = this.left.remove(i_data);
					if (success) {
						updateHeight();
						rebalanceAfterRemove();
					}
					return success;
				}
			} else if (c > 0) {
				if (this.right == null) {
					return false;
				} else {
					boolean success = this.right.remove(i_data);
					if (success) {
						updateHeight();
						rebalanceAfterRemove();
					}
					return success;
				}
			} else {
				m_nodeMap.remove(i_data);

				if (this.left == null || this.right == null) {
					AVLNode child = null;
					if (this.left != null)
						child = this.left;
					else if (this.right != null)
						child = this.right;

					if (isLeft())
						this.parent.left = child;
					else if (isRight())
						this.parent.right = child;
					else
						m_root = child;

					if (child != null)
						child.parent = this.parent;

					if (this.previous != null)
						this.previous.next = this.next;
					else
						m_first = this.next;

					if (this.next != null)
						this.next.previous = this.previous;
					else
						m_last = this.previous;
				} else {
					AVLNode node = getNext();

					this.next = node.next;
					if (this.next != null)
						this.next.previous = this;
					else
						m_last = this;

					if (node.isLeft())
						node.parent.left = node.right;
					else
						node.parent.right = node.right;

					if (node.right != null)
						node.right.parent = node.parent;

					this.data = node.data;
					m_nodeMap.put(this.data, this);

					do {
						node = node.parent;
						node.updateHeight();
						node.rebalanceAfterRemove();
					} while (node != this);
				}

				return true;
			}
		}

		private void rotateCCW() {

			AVLNode tmpRight = this.right;
			AVLNode tmpParent = this.parent;

			this.right = tmpRight.left;
			if (this.right != null)
				this.right.parent = this;

			tmpRight.left = this;
			this.parent = tmpRight;

			if (tmpParent == null) {
				m_root = tmpRight;
				tmpRight.parent = null;
			} else if (tmpParent.left == this) {
				tmpParent.left = tmpRight;
				tmpRight.parent = tmpParent;
			} else {
				tmpParent.right = tmpRight;
				tmpRight.parent = tmpParent;
			}

			updateHeight();
			tmpRight.updateHeight();

			if (tmpParent != null)
				tmpParent.updateHeight();
		}

		private void rotateCW() {

			AVLNode tmpLeft = this.left;
			AVLNode tmpParent = this.parent;

			this.left = tmpLeft.right;
			if (this.left != null)
				this.left.parent = this;

			tmpLeft.right = this;
			this.parent = tmpLeft;

			if (tmpParent == null) {
				m_root = tmpLeft;
			} else if (tmpParent.left == this) {
				tmpParent.left = tmpLeft;
				tmpLeft.parent = tmpParent;
			} else {
				tmpParent.right = tmpLeft;
				tmpLeft.parent = tmpParent;
			}

			updateHeight();
			tmpLeft.updateHeight();

			if (tmpParent != null)
				tmpParent.updateHeight();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			return data.toString() + ":" + height;
		}

		private void updateHeight() {

			int l = this.left != null ? this.left.height : -1;
			int r = this.right != null ? this.right.height : -1;

			this.height = Math.max(l, r) + 1;
		}
	}

	private class AVLTreeIterator implements Iterator<T> {

		private AVLNode m_node;

		public AVLTreeIterator(AVLNode i_startNode) {

			m_node = i_startNode;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {

			return m_node != null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Iterator#next()
		 */
		public T next() {

			if (!hasNext())
				throw new NoSuchElementException();

			AVLNode tmp = m_node;
			m_node = m_node.getNext();

			return tmp.getData();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {

			if (!hasNext())
				throw new NoSuchElementException();

			AVLNode tmp = m_node;
			m_node = m_node.getNext();

			AVLTree.this.remove(tmp.getData());
		}
	}

	private Comparator<? super T> m_comparator;

	private AVLNode m_first;

	@SuppressWarnings("unused")
	private AVLNode m_last;

	private Map<T, AVLNode> m_nodeMap = new HashMap<T, AVLNode>();

	private AVLNode m_root;

	/**
	 * Creates a new empty tree.
	 */
	public AVLTree() {
		// nothing to initialize
	}

	/**
	 * Creates a new empty tree that uses the given comparator to determine the
	 * order of the inserted elements.
	 * 
	 * @param i_comparator the comparator
	 */
	public AVLTree(Comparator<? super T> i_comparator) {

		if (i_comparator == null)
			throw new NullPointerException("i_comparator must not be null");

		m_comparator = i_comparator;
	}

	/**
	 * Clears this tree.
	 */
	public void clear() {

		m_first = null;
		m_last = null;
		m_root = null;
		m_nodeMap.clear();
	}

	@SuppressWarnings("unchecked")
	private int compare(T i_o1, T i_o2) {

		if (m_comparator != null)
			return m_comparator.compare(i_o1, i_o2);

		Comparable<? super T> comparable = (Comparable<? super T>) i_o1;
		return comparable.compareTo(i_o2);
	}

	/**
	 * Indicates whether the given element is contained in this tree.
	 * 
	 * @param i_data the element to check for
	 * @return <code>true</code> if the given element is contained in this tree
	 *         or <code>false</code> otherwise
	 */
	public boolean contains(T i_data) {

		return m_nodeMap.containsKey(i_data);
	}

	/**
	 * Returns the data object that has the same order index as the given data
	 * object.
	 * 
	 * @param i_data the data object to search for
	 * @return a data object with the same order index or <code>null</code> if
	 *         no such data object is stored in this tree
	 */
	public T get(T i_data) {

		if (i_data == null)
			throw new NullPointerException("i_data must not be null");

		return m_nodeMap.get(i_data).getData();
	}

	/**
	 * Returns the first element in this tree.
	 * 
	 * @return the first element or <code>null</code> if this tree is empty
	 */
	public T getFirst() {

		if (m_first == null)
			return null;

		return m_first.getData();
	}

	/**
	 * Returns the last element in this tree.
	 * 
	 * @return the last element or <code>null</code> if this tree is empty
	 */
	public T getLast() {

		if (m_last == null)
			return null;

		return m_last.getData();
	}

	/**
	 * Returns the successor of the given element, if any.
	 * 
	 * @param i_data the element whose successor is requested
	 * @return the successor of the given element or <code>null</code> if the
	 *         given element is not contained in this tree or if the given
	 *         element does not have a successor
	 */
	public T getNext(T i_data) {

		AVLNode node = m_nodeMap.get(i_data);
		if (node != null) {
			AVLNode nextNode = node.getNext();
			if (nextNode != null)
				return nextNode.getData();
		}

		return null;
	}

	/**
	 * Returns the predecessor of the given element, if any.
	 * 
	 * @param i_data the element whose predecessor is requested
	 * @return the predecessor of the given element or <code>null</code> if the
	 *         given element is not contained in this tree or if the given
	 *         element does not have a predecessor
	 */
	public T getPrevious(T i_data) {

		AVLNode node = m_nodeMap.get(i_data);
		if (node != null) {
			AVLNode previousNode = node.getPrevious();
			if (previousNode != null)
				return previousNode.getData();
		}

		return null;
	}

	/**
	 * Inserts the given element into this tree.
	 * 
	 * @param i_data the element to insert
	 * @return <code>true</code> if the given element was inserted or
	 *         <code>false</code> if the element was already contained in this
	 *         tree
	 */
	public boolean insert(T i_data) {

		if (i_data == null)
			throw new NullPointerException("i_data must not be null");

		if (m_root != null) {
			return m_root.insert(i_data);
		} else {
			m_root = new AVLNode(null, i_data);
			m_first = m_root;
			m_last = m_root;
			return true;
		}
	}

	/**
	 * Indicates whether this tree is empty.
	 * 
	 * @return <code>true</code> if this tree is empty and <code>false</code>
	 *         otherwise
	 */
	public boolean isEmpty() {

		return m_root == null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {

		return new AVLTreeIterator(m_first);
	}

	public T query(Object i_query, Comparator<Object> i_comparator) {

		if (i_query == null)
			throw new NullPointerException("i_query must not be null");

		if (i_comparator == null)
			throw new NullPointerException("i_comparator must not be null");

		AVLNode node = m_root.query(i_query, i_comparator);
		if (node != null)
			return node.getData();

		return null;
	}

	public T queryNext(Object i_query, Comparator<Object> i_comparator) {

		if (i_query == null)
			throw new NullPointerException("i_query must not be null");

		if (i_comparator == null)
			throw new NullPointerException("i_comparator must not be null");

		AVLNode node = m_root.queryNext(i_query, i_comparator);
		if (node != null)
			return node.getData();

		return null;
	}

	public T queryPrevious(Object i_query, Comparator<Object> i_comparator) {

		if (i_query == null)
			throw new NullPointerException("i_query must not be null");

		if (i_comparator == null)
			throw new NullPointerException("i_comparator must not be null");

		AVLNode node = m_root.queryPrevious(i_query, i_comparator);
		if (node != null)
			return node.getData();

		return null;
	}

	/**
	 * Removes the given element from this tree.
	 * 
	 * @param i_data the element to remove
	 * @return <code>true</code> if the given element was removed or
	 *         <code>false</code> if the given element was not contained in this
	 *         tree
	 */
	public boolean remove(T i_data) {

		if (i_data == null)
			throw new NullPointerException("i_data must not be null");

		if (m_root != null)
			return m_root.remove(i_data);
		else
			return false;
	}

	/**
	 * Returns the number of elements contained in this tree.
	 * 
	 * @return the number of elements
	 */
	public int size() {

		return m_nodeMap.size();
	}

	/**
	 * Returns an array containing the elements in this tree.
	 * 
	 * @return an array
	 */
	@SuppressWarnings("unchecked")
	public T[] toArray() {

		Object[] array = new Object[size()];

		int i = 0;
		for (Object data : this)
			array[i++] = data;

		return (T[]) array;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		if (m_root == null)
			return "[]";

		StringBuilder b = new StringBuilder();
		toString(m_root, b);
		return b.toString();
	}

	private void toString(AVLNode i_node, StringBuilder i_builder) {

		i_builder.append("[");
		if (i_node.left != null)
			toString(i_node.left, i_builder);
		else if (i_node.right != null)
			i_builder.append("[]");
		i_builder.append(i_node.getData());
		if (i_node.right != null)
			toString(i_node.right, i_builder);
		else if (i_node.left != null)
			i_builder.append("[]");
		i_builder.append("]");
	}
}
