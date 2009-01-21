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

import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * Mutable position, i.e. triple of location, size (scale), and rotation.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 */
public interface Position3D extends IPosition3D {
	
	/**
	 * Sets this position by copying location, scale, and rotation from
	 * given source.
	 * @param source
	 */
	public void setPosition(IPosition3D source);
	
	/**
	 * Sets the location of this IFigure.
	 * 
	 * @param point The new location, this is usually the lower left corner, must not be null
	 */
	public void setLocation3D(IVector3f point);

	/**
	 * Sets rotation of figure, i.e. angles for X, Y and Z axis. Rotations are
	 * applied in the following order: Y first, then Z and finally X.
	 * 
	 * @param rotation, muste not be null
	 */
	public void setRotation3D(IVector3f rotation);

	/**
	 * @param size, must not be null, none component of the vector must be less 0
	 */
	public void setSize3D(IVector3f size);
	
	/**
	 * Sets the matrix state to {@link MatrixState#INVALID}. That is, a next
	 * time a matrix is queried, all matrices are recalculated. The matrix
	 * state is automatically invalidated when size, location, or rotation
	 * have been changed.
	 */
	public void invalidateMatrices();

}
