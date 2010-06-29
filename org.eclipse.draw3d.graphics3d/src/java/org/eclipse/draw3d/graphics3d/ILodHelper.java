/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d;

import org.eclipse.draw3d.geometry.IVector3f;

/**
 * Instances of this class calculate an LOD factor (a float between 0 and 1,
 * inclusive) for a given object. This factor can be used as a measure to
 * determine the detail level for that object.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.01.2010
 */
public interface ILodHelper {
	/**
	 * Returns the LOD factor of a 2D object at the given position with the
	 * given normal vector.
	 * 
	 * @param i_position the object's position
	 * @param i_normal the object's normal vector
	 * @return a value between 0 and 1, inclusive
	 * @throws NullPointerException if any of the given arguments is
	 *             <code>null</code>
	 */
	public float getLODFactor(IVector3f i_position, IVector3f i_normal);

	/**
	 * Returns the LOD factor of an object at the given position.
	 * 
	 * @param i_position the object's position
	 * @return a value between 0 and 1, inclusive
	 * @throws NullPointerException if any of the given arguments is
	 *             <code>null</code>
	 */
	public float getLODFactor(IVector3f i_position);
}
