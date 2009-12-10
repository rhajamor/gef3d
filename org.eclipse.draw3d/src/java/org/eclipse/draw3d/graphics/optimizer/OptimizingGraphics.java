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
package org.eclipse.draw3d.graphics.optimizer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics.StatefulGraphics;
import org.eclipse.draw3d.util.ArcHelper;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * OptimizingGraphics There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class OptimizingGraphics extends StatefulGraphics {

	private static enum DrawType {
		FILL, HGRADIENT, OUTLINE, VGRADIENT
	}

	private static final float ARC_PREC = 0.5f;

	private static final float PI_4 = (float) Math.PI / 4;

	private PrimitiveSet m_primitives;

	private void addGradientPrimitive(Primitive i_primitive) {

		addPrimitive(i_primitive, getAttributes(i_primitive.getType(), true));
	}

	private void addPrimitive(Primitive i_primitive) {

		addPrimitive(i_primitive, getAttributes(i_primitive.getType(), false));

	}

	private void addPrimitive(Primitive i_primitive, Attributes i_attributes) {

		PrimitiveType type = i_primitive.getType();
		if (m_primitives == null)
			m_primitives = new PrimitiveSet(type, i_attributes);

		if (!m_primitives.add(i_primitive, i_attributes)) {
			m_primitives = new PrimitiveSet(m_primitives, type, i_attributes);
			if (!m_primitives.add(i_primitive, i_attributes))
				throw new AssertionError("cannot add primitive " + i_primitive);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int i_x, int i_y, int i_w, int i_h, int i_offset,
		int i_length) {

		float rOffset = (float) Math.toRadians(i_offset + i_length);
		float rLength = (float) Math.toRadians(-i_length);

		ArcHelper helper =
			new ArcHelper(ARC_PREC, i_x, i_y, i_w, i_h, rOffset, rLength, false);

		float[] vertices = helper.getArray();
		transform(vertices);

		addPrimitive(new PolylinePrimitive(vertices));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawFocus(int, int, int, int)
	 */
	@Override
	public void drawFocus(int i_x, int i_y, int i_w, int i_h) {

		pushState();
		try {
			Device dev = Display.getCurrent();
			Color c = dev.getSystemColor(SWT.COLOR_LIST_SELECTION);
			setForegroundColor(c);
			setLineStyle(SWT.LINE_SOLID);
			setLineWidth(2);
			drawRectangle(i_x, i_y, i_w, i_h);
		} finally {
			popState();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image,
	 *      int, int)
	 */
	@Override
	public void drawImage(Image i_srcImage, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.drawImage

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image,
	 *      int, int, int, int, int, int, int, int)
	 */
	@Override
	public void drawImage(Image i_srcImage, int i_x1, int i_y1, int i_w1,
		int i_h1, int i_x2, int i_y2, int i_w2, int i_h2) {
		// TODO implement method OptimizingGraphics.drawImage

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int i_x1, int i_y1, int i_x2, int i_y2) {

		addPrimitive(new LinePrimitive(i_x1, i_y1, i_x2, i_y2));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int i_x, int i_y, int i_w, int i_h) {

		drawArc(i_x, i_y, i_w, i_h, 0, 360);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolygon(PointList i_points) {

		polygon(i_points, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPolyline(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolyline(PointList i_points) {

		Point p = Draw3DCache.getPoint();
		try {
			float[] vertices = new float[2 * i_points.size()];
			for (int i = 0; i < i_points.size(); i++) {
				i_points.getPoint(p, i);
				vertices[2 * i] = p.x;
				vertices[2 * i + 1] = p.y;
			}

			transform(vertices);
			addPrimitive(new PolylinePrimitive(vertices));
		} finally {
			Draw3DCache.returnPoint(p);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawRectangle(int, int, int, int)
	 */
	@Override
	public void drawRectangle(int i_x, int i_y, int i_width, int i_height) {

		rectangle(i_x, i_y, i_width, i_height, DrawType.OUTLINE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void drawRoundRectangle(Rectangle i_r, int i_arcWidth,
		int i_arcHeight) {

		roundRectangle(i_r, i_arcWidth, i_arcHeight, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawString(java.lang.String, int, int)
	 */
	@Override
	public void drawString(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.drawString

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawText(java.lang.String, int, int)
	 */
	@Override
	public void drawText(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.drawText

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int i_x, int i_y, int i_w, int i_h, int i_offset,
		int i_length) {

		ArcHelper helper =
			new ArcHelper(ARC_PREC, i_x, i_y, i_w, i_h, i_offset + i_length,
				-i_length, false);

		float[] vertices = new float[2 * (helper.getNumVertices() + 1)];
		helper.getArray(vertices);

		vertices[vertices.length - 2] = i_x + i_w;
		vertices[vertices.length - 1] = i_y + i_h;

		transform(vertices);
		addPrimitive(new PolygonPrimitive(vertices, true));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillGradient(int, int, int, int,
	 *      boolean)
	 */
	@Override
	public void fillGradient(int i_x, int i_y, int i_w, int i_h,
		boolean i_vertical) {

		rectangle(i_x, i_y, i_w, i_h, i_vertical ? DrawType.VGRADIENT
			: DrawType.HGRADIENT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int i_x, int i_y, int i_w, int i_h) {

		ArcHelper helper =
			new ArcHelper(ARC_PREC, i_x, i_y, i_w, i_h, 0,
				-2 * (float) Math.PI, true);

		float[] vertices = helper.getArray();

		transform(vertices);
		addPrimitive(new PolygonPrimitive(vertices, true));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void fillPolygon(PointList i_points) {

		polygon(i_points, true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillRectangle(int, int, int, int)
	 */
	@Override
	public void fillRectangle(int i_x, int i_y, int i_width, int i_height) {

		rectangle(i_x, i_y, i_width, i_height, DrawType.FILL);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void fillRoundRectangle(Rectangle i_r, int i_arcWidth,
		int i_arcHeight) {

		roundRectangle(i_r, i_arcWidth, i_arcHeight, true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillString(java.lang.String, int, int)
	 */
	@Override
	public void fillString(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.fillString

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillText(java.lang.String, int, int)
	 */
	@Override
	public void fillText(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.fillText

	}

	private Attributes getAttributes(PrimitiveType i_type, boolean i_gradient) {

		switch (i_type) {
		case FILLED_POLYGON:
		case FILLED_QUAD:
			return i_gradient ? new GradientAttributes(getState())
				: new FillAttributes(getState());
		case OUTLINED_POLYGON:
		case OUTLINED_QUAD:
		case POLYLINE:
		case LINE:
			return new OutlineAttributes(getState());
		default:
			throw new AssertionError("unknown primitive type: " + i_type);
		}
	}

	private void polygon(PointList i_points, boolean i_fill) {

		Point p = Draw3DCache.getPoint();
		try {
			float[] vertices = new float[2 * i_points.size()];
			for (int i = 0; i < i_points.size(); i++) {
				i_points.getPoint(p, i);
				vertices[2 * i] = p.x;
				vertices[2 * i + 1] = p.y;
			}

			transform(vertices);
			addPrimitive(new PolygonPrimitive(vertices, i_fill));
		} finally {
			Draw3DCache.returnPoint(p);
		}
	}

	private void rectangle(int i_x, int i_y, int i_width, int i_height,
		DrawType i_drawType) {

		int x2 = i_x + i_width;
		int y2 = i_y + i_height;

		float[] vertices;
		if (i_drawType == DrawType.VGRADIENT)
			vertices = new float[] { x2, i_y, i_x, i_y, i_x, y2, x2, y2 };
		else
			vertices = new float[] { i_x, i_y, i_x, y2, x2, y2, x2, i_y };

		transform(vertices);
		switch (i_drawType) {
		case OUTLINE:
			addPrimitive(new QuadPrimitive(vertices, false));
			break;
		case FILL:
			addPrimitive(new QuadPrimitive(vertices, true));
			break;
		case HGRADIENT:
		case VGRADIENT:
			addGradientPrimitive(new QuadPrimitive(vertices, true));
			break;
		default:
			throw new AssertionError();
		}
	}

	private void roundRectangle(Rectangle i_r, int i_arcWidth, int i_arcHeight,
		boolean i_fill) {

		float x1 = i_r.x;
		float y1 = i_r.y;
		float x2 = x1 + i_r.width;
		float y2 = y1 + i_r.height;
		float w = i_arcWidth;
		float h = i_arcHeight;

		ArcHelper helper =
			new ArcHelper(ARC_PREC, x1, y1, w, h, PI_4, -PI_4, false);

		int offset = 0;
		int n = helper.getNumVertices();
		float[] vertices = new float[4 * 2 * n];

		helper.getArray(vertices, offset);
		offset += 2 * n;

		helper =
			new ArcHelper(ARC_PREC, x1, y2 - h, w, h, 2 * PI_4, -PI_4, false);
		helper.getArray(vertices, offset);
		offset += 2 * n;

		helper =
			new ArcHelper(ARC_PREC, x2 - w, y2 - h, w, h, 3 * PI_4, -PI_4,
				false);
		helper.getArray(vertices, offset);
		offset += 2 * n;

		helper = new ArcHelper(ARC_PREC, x2 - w, y1, w, h, 0, -PI_4, false);
		helper.getArray(vertices, offset);

		transform(vertices);
		addPrimitive(new PolygonPrimitive(vertices, i_fill));
	}

	private void transform(float[] i_vertices) {

		Matrix4f t = getState().getTransformation();
		if (t == null || IMatrix4f.IDENTITY.equals(t))
			return;

		Vector3f v = Draw3DCache.getVector3f();
		Vector3f r = Draw3DCache.getVector3f();
		try {
			for (int i = 0; i < i_vertices.length / 2; i++) {
				v.setX(i_vertices[2 * i]);
				v.setY(i_vertices[2 * i + 1]);

				Math3D.transform(v, t, r);

				i_vertices[2 * i] = r.getX();
				i_vertices[2 * i + 1] = r.getY();
			}
		} finally {
			Draw3DCache.returnVector3f(v, r);
		}
	}

	/**
	 * @return
	 */
	public List<PrimitiveSet> getPrimiveSets() {

		if (m_primitives == null)
			return Collections.emptyList();

		List<PrimitiveSet> result = new LinkedList<PrimitiveSet>();
		m_primitives.getSets(result);

		return result;
	}
}
