/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.TransparentObject;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * Adapter for shapes with transparency. This class can be used for simply
 * adapting {@link Shape} objects to enable transparent shapes used by
 * {@link IFigure3D} objects. The figure is used to calculate the transparency
 * depth of the shape, it is simply the center of the figure's bounds. This is a
 * not very accurate method, but it works in most cases.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Aug 16, 2008
 */
public class TransparentShape implements TransparentObject, Shape {

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();

		b.append("TransparentShape[figure=");
		b.append(m_figure);
		b.append(", shape=");
		b.append(m_shape);
		b.append("]\n");

		return b.toString();
	}

	/**
	 * The figure to which the shape belongs.
	 */
	protected IFigure3D m_figure;

	/**
	 * The shape to adapt.
	 */
	protected Shape m_shape;

	/**
	 * Creates a new transparency adapter for the given figure and shape.
	 * 
	 * @param i_figure the figure to which the shape belongs
	 * @param i_shape the shape to adapt
	 * @throws NullPointerException if either of the given arguments is
	 *             <code>null</code>
	 */
	public TransparentShape(IFigure3D i_figure, Shape i_shape) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		m_figure = i_figure;
		m_shape = i_shape;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getDistance(org.eclipse.draw3d.picking.Query)
	 */
	public float getDistance(Query i_query) {

		return m_shape.getDistance(i_query);
	}

	/**
	 * Returns center of figure's bounds. This method calculates the center of
	 * the container figures {@link IFigure3D#getBounds()}.
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#getTransparencyDepth()
	 */
	public float getTransparencyDepth(RenderContext renderContext) {
		ICamera camera = renderContext.getScene().getCamera();

		Vector3f center = Draw3DCache.getVector3f();
		try {
			m_figure.getBounds3D().getCenter(center);
			float dist = camera.getDistance(center);
			return dist;
		} finally {
			Draw3DCache.returnVector3f(center);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render(org.eclipse.draw3d.RenderContext)
	 */
	public void render(RenderContext i_renderContext) {

		m_shape.render(i_renderContext);
	}

	/**
	 * Calls figure's {@link Shape#render()} method. {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#renderTransparent()
	 */
	public void renderTransparent(RenderContext i_renderContext) {

		render(i_renderContext);
	}
}