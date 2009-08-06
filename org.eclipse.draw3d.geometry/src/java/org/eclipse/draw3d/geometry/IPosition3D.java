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

/**
 * Stores location, size, and rotation of an 3D object. Immutable triple of
 * position properties for 3D objects, that is location, size (scale), and
 * rotation. These three properties can be combined in a so called
 * transformation matrix, which can then be passed to OpenGL or renderes able to
 * handle these kind of information.
 * <p>
 * This interface and its subinterfaces and implementations were created in
 * order to resolve the problem stated in bug 261775.
 * </p>
 * <p>
 * Every Position is expected to have a host, i.e. @link{#getHost()} must not
 * return null. If you do not have a host (e.g. you only need a temporary
 * position of the position is absolute), you can use
 * {@link Position3DImpl#Position3DImpl()} in order to create a position with a
 * dummy host.
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 * @href https://bugs.eclipse.org/bugs/show_bug.cgi?id=261775
 */
public interface IPosition3D {

	/**
	 * Hint passed in events, see Host3D#positionChanged
	 * 
	 * @author Jens von Pilgrim
	 * @version $Revision$
	 * @since Jan 21, 2009
	 */
	public enum PositionHint {
		/**
		 * Location has changed.
		 */
		LOCATION,
		/**
		 * Rotation has changed.
		 */
		ROTATION,
		/**
		 * Size has changed.
		 */
		SIZE;
	}

	/**
	 * Returns the smallest box completely enclosing the IFigure. This method is
	 * the 3D equivalent of {@link IFigure#getBounds()}. While GEF's version
	 * returns a mutable class (Rectangle) and forbids to change the returned
	 * object, here an immutable class (interface) is returned avoiding these
	 * problems.
	 * <p>
	 * Returns bounds, i.e. lower left back corner and size. The coordinates are
	 * relative coordinates, that is rotation is not recognized here.
	 * </p>
	 * <p>
	 * Note that the returned object is immutable, use setters for location,
	 * size, and rotation in {@link Position3D}.
	 * </p>
	 * 
	 * @return the bounding box
	 * @see org.eclipse.draw3d.IFigure3D#getBounds3D()
	 */
	public IBoundingBox getBounds3D();

	/**
	 * Returns the host (or context) of this position
	 * 
	 * @return returns the host, must not be null
	 */
	public IHost3D getHost();

	/**
	 * Returns the lower left corner of the bounding box.
	 * 
	 * @return the location
	 */
	public IVector3f getLocation3D();

	/**
	 * Returns the matrix that performs the transformation of this figure's
	 * rotation and location, but does not include the scaling transformation.
	 * 
	 * @return the rotation / location matrix
	 */
	public IMatrix4f getRotationLocationMatrix();

	/**
	 * Returns the matrix that performs the transformation of this figure's
	 * basic shape to it's intended shape.
	 * 
	 * @return the model matrix
	 */
	public IMatrix4f getTransformationMatrix();

	/**
	 * Returns the rotation angles of the figure.
	 * 
	 * @return the rotation angles
	 */
	public IVector3f getRotation3D();

	/**
	 * Returns the 3D dimensions.
	 * 
	 * @return the dimensions
	 */
	public IVector3f getSize3D();

	/**
	 * Indicates whether the internal cached information of this position and of
	 * all its ancestors is valid.
	 * 
	 * @return <code>true</code> if the internal cached information of this
	 *         position and of all its ancestors is valid and <code>false</code>
	 *         otherwise
	 */
	public boolean isValid();
}
