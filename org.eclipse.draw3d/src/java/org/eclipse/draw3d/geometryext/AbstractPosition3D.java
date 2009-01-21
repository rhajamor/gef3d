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

import quicktime.streaming.SettingsDialog;

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
	 * The rotation angles ofthis figure.
	 */
	protected Vector3f rotation;

	protected MatrixState matrixState;

	/**
	 * Boolean semaphore used by {@link #syncSize()} and {@link #syncSize3D()}
	 * to avoid infinite loop.
	 */
	protected boolean updatingBounds;

	/**
	 * The object matrix is the matrix that transforms a unit cube into the
	 * cuboid that represents this figure's shape in world space. It is used as
	 * the OpenGL modelview matrix when the figure is drawn. <br /> <br /> The
	 * object matrix is derived from
	 * <ol>
	 * <li>the figure's dimension</li>
	 * <li>the figure's rotation</li>
	 * <li>the figure's location</li>
	 * <li>the figure's parent's location</li>
	 * </ol>
	 */
	protected transient Matrix4fImpl modelMatrix;

	protected transient Matrix4fImpl locationMatrix;

	/**
	 * 
	 */
	public AbstractPosition3D() {
		rotation = new Vector3fImpl(0, 0, 0);
		modelMatrix = new Matrix4fImpl(); // IDENTITY
		locationMatrix = new Matrix4fImpl(); // IDENTITY
		matrixState = MatrixState.INVALID;
		updatingBounds = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.Position3D#setPosition(org.eclipse.draw3d.geometryext.IPosition3D)
	 */
	public void setPosition(IPosition3D i_source) {
		setRotation3D(i_source.getRotation3D());
		setSize3D(i_source.getSize3D());
		setLocation3D(i_source.getLocation3D());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getRotation3D()
	 */
	public IVector3f getRotation3D() {
		return rotation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.Position3D#setRotation3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setRotation3D(IVector3f i_rotation) {
		if (i_rotation == null) // parameter precondition
			throw new NullPointerException("i_rotation must not be null");

		if (rotation.equals(i_rotation))
			return;

		Vector3fImpl delta = new Vector3fImpl();
		Math3D.sub(i_rotation, rotation, delta);

		rotation.set(i_rotation);
		invalidateMatrices();

		firePositionChanged(PositionHint.rotation, delta);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getMatrixState()
	 */
	public MatrixState getMatrixState() {
		if (getHost() != null) {
			IHost3D parent = getHost().getParentHost3D();
			if (parent != null
					&& parent.getPosition3D() != null
					&& parent.getPosition3D().getMatrixState() == MatrixState.INVALID) {
				invalidateMatrices();
			}
		}
		return matrixState;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.Position3D#invalidateMatrices()
	 */
	public void invalidateMatrices() {
		matrixState = MatrixState.INVALID;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getLocationMatrix()
	 */
	public IMatrix4f getLocationMatrix() {
		recalculateMatrices();
		return locationMatrix;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometryext.IPosition3D#getModelMatrix()
	 */
	public IMatrix4f getModelMatrix() {
		recalculateMatrices();
		return modelMatrix;
	}

	/**
	 * Precondition: matrixstate == invalid Postcondition: matrixstate ==
	 * updated
	 */
	private void recalculateMatrices() {
		if (getMatrixState() != MatrixState.INVALID)
			return;

		recalculateLocationMatrix();
		recalculateModelMatrix();

		matrixState = MatrixState.VALID;

	}

	/**
	 * Recalculates the location matrix.
	 */
	private void recalculateLocationMatrix() {

		IMatrix4f parentLocationMatrix = Position3DUtil
				.getParentLocationMatrix(this);

		locationMatrix.set(parentLocationMatrix);

		IVector3f location = getLocation3D();
		Math3D.translate(location, locationMatrix, locationMatrix);

		rotate(locationMatrix, rotation);
	}

	/**
	 * Rotates serially the given matrix by angles defined in rotation vector.
	 * First, the matrix is rotated around the x axis by the x value of the
	 * vector, then around the y axis by the y value and so on.
	 * 
	 * @param io_matrix
	 * @param i_rotate
	 */
	private static void rotate(Matrix4f io_matrix, IVector3f i_rotate) {

		float yAngle = i_rotate.getY();
		if (yAngle != 0)
			Math3D.rotate(yAngle, IVector3f.Y_AXIS, io_matrix, io_matrix);

		float zAngle = i_rotate.getZ();
		if (zAngle != 0)
			Math3D.rotate(zAngle, IVector3f.Z_AXIS, io_matrix, io_matrix);

		float xAngle = i_rotate.getX();
		if (xAngle != 0)
			Math3D.rotate(xAngle, IVector3f.X_AXIS, io_matrix, io_matrix);

	}

	/**
	 * Recalculates the model matrix. Make sure that
	 * {@link #recalculateLocationMatrix()} has been called before calling this
	 * method.
	 */
	private void recalculateModelMatrix() {
		modelMatrix.set(locationMatrix);
		IVector3f size = getSize3D();
		Math3D.scale(size, modelMatrix, modelMatrix);
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
}
