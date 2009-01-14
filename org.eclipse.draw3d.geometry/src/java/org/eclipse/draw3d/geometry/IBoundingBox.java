/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API
 *    Matthias Thiele -- initial implementation
 ******************************************************************************/

package org.eclipse.draw3d.geometry;

/**
 * A bounding box is a box which is defined by two vectors. Hence one
 * implementation may define the center position of the box while the other one
 * specifies the box' size from the center position, the getters defined here do
 * not assume to simply return attributes but may calculate derived values.
 * This interface defines an immutable
 * bounding box.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since 12.10.2008
 */
public interface IBoundingBox {
	/**
	 * Returns a vector pointing to the center of this bounding box. The
	 * returned vector is relative to the coordinate system of this bounding box
	 * and not to its lower left corner.
	 * 
	 * @param o_resultVector3f The result vector, if <code>null</code>, a new
	 *            vector will be returned
	 * @return the absolute center of this bounding box
	 */
	public Vector3f getCenter(Vector3f o_resultVector3f);

	/**
	 * Returns the position of this bounding box.
	 * 
	 * @param o_resultVector3f The result vector, if <code>null</code>, a new
	 *            vector will be returned
	 * @return the position of this bounding box
	 */
	public Vector3f getPosition(Vector3f o_resultVector3f);

	/**
	 * Returns the size of this bounding box.
	 * 
	 * @param o_resultVector3f The result vector, if <code>null</code>, a new
	 *            vector will be returned
	 * @return the size of this bounding box
	 */
	public Vector3f getSize(Vector3f o_resultVector3f);
}
