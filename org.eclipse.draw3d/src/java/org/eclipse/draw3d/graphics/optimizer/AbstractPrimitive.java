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

/**
 * PolygonPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public abstract class AbstractPrimitive implements Primitive {

	private PrimitiveBounds m_bounds;

	private PrimitiveType m_type;

	public AbstractPrimitive(PrimitiveType i_type) {

		if (i_type == null)
			throw new NullPointerException("i_type must not be null");

		m_type = i_type;
	}

	protected abstract PrimitiveBounds createBounds();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.Primitive#getBounds()
	 */
	public PrimitiveBounds getBounds() {

		if (m_bounds == null) {
			m_bounds = createBounds();
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

		return getBounds().intersects(i_candidate.getBounds());
	}
}
