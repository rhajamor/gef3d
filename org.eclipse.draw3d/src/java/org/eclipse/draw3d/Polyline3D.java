/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw3d.picking.ColorProvider;
import org.eclipse.draw3d.shapes.PolylineShape;
import org.eclipse.swt.graphics.Color;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;


/**
 * 3D version of {@link org.eclipse.draw2d.Polyline}.
 * 
 * @todo Derive this figure from Shape3D (when Shape3D is needed)
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 26.11.2007
 */
public class Polyline3D extends Figure3D {

	/**
	 * The width of this poly line.
	 */
	float lineWidth = 1.0f;

	private PolylineShape m_shape = new PolylineShape();

	/**
	 * @todo Replace this with more efficient collection
	 */
	List<Vector3f> points = new ArrayList<Vector3f>(2);

	/**
	 * Adds a copy of the given point to the Polyline. Changes to the given
	 * point are not reflected by this polyline.
	 * 
	 * @param i_point the point to be added to the Polyline
	 */
	public void addPoint(IVector3f i_point) {

		points.add(new Vector3fImpl(i_point));
		// bounds = null;
		repaint();
	}

	/**
	 * Returns the last point in the Polyline.
	 * 
	 * @throws IndexOutOfBoundsException if the list is empty or contains only
	 *             one point
	 * @return the last point
	 */
	public IVector3f getEnd() {

		if (points.size() < 2) {
			throw new IndexOutOfBoundsException(
					"pointlist is empty or contains only one point");
		}

		Vector3f pt = points.get(points.size() - 1);
		log.info("getEnd(): " + pt);

		return pt;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Connection#getPoints()
	 */
	public PointList getPoints() {
		// TODO implement method PolylineConnection3D.getPoints
		return null;
	}

	/**
	 * Returns the points in this Polyline <B>by reference</B>. If the returned
	 * list is modified, this Polyline must be informed by calling
	 * {@link #setPoints(PointList)}. Failure to do so will result in layout and
	 * paint problems.
	 * 
	 * @return this Polyline's points
	 * @since 2.0
	 */
	public List<Vector3f> getPoints3D() {
		return points;
	}

	/**
	 * @return the first point in the Polyline or null, if line contains no
	 *         points
	 * @throws IndexOutOfBoundsException if the list is empty
	 * @since 2.0
	 */
	public IVector3f getStart() {
		return points.get(0);
	}

	/**
	 * Inserts a copy of the given point at a specified index in the polyline.
	 * Changes to the given point are not reflected by this polyline.
	 * 
	 * @param i_point the point to be added
	 * @param i_index the position in the Polyline where the point is to be
	 *            added
	 * @throws IndexOutOfBoundsException - if the index is out of range (index <
	 *             0 || index >= size()).
	 */
	public void insertPoint(IVector3f i_point, int i_index) {

		// bounds = null;
		log.info("insertPoint(IVector3f, int): " + i_point + ", "
				+ i_index);
		points.set(i_index, new Vector3fImpl(i_point));
		repaint();
	}

	/**
	 * @return <code>false</code> because Polyline's aren't filled
	 */
	@Override
	public boolean isOpaque() {
		return false;
	}

	/**
	 * @see Figure#primTranslate(int, int)
	 */
	@Override
	public void primTranslate(int x, int y) {
	}

	/**
	 * Erases the Polyline and removes all of its {@link IVector3f
	 * Points}.
	 * 
	 * @since 2.0
	 */
	public void removeAllPoints() {
		erase();
		// bounds = null;
		points.clear();
	}

	/**
	 * Removes a point from the Polyline.
	 * 
	 * @param index the position of the point to be removed
	 * @throws IndexOutOfBoundsException - if the index is out of range (index <
	 *             0 || index >= size()).
	 */
	public void removePoint(int index) {
		erase();
		// bounds = null;
		points.remove(index);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#render()
	 */
	@Override
	public void render(RenderContext renderContext) {

		m_shape.setPoints(points);

		if (renderContext.getMode().isPaint()) {
			Color color = getForegroundColor();
			int alpha = getAlpha();

			m_shape.setColor(color, alpha);
			m_shape.render(renderContext);
		} else {
			int color = renderContext.getColor(this);
			if (color != ColorProvider.IGNORE) {
				m_shape.setColor(color, 255);
				m_shape.render(renderContext);
			}
		}

	}

	/**
	 * Sets the end point of the Polyline. If line contains only one point, the
	 * new point is added as second point, otherwise the last point is replaced
	 * by the new point.
	 * 
	 * @param end the point that will become the last point in the Polyline
	 * @throws IndexOutOfBoundsException - if line contains no point
	 */
	public void setEnd(IVector3f end) {
		if (points.size() == 0) {
			throw new IndexOutOfBoundsException(
					"line contains no points, set start point before");
		}
		if (points.size() < 2) {
			addPoint(end);
		} else {
			setPoint(end, points.size() - 1);
		}
	}

	/**
	 * Sets the points at both extremes of the Polyline
	 * 
	 * @param start the point to become the first point in the Polyline
	 * @param end the point to become the last point in the Polyline
	 */
	public void setEndpoints(IVector3f start, IVector3f end) {
		setStart(start);
		setEnd(end);
	}

	/**
	 * @see org.eclipse.draw2d.Shape#setLineWidth(int)
	 */
	public void setLineWidth(float w) {
		if (lineWidth == w) {
			return;
		}
		if (w < lineWidth) {
			erase();
		}
		// bounds = null;
		// super.setLineWidth(w);
		lineWidth = w;
	}

	/**
	 * Sets the point at <code>index</code> to the IVector3f
	 * <code>pt</code>. Calling this method results in a recalculation of the
	 * polyline's bounding box. If you're going to set multiple Points, use
	 * {@link #setPoints(PointList)}.
	 * 
	 * @param pt the point
	 * @param index the index
	 */
	public void setPoint(IVector3f pt, int index) {
		erase();
		points.get(index).set(pt);
		// points.set(index, new Vector3f(pt));
		// bounds = null;
		repaint();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Connection#setPoints(org.eclipse.draw2d.geometry.PointList)
	 */
	public void setPoints(PointList i_list) {
		// TODO implement method PolylineConnection3D.setPoints

	}

	/**
	 * Sets the list of points to be used by this polyline connection. Removes
	 * any previously existing points. The polyline will hold onto the given
	 * list by reference.
	 * 
	 * @param points new set of points
	 * @since 2.0
	 */
	public void setPoints3D(List<Vector3f> points) {
		erase();
		this.points = points;
		// TODO what's that?
		// bounds = null;
		firePropertyChange(Connection.PROPERTY_POINTS, null, points);
		repaint();
		invalidate();
	}

	/**
	 * Sets the start point of the Polyline. If list contains no points, the new
	 * point is added to the point list, otherwise the first point is replaced.
	 * 
	 * @param start the point that will become the first point in the Polyline
	 * @since 2.0
	 */
	public void setStart(IVector3f start) {
		if (points.size() == 0) {
			addPoint(start);
		} else {
			setPoint(start, 0);
		}
	}
}
