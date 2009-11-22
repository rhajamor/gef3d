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

import java.util.NoSuchElementException;

/**
 * PointQueueImpl There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 19.11.2009
 */
public class PointQueue {

	private int m_index;

	private int m_size;

	private int[] m_sorted;

	public PointQueue(int[] i_sorted, int i_size) {

		m_sorted = i_sorted;
		m_size = i_size;
		m_index = 0;
	}

	public boolean isEmpty() {

		return m_index == m_size;
	}

	public int next() {

		if (isEmpty())
			throw new NoSuchElementException();

		if (m_sorted[m_index] == m_size - 1)
			return -1;

		return m_sorted[m_index] + 1;
	}

	public int peek() {

		if (isEmpty())
			throw new NoSuchElementException();

		return m_sorted[m_index];
	}

	public int pop() {

		if (isEmpty())
			throw new NoSuchElementException();

		return m_sorted[m_index]++;
	}

	public int previous() {

		if (isEmpty())
			throw new NoSuchElementException();

		if (m_sorted[m_index] == 0)
			return -1;

		return m_sorted[m_index] - 1;
	}

	public int[] push(int i_x, int i_y, int[] i_points) {

		int[] points = i_points;
		if (m_size == points.length / 2) {
			int[] t = points;
			points = new int[2 * points.length];
			System.arraycopy(t, 0, points, 0, t.length);
		}

		if (m_size == m_sorted.length) {
			int[] t = m_sorted;
			m_sorted = new int[2 * m_sorted.length];
			System.arraycopy(t, 0, m_sorted, 0, t.length);
		}

		// find insert position
		// TODO use binary search
		int ins = 0;
		while (ins < m_size && points[2 * m_sorted[ins]] < i_x)
			ins++;

		if (ins < m_size)
			System.arraycopy(m_sorted, ins, m_sorted, ins + 1, m_size - ins);

		points[2 * m_size] = i_x;
		points[2 * m_size + 1] = i_y;
		m_sorted[ins] = m_size;

		m_size++;
		if (ins < m_index)
			m_index++;

		return points;
	}

	public int size() {

		return m_size;
	}

}
