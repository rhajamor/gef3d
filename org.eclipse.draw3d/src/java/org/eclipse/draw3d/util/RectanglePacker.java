/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * An algorithm that attempts to pack rectangles as tightly as possible into a
 * square. This algorithm was based on ideas presented in &quot;Two-Dimensional
 * Finite Bin-Packing Algorithms&quot; by J. 0. Berkey and P. Y. Wang (DUI
 * 0160-5682/87).
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @param <T> the type of the payload data associated with each rectangle
 * @since 05.01.2010
 */
public class RectanglePacker<T> {

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (!m_packed)
			return m_rectangles.toString();

		StringBuilder sb = new StringBuilder();
		for (Strip strip : m_strips) {
			sb.append(strip.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	private class Strip {

		private int m_height = 0;

		private int m_x = 0;

		private int m_y;

		private List<Rectangle> m_rectangles = new LinkedList<Rectangle>();

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[Y: ");
			sb.append(m_y);
			sb.append(", H: ");
			sb.append(m_height);
			sb.append(", N: ");
			sb.append(m_rectangles.size());
			sb.append("]");

			for (Iterator<Rectangle> i = m_rectangles.iterator(); i.hasNext();) {
				Rectangle rect = i.next();
				sb.append(" [X: ");
				sb.append(rect.x);
				sb.append(", Y: ");
				sb.append(rect.y);
				sb.append(", W: ");
				sb.append(rect.width);
				sb.append(", H: ");
				sb.append(rect.height);
				sb.append("]");

				if (i.hasNext())
					sb.append(",");
			}

			return sb.toString();
		}

		public Strip(int i_y, Rectangle i_firstRectangle) {
			m_y = i_y;
			m_height = i_firstRectangle.height;

			add(i_firstRectangle);
		}

		public boolean add(Rectangle i_rectangle) {
			if (i_rectangle.width > getCurrentLength() - m_x
				|| i_rectangle.height > m_height)
				return false;

			i_rectangle.x = m_x;
			i_rectangle.y = m_y;
			m_x += i_rectangle.width;

			m_rectangles.add(i_rectangle);
			return true;
		}
	}

	private Comparator<Rectangle> m_comparator = new Comparator<Rectangle>() {
		public int compare(Rectangle i_r1, Rectangle i_r2) {
			if (i_r1.height < i_r2.height)
				return 1;

			if (i_r1.height > i_r2.height)
				return -1;

			return 0;
		}
	};

	private int m_length = 16;

	private boolean m_packed = false;

	private Map<T, Rectangle> m_rectangles = new HashMap<T, Rectangle>();

	private List<Strip> m_strips = new LinkedList<Strip>();

	private int m_totalHeight = 0;

	/**
	 * Adds the given rectangle to this packer.
	 * 
	 * @param i_w the width of the rectangle
	 * @param i_h the height of the rectangle
	 * @param i_data the payload data
	 * @throws IllegalStateException if this packer has already been packed
	 * @throws NullPointerException if the given payload data is
	 *             <code>null</code>
	 */
	public void add(int i_w, int i_h, T i_data) {
		if (m_packed)
			throw new IllegalStateException(this + " is already packed");

		m_rectangles.put(i_data, new Rectangle(0, 0, i_w, i_h));
	}

	private int getCurrentLength() {
		return m_length;
	}

	/**
	 * Returns the dimension of the rectangle with the given payload data. This
	 * are the exact same values that haven been supplied to
	 * {@link #add(int, int, Object)}.
	 * 
	 * @param i_data the payload data
	 * @param io_result the result object, if <code>null</code>, a new instance
	 *            of {@link Dimension} will be created
	 * @return the dimension
	 * @throws IllegalArgumentException if no rectangle with the given payload
	 *             data has been added to this packer
	 * @throws IllegalStateException if this packer has not been packed yet
	 * @throws NullPointerException if the given payload data is
	 *             <code>null</code>
	 */
	public Dimension getDimension(T i_data, Dimension io_result) {
		if (!m_packed)
			throw new IllegalStateException(this + " has not been packed yet");
		if (i_data == null)
			throw new NullPointerException("i_data must not be null");

		Rectangle rectangle = m_rectangles.get(i_data);
		if (rectangle == null)
			throw new IllegalArgumentException(
				"no rectangle with payload data '" + i_data
					+ "' has been added to this packer");

		Dimension result = io_result;
		if (result == null)
			result = new Dimension();

		result.width = rectangle.width;
		result.height = rectangle.height;

		return result;
	}

	/**
	 * Returns the length of the square which the rectangles have been packed
	 * into.
	 * 
	 * @return the length
	 * @throws IllegalStateException if this packer has not been packed yet
	 */
	public int getLength() {
		if (!m_packed)
			throw new IllegalStateException(this + " has not been packed yet");

		return getCurrentLength();
	}

	/**
	 * Returns the number of rectangles contained in this packer.
	 * 
	 * @return the number of rectangles
	 */
	public int getNumRectangles() {
		return m_rectangles.size();
	}

	public boolean contains(T i_data) {
		return m_rectangles.containsKey(i_data);
	}

	/**
	 * Returns the position of the rectangle with the given payload data.
	 * 
	 * @param i_data the data
	 * @param io_result the result point, if <code>null</code>, a new point will
	 *            be created
	 * @return the position
	 * @throws IllegalArgumentException if no rectangle with the given payload
	 *             data has been added to this packer
	 * @throws IllegalStateException if this packer has not been packed yet
	 * @throws NullPointerException if the given payload data is
	 *             <code>null</code>
	 */
	public Point getPosition(T i_data, Point io_result) {
		if (!m_packed)
			throw new IllegalStateException(this + " has not been packed yet");
		if (i_data == null)
			throw new NullPointerException("i_data must not be null");

		Rectangle rectangle = m_rectangles.get(i_data);
		if (rectangle == null)
			throw new IllegalArgumentException(
				"no rectangle with payload data '" + i_data
					+ "' has been added to this packer");

		Point result = io_result;
		if (result == null)
			result = new Point();

		result.x = rectangle.x;
		result.y = rectangle.y;

		return result;
	}

	/**
	 * Packs the rectangles that were stored in this packer.
	 * 
	 * @throws IllegalStateException if this packer has already been packed
	 */
	public void pack() {
		if (m_packed)
			throw new IllegalStateException(this + " is already packed");

		List<Rectangle> sorted =
			new ArrayList<Rectangle>(m_rectangles.values());

		Collections.sort(sorted, m_comparator);
		for (Rectangle rectangle : sorted)
			pack(rectangle);

		m_packed = true;
	}

	private void pack(Rectangle i_rectangle) {
		// find strip that can contain the rectangle
		for (Strip strip : m_strips)
			if (strip.add(i_rectangle))
				return;

		// create new strip
		while (i_rectangle.height > m_length - m_totalHeight
			|| i_rectangle.width > m_length)
			m_length *= 2;

		m_strips.add(new Strip(m_totalHeight, i_rectangle));
		m_totalHeight += i_rectangle.height;
	}
}
