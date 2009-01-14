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

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.util.ColorConverter;

/**
 * A polyline shape can be used to render polylines.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 03.04.2008
 */
public class PolylineShape implements Shape {

	private final float[] m_color = new float[] { 0, 0, 0, 1 };

	private List<Vector3f> m_points;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render()
	 */
	public void render() {

		if (m_points == null || m_points.isEmpty())
			return;

		float red = m_color[0];
		float green = m_color[1];
		float blue = m_color[2];
		float alpha = m_color[3];
		Graphics3D g3d = RenderContext.getContext().getGraphics3D();

		g3d.glColor4f(red, green, blue, alpha);

		g3d.glBegin(Graphics3DDraw.GL_LINE_STRIP);
		for (Vector3f points : m_points)
			g3d.glVertex3f(points.getX(), points.getY(), points.getZ());
		g3d.glEnd();
	}

	/**
	 * Sets the color of this polyline shape.
	 * 
	 * @param i_color the color
	 * @param i_alpha the alpha value
	 * @throws NullPointerException if the given color is <code>null</code>
	 */
	public void setColor(Color i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_color);
	}

	/**
	 * Sets the color of this polyline shape.
	 * 
	 * @param i_color the color in BBGGRR format
	 * @param i_alpha the alpha value
	 */
	public void setColor(int i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_color);
	}

	/**
	 * Sets the points of the polyline to render.
	 * 
	 * @param i_points the points
	 * @throws NullPointerException if the given list is <code>null</code>
	 */
	public void setPoints(List<Vector3f> i_points) {

		if (i_points == null)
			throw new NullPointerException("i_points must not be null");

		m_points = i_points;
	}

	/**
	 * @return the points
	 */
	public List<Vector3f> getPoints() {
		return m_points;
	}

}
