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

import java.nio.FloatBuffer;

/**
 * PolylinePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class PolylinePrimitive extends AbstractPrimitive {

	private float[] m_points;

	public PolylinePrimitive(float[] i_points) {

		this(i_points, PrimitiveType.POLYLINE);
	}

	protected PolylinePrimitive(float[] i_points, PrimitiveType i_type) {

		super(i_type);

		if (i_points == null)
			throw new NullPointerException("i_points must not be null");

		m_points = i_points;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.AbstractPrimitive#createBounds()
	 */
	@Override
	protected PrimitiveBounds createBounds() {

		return new PrimitiveBounds(m_points);
	}

	protected float[] getPoints() {

		return m_points;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.Primitive#getVertices(java.nio.FloatBuffer)
	 */
	public void getVertices(FloatBuffer i_buffer) {

		i_buffer.put(m_points);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.Primitive#getNumVertices()
	 */
	public int getNumVertices() {

		return m_points.length / 2;
	}
}
