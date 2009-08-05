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
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.picking.Query;

/**
 * A figure shape that renders itself as a cuboid.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.08.2009
 */
public class CuboidFigureShape extends FigureShape {

	private CuboidShape m_shape;

	/**
	 * Creates a new cuboid figure shape.
	 * 
	 * @param i_figure the figure which this shape represents
	 */
	public CuboidFigureShape(IFigure3D i_figure) {

		super(i_figure);
		m_shape = new CuboidShape(i_figure.getPosition3D());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.FigureShape#doGetDistance(org.eclipse.draw3d.IFigure3D,
	 *      org.eclipse.draw3d.picking.Query)
	 */
	@Override
	protected float doGetDistance(IFigure3D i_figure, Query i_query) {

		return m_shape.getDistance(i_query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.FigureShape#doRender(org.eclipse.draw3d.IFigure3D,
	 *      org.eclipse.draw3d.RenderContext)
	 */
	@Override
	protected void doRender(IFigure3D i_figure, RenderContext i_renderContext) {

		m_shape
			.setFillColor(i_figure.getBackgroundColor(), i_figure.getAlpha());

		m_shape.setOutlineColor(i_figure.getForegroundColor(), i_figure
			.getAlpha());

		Graphics3D g3d = i_renderContext.getGraphics3D();
		if (g3d.hasGraphics2D(i_figure))
			m_shape.setTextureId(g3d.getGraphics2DId(i_figure));
		else
			m_shape.setTextureId(null);

		m_shape.render(i_renderContext);
	}

	/**
	 * Specifies whether this cuboid should render its faces.
	 * 
	 * @param i_fill <code>true</code> if the cuboid should render its faces and
	 *            <code>false</code> otherwise
	 */
	public void setFill(boolean i_fill) {

		m_shape.setFill(i_fill);
	}

	/**
	 * Specifies whether this cuboid should render its outline.
	 * 
	 * @param i_outline <code>true</code> if the cuboid should render its
	 *            outline and <code>false</code> otherwise
	 */
	public void setOutline(boolean i_outline) {

		m_shape.setOutline(i_outline);
	}

}
