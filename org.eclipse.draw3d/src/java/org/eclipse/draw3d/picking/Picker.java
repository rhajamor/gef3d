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

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;

/**
 * Common interface for pickers.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.07.2009
 */
public interface Picker {

    /**
     * This is a constant that is used in GEF3D as the key of a subset picker
     * that contains only handles and feedback figures. Since draw3d doesn't
     * know about such figure types, it is meaningles outside of GEF3D, but it
     * is introduced here for convenience.
     */
    public static final String HANDLE_PICKER = "handlePicker";

    /**
     * Returns the current surface.
     * 
     * @return the current surface
     */
    public ISurface getCurrentSurface();

    /**
     * Returns the depth value for the foremost figure at the given mouse
     * coordinates.
     * 
     * @param i_mx
     *            the mouse X coordinate
     * @param i_my
     *            the mouse Y coordinate
     * @return the depth value
     * @throws IllegalStateException
     *             if this figure picker is invalid
     */
    public float getDepth(int i_mx, int i_my);

    /**
     * Returns the foremost 3D figure at the given mouse coordinates.
     * 
     * @param i_mx
     *            the mouse X coordinate
     * @param i_my
     *            the mouse Y coordinate
     * @return the 3D figure at the given coordinates or <code>null</code> if
     *         the pixel at the given coordinates is void
     * @throws IllegalStateException
     *             if this color picker is disposed
     */
    public IFigure3D getFigure3D(int i_mx, int i_my);

    /**
     * Updates the current surface to be the surface of the host figure at the
     * given mouse coordinates, if there is any.
     * 
     * @param i_mx
     *            the mouse X coordinate
     * @param i_my
     *            the mouse Y coordinate
     * 
     * @throws IllegalStateException
     *             if this color picker is disposed
     */
    public void updateCurrentSurface(int i_mx, int i_my);

}