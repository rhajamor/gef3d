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

import org.eclipse.draw3d.DisplayListManager;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.swt.graphics.Color;

/**
 * A shape that renders a cylinder or a (truncated) right circular cone. To
 * render a cylinder, set the radius proportions to 1 (bottom and top radius are
 * same). To render a truncated right cone, set the radius proportions to a
 * value greater than 0 and less than 1. To render a right cone, set the radius
 * proportions to 0. The radius proportions specify the proportions between the
 * top radius and the bottom radius, where the bottom radius is fixed.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 03.06.2009
 */
public class Cylinder extends AbstractModelShape {

	/**
	 * A key to store a display list for a cylinder in the display list manager.
	 * 
	 * @author Kristian Duske
	 * @version $Revision$
	 * @since 04.06.2009
	 */
	private class CylinderKey {

		private int m_hashCode;

		/**
		 * Creates a new key with the given values.
		 * 
		 * @param i_segments
		 *            the number of segments
		 * @param i_radiusProportions
		 *            the radius proportions
		 * @param i_outline
		 *            <code>true</code> if this key is for the display list that
		 *            draws the outline and <code>false</code> if it is for the
		 *            display list that fills the cylinder
		 */
		public CylinderKey(int i_segments, float i_radiusProportions,
				boolean i_outline) {

			m_hashCode = 17;
			m_hashCode = 37 * m_hashCode + new Integer(i_segments).hashCode();
			m_hashCode = 37 * m_hashCode
					+ new Float(i_radiusProportions).hashCode();
			m_hashCode = 37 * m_hashCode + new Boolean(i_outline).hashCode();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object i_obj) {

			if (i_obj == null)
				return false;

			if (this == i_obj)
				return true;

			if (!(i_obj instanceof CylinderKey))
				return false;

			return m_hashCode == i_obj.hashCode();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {

			return m_hashCode;
		}
	}

	/**
	 * Convenience method that creates a new cone.
	 * 
	 * @param i_segments
	 *            the number of segments
	 * @return the cone
	 */
	public static Cylinder createCone(int i_segments) {

		return new Cylinder(i_segments, 0);
	}

	/**
	 * Convenience method that creates a new cylinder.
	 * 
	 * @param i_segments
	 *            the number of segments
	 * @return the cylinder
	 */
	public static Cylinder createCylinder(int i_segments) {

		return new Cylinder(i_segments, 1);
	}

	private float[] m_bottomVertices;

	private boolean m_fill = true;

	private float[] m_fillColor = new float[] { 1, 1, 1, 1 };

	private CylinderKey m_fillKey;

	private boolean m_outline = true;

	private float[] m_outlineColor = new float[] { 0, 0, 0, 1 };

	private CylinderKey m_outlineKey;

	private float m_radiusProportions = 1;

	private int m_segments = 12;

	private float[] m_topVertices;

	/**
	 * Creates a new cylinder with the given number of segments and radius
	 * proportions.
	 * 
	 * @param i_segments
	 *            the number of segments
	 * @param i_radiusProportions
	 *            the radius proportions
	 */
	public Cylinder(int i_segments, float i_radiusProportions) {

		if (i_segments < 3)
			throw new IllegalArgumentException(
					"cylinders must have at least 3 segments");

		if (i_radiusProportions < 0 || i_radiusProportions > 1)
			throw new IllegalArgumentException(
					"radius proportions must be between 0 and 1, inclusive");

		m_segments = i_segments;
		m_radiusProportions = i_radiusProportions;

		// calculate bottom vertices
		m_bottomVertices = new float[m_segments * 2];

		double c = 0;
		double a = 2 * Math.PI / m_segments;

		for (int i = 0; i < m_segments; i++) {
			m_bottomVertices[2 * i] = (float) Math.cos(c);
			m_bottomVertices[2 * i + 1] = (float) Math.sin(c);
			c += a;
		}

		// calculate top vertices
		if (m_radiusProportions == 0)
			m_topVertices = new float[] { 0, 0 };
		else if (m_radiusProportions == 1)
			m_topVertices = m_bottomVertices;
		else {
			m_topVertices = new float[m_segments * 2];
			for (int i = 0; i < m_bottomVertices.length; i++)
				m_topVertices[i] = m_bottomVertices[i] * m_radiusProportions;
		}

		m_fillKey = new CylinderKey(m_segments, m_radiusProportions, false);
		m_outlineKey = new CylinderKey(m_segments, m_radiusProportions, true);
	}

