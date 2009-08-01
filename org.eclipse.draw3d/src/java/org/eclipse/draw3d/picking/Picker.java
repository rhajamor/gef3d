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
package org.eclipse.draw3d.picking;

import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.ISurface;

/**
 * A picker allows picking of 3D figures using mouse coordinates.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 31.07.2009
 */
public interface Picker {

	/**
	 * Returns the surface of the figure that was last hit by the picking ray.
	 * If no figure has been hit yet, the surface of the root figure is
	 * returned.
	 * 
	 * @return the current surface
	 */
	public ISurface getCurrentSurface();

	/**
	 * Returns a hit for the figure at the given mouse coordinates.
	 * 
	 * @param i_mx the mouse X coordinate
	 * @param i_my the mouse Y coordinate
	 * @return the hit or <code>null</code> if no figure was hit at the given
	 *         coordinates
	 * @see Hit
	 */
	public Hit getHit(int i_mx, int i_my);

	/**
	 * Returns a hit for the figure at the given mouse coordinates. Only figures
	 * which are accepted and not pruned by the given search are considered.
	 * 
	 * @param i_mx the mouse X coordinate
	 * @param i_my the mouse Y coordinate
	 * @param i_search the search
	 * @return the hit or <code>null</code> if no figure was hit at the given
	 *         coordinates
	 * @see Hit
	 */
	public Hit getHit(int i_mx, int i_my, TreeSearch i_search);
}
