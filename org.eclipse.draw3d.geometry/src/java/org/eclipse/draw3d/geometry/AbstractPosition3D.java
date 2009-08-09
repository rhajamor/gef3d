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
package org.eclipse.draw3d.geometry;

import java.util.EnumSet;

/**
 * Abstract implementation of {@link Position3D}, this implementation is the
 * base class for 2D-bounds synchronized and independent implementations.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 */
public abstract class AbstractPosition3D implements Position3D {

	/**
	 * The matrix with which to transform the direction of a ray.
	 * 
	 * @see #transformRay(Vector3f, Vector3f)
	 */
	private transient Matrix4fImpl m_rayDirectionMatrix;

	/**
	 * The matrix with which to transform the origin of a ray.
	 * 
	 * @see #transformRay(Vector3f, Vector3f)
	 */
	private transient Matrix4fImpl m_rayOriginMatrix;

	private transient Matrix4fImpl m_rotationLocationMatrix;

	private transient Matrix4fImpl m_transformationMatrix;

	private boolean m_valid;

	private boolean m_invertible;

	/**
	 * The rotation angles of this figure.
	 */
	protected Vector3f rotationAngles;

	/**
	 * Boolean semaphore used by {@link #syncSize()} and {@link #syncSize3D()}
	 * to avoid infinite loop.
	 */
	protected boolean updatingBounds;

	/**
	 * Creates and initializes a new instance.
	 */
	public AbstractPosition3D() {

		rotationAngles = new Vector3fImpl(); // null vector
		m_transformationMatrix = new Matrix4fImpl(); // identity
		m_rotationLocationMatrix = new Matrix4fImpl(); // identity
		m_rayOriginMatrix = new Matrix4fImpl(); // identity
		m_rayDirectionMatrix = new Matrix4fImpl(); // identiy
		m_valid = false;
		m_invertible = false;
		updatingBounds = false;
	}

	/**
	 * Notifies host if present.
	 * 
	 * @param hint
	 * @param delta
	 */
	protected void firePositionChanged(PositionHint hint, IVector3f delta) {
		if (getHost() != null)
			getHost().positionChanged(EnumSet.of(hint), delta);
	}

	private Position3D getParentPosition() {

		IHost3D host = getHost();
		if (host != null) {
			IHost3D parent = host.getParentHost3D();
			if (parent != null)
				return parent.getPosition3D();

		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.IPosition3D#getRotation3D()
	 */
	public IVector3f getRotation3D() {

		return rotationAngles;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.IPosition3D#getRotationLocationMatrix()
	 */
	public IMatrix4f getRotationLocationMatrix() {

		validate();
		return m_rotationLocationMatrix;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.IPosition3D#getTransformationMatrix()
	 */
	public IMatrix4f getTransformationMatrix() {

		validate();
		return m_transformationMatrix;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.Position3D#invalidate()
	 */
	public void invalidate() {

		m_valid = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.IPosition3D#isValid()
	 */
	public boolean isValid() {

		if (!m_valid)
			return false;

		Position3D parentPosition = getParentPosition();
		if (parentPosition != null)
			if (!parentPosition.isValid())
				return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.Position3D#setPosition(org.eclipse.draw3d.geometry.IPosition3D)
	 */
	public void setPosition(IPosition3D i_source) {
		setRotation3D(i_source.getRotation3D());
		setSize3D(i_source.getSize3D());
		setLocation3D(i_source.getLocation3D());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.Position3D#setRotation3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setRotation3D(IVector3f i_rotation) {
		if (i_rotation == null) // parameter precondition
			throw new NullPointerException("i_rotation must not be null");

		if (rotationAngles.equals(i_rotation))
			return;

		Vector3fImpl delta = new Vector3fImpl();
		Math3D.sub(i_rotation, rotationAngles, delta);

		rotationAngles.set(i_rotation);
		invalidate();

		firePositionChanged(PositionHint.ROTATION, delta);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("l=").append(getLocation3D());
		result.append(", s=").append(getSize3D());
		result.append(", r=").append(getRotation3D());
		result.append(", host=").append(getHost());
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.IPosition3D#transformRay(org.eclipse.draw3d.geometry.Vector3f,
	 *      org.eclipse.draw3d.geometry.Vector3f)
	 */
	public boolean transformRay(Vector3f i_origin, Vector3f i_direction) {

		validate();

		if (!m_invertible)
			return false;

		i_origin.transform(m_rayOriginMatrix);
		i_direction.transform(m_rayDirectionMatrix);
		return true;
	}

	private void validate() {

		if (isValid())
			return;

		Vector3f center = Math3DCache.getVector3f();
		Vector3f invRotation = Math3DCache.getVector3f();
		Vector3f invScale = Math3DCache.getVector3f();
		try {
			// calculate the transformation and rotation / location matrices
			// transformations are applied in reverse order
			Position3D parent = getParentPosition();
			if (parent != null)
				Math3D.translate(parent.getRotationLocationMatrix(),
					getLocation3D(), m_rotationLocationMatrix);
			else
				Math3D.translate(IMatrix4f.IDENTITY, getLocation3D(),
					m_rotationLocationMatrix);

			if (!IVector3f.NULLVEC3f.equals(getRotation3D())) {
				Math3D.scale(0.5f, getSize3D(), center);
				Math3D.translate(m_rotationLocationMatrix, center,
					m_rotationLocationMatrix);

				Math3D.rotate(getRotation3D(), m_rotationLocationMatrix,
					m_rotationLocationMatrix);

				Math3D.scale(-0.5f, getSize3D(), center);
				Math3D.translate(m_rotationLocationMatrix, center,
					m_rotationLocationMatrix);
			}

			m_transformationMatrix.set(m_rotationLocationMatrix);

			Math3D.scale(getSize3D(), m_transformationMatrix,
				m_transformationMatrix);

			if (getSize3D().getX() == 0 || getSize3D().getY() == 0
				|| getSize3D().getZ() == 0) {
				m_invertible = false;
			} else {
				m_invertible = true;

				// now calculate the inverse ray transformation matrices
				Math3D.invert(m_transformationMatrix, m_rayOriginMatrix);

				m_rayDirectionMatrix.setIdentity();
				if (!IVector3f.NULLVEC3f.equals(getRotation3D())) {
					invRotation.set(getRotation3D());
					invRotation.scale(-1);
					Math3D.rotate(invRotation, m_rayDirectionMatrix,
						m_rayDirectionMatrix);
				}

				invScale.setX(1 / getSize3D().getX());
				invScale.setY(1 / getSize3D().getY());
				invScale.setZ(1 / getSize3D().getZ());
				Math3D.scale(invScale, m_rayDirectionMatrix,
					m_rayDirectionMatrix);
			}

			m_valid = true;
		} finally {
			Math3DCache.returnVector3f(center, invRotation, invScale);
		}
	}
}