	private float[] getBottomVertices() {

		if (m_bottomVertices == null) {
			m_bottomVertices = new float[m_segments * 2];

			double c = 0;
			double a = 2 * Math.PI / m_segments;

			for (int i = 0; i < m_segments; i++) {
				m_bottomVertices[2 * i] = (float) Math.cos(c);
				m_bottomVertices[2 * i + 1] = (float) Math.sin(c);
				c += a;
			}
		}

		return m_bottomVertices;
	}

	private float[] getTopVertices() {

		if (m_topVertices == null) {
			if (m_radiusProportions == 0)
				m_topVertices = new float[] { 0, 0 };
			else if (m_radiusProportions == 1)
				m_topVertices = getBottomVertices();
			else {
				m_topVertices = new float[m_segments * 2];
				float[] bottomVertices = getBottomVertices();
				for (int i = 0; i < bottomVertices.length; i++)
					m_topVertices[i] = bottomVertices[i] * m_radiusProportions;
			}
		}

		return m_topVertices;
	}

	private void initDisplayLists(DisplayListManager i_manager,
			final Graphics3D i_graphics3D) {

		if (m_fill && !i_manager.isDisplayList(m_fillKey)) {
			i_manager.createDisplayList(m_fillKey, new Runnable() {
				public void run() {
					renderFill(i_graphics3D);
				}
			});
		}

		if (m_outline && !i_manager.isDisplayList(m_outlineKey)) {
			i_manager.createDisplayList(m_outlineKey, new Runnable() {
				public void run() {
					renderOutline(i_graphics3D);
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.AbstractModelShape#performRender(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	protected void performRender(RenderContext i_renderContext) {

		Graphics3D g3d = i_renderContext.getGraphics3D();
		DisplayListManager displayListManager = i_renderContext
				.getDisplayListManager();

		initDisplayLists(displayListManager, g3d);

		if (m_fill) {
			g3d.glColor4f(m_fillColor[0], m_fillColor[1], m_fillColor[2],
					m_fillColor[3]);
			displayListManager.executeDisplayList(m_fillKey);
		}

		if (m_outline) {
			g3d.glColor4f(m_outlineColor[0], m_outlineColor[1],
					m_outlineColor[2], m_outlineColor[3]);
			displayListManager.executeDisplayList(m_outlineKey);
		}
	}

	private void renderFill(Graphics3D g3d) {

		g3d.glPolygonMode(Graphics3DDraw.GL_FRONT_AND_BACK,
				Graphics3DDraw.GL_FILL);

		float[] bVerts = getBottomVertices();

		// bottom
		g3d.glBegin(Graphics3DDraw.GL_POLYGON);
		for (int i = bVerts.length / 2 - 1; i >= 0; i--) {
			float x = bVerts[2 * i];
			float y = bVerts[2 * i + 1];
			g3d.glVertex3f(x, y, 0);
		}
		g3d.glEnd();

		if (m_radiusProportions == 0) {
			// sides
			g3d.glBegin(Graphics3DDraw.GL_TRIANGLE_FAN);
			g3d.glVertex3f(0, 0, 1);
			for (int i = 0; i < bVerts.length / 2; i++) {
				float x = bVerts[2 * i];
				float y = bVerts[2 * i + 1];
				g3d.glVertex3f(x, y, 0);
			}
			float x = bVerts[0];
			float y = bVerts[1];
			g3d.glVertex3f(x, y, 0);
			g3d.glEnd();
		} else {
			// sides
			float[] tVerts = getTopVertices();
			g3d.glBegin(Graphics3DDraw.GL_QUAD_STRIP);
			for (int i = 0; i < bVerts.length / 2; i++) {
				float bx = bVerts[2 * i];
				float by = bVerts[2 * i + 1];
				float tx = tVerts[2 * i];
				float ty = tVerts[2 * i + 1];

				g3d.glVertex3f(tx, ty, 1);
				g3d.glVertex3f(bx, by, 0);
			}

			float bx = bVerts[0];
			float by = bVerts[1];
			float tx = tVerts[0];
			float ty = tVerts[1];

			g3d.glVertex3f(tx, ty, 1);
			g3d.glVertex3f(bx, by, 0);

			g3d.glEnd();

			// top
			g3d.glBegin(Graphics3DDraw.GL_POLYGON);
			for (int i = 0; i < tVerts.length / 2; i++) {
				float x = tVerts[2 * i];
				float y = tVerts[2 * i + 1];
				g3d.glVertex3f(x, y, 1);
			}
			g3d.glEnd();
		}
	}

	private void renderOutline(Graphics3D g3d) {

		g3d.glPolygonMode(Graphics3DDraw.GL_FRONT_AND_BACK,
				Graphics3DDraw.GL_LINE);

		float[] bVerts = getBottomVertices();

		// bottom
		g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
		g3d.glNormal3f(0, 0, -1);
		for (int i = 0; i < bVerts.length / 2; i++) {
			float x = bVerts[2 * i];
			float y = bVerts[2 * i + 1];
			g3d.glVertex3f(x, y, 0);
		}
		g3d.glEnd();

		if (m_radiusProportions == 0) {
			// sides
			g3d.glBegin(Graphics3DDraw.GL_LINES);
			for (int i = 0; i < bVerts.length / 2; i++) {
				float x = bVerts[2 * i];
				float y = bVerts[2 * i + 1];
				g3d.glVertex3f(x, y, 0);
				g3d.glVertex3f(0, 0, 1);
			}
			g3d.glEnd();
		} else {
			// sides
			float[] tVerts = getTopVertices();
			g3d.glBegin(Graphics3DDraw.GL_LINES);
			for (int i = 0; i < bVerts.length / 2; i++) {
				float x = bVerts[2 * i];
				float y = bVerts[2 * i + 1];
				g3d.glVertex3f(x, y, 0);

				x = tVerts[2 * i];
				y = tVerts[2 * i + 1];
				g3d.glVertex3f(x, y, 1);
			}
			g3d.glEnd();

			g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
			for (int i = 0; i < tVerts.length / 2; i++) {
				float x = tVerts[2 * i];
				float y = tVerts[2 * i + 1];
				g3d.glVertex3f(x, y, 1);
			}
			g3d.glEnd();
		}
	}

	/**
	 * Specifies whether the polygons should be filled.
	 * 
	 * @param i_fill
	 *            <code>true</code> if the polygons should be filled and
	 *            <code>false</code> otherwise
	 */
	public void setFill(boolean i_fill) {

		m_fill = i_fill;
	}

	/**
	 * Sets the fill color of this cylinder.
	 * 
	 * @param i_color
	 *            the fill color
	 * @param i_alpha
	 *            the alpha value
	 */
	public void setFillColor(Color i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_fillColor);
	}

	/**
	 * Specifies whether an outline should be drawn.
	 * 
	 * @param i_outline
	 *            <code>true</code> if an outline should be drawn and
	 *            <code>false</code> otherwise
	 */
	public void setOutline(boolean i_outline) {

		m_outline = i_outline;
	}

	/**
	 * Sets the outline color of this cylinder.
	 * 
	 * @param i_color
	 *            the outline color
	 * @param i_alpha
	 *            the alpha value
	 */
	public void setOutlineColor(Color i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_outlineColor);
	}

}
