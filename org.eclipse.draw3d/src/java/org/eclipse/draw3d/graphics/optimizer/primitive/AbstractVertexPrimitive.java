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
package org.eclipse.draw3d.graphics.optimizer.primitive;

import java.util.Arrays;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveBounds;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * AbstractVertexPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 25.12.2009
 */
public abstract class AbstractVertexPrimitive extends AbstractPrimitive
		implements VertexPrimitive {



	private static float[] getVertices(PointList i_points) {

		Point p = Draw3DCache.getPoint();
		try {
			int s = i_points.size();
			float[] vertices = new float[2 * s];

			for (int i = 0; i < s; i++) {
				i_points.getPoint(p, i);
				vertices[2 * i] = p.x;
				vertices[2 * i + 1] = p.y;
			}

			return vertices;
		} finally {
			Draw3DCache.returnPoint(p);
		}
	}

	protected float[] m_transformedVertices;

	protected float[] m_vertices;

	protected AbstractVertexPrimitive(IMatrix4f i_transformation,
			RenderRule i_renderRule, float[] i_vertices) {

		super(i_transformation, i_renderRule);

		m_vertices = i_vertices;
		if (i_transformation != null
			&& !IMatrix4f.IDENTITY.equals(i_transformation)) {

			Vector3f v = Draw3DCache.getVector3f();
			try {
				int s = m_vertices.length / 2;
				m_transformedVertices = new float[2 * s];

				for (int i = 0; i < s; i++) {
					v.set(m_vertices[2 * i], m_vertices[2 * i + 1], 0);
					Math3D.transform(v, i_transformation, v);
					m_transformedVertices[2 * i] = v.getX();
					m_transformedVertices[2 * i + 1] = v.getY();
				}
			} finally {
				Draw3DCache.returnVector3f(v);
			}
		} else
			m_transformedVertices = i_vertices;
	}

	protected AbstractVertexPrimitive(IMatrix4f i_transformation,
			RenderRule i_renderRule, PointList i_vertices) {

		this(i_transformation, i_renderRule, getVertices(i_vertices));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractPrimitive#calculateBounds()
	 */
	@Override
	protected PrimitiveBounds calculateBounds() {

		return new PrimitiveBounds(m_transformedVertices);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.VertexPrimitive#getTransformedVertices()
	 */
	public float[] getTransformedVertices() {

		return m_transformedVertices;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.VertexPrimitive#getVertexCount()
	 */
	public int getVertexCount() {

		return m_vertices.length / 2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.VertexPrimitive#getVertices()
	 */
	public float[] getVertices() {

		return m_vertices;
	}

	@Override
	public String toString() {
		return "AbstractVertexPrimitive [m_transformedVertices="
			+ Arrays.toString(m_transformedVertices) + ", m_vertices="
			+ Arrays.toString(m_vertices) + "]";
	}
}
