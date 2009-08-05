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
import org.eclipse.draw3d.shapes.Shape;
import org.eclipse.draw3d.shapes.TransparencyAdapter;

/**
 * A figure that is represented visually by a {@link Shape}. Automatically
 * handles transparency.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.08.2009
 */
public abstract class ShapeFigure3D extends Figure3D {

	private TransparencyAdapter m_adapter;

	private Shape m_shape;

	/**
	 * Creates the shape that represents this figure.
	 * 
	 * @return the shape
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
	 * Returns the shape that represents this figure.
	 * 
	 * @return the shape
	 */
	protected Shape getShape() {

		if (m_shape == null)
			m_shape = createShape();

		return m_shape;
	}

	/**
	 * Returns the transparency adapter for this figure.
	 * 
	 * @return the transparency adapter for this figurre
	 */
	protected TransparencyAdapter getTransparencyAdapter() {

		if (m_adapter == null)
			m_adapter = new TransparencyAdapter(this, getShape());

		return m_adapter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#render(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	public void render(RenderContext i_renderContext) {

		if (getAlpha() == 255)
			getShape().render(i_renderContext);
		else
			i_renderContext.addTransparentObject(getTransparencyAdapter());
	}
}
