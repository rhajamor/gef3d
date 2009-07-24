/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - refactoring and optimizations
 ******************************************************************************/
package org.eclipse.gef3d.tools;

import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.ColorPicker;

/**
 * Contains the state of a tracking operation and some convience method.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 17.05.2008
 */
public class TrackState {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(TrackState.class.getName());

    private ISurface m_currentSurface;

    private Point m_currentSurfaceLocation = new Point();

    private Vector3f m_currentWorldLocation = new Vector3fImpl();

    private ISurface m_initialSurface;

    private Point m_initialSurfaceLocation = new Point();

    private Vector3f m_initialWorldLocation = new Vector3fImpl();

    private Point m_lastSurfaceLocation = new Point();

    private ColorPicker m_picker;

    private Dimension m_surfaceMoveDelta = new Dimension();

    private Vector3f m_worldMoveDelta = new Vector3fImpl();

    /**
     * Creates a new instance and initializes it with the given arguments.
     * 
     * @param i_picker
     *            the picker to use for calculating 3D coordinates
     * @param i_surfaceLocation
     *            the current surface location
     * @throws NullPointerException
     *             if any of the given arguments is null
     */
    public TrackState(ColorPicker i_picker, Point i_surfaceLocation) {

        if (i_picker == null)
            throw new NullPointerException("i_picker must not be null");

        if (i_surfaceLocation == null)
            throw new NullPointerException("i_mouseLocation must not be null");

        m_picker = i_picker;

        m_initialSurface = m_picker.getCurrentSurface();
        m_initialSurfaceLocation.setLocation(i_surfaceLocation);

        m_initialSurface.getWorldLocation(m_initialSurfaceLocation,
            m_initialWorldLocation);

        update(i_surfaceLocation);
    }

    /**
     * Returns the current surface under the mouse cursor.
     * 
     * @return the current surface
     */
    public ISurface getCurrentSurface() {

        return m_currentSurface;
    }

    /**
     * Returns the current surface location in 2D coordinates.
     * 
     * @return the current surface location
     */
    public Point getCurrentSurfaceLocation() {

        return m_currentSurfaceLocation;
    }

    /**
     * Returns the current world drag location.
     * 
     * @return the current 3D drag location or <code>null</code> if no location
     *         has been set
     */
    public IVector3f getCurrentWorldLocation() {

        return m_currentWorldLocation;
    }

    /**
     * Returns the initial surface, e.g. the surface on which the drag
     * originated.
     * 
     * @return the initial surface
     */
    public ISurface getInitialSurface() {

        return m_initialSurface;
    }

    /**
     * Returns the initial surface location, e.g. the surface location where
     * this drag started.
     * 
     * @return the initial surface location
     */
    public Point getInitialSurfaceLocation() {

        return m_initialSurfaceLocation;
    }

    /**
     * Returns the initial world location, e.g. the world location where this
     * drag started.
     * 
     * @return the initial world location
     */
    public IVector3f getInitialWorldLocation() {

        return m_initialWorldLocation;
    }

    /**
     * Returns the 2D move delta.
     * 
     * @return the 2D move delta.
     */
    public Dimension getSurfaceMoveDelta() {

        return m_surfaceMoveDelta;
    }

    /**
     * Returns the 3D delta vector of the initial and the current drag location.
     * 
     * @return the delta vector
     */
    public Vector3f getWorldMoveDelta() {

        return m_worldMoveDelta;
    }

    /**
     * Sets the current location of the drag and the current surface.
     * 
     * @param i_surfaceLocation
     *            the current location in surface coordinates
     * 
     * @throws NullPointerException
     *             if the given location is <code>null</code>
     */
    public void update(Point i_surfaceLocation) {

        if (i_surfaceLocation == null)
            throw new NullPointerException("i_surfaceLocation must not be null");

        if (i_surfaceLocation.equals(m_lastSurfaceLocation))
            return;

        m_currentSurface = m_picker.getCurrentSurface();
        m_currentSurfaceLocation.setLocation(i_surfaceLocation);
        m_currentSurface.getWorldLocation(m_currentSurfaceLocation,
            m_currentWorldLocation);

        m_lastSurfaceLocation.setLocation(i_surfaceLocation);

        m_surfaceMoveDelta.width = m_currentSurfaceLocation.x
                - m_initialSurfaceLocation.x;
        m_surfaceMoveDelta.height = m_currentSurfaceLocation.y
                - m_initialSurfaceLocation.y;

        Math3D.sub(m_currentWorldLocation, m_initialWorldLocation,
            m_worldMoveDelta);
    }
}
