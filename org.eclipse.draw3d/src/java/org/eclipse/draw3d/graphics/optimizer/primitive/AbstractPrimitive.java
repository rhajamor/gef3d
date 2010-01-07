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

import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4fImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveBounds;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * AbstractPrimitve There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 24.12.2009
 */
public abstract class AbstractPrimitive implements Primitive {

	private PrimitiveBounds m_bounds;

	private RenderRule m_renderRule;

	private IMatrix4f m_transformation;

	protected AbstractPrimitive(IMatrix4f i_transformation,
			RenderRule i_renderRule) {

		if (i_transformation != null)
			m_transformation = new Matrix4fImpl(i_transformation);

		m_renderRule = i_renderRule;
	}

	protected abstract PrimitiveBounds calculateBounds();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.Primitive#getBounds()
	 */
	public PrimitiveBounds getBounds() {

		if (m_bounds == null)
			m_bounds = calculateBounds();

		return m_bounds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.Primitive#getRenderRule()
	 */
	public RenderRule getRenderRule() {

		return m_renderRule;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.Primitive#getTransformation()
	 */
	public IMatrix4f getTransformation() {

		return m_transformation;
	}

	protected float[] getTransformedVertices(int i_x, int i_y, int i_w, int i_h) {

		IMatrix4f t = getTransformation();
		float[] vertices = new float[8];

		if (t != null && !IMatrix4f.IDENTITY.equals(t)) {
			Vector3f v = Draw3DCache.getVector3f();
			try {
				v.set(i_x, i_y, 0);
				Math3D.transform(v, t, v);
				vertices[0] = v.getX();
				vertices[1] = v.getY();

				v.set(i_x, i_y + i_h, 0);
				Math3D.transform(v, t, v);
				vertices[2] = v.getX();
				vertices[3] = v.getY();

				v.set(i_x + i_w, i_y + i_h, 0);
				Math3D.transform(v, t, v);
				vertices[4] = v.getX();
				vertices[5] = v.getY();

				v.set(i_x + i_w, i_y, 0);
				Math3D.transform(v, t, v);
				vertices[6] = v.getX();
				vertices[7] = v.getY();
			} finally {
				Draw3DCache.returnVector3f(v);
			}
		} else {
			vertices[0] = i_x;
			vertices[1] = i_y;
			vertices[2] = i_x;
			vertices[3] = i_y + i_h;
			vertices[4] = i_x + i_w;
			vertices[5] = i_y + i_h;
			vertices[6] = i_x + i_h;
			vertices[7] = i_y;
		}

		return vertices;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.Primitive#intersects(org.eclipse.draw3d.graphics.optimizer.primitive.Primitive)
	 */
	public boolean intersects(Primitive i_primitive) {

		PrimitiveBounds bounds = i_primitive.getBounds();
		if (getBounds().intersects(bounds))
			return true;

		if (getBounds().contains(bounds))
			return true;

		if (bounds.contains(getBounds()))
			return true;

		return false;
	}
}
