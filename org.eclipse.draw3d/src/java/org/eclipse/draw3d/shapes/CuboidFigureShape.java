/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import java.util.logging.Logger;

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.picking.ColorProvider;
import org.eclipse.swt.graphics.Color;

/**
 * A shape that draws a 3D figure as a cuboid. The colors and textures are
 * retrieved from the figure's settings. This is a convience class.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 31.03.2008
 */
public class CuboidFigureShape implements Shape {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CuboidFigureShape.class
			.getName());

	private final IFigure3D m_figure;

	private final SolidCube m_solidCube = new SolidCube();

	private final WiredCube m_wiredCube = new WiredCube();

	/**
	 * Creates a new shape that retrieves its configuration from the given
	 * figure.
	 * 
	 * @param i_figure the figure
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public CuboidFigureShape(IFigure3D i_figure) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		m_figure = i_figure;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render()
	 */
	public void render() {

		RenderContext renderContext = RenderContext.getContext();

		int alpha = m_figure.getAlpha();
		IMatrix4f modelMatrix = m_figure.getModelMatrix();

		if (renderContext.getMode().isPaint()) {
			Color color = m_figure.getForegroundColor();
			m_wiredCube.setColor(color, alpha);
			m_wiredCube.setModelMatrix(modelMatrix);

			m_wiredCube.render();
		}

		m_solidCube.setModelMatrix(modelMatrix);

		if (renderContext.getMode().isPaint()) {
			Graphics3D g3d = RenderContext.getContext().getGraphics3D();
			if (g3d.hasGraphics2D(m_figure)) {
				int textureId = g3d.getGraphics2DId(m_figure);
				m_solidCube.setTexture(textureId);
			} else {
				m_solidCube.setTexture(null);
			}

			Color color = m_figure.getBackgroundColor();

			m_solidCube.setColor(color, alpha);
			m_solidCube.render();
		} else if (renderContext.getMode().isColor()) {
			int color = renderContext.getColor(m_figure);
			if (color != ColorProvider.IGNORE) {
				m_solidCube.setColor(color, 255);
				m_solidCube.setTexture(null);
				m_solidCube.render();
			}
		}
	}
}
