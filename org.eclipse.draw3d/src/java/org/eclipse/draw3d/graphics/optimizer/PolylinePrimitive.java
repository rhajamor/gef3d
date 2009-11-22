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
 * PolylinePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class PolylinePrimitive extends AbstractPrimitive {

	private int[] m_points;

	private short[] m_sorted;

	protected PolylinePrimitive(int[] i_points, PrimitiveType i_type) {

		super(i_type);

		if (i_points == null)
			throw new NullPointerException("i_points must not be null");

		m_points = i_points;
	}

	public PolylinePrimitive(int[] i_points) {

		this(i_points, PrimitiveType.POLYLINE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#calculateBounds(org.eclipse.draw2d.geometry.Rectangle)
	 */
	@Override
	protected void calculateBounds(Rectangle io_bounds) {
		// TODO implement method PolylinePrimitive.calculateBounds

	}

	protected int[] getPoints() {

		return m_points;
	}

	protected short[] getSortedIndices() {

		if (m_sorted == null)
			m_sorted = Math2D.getSortedIndices(m_points);

		return m_sorted;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsLine(org.eclipse.draw3d.graphics.optimizer.LinePrimitive)
	 */
	@Override
	protected boolean intersectsLine(LinePrimitive i_line) {
		// TODO implement method AbstractPrimitive.intersectsLine
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsPolygon(org.eclipse.draw3d.graphics.optimizer.PolygonPrimitive)
	 */
	@Override
	protected boolean intersectsPolygon(PolygonPrimitive i_polygon) {
		// TODO implement method AbstractPrimitive.intersectsPolygon
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsPolyline(org.eclipse.draw3d.graphics.optimizer.PolylinePrimitive)
	 */
	@Override
	protected boolean intersectsPolyline(PolylinePrimitive i_polyline) {
		// TODO implement method AbstractPrimitive.intersectsPolyline
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#intersectsQuad(org.eclipse.draw3d.graphics.optimizer.QuadPrimitive)
	 */
	@Override
	protected boolean intersectsQuad(QuadPrimitive i_quad) {
		// TODO implement method AbstractPrimitive.intersectsQuad
		return false;
	}
}
