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

		public AVLNode findNode(T i_data) {

			int c = compare(i_data, data);
			if (c < 0)
				return left.findNode(i_data);

			if (c > 0)
				return right.findNode(i_data);

			return this;
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
					if (this.left == null && this.right == null) {
						if (this.parent == null)
							m_root = null;
						else if (this == this.parent.left)
							this.parent.left = null;
						else
							this.parent.right = null;
					} else if (this.left == null) {
						if (this.parent == null)
							m_root = this.right;
						else if (this == this.parent.left)
							this.parent.left = this.right;
						else
							this.parent.right = this.right;
						this.right.parent = this.parent;
					} else if (this.right == null) {
						if (this.parent == null)
							m_root = this.left;
						else if (this == this.parent.left)
							this.parent.left = this.left;
						else
							this.parent.right = this.left;
						this.left.parent = this.parent;
					}

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
					this.data = node.data;

					this.next = node.next;
					if (this.next != null)
						this.next.previous = this;

					if (m_first == node)
						m_first = this;

					if (m_last == node)
						m_last = this;

					node.parent.left = node.right;
					m_nodeMap.put(this.data, this);

					while (node.parent != null) {
						node = node.parent;
						node.updateHeight();
					}
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
	 * Returns the successor of the given element, if any.
	 * 
	 * @param i_data the element whose successor is requested
	 * @return the successor of the given element or <code>null</code> if the
	 *         given element is not contained in this tree or if the given
	 *         element does not have a successor
	 */
	public T getNext(T i_data) {

		AVLNode node = m_nodeMap.get(i_data);
		if (node == null)
			return null;

		AVLNode nextNode = node.getNext();
		if (nextNode == null)
			return null;

		return nextNode.getData();
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
		if (node == null)
			return null;

		AVLNode previousNode = node.getPrevious();
		if (previousNode == null)
			return null;

		return previousNode.getData();
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
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {

		return new AVLTreeIterator(m_first);
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
