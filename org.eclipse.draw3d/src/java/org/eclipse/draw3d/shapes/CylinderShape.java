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
import java.util.WeakHashMap;

import org.eclipse.draw3d.DisplayListManager;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.draw3d.util.Draw3DCache;
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
 * @since 05.08.2009
 */
public class CylinderShape extends PositionableShape {

	private static class CylinderConfig {

		private IVector3f[] m_bottomVertices;

		private float m_cosApex2;

		private float m_height;

		private float m_radiusProportions;

		private float m_sinApex2;

		private IVector3f[] m_topVertices;

		/**
		 * Creates a new cylinder config for the given values.
		 * 
		 * @param i_radiusProportions the radius proportions
		 * @param i_topVertices the top vertices
		 * @param i_bottomVertices the bottom vertices
		 */
		public CylinderConfig(float i_radiusProportions,
				IVector3f[] i_topVertices, IVector3f[] i_bottomVertices) {

			m_radiusProportions = i_radiusProportions;
			m_topVertices = i_topVertices;
			m_bottomVertices = i_bottomVertices;

			if (m_radiusProportions != 1)
				m_height = 1 / (1 - m_radiusProportions);
			else
				m_height = 1;

			double a = Math.atan(0.5d / m_height);
			double sa = Math.sin(a);
			double ca = Math.cos(a);

			m_sinApex2 = (float) (sa * sa);
			m_cosApex2 = (float) (ca * ca);
		}

		/**
		 * Returns the bottom vertices.
		 * 
		 * @return an array containing the bottom vertices
		 */
		public IVector3f[] getBottomVertices() {

			return m_bottomVertices;
		}

		/**
		 * Returns the squared cosine of the apex angle.
		 * 
		 * @return the the squared cosine of the apex angle
		 */
		public float getCosApex2() {

			return m_cosApex2;
		}

		/**
		 * Returns the height of the cylinder or the cone. In the case of a
		 * truncated cone, this returns the height of the entire cone, not just
		 * the truncated chunk. This is a bit misleading, but we need this value
		 * when doing intersections.
		 * 
		 * @return the height of the cylinder
		 */
		public float getHeight() {

			return m_height;
		}

		/**
		 * Returns the radius proportions.
		 * 
		 * @return the radius proportions
		 */
		public float getRadiusProportions() {

			return m_radiusProportions;
		}

		/**
		 * Returns the squared sine of the apex angle
		 * 
		 * @return the squared sine of the apex angle
		 */
		public float getSinApex2() {

			return m_sinApex2;
		}

		/**
		 * Returns the top vertices.
		 * 
		 * @return the top vertices
		 */
		public IVector3f[] getTopVertices() {

			return m_topVertices;
		}
	}

	/**
	 * A key for the configuration cache map.
	 * 
	 * @author Kristian Duske
	 * @version $Revision$
	 * @since 08.08.2009
	 */
	private static class CylinderConfigKey {

		private int m_hashCode;

