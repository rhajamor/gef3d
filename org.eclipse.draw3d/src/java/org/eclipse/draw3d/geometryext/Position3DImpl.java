/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.geometryext;

import java.util.EnumSet;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.geometry.BoundingBox;
import org.eclipse.draw3d.geometry.BoundingBoxImpl;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Matrix4fImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.geometryext.IPosition3D.MatrixState;
import org.eclipse.draw3d.geometryext.IPosition3D.PositionHint;

/**
 * Implementation of {@link Position3D} with a backing bound object.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 */
public class Position3DImpl extends AbstractPosition3D {
	
	
	
	private IHost3D host;
	private BoundingBoxImpl bounds3D;

	/**
	 * @param i_host, must not be null
	 */
	public Position3DImpl(IHost3D i_host) {
		if (i_host == null) // parameter precondition
			throw new NullPointerException("i_host must not be null");

		host = i_host;
		bounds3D = new BoundingBoxImpl();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getHost()
	 */
	public IHost3D getHost() {
		return host;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getBounds3D()
	 */
	public IBoundingBox getBounds3D() {
		return bounds3D;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getLocation3D()
	 */
	public IVector3f getLocation3D() {
		return bounds3D.m_position;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.Position3D#setLocation3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setLocation3D(IVector3f i_point) {
		if (i_point == null) // parameter precondition
			throw new NullPointerException("i_point must not be null");

		if (getLocation3D().equals(i_point))
			return;

		Vector3fImpl delta = new Vector3fImpl();
		Math3D.sub(i_point, getLocation3D(), delta);

		bounds3D.setLocation(i_point.getX(), i_point.getY(),i_point.getZ());

		invalidateMatrices();

		host.positionChanged(EnumSet.of(PositionHint.location), delta);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getSize3D()
	 */
	public IVector3f getSize3D() {
		return bounds3D.m_size;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.Position3D#setSize3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setSize3D(IVector3f i_size) {
		if (i_size == null) // parameter precondition
			throw new NullPointerException("i_size must not be null");
		if (i_size.getX() < 0 || i_size.getY() < 0 || i_size.getZ() < 0) // parameter
			// precondition
			throw new IllegalArgumentException(
					"no value of given vector must be less 0, , was " + i_size);

		IVector3f size3D = getSize3D();

		if (size3D.equals(i_size))
			return;

		Vector3fImpl delta = new Vector3fImpl();
		Math3D.sub(i_size, size3D, delta);

		bounds3D.setSize(i_size);
		
		invalidateMatrices();

		host.positionChanged(EnumSet.of(PositionHint.size), delta);
	}
}
