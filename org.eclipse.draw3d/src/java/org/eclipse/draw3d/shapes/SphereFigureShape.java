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
package org.eclipse.draw3d.shapes;

import java.util.Map;

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.ParaxialBoundingBox;
import org.eclipse.draw3d.graphics3d.Graphics3D;

/**
 * A figure shape that renders itself as a sphere.
 * 
 * @author Kristian Duske, Jens von Pilgrim
 * @version $Revision$
 * @since 05.08.2009
 */
public class SphereFigureShape implements Shape {

	private IFigure3D m_figure;

	private SphereShape m_shape;

	/**
	 * Creates a new cuboid figure shape. The figure is not superimposed, this
	 * is a convenient method, it's equal to
	 * {@link #CuboidFigureShape(IFigure3D, boolean)}.
	 * 
	 * @param i_figure the figure which this shape represents
	 */
	public SphereFigureShape(IFigure3D i_figure, int i_precision) {

		this(i_figure, i_precision, false);
	}

	/**
	 * Creates a new cuboid figure shape.
	 * 
	 * @param i_figure the figure which this shape represents
	 * @param i_superimposed whether this shape is superimposed
	 */
	public SphereFigureShape(IFigure3D i_figure, int i_precision, boolean i_superimposed) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		m_figure = i_figure;
		m_shape = new SphereShape(m_figure.getPosition3D(), i_precision, i_superimposed);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getDistance(org.eclipse.draw3d.geometry.IVector3f,
	 *      org.eclipse.draw3d.geometry.IVector3f, java.util.Map)
	 */
	public float getDistance(IVector3f i_rayOrigin, IVector3f i_rayDirection,
		Map<Object, Object> i_context) {

		return m_shape.getDistance(i_rayOrigin, i_rayDirection, i_context);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.RenderFragment#getDistanceMeasure(org.eclipse.draw3d.RenderContext)
	 */
	public float getDistanceMeasure(RenderContext i_renderContext) {

		return m_shape.getDistanceMeasure(i_renderContext);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getParaxialBoundingBox(org.eclipse.draw3d.geometry.ParaxialBoundingBox)
	 */
	public ParaxialBoundingBox getParaxialBoundingBox(
		ParaxialBoundingBox o_result) {

		return m_shape.getParaxialBoundingBox(o_result);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.RenderFragment#getRenderType()
	 */
	public RenderType getRenderType() {

		return m_shape.getRenderType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.RenderFragment#render(org.eclipse.draw3d.RenderContext)
	 */
	public void render(RenderContext i_renderContext) {

		m_shape.setAlpha(m_figure.getAlpha());
		m_shape.setFillColor(m_figure.getBackgroundColor());
		m_shape.setOutlineColor(m_figure.getForegroundColor());

//		Graphics3D g3d = i_renderContext.getGraphics3D();
//		if (g3d.hasGraphics2D(m_figure))
//			m_shape.setTextureId(g3d.getGraphics2DId(m_figure));
//		else
//			m_shape.setTextureId(null);

		m_shape.render(i_renderContext);
	}

	/**
	 * Specifies whether this sphere should render its faces.
	 * 
	 * @param i_fill <code>true</code> if the sphere should render its faces and
	 *            <code>false</code> otherwise
	 */
	public void setFill(boolean i_fill) {

		m_shape.setFill(i_fill);
	}

	/**
	 * Specifies whether this sphere should render its outline.
	 * 
	 * @param i_outline <code>true</code> if the sphere should render its
	 *            outline and <code>false</code> otherwise
	 */
	public void setOutline(boolean i_outline) {

		m_shape.setOutline(i_outline);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SphereFigureShape [m_figure=" + m_figure + "]";
	}
}