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
package org.eclipse.draw3d;

import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.shapes.CompositeShape;
import org.eclipse.draw3d.shapes.Shape;

/**
 * A figure that is represented visually by a {@link Shape}. Automatically
 * handles transparency.
 * 
 * @author Kristian Duske, Jens von Pilgrim
 * @version $Revision$
 * @since 05.08.2009
 */
public abstract class ShapeFigure3D extends Figure3D {

	/**
	 * The shape of this figure, created in {@link #createShape()}.
	 */
	protected Shape m_shape;

	/**
	 * Creates the shape(s) that represent this figure. This method must be 
	 * overridden by subclasses and must not return null.
	 * 
	 * @see CompositeShape
	 * @return the shape of this figure, must not return null.
	 */
	protected abstract Shape createShape();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#getDistance(org.eclipse.draw3d.picking.Query)
	 */
	@Override
	public float getDistance(Query i_query) {
		return getShape().getDistance(i_query);
	}

	/**
	 * Returns the shape that represents this figure. If the shape doesn't exist
	 * yet, it is lazily created by calling {@link #createShape()}. This method
	 * never returns null.
	 * 
	 * @return the shape
	 */
	protected Shape getShape() {
		if (m_shape == null) {
			m_shape = createShape();
			if (m_shape==null) {
				throw new NullPointerException("created shape mmust not be null");
			}
		}

		return m_shape;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#render(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	public void render(RenderContext i_renderContext) {
		getShape().render(i_renderContext);
	}
}
