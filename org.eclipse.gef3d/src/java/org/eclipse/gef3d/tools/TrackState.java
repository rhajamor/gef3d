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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Cache;
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

    private Point m_lastMouseLocation = new Point();

    private ColorPicker m_picker;

    private IScene m_scene;

    private Dimension m_surfaceMoveDelta = new Dimension();

    private Vector3f m_worldMoveDelta = new Vector3fImpl();

    /**
     * Creates a new instance and initializes it with the given arguments.
     * 
     * @param i_scene
     *            the scene
     * @param i_picker
     *            the picker to use for calculating 3D coordinates
     * @param i_mouseLocation
     *            the current mouse location
     * @throws NullPointerException
     *             if any of the given arguments is null
     */
    public TrackState(IScene i_scene, ColorPicker i_picker,
            Point i_mouseLocation) {

        if (i_scene == null)
            throw new NullPointerException("i_scene must not be null");

        if (i_picker == null)
            throw new NullPointerException("i_picker must not be null");

        if (i_mouseLocation == null)
            throw new NullPointerException("i_mouseLocation must not be null");

        m_scene = i_scene;
        m_picker = i_picker;

        m_initialSurface = m_picker.getCurrentSurface();
        getLocations(m_initialSurface, i_mouseLocation,
            m_initialSurfaceLocation, m_initialWorldLocation);

        m_currentSurface = m_initialSurface;
        m_currentSurfaceLocation.setLocation(m_initialSurfaceLocation);
        m_currentWorldLocation.set(m_initialWorldLocation);

        m_surfaceMoveDelta.width = 0;
        m_surfaceMoveDelta.height = 0;
        m_worldMoveDelta.set(0, 0, 0);

        m_lastMouseLocation.setLocation(i_mouseLocation);
    }

    private void getLocations(ISurface i_surface, Point i_mouseLocation,
            Point i_surfaceLocation, Vector3f i_worldLocation) {

        Vector3f eye = Cache.getVector3f();
        Vector3f point = Cache.getVector3f();
        try {
            ICamera camera = m_scene.getCamera();
            camera.getPosition(eye);

            camera.unProject(i_mouseLocation.x, i_mouseLocation.y, 0, null,
                point);

            i_surface.getSurfaceLocation2D(eye, point, i_surfaceLocation);
            i_surface.getWorldLocation(i_surfaceLocation, i_worldLocation);
        } finally {
            Cache.returnVector3f(eye);
            Cache.returnVector3f(point);
        }
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
     * Sets the current mouse location of the drag and the current surface.
     * 
     * @param i_mouseLocation
     *            the current location in mouse coordinates
     * 
     * @throws NullPointerException
     *             if the given location is <code>null</code>
     */
    public void update(Point i_mouseLocation) {

        if (i_mouseLocation == null)
            throw new NullPointerException("i_surfaceLocation must not be null");

        if (i_mouseLocation.equals(m_lastMouseLocation))
            return;

        m_currentSurface = m_picker.getCurrentSurface();

        if (!m_currentSurface.equals(m_initialSurface)
                && log.isLoggable(Level.FINE))
            log.fine("surface has changed from " + m_initialSurface + " to "
                    + m_currentSurface);

        getLocations(m_currentSurface, i_mouseLocation,
            m_currentSurfaceLocation, m_currentWorldLocation);

        m_surfaceMoveDelta.width = m_currentSurfaceLocation.x
                - m_initialSurfaceLocation.x;
        m_surfaceMoveDelta.height = m_currentSurfaceLocation.y
                - m_initialSurfaceLocation.y;

        Math3D.sub(m_currentWorldLocation, m_initialWorldLocation,
            m_worldMoveDelta);

        log.info(m_worldMoveDelta.toString());

        m_lastMouseLocation.setLocation(i_mouseLocation);
    }
}
