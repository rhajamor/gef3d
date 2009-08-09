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

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.ParaxialBoundingBox;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.geometry.Position3DUtil;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * Renders the paraxial bounding box of a figure.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 08.08.2009
 */
public class ParaxialBoundsFigureShape extends FigureShape {

	private Position3D m_position;

	private CuboidShape m_shape;

	/**
	 * Creates a new shape for the given figure.
	 * 
	 * @param i_figure the figure
	 */
	public ParaxialBoundsFigureShape(IFigure3D i_figure) {

		super(i_figure);

		m_position = Position3DUtil.createAbsolutePosition();
		m_shape = new CuboidShape(m_position);

		m_shape.setFill(false);
		m_shape.setOutlineColor(1, 0, 0, 0.3f);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.FigureShape#doGetDistance(org.eclipse.draw3d.IFigure3D,
	 *      org.eclipse.draw3d.picking.Query)
	 */
	@Override
	protected float doGetDistance(IFigure3D i_figure, Query i_query) {

		return Float.NaN;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.FigureShape#doGetParaxialBoundingBox(org.eclipse.draw3d.IFigure3D,
	 *      org.eclipse.draw3d.geometry.ParaxialBoundingBox)
	 */
	@Override
	protected ParaxialBoundingBox doGetParaxialBoundingBox(IFigure3D i_figure,
		ParaxialBoundingBox o_result) {

		return m_shape.getParaxialBoundingBox(o_result);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.FigureShape#doRender(org.eclipse.draw3d.IFigure3D,
	 *      org.eclipse.draw3d.RenderContext)
	 */
	@Override
	protected void doRender(IFigure3D i_figure, RenderContext i_renderContext) {

		Vector3f position = Draw3DCache.getVector3f();
		Vector3f size = Draw3DCache.getVector3f();
		ParaxialBoundingBox pBounds = Draw3DCache.getParaxialBoundingBox();
		try {
			ParaxialBoundingBox figureBounds =
				i_figure.getParaxialBoundingBox(pBounds);

			if (figureBounds != null) {
				pBounds.getLocation(position);
				pBounds.getSize(size);

				m_position.setLocation3D(position);
				m_position.setSize3D(size);

				m_shape.render(i_renderContext);
			}
		} finally {
			Draw3DCache.returnVector3f(position, size);
			Draw3DCache.returnParaxialBoundingBox(pBounds);
		}
	}
}
