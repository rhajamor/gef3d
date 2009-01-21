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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;

/**
 * Stores location, size, and rotation of an 3D object. 
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 */
/**
 * Immutable triple of position properties for 3D objects, that is location,
 * size (scale), and rotation. These three properties can be combined in a so
 * called transformation matrix, which can then be passed to OpenGL or renderes
 * able to handle these kind of information.
 * <p>
 * This interface and its subinterfaces and implementations were created in 
 * order to resolve the problem stated in bug 261775.
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=261775
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
		location, size, rotation;

	};

	/**
	 * Defines the state of the internal matrices.
	 * 
	 * @author Jens von Pilgrim
	 * @version $Revision$
	 * @since 23.11.2007
	 */
	public enum MatrixState {
		/**
		 * Matrices are invalid and need to be recalculated.
		 */
		INVALID,
		/**
		 * Matrices are valid.
		 */
		VALID;
	}

	/**
	 * Returns the host (or context) of this position
	 * 
	 * @return
	 */
	public IHost3D getHost();

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
	 * @see org.eclipse.draw3d.IFigure3D#getBounds3D()
	 */
	public IBoundingBox getBounds3D();

	/**
	 * Returns the lower left corner of the bounding box.
	 * 
	 * @return
	 */
	public IVector3f getLocation3D();

	/**
	 * Returns the rotation vector of this figure.
	 * 
	 * @return
	 */
	public IVector3f getRotation3D();

	/**
	 * Gets the size.
	 * 
	 * @return
	 */
	public IVector3f getSize3D();

	/**
	 * Returns the matrix that performs the absolute transformation to this
	 * figure's location. This is the matrix that transforms (0, 0, 0) to this
	 * figure's origin.
	 * 
	 * @return
	 */
	public IMatrix4f getLocationMatrix();

	/**
	 * Returns matrix state of the model and location matrix. Note that these
	 * matrices cannot be set directly, they are calculated from location, size,
	 * and rotation.
	 * <p>
	 * Note: Some renderes, such as OpoenGL, use matrices to position an object
	 * in the 3D scene. Others, like X3D, use the location, size, and rotation.
	 * To enable objects to be renderable by both types, it is necessary to
	 * always provide all information, the matrix is only used for chaching
	 * reasons.
	 * </p>
	 * 
	 * @return
	 */
	public MatrixState getMatrixState();

	/**
	 * Returns the matrix that performs the transformation of this figure's
	 * basic shape to it's intended shape.
	 * 
	 * @return
	 */
	public IMatrix4f getModelMatrix();

}
