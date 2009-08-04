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
package org.eclipse.draw3d.util;

import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.Math3DCache;

/**
 * Extends {@link org.eclipse.draw3d.geometry.Math3DCache} with support for
 * draw2d primitives. This way, only the plugin containing this class needs to
 * depend on draw2d.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.07.2009
 */
public class Draw3DCache extends Math3DCache {

	private static final Queue<Dimension> m_dimension =
		new LinkedList<Dimension>();

	private static final Queue<Point> m_point = new LinkedList<Point>();

	/**
	 * Returns a cached {@link Dimension}.
	 * 
	 * @return a cached dimension
	 */
	public static Dimension getDimension() {

		if (m_synchronized) {
			synchronized (m_dimension) {
				if (m_dimension.isEmpty())
					return new Dimension();
				else
					return m_dimension.remove();
			}
		} else {
			if (m_dimension.isEmpty())
				return new Dimension();
			else
				return m_dimension.remove();
		}
	}

	/**
	 * Returns a cached {@link Point}.
	 * 
	 * @return a cached point
	 */
	public static Point getPoint() {

		if (m_synchronized) {
			synchronized (m_point) {
				if (m_point.isEmpty())
					return new Point();
				else
					return m_point.remove();
			}
		} else {
			if (m_point.isEmpty())
				return new Point();
			else
				return m_point.remove();
		}
	}

	/**
	 * Returns the given dimensions to the cache. If any of the given dimensions
	 * is <code>null</code>, it is ignored.
	 * 
	 * @param i_ds the dimensions to return
	 */
	public static void returnDimension(Dimension... i_ds) {

		if (m_synchronized)
			synchronized (m_dimension) {
				for (Dimension d : i_ds)
					if (d != null)
						m_dimension.offer(d);
			}
		else
			for (Dimension d : i_ds)
				if (d != null)
					m_dimension.offer(d);
	}

	/**
	 * Returns the given points to the cache. If any of the given points is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_ps the points to return
	 */
	public static void returnPoint(Point... i_ps) {

		if (m_synchronized)
			synchronized (m_point) {
				for (Point p : i_ps)
					if (p != null)
						m_point.offer(p);
			}
		else
			for (Point p : i_ps)
				if (p != null)
					m_point.offer(p);
	}
}
