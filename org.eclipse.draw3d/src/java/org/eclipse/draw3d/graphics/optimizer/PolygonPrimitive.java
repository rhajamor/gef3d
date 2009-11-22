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
package org.eclipse.draw3d.graphics.optimizer;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.geometry.Math2D;

/**
 * PolygonPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class PolygonPrimitive extends PolylinePrimitive {

	private boolean m_filled;

	public PolygonPrimitive(int[] i_points, boolean i_filled) {

		super(i_points, PrimitiveType.POLYGON);

		m_filled = i_filled;
	}

	protected boolean isFilled() {

		return m_filled;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#calculateBounds(org.eclipse.draw2d.geometry.Rectangle)
	 */
	@Override
	protected void calculateBounds(Rectangle io_bounds) {

		io_bounds.setLocation(0, 0);
		io_bounds.setSize(0, 0);

		int[] points = getPoints();
		if (points.length == 0)
			return;

		io_bounds.setLocation(points[0], points[1]);
		for (int i = 1; i < points.length / 2; i++) {
			int x = points[2 * i];
			int y = points[2 * i + 1];
			int r = io_bounds.x + io_bounds.width;
			int b = io_bounds.y + io_bounds.height;

			if (x < io_bounds.x) {
				io_bounds.width += io_bounds.x - x;
				io_bounds.x = x;
			} else if (x > r)
				io_bounds.width += x - r;

			if (y < io_bounds.y) {
				io_bounds.height += io_bounds.y - y;
				io_bounds.y = y;
			} else if (y > b)
				io_bounds.height += y - b;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsLine(org.eclipse.draw3d.graphics.optimizer.LinePrimitive)
	 */
	@Override
	protected boolean intersectsLine(LinePrimitive i_line) {

		return intersectsPolyline(i_line);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsPolygon(org.eclipse.draw3d.graphics.optimizer.PolygonPrimitive)
	 */
	@Override
	protected boolean intersectsPolygon(PolygonPrimitive i_polygon) {

		if (isFilled()) {
			if (i_polygon.isFilled())
				return Math2D.polygonIntersectsPolygon(getPoints(),
					i_polygon.getPoints());
			else
				return Math2D.sortedPointsInPolygon(getPoints(),
					i_polygon.getPoints(), i_polygon.getSortedIndices(), 1) > 0;
		} else {
			if (i_polygon.isFilled()) {
				return Math2D.sortedPointsInPolygon(i_polygon.getPoints(),
					getPoints(), getSortedIndices(), 1) > 0;
			} else {
				int n = i_polygon.getPoints().length / 2;
				int p =
					Math2D.sortedPointsInPolygon(getPoints(),
						i_polygon.getPoints(), i_polygon.getSortedIndices(), n);

				// either the other polygon is entirely contained in this
				// polygon or not at all
				return p > 0 && p < n;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsPolyline(org.eclipse.draw3d.graphics.optimizer.PolylinePrimitive)
	 */
	@Override
	protected boolean intersectsPolyline(PolylinePrimitive i_polyline) {

		if (isFilled())
			return Math2D.sortedPointsInPolygon(getPoints(),
				i_polyline.getPoints(), i_polyline.getSortedIndices(), 1) > 0;
		else {
			int n = i_polyline.getPoints().length / 2;
			int p =
				Math2D.sortedPointsInPolygon(getPoints(),
					i_polyline.getPoints(), i_polyline.getSortedIndices(), n);

			// either the polyline is entirely contained in this
			// polygon or not at all
			return p > 0 && p < n;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsQuad(org.eclipse.draw3d.graphics.optimizer.QuadPrimitive)
	 */
	@Override
	protected boolean intersectsQuad(QuadPrimitive i_quad) {

		return intersectsPolygon(i_quad);
	}
}
