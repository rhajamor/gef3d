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

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Math3DCache;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Math3D.Side;
import org.eclipse.draw3d.geometryext.Plane;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.swt.graphics.Color;

/**
 * A polyline shape can be used to render polylines.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 03.04.2008
 */
public class PolylineShape implements Shape {

	private static final float ACCURACY = 10f;

	private static final String KEY_HORIZONTAL_BORDER = "horizontal border";

	private static final String KEY_VERTICAL_BORDER = "vertical border";

	private static final String KEY_VISIBLE_BORDER = "visible border";

	private final float[] m_color = new float[] { 0, 0, 0, 1 };

	private List<IVector3f> m_points;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#getDistance(org.eclipse.draw3d.picking.Query,
	 *      org.eclipse.draw3d.geometry.Position3D)
	 */
	public float getDistance(Query i_query, Position3D i_position) {

		if (m_points.size() < 2)
			return Float.NaN;

		Plane visBorder = getVisibleBorder(i_query);
		Plane hBorder = getHorizontalBorder(i_query);
		Plane vBorder = getVerticalBorder(i_query);

		IVector3f p1 = m_points.get(0);
		IVector3f p2;

		Side visSide1 = visBorder.getSide(p1);
		Side hSide1 = null, vSide1 = null, visSide2, hSide2, vSide2;

		for (int i = 1; i < m_points.size(); i++) {
			p2 = m_points.get(i);
			visSide2 = visBorder.getSide(p2);

			// is at least one point in front of the camera?
			if (visSide1 != visSide2 || visSide1 == Side.FRONT) {

				if (hSide1 == null)
					hSide1 = hBorder.getSide(p1);
				hSide2 = hBorder.getSide(p2);

				// are the points on different sides or on the horizontal plane?
				if (hSide1 != hSide2 || (hSide1 == null && hSide2 == null)) {

					if (vSide1 == null)
						vSide1 = vBorder.getSide(p1);
					vSide2 = vBorder.getSide(p2);

					// are the points on different sides or on the vertical
					// plane?
					if (vSide1 != vSide2 || (vSide1 == null && vSide2 == null)) {
						Vector3f intersection = Math3DCache.getVector3f();
						Vector3f tmp = Math3DCache.getVector3f();
						try {
							// the two points are in different quadrants or on a
							// plane, so we try and hit the segment
							Plane intersectingBorder = null;
							if ((hSide1 == null && hSide2 == null))
								intersectingBorder = vBorder;
							else
								intersectingBorder = hBorder;

							intersectingBorder.intersectionWithSegment(p1, p2,
								intersection);

							// intersection only if tmp is on the
							// picking ray
							IVector3f rayStart = i_query.getRayStart();
							IVector3f rayDirection = i_query.getRayDirection();

							Math3D.sub(intersection, rayStart, tmp);
							float fx = tmp.getX() / rayDirection.getX();
							float fy = tmp.getY() / rayDirection.getY();
							float fz = tmp.getZ() / rayDirection.getZ();

							if (!Math3D.equals(fx, fy, ACCURACY))
								return Float.NaN;

							if (!Math3D.equals(fx, fz, ACCURACY))
								return Float.NaN;

							if (!Math3D.equals(fy, fz, ACCURACY))
								return Float.NaN;

							return (fx + fy + fz) / 3f;
						} finally {
							Math3DCache.returnVector3f(intersection);
							Math3DCache.returnVector3f(tmp);
						}
					}

					vSide1 = vSide2;
				} else {
					vSide1 = null;
				}

				hSide1 = hSide2;
			} else {
				hSide1 = null;
				vSide1 = null;
			}

			visSide1 = visSide2;
			p1 = p2;
		}

		return Float.NaN;
	}

	private Plane getHorizontalBorder(Query i_query) {

		Plane horizontalBorder = (Plane) i_query.get(KEY_HORIZONTAL_BORDER);
		if (horizontalBorder == null) {
			Vector3f normal = Math3DCache.getVector3f();
			try {
				Math3D.cross(i_query.getRayStart(), i_query.getRayDirection(),
					normal);
				Math3D.normalise(normal, normal);

				horizontalBorder = new Plane();
				horizontalBorder.set(i_query.getRayStart(), normal);

				i_query.set(KEY_HORIZONTAL_BORDER, horizontalBorder);
			} finally {
				Math3DCache.returnVector3f(normal);
			}
		}

		return horizontalBorder;
	}

	/**
	 * @return the points
	 */
	public List<IVector3f> getPoints() {
		return m_points;
	}

	private Plane getVerticalBorder(Query i_query) {

		Plane verticalBorder = (Plane) i_query.get(KEY_VERTICAL_BORDER);
		if (verticalBorder == null) {
			Vector3f hNormal = Math3DCache.getVector3f();
			Vector3f vNormal = Math3DCache.getVector3f();
			try {
				Plane horizontalBorder = getHorizontalBorder(i_query);
				horizontalBorder.getNormal(hNormal);
				Math3D.cross(i_query.getRayDirection(), hNormal, vNormal);

				verticalBorder = new Plane();
				verticalBorder.set(i_query.getRayStart(), vNormal);

				i_query.set(KEY_VERTICAL_BORDER, verticalBorder);
			} finally {
				Math3DCache.returnVector3f(hNormal);
				Math3DCache.returnVector3f(vNormal);
			}
		}

		return verticalBorder;
	}

	private Plane getVisibleBorder(Query i_query) {

		Plane visibleBorder = (Plane) i_query.get(KEY_VISIBLE_BORDER);
		if (visibleBorder == null) {
			visibleBorder = new Plane();
			visibleBorder.set(i_query.getRayStart(), i_query.getRayDirection());

			i_query.set(KEY_VISIBLE_BORDER, visibleBorder);
		}

		return visibleBorder;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render()
	 */
	public void render(RenderContext renderContext) {

		if (m_points == null || m_points.isEmpty())
			return;

		float red = m_color[0];
		float green = m_color[1];
		float blue = m_color[2];
		float alpha = m_color[3];
		Graphics3D g3d = renderContext.getGraphics3D();

		g3d.glColor4f(red, green, blue, alpha);

		g3d.glBegin(Graphics3DDraw.GL_LINE_STRIP);
		for (IVector3f point : m_points)
			g3d.glVertex3f(point.getX(), point.getY(), point.getZ());
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
	public void setPoints(List<IVector3f> i_points) {

		if (i_points == null)
			throw new NullPointerException("i_points must not be null");

		m_points = i_points;
	}

}