		public CylinderConfigKey(int i_segments, float i_radiusProportions) {

			m_hashCode = 17;
			m_hashCode = 37 * m_hashCode + new Integer(i_segments).hashCode();
			m_hashCode =
				37 * m_hashCode + new Float(i_radiusProportions).hashCode();
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

			if (!(i_obj instanceof CylinderConfigKey))
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
		 * @param i_segments the number of segments
		 * @param i_radiusProportions the radius proportions
		 * @param i_outline <code>true</code> if this key is for the display
		 *            list that draws the outline and <code>false</code> if it
		 *            is for the display list that fills the cylinder
		 */
		public CylinderKey(int i_segments, float i_radiusProportions,
				boolean i_outline) {

			m_hashCode = 17;
			m_hashCode = 37 * m_hashCode + new Integer(i_segments).hashCode();
			m_hashCode =
				37 * m_hashCode + new Float(i_radiusProportions).hashCode();
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

	private static final Map<CylinderConfigKey, CylinderConfig> CONFIG_CACHE =
		new WeakHashMap<CylinderConfigKey, CylinderConfig>();

	private static final float[] TMP_F2 = new float[2];

	/**
	 * Convenience method that creates a new cone.
	 * 
	 * @param i_position3D the position of the cone
	 * @param i_segments the number of segments
	 * @return the cone
	 */
	public static CylinderShape createCone(IPosition3D i_position3D,
		int i_segments) {

		return new CylinderShape(i_position3D, i_segments, 0);
	}

	/**
	 * Convenience method that creates a new cylinder.
	 * 
	 * @param i_position3D the position of the cylinder
	 * @param i_segments the number of segments
	 * @return the cylinder
	 */
	public static CylinderShape createCylinder(IPosition3D i_position3D,
		int i_segments) {

		return new CylinderShape(i_position3D, i_segments, 1);
	}

	private CylinderConfig m_config;

	private boolean m_fill = true;

	private float[] m_fillColor = new float[] { 1, 1, 1, 1 };

	private CylinderKey m_fillKey;

	private boolean m_outline = true;

	private float[] m_outlineColor = new float[] { 0, 0, 0, 1 };

	private CylinderKey m_outlineKey;

	/**
	 * Creates a new cylinder with the given position, number of segments and
	 * radius proportions.
	 * 
	 * @param i_position3D the position of this shape
	 * @param i_segments the number of segments
	 * @param i_radiusProportions the radius proportions
	 */
	public CylinderShape(IPosition3D i_position3D, int i_segments,
			float i_radiusProportions) {

		super(i_position3D);

		if (i_segments < 3)
			throw new IllegalArgumentException(
				"cylinders must have at least 3 segments");

		if (i_radiusProportions < 0 || i_radiusProportions > 1)
			throw new IllegalArgumentException(
				"radius proportions must be between 0 and 1, inclusive");

		CylinderConfigKey key =
			new CylinderConfigKey(i_segments, i_radiusProportions);

		m_config = CONFIG_CACHE.get(key);
		if (m_config == null) {

			// calculate top vertices
			IVector3f[] top = new IVector3f[i_segments];

			double c = 0;
			double a = 2 * Math.PI / i_segments;

			for (int i = 0; i < i_segments; i++) {
				float x = (float) Math.cos(c) / 2 + 0.5f;
				float y = (float) Math.sin(c) / 2 + 0.5f;

				top[i] = new Vector3fImpl(x, y, 1);
				c += a;
			}

			// calculate bottom vertices
			IVector3f[] bottom;
			if (i_radiusProportions == 0)
				bottom = new IVector3f[] { new Vector3fImpl(0.5f, 0.5f, 0) };
			else {
				bottom = new IVector3f[i_segments];
				for (int i = 0; i < top.length; i++) {
					Vector3f v = new Vector3fImpl(top[i]);
					if (i_radiusProportions != 1) {
						v.translate(-0.5f, -0.5f, -1);
						v.scale(i_radiusProportions);
						v.translate(0.5f, 0.5f, 0);
					} else
						v.translate(0, 0, -1);
					bottom[i] = v;
				}
			}

			m_config = new CylinderConfig(i_radiusProportions, top, bottom);
			CONFIG_CACHE.put(key, m_config);
		}

		m_fillKey = new CylinderKey(i_segments, i_radiusProportions, false);
		m_outlineKey = new CylinderKey(i_segments, i_radiusProportions, true);
	}

	private float doGetConeDistance(Vector3f rayOrigin, IVector3f rayDirection,
		float i_minZ, float i_height) {

		float sa2 = m_config.getSinApex2();
		float ca2 = m_config.getCosApex2();

		float xo = rayOrigin.getX();
		float yo = rayOrigin.getY();
		float zo = rayOrigin.getZ();

		float xd = rayDirection.getX();
		float yd = rayDirection.getY();
		float zd = rayDirection.getZ();

		float xo2 = xo * xo;
		float yo2 = yo * yo;
		float zo2 = zo * zo;

		float xd2 = xd * xd;
		float yd2 = yd * yd;
		float zd2 = zd * zd;

		float A = ca2 * (xd2 + yd2) - sa2 * zd2;
		float B = 2 * (ca2 * (xo * xd + yo * yd) - sa2 * zo * zd);
		float C = ca2 * (xo2 + yo2) - sa2 * zo2;

		Math3D.solveQuadraticEquation(A, B, C, TMP_F2);

		// we must check the z coordinates of possible hits now
		float d0 = TMP_F2[0];
		float d1 = TMP_F2[1];

		float zi0 = Float.isNaN(d0) ? Float.NaN : zo + zd * d0;
		float zi1 = Float.isNaN(d1) ? Float.NaN : zo + zd * d1;

		if (!Math3D.in(i_minZ, i_height, zi0))
			return d1;

		if (!Math3D.in(i_minZ, i_height, zi1))
			return d0;

		// both hits are valid
		return Math3D.minDistance(d0, d1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#doGetDistance(org.eclipse.draw3d.picking.Query)
	 */
	@Override
	protected float doGetDistance(Query i_query) {

		float radiusProportions = m_config.getRadiusProportions();
		if (radiusProportions == 1)
			return getCylinderDistance(i_query);
		else if (radiusProportions == 0)
			return getConeDistance(i_query);
		else
			return getTruncatedConeDistance(i_query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#doRender(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	protected void doRender(RenderContext i_renderContext) {

		Graphics3D g3d = i_renderContext.getGraphics3D();
		DisplayListManager displayListManager =
			i_renderContext.getDisplayListManager();

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

	private float getCapDistance(Query i_query) {

		IVector3f rayOrigin = i_query.getRayOrigin();
		IVector3f rayDirection = i_query.getRayDirection();

		IVector3f[] bottom = m_config.getBottomVertices();
		if (bottom.length > 1) {
			float d =
				Math3D.rayIntersectsPolygon(rayOrigin, rayDirection, bottom,
					IVector3f.Z_AXIS_NEG, null);
			if (!Float.isNaN(d))
				return d;
		}

		IVector3f[] top = m_config.getTopVertices();
		return Math3D.rayIntersectsPolygon(rayOrigin, rayDirection, top,
			IVector3f.Z_AXIS, null);
	}

	private float getConeDistance(Query i_query) {

		// if we hit an end cap, we are done
		float d = getCapDistance(i_query);
		if (!Float.isNaN(d))
			return d;

		// it's much quicker to intersect with a cone whose apex is the
		// origin and that extends along the positive Z axis, so translate the
		// ray accordingly
		Vector3f rayOrigin = Draw3DCache.getVector3f();
		try {
			Math3D
				.translate(i_query.getRayOrigin(), -0.5f, -0.5f, 0, rayOrigin);
			IVector3f rayDirection = i_query.getRayDirection();

			return doGetConeDistance(rayOrigin, rayDirection, 0, 1);
		} finally {
			Draw3DCache.returnVector3f(rayOrigin);
		}
	}

	private float getCylinderDistance(Query i_query) {

		// if we hit one of the end caps, we are done
		float d = getCapDistance(i_query);
		if (!Float.isNaN(d))
			return d;

		// it's much quicker to intersect with a cylinder that stands on the
		// origin and extends along the positive Z axis, so translate the ray
		// accordingly
		Vector3f rayOrigin = Draw3DCache.getVector3f();
		try {
			Math3D
				.translate(i_query.getRayOrigin(), -0.5f, -0.5f, 0, rayOrigin);
			IVector3f rayDirection = i_query.getRayDirection();

			// now intersect with infinite cylinder along Z axis:
			float xo = rayOrigin.getX();
			float yo = rayOrigin.getY();
			float xd = rayDirection.getX();
			float yd = rayDirection.getY();

			float A = xd * xd + yd * yd;
			float B = 2 * (xo * xd + yo * yd);
			float C = xo * xo + yo * yo - 0.25f; // radius^2 = 0.25f

			Math3D.solveQuadraticEquation(A, B, C, TMP_F2);
			return Math3D.minDistance(TMP_F2);
		} finally {
			Draw3DCache.returnVector3f(rayOrigin);
		}
	}

	private float getTruncatedConeDistance(Query i_query) {

		// if we hit an end cap, we are done
		float d = getCapDistance(i_query);
		if (!Float.isNaN(d))
			return d;

		// it's much quicker to intersect with a cone whose apex is the
		// origin and that extends along the positive Z axis, so translate the
		// ray accordingly and then check with a positive min Z value
		Vector3f rayOrigin = Draw3DCache.getVector3f();
		try {
			float h = m_config.getHeight();
			Math3D
				.translate(i_query.getRayOrigin(), -0.5f, -0.5f, h, rayOrigin);
			IVector3f rayDirection = i_query.getRayDirection();

			return doGetConeDistance(rayOrigin, rayDirection, h - 1, h);
		} finally {
			Draw3DCache.returnVector3f(rayOrigin);
		}
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

	private void renderFill(Graphics3D g3d) {

		g3d.glPolygonMode(Graphics3DDraw.GL_FRONT_AND_BACK,
			Graphics3DDraw.GL_FILL);

		float radiusProportions = m_config.getRadiusProportions();
		IVector3f[] top = m_config.getTopVertices();
		IVector3f[] bottom = m_config.getBottomVertices();

		// top
		g3d.glBegin(Graphics3DDraw.GL_POLYGON);
		for (int i = 0; i < top.length; i++)
			g3d.glVertex3f(top[i]);
		g3d.glEnd();

		if (radiusProportions == 0) {
			// sides
			g3d.glBegin(Graphics3DDraw.GL_TRIANGLE_FAN);
			g3d.glVertex3f(bottom[0]);
			for (int i = top.length - 1; i >= 0; i--)
				g3d.glVertex3f(top[i]);
			g3d.glVertex3f(top[top.length - 1]);
			g3d.glEnd();
		} else {
			g3d.glBegin(Graphics3DDraw.GL_QUAD_STRIP);
			for (int i = bottom.length - 1; i >= 0; i--) {
				g3d.glVertex3f(bottom[i]);
				g3d.glVertex3f(top[i]);
			}

			g3d.glVertex3f(bottom[bottom.length - 1]);
			g3d.glVertex3f(top[top.length - 1]);
			g3d.glEnd();

			// bottom
			g3d.glBegin(Graphics3DDraw.GL_POLYGON);
			for (int i = bottom.length - 1; i >= 0; i--)
				g3d.glVertex3f(bottom[i]);
			g3d.glEnd();
		}
	}

	private void renderOutline(Graphics3D g3d) {

		g3d.glPolygonMode(Graphics3DDraw.GL_FRONT_AND_BACK,
			Graphics3DDraw.GL_LINE);

		float radiusProportions = m_config.getRadiusProportions();
		IVector3f[] top = m_config.getTopVertices();
		IVector3f[] bottom = m_config.getBottomVertices();

		// top
		g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
		g3d.glNormal3f(0, 0, -1);
		for (int i = 0; i < top.length; i++)
			g3d.glVertex3f(top[i]);
		g3d.glEnd();

		if (radiusProportions == 0) {
			// sides
			g3d.glBegin(Graphics3DDraw.GL_LINES);
			for (int i = 0; i < top.length; i++) {
				g3d.glVertex3f(top[i]);
				g3d.glVertex3f(bottom[0]);
			}
			g3d.glEnd();
		} else {
			g3d.glBegin(Graphics3DDraw.GL_LINES);
			for (int i = 0; i < top.length; i++) {
				g3d.glVertex3f(top[i]);
				g3d.glVertex3f(bottom[i]);
			}
			g3d.glEnd();

			g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
			for (int i = 0; i < bottom.length; i++)
				g3d.glVertex3f(bottom[i]);
			g3d.glEnd();
		}
	}

	/**
	 * Specifies whether the polygons should be filled.
	 * 
	 * @param i_fill <code>true</code> if the polygons should be filled and
	 *            <code>false</code> otherwise
	 */
	public void setFill(boolean i_fill) {

		m_fill = i_fill;
	}

	/**
	 * Sets the fill color of this cylinder.
	 * 
	 * @param i_color the fill color
	 * @param i_alpha the alpha value
	 */
	public void setFillColor(Color i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_fillColor);
	}

	/**
	 * Specifies whether an outline should be drawn.
	 * 
	 * @param i_outline <code>true</code> if an outline should be drawn and
	 *            <code>false</code> otherwise
	 */
	public void setOutline(boolean i_outline) {

		m_outline = i_outline;
	}

	/**
	 * Sets the outline color of this cylinder.
	 * 
	 * @param i_color the outline color
	 * @param i_alpha the alpha value
	 */
	public void setOutlineColor(Color i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_outlineColor);
	}
}
