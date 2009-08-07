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

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * PositionableShape There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.08.2009
 */
public abstract class PositionableShape implements Shape {

	private IPosition3D m_position3D;

	/**
	 * Creates a new positionable shape with the given position.
	 * 
	 * @param i_position3D the position of this shape
	 * @throws NullPointerException if the given position is <code>null</code>
	 */
	public PositionableShape(IPosition3D i_position3D) {

		m_position3D = i_position3D;
	}

	/**
	 * Performs the intersection calculation using the given picking ray. The
	 * picking ray may have been transformed due to the position of this shape.
	 * 
	 * @param i_query the modified query
	 * @return the distance between the point of intersection of the picking ray
	 *         and this shape or {@link Float#NaN} if the ray does not intersect
	 *         with this shape
	 */
	protected abstract float doGetDistance(Query i_query);

	/**
	 * Performs the actual rendering.
	 * 
	 * @param i_renderContext the current render context
	 */
	protected abstract void doRender(RenderContext i_renderContext);

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getDistance(org.eclipse.draw3d.picking.Query)
	 */
	public float getDistance(Query i_query) {

		if (m_position3D == null
			|| m_position3D.getTransformationMatrix()
				.equals(IMatrix4f.IDENTITY))
			return doGetDistance(i_query);

		Vector3f newOrigin = Draw3DCache.getVector3f();
		Vector3f newDirection = Draw3DCache.getVector3f();
		try {
			IVector3f oldOrigin = i_query.getRayOrigin();
			IVector3f oldDirection = i_query.getRayDirection();

			newOrigin.set(oldOrigin);
			newDirection.set(oldDirection);

			// transform picking ray
			m_position3D.transformRay(newOrigin, newDirection);

			float newLength = newDirection.length();
			if (newLength != 1)
				newDirection.scale(1 / newLength); // normalise

			i_query.setRay(newOrigin, newDirection);
			float distance = doGetDistance(i_query);

			i_query.setRay(oldOrigin, oldDirection);
			return distance / newLength;
		} finally {
			Draw3DCache.returnVector3f(newOrigin, newDirection);
		}
	}

	/**
	 * Returns the position of this shape.
	 * 
	 * @return the position of this shape
	 */
	public IPosition3D getPosition3D() {

		return m_position3D;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render(org.eclipse.draw3d.RenderContext)
	 */
	public void render(RenderContext i_renderContext) {

		Graphics3D g3d = i_renderContext.getGraphics3D();

		if (m_position3D == null)
			doRender(i_renderContext);
		else {

			IMatrix4f modelMatrix = m_position3D.getTransformationMatrix();

			boolean useModelMatrix = !IMatrix4f.IDENTITY.equals(modelMatrix);
			g3d.glMatrixMode(Graphics3DDraw.GL_MODELVIEW);

			if (useModelMatrix)
				g3d.glPushMatrix();

			try {
				// TODO this must be optimized
				if (useModelMatrix)
					g3d.setPosition(m_position3D);

				doRender(i_renderContext);
			} finally {
				if (useModelMatrix)
					g3d.glPopMatrix();
			}
		}
	}
}
