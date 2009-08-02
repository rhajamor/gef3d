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
import org.eclipse.draw3d.geometry.IParaxialBoundingBox;
import org.eclipse.draw3d.geometry.Math3DCache;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;

/**
 * Renders the paraxial bounding box of a figure as a wireframe. Attention, this
 * shape is not optimized for performance.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 01.08.2009
 */
public class ParaxialBoundingBoxCube implements Shape {

	private IFigure3D m_figure;

	/**
	 * Creates a new shape that renders the paraxial bounding box of the given
	 * figure.
	 * 
	 * @param i_figure the figure
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public ParaxialBoundingBoxCube(IFigure3D i_figure) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		m_figure = i_figure;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render(org.eclipse.draw3d.RenderContext)
	 */
	public void render(RenderContext i_renderContext) {

		Vector3f p1 = Math3DCache.getVector3f();
		Vector3f p2 = Math3DCache.getVector3f();
		try {
			IParaxialBoundingBox paraxialBoundingBox =
				m_figure.getParaxialBoundingBox();

			paraxialBoundingBox.getPosition(p1);
			paraxialBoundingBox.getEnd(p2);

			Graphics3D g3d = i_renderContext.getGraphics3D();
			g3d.glColor4f(1, 0, 0, 1);

			g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
			g3d.glVertex3f(p1.getX(), p1.getY(), p1.getZ());
			g3d.glVertex3f(p2.getX(), p1.getY(), p1.getZ());
			g3d.glVertex3f(p2.getX(), p1.getY(), p2.getZ());
			g3d.glVertex3f(p1.getX(), p1.getY(), p2.getZ());
			g3d.glEnd();

			g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
			g3d.glVertex3f(p1.getX(), p2.getY(), p1.getZ());
			g3d.glVertex3f(p2.getX(), p2.getY(), p1.getZ());
			g3d.glVertex3f(p2.getX(), p2.getY(), p2.getZ());
			g3d.glVertex3f(p1.getX(), p2.getY(), p2.getZ());
			g3d.glEnd();

			g3d.glBegin(Graphics3DDraw.GL_LINES);
			g3d.glVertex3f(p1.getX(), p1.getY(), p1.getZ());
			g3d.glVertex3f(p1.getX(), p2.getY(), p1.getZ());
			g3d.glVertex3f(p2.getX(), p1.getY(), p1.getZ());
			g3d.glVertex3f(p2.getX(), p2.getY(), p1.getZ());
			g3d.glVertex3f(p1.getX(), p1.getY(), p2.getZ());
			g3d.glVertex3f(p1.getX(), p2.getY(), p2.getZ());
			g3d.glVertex3f(p2.getX(), p1.getY(), p2.getZ());
			g3d.glVertex3f(p2.getX(), p2.getY(), p2.getZ());
			g3d.glEnd();
		} finally {
			Math3DCache.returnVector3f(p1);
			Math3DCache.returnVector3f(p2);
		}
	}
}
