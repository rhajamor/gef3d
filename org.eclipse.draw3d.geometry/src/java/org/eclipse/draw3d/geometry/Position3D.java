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
 * Mutable position, i.e. triple of location, size (scale), and rotation.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 */
public interface Position3D extends IPosition3D {

	/**
	 * Invalidates some internal cached information and must be called whenever
	 * this position becomes invalid. Called automatically when location, size
	 * or rotation changes.
	 */
	public void invalidate();

	/**
	 * Modifies the location of this position so that its center is at the given
	 * coordinates.
	 * 
	 * @param i_center the center of this position
	 */
	public void setCenter3D(IVector3f i_center);

	/**
	 * Sets the location of this position.
	 * 
	 * @param i_location the new location, must not be <code>null</code>
	 */
	public void setLocation3D(IVector3f i_location);

	/**
	 * Sets this position's location, size and rotation so that the given
	 * position's absolute location, size and rotation is the same as this
	 * position's absolute location, size and rotation. In other words, the two
	 * positions will have the same spacial configuration regardless of whether
	 * they are absolute or relative, respectively.
	 * 
	 * @param i_position3D the position to set
	 */
	public void setPosition(IPosition3D i_position3D);

	/**
	 * Sets rotation of figure, i.e. angles for X, Y and Z axis. Rotations are
	 * applied in the following order: Y first, then Z and finally X. 
	 * Rotation is clockwise.
	 * 
	 * @param i_rotation must not be <code>null</code>
	 */
	public void setRotation3D(IVector3f i_rotation);

	/**
	 * Sets the size of this position. Even though the size is applied to the
	 * transformation matrix as a scaling, it is not applied to the any position
	 * that is relative to this one, as opposed to location and rotation.
	 * 
	 * @param i_size must not be <code>null</code>
	 */
	public void setSize3D(IVector3f i_size);

}
