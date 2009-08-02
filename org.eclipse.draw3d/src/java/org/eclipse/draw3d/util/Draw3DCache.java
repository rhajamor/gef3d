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
 * Extends {@link org.eclipse.draw3d.geometry.Math3DCache} with support for draw2d
 * primitives. This way, only the plugin containing this class needs to depend
 * on draw2d.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.07.2009
 */
public class Draw3DCache extends Math3DCache {

    private static final Queue<Dimension> m_dimension = new LinkedList<Dimension>();

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
     * Returns the given dimension to the cache. If the given dimension is
     * <code>null</code>, it is ignored.
     * 
     * @param i_d
     *            the dimension to return
     */
    public static void returnDimension(Dimension i_d) {

        if (i_d == null)
            return;

        if (m_synchronized) {
            synchronized (m_dimension) {
                m_dimension.offer(i_d);
            }
        } else
            m_dimension.offer(i_d);
    }

    /**
     * Returns the given point to the cache. If the given point is
     * <code>null</code>, it is ignored.
     * 
     * @param i_p
     *            the point to return
     */
    public static void returnPoint(Point i_p) {

        if (i_p == null)
            return;

        if (m_synchronized) {
            synchronized (m_point) {
                m_point.offer(i_p);
            }
        } else
            m_point.offer(i_p);
    }
}
