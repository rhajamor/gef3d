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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * PointQueue There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.11.2009
 */
public class PointQueue {

	private static class IndexedList {

		private int m_index = -1;

		private PointList m_list;

		public IndexedList(PointList i_list) {

			m_list = i_list;
		}

		public void dec() {

			if (m_index <= -1)
				throw new NoSuchElementException();
		}

		public int getX() {

			return m_list.getX(m_index);
		}

		public int getY() {

			return m_list.getY(m_index);
		}

		public boolean hasNext() {

			return m_index < m_list.getSize() - 1;
		}

		public void inc() {

			if (!hasNext())
				throw new NoSuchElementException();

			m_index++;
		}
	}

	private static class Point implements Comparable<Point> {

		private int m_x;

		private int m_y;

		public Point(int i_x, int i_y) {

			m_x = i_x;
			m_y = i_y;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Point i_o) {

			return getX() - i_o.getX();
		}

		public int getX() {
			return m_x;
		}

		public int getY() {
			return m_y;
		}
	}

	private int m_next = -1;

	private List<IndexedList> m_readable = new ArrayList<IndexedList>();

	private AVLTree<Point> m_writable = new AVLTree<Point>();

	public PointQueue(PointList... i_lists) {

		for (PointList list : i_lists)
			m_readable.add(new IndexedList(list));
	}

	private void findNext() {

		if (m_next != -2)
			return;

		int x;
		if (m_writable.size() > 0) {
			m_next = -1;
			x = m_writable.getFirst().getX();
		} else {
			m_next = -2;
			x = Integer.MIN_VALUE;
		}

		for (int i = 0; i < m_readable.size(); i++) {
			IndexedList list = m_readable.get(i);
			if (list.hasNext()) {
				list.inc();
				int listX = list.getX();
				if (listX < x) {
					x = listX;
					m_next = i;
				}
				list.dec();
			}
		}
	}

	public int getX() {

		if (m_next == -2)
			throw new NoSuchElementException();

		if (m_next == -1)
			return m_writable.getFirst().getX();

		return m_readable.get(m_next).getX();
	}

	public int getY() {

		if (m_next == -2)
			throw new NoSuchElementException();

		if (m_next == -1)
			return m_writable.getFirst().getY();

		return m_readable.get(m_next).getY();
	}

	public boolean isEmpty() {

		findNext();
		return m_next == -2;
	}

	public void pop() {

		if (isEmpty())
			throw new NoSuchElementException();

		if (m_next == -1)
			m_writable.remove(m_writable.getFirst());
		else
			m_readable.get(m_next).inc();

		m_next = -2;
	}

	public void push(int i_x, int i_y) {

		m_writable.insert(new Point(i_x, i_y));
	}

}
