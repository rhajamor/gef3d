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

/**
 * PolygonPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public abstract class AbstractPrimitive implements Primitive {

	private Rectangle m_bounds;

	private PrimitiveType m_type;

	protected abstract void calculateBounds(Rectangle io_bounds);

	public AbstractPrimitive(PrimitiveType i_type) {

		if (i_type == null)
			throw new NullPointerException("i_type must not be null");

		m_type = i_type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.Primitive#getBounds()
	 */
	public Rectangle getBounds() {

		if (m_bounds == null) {
			m_bounds = new Rectangle();
			calculateBounds(m_bounds);
		}

		return m_bounds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.Primitive#getType()
	 */
	public PrimitiveType getType() {

		return m_type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.Primitive#intersects(org.eclipse.draw3d.graphics.optimizer.Primitive)
	 */
	public boolean intersects(Primitive i_candidate) {

		if (!getBounds().intersects(i_candidate.getBounds()))
			return false;

		switch (i_candidate.getType()) {
		case POLYGON:
			return intersectsPolygon((PolygonPrimitive) i_candidate);
		case QUAD:
			return intersectsQuad((QuadPrimitive) i_candidate);
		case POLYLINE:
			return intersectsPolyline((PolylinePrimitive) i_candidate);
		case LINE:
			return intersectsLine((LinePrimitive) i_candidate);
		default:
			throw new AssertionError();
		}
	}

	protected abstract boolean intersectsLine(LinePrimitive i_line);

	protected abstract boolean intersectsPolygon(PolygonPrimitive i_polygon);

	protected abstract boolean intersectsPolyline(PolylinePrimitive i_polyline);

	protected abstract boolean intersectsQuad(QuadPrimitive i_quad);

}
