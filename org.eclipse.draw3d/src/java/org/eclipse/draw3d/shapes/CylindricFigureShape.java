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

/**
 * CylindricFigureShape There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 31.08.2009
 */
public class CylindricFigureShape implements Shape {

	private IFigure3D m_figure;

	private CylinderShape m_shape;

	/**
	 * Creates a new cylindric shape that represents the given figure.
	 * 
	 * @param i_figure the figure which this shape represents
	 * @param i_segments the number of segments of the cylinder
	 * @param i_radiusProportions the radius proportions of the cylinder
	 * @param i_superimposed whether this shape is superimposed
	 * @see CylinderShape#CylinderShape(org.eclipse.draw3d.geometry.IPosition3D,
	 *      int, float, boolean)
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public CylindricFigureShape(IFigure3D i_figure, int i_segments,
			float i_radiusProportions, boolean i_superimposed) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		m_figure = i_figure;
		m_shape =
			new CylinderShape(m_figure.getPosition3D(), i_segments,
				i_radiusProportions, i_superimposed);
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

		m_shape.render(i_renderContext);
	}

	/**
	 * Specifies whether this cylinder should render its faces.
	 * 
	 * @param i_fill <code>true</code> if the cylinder should render its faces
	 *            and <code>false</code> otherwise
	 */
	public void setFill(boolean i_fill) {

		m_shape.setFill(i_fill);
	}

	/**
	 * Specifies whether this cylinder should render its outline.
	 * 
	 * @param i_outline <code>true</code> if the cylinder should render its
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
		return "CylindricFigureShape [m_figure=" + m_figure + "]";
	}
}
