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
package org.eclipse.draw3d;

import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Matrix4fImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * An abstract base implementation of a surface.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 13.07.2009
 */
public abstract class AbstractSurface implements ISurface {

    @SuppressWarnings("unused")
    private final Logger log = Logger.getLogger(getClass().getName());

    /**
     * The matrix that transforms a vector given in surface coordinates to world
     * coordinates.
     */
    private Matrix4f m_surfaceToWorld = new Matrix4fImpl();

    private boolean m_surfaceToWorldValid = false;

    /**
     * The matrix that transforms a vector given in world coordinates to surface
     * coordinates.
     */
    private Matrix4f m_worldToSurface = new Matrix4fImpl();

    private boolean m_worldToSurfaceValid = false;

    /**
     * This method must be called whenever the surface coordinate system has
     * changed.
     */
    protected void coordinateSystemChanged() {

        m_surfaceToWorldValid = false;
        m_worldToSurfaceValid = false;
    }

    /**
     * Returns the origin of this surface.
     * 
     * @param io_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            returned
     * 
     * @return a vector pointing to the origin of this surface
     */
    protected abstract Vector3f getOrigin(Vector3f io_result);

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getSurfaceLocation2D(float, float,
     *      float, org.eclipse.draw2d.geometry.Point)
     */
    public Point getSurfaceLocation2D(float i_wx, float i_wy, float i_wz,
            Point io_result) {

        Vector3f w = Draw3DCache.getVector3f();
        try {
            w.set(i_wx, i_wy, i_wz);
            return getSurfaceLocation2D(w, io_result);
        } finally {
            Draw3DCache.returnVector3f(w);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getSurfaceLocation2D(org.eclipse.draw3d.geometry.IVector3f,
     *      org.eclipse.draw3d.geometry.IVector3f,
     *      org.eclipse.draw2d.geometry.Point)
     */
    public Point getSurfaceLocation2D(IVector3f i_rayStart,
            IVector3f i_rayPoint, Point io_result) {

        Point result = io_result;
        if (result == null)
            result = new Point();

        Vector3f sLocation = Draw3DCache.getVector3f();
        try {
            getSurfaceLocation3D(i_rayStart, i_rayPoint, sLocation);

            result.x = (int) sLocation.getX();
            result.y = (int) sLocation.getY();

            return result;
        } finally {
            Draw3DCache.returnVector3f(sLocation);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getSurfaceLocation2D(org.eclipse.draw3d.geometry.IVector3f,
     *      org.eclipse.draw2d.geometry.Point)
     */
    public Point getSurfaceLocation2D(IVector3f i_world, Point io_result) {

        Point result = io_result;
        if (result == null)
            result = new Point();

        Vector3f vector = Draw3DCache.getVector3f();
        try {
            updateWorldToSurface();
            vector.set(i_world);
            vector.transform(m_worldToSurface);

            result.x = (int) vector.getX();
            result.y = (int) vector.getY();

            return result;
        } finally {
            Draw3DCache.returnVector3f(vector);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getSurfaceLocation3D(float, float,
     *      float, org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getSurfaceLocation3D(float i_wx, float i_wy, float i_wz,
            Vector3f io_result) {

        Vector3f w = Draw3DCache.getVector3f();
        try {
            w.set(i_wx, i_wy, i_wz);
            return getSurfaceLocation3D(w, io_result);
        } finally {
            Draw3DCache.returnVector3f(w);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getSurfaceLocation3D(org.eclipse.draw3d.ISurface,
     *      org.eclipse.draw3d.geometry.Vector3f,
     *      org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getSurfaceLocation3D(ISurface i_reference,
            Vector3f i_surface, Vector3f io_result) {

        Vector3f w = Draw3DCache.getVector3f();
        try {
            i_reference.getWorldLocation(i_surface, w);
            return getSurfaceLocation3D(w, io_result);
        } finally {
            Draw3DCache.returnVector3f(w);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getSurfaceLocation3D(org.eclipse.draw3d.geometry.IVector3f,
     *      org.eclipse.draw3d.geometry.IVector3f,
     *      org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getSurfaceLocation3D(IVector3f i_rayStart,
            IVector3f i_rayPoint, Vector3f io_result) {

        if (i_rayPoint.equals(i_rayStart))
            return null;

        Vector3f rayDirection = Draw3DCache.getVector3f();
        Vector3f p = Draw3DCache.getVector3f();
        Vector3f n = Draw3DCache.getVector3f();
        Vector3f w = Draw3DCache.getVector3f();

        try {
            Math3D.sub(i_rayPoint, i_rayStart, rayDirection);
            Math3D.normalise(rayDirection, rayDirection);

            getOrigin(p);
            getZAxis(n);

            Math3D.rayIntersectsPlane(i_rayStart, rayDirection, p, n, w);
            if (w == null)
                return null;

            return getSurfaceLocation3D(w, io_result);
        } finally {
            Draw3DCache.returnVector3f(rayDirection);
            Draw3DCache.returnVector3f(p);
            Draw3DCache.returnVector3f(n);
            Draw3DCache.returnVector3f(w);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getSurfaceLocation3D(org.eclipse.draw3d.geometry.IVector3f,
     *      org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getSurfaceLocation3D(IVector3f i_world, Vector3f io_result) {

        Vector3f result = io_result;
        if (result == null)
            result = new Vector3fImpl();

        updateWorldToSurface();
        result.set(i_world);
        result.transform(m_worldToSurface);

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getWorldDimension(org.eclipse.draw2d.geometry.Dimension,
     *      org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getWorldDimension(Dimension i_surface, Vector3f io_result) {

        Point p = Draw3DCache.getPoint();
        try {
            p.x = i_surface.width;
            p.y = i_surface.height;
            return getWorldLocation(p, io_result);
        } finally {
            Draw3DCache.returnPoint(p);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getWorldLocation(float, float, float,
     *      org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getWorldLocation(float i_sx, float i_sy, float i_sz,
            Vector3f io_result) {

        Vector3f s = Draw3DCache.getVector3f();
        try {
            s.set(i_sx, i_sy, i_sz);
            return getWorldLocation(s, io_result);
        } finally {
            Draw3DCache.returnVector3f(s);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getWorldLocation(org.eclipse.draw3d.geometry.IVector3f,
     *      org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getWorldLocation(IVector3f i_surface, Vector3f io_result) {

        Vector3f result = io_result;
        if (result == null)
            result = new Vector3fImpl();

        result.set(i_surface);
        updateSurfaceToWorld();
        result.transform(m_surfaceToWorld);

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getWorldLocation(org.eclipse.draw2d.geometry.Point,
     *      org.eclipse.draw3d.geometry.Vector3f)
     */
    public Vector3f getWorldLocation(Point i_surface, Vector3f io_result) {

        return getWorldLocation(i_surface.x, i_surface.y, 0, io_result);
    }

    /**
     * Returns the X axis vector of this surface. The returned vector is
     * normalized.
     * 
     * @param io_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            returned
     * 
     * @return the X axis vector
     */
    protected abstract Vector3f getXAxis(Vector3f io_result);

    /**
     * Returns the Y axis vector of this surface. The returned vector is
     * normalized.
     * 
     * @param io_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            returned
     * 
     * @return the Y axis vector
     */
    protected abstract Vector3f getYAxis(Vector3f io_result);

    /**
     * Returns the Z axis vector of this surface. The returned vector is
     * normalized.
     * 
     * @param io_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            returned
     * 
     * @return the Z axis vector
     */
    protected abstract Vector3f getZAxis(Vector3f io_result);

    private void updateSurfaceToWorld() {

        if (m_surfaceToWorldValid)
            return;

        Vector3f xAxis = Draw3DCache.getVector3f();
        Vector3f yAxis = Draw3DCache.getVector3f();
        Vector3f zAxis = Draw3DCache.getVector3f();
        Vector3f origin = Draw3DCache.getVector3f();

        try {
            getXAxis(xAxis);
            getYAxis(yAxis);
            getZAxis(zAxis);
            getOrigin(origin);

            m_surfaceToWorld.set(0, 0, xAxis.getX());
            m_surfaceToWorld.set(0, 1, xAxis.getY());
            m_surfaceToWorld.set(0, 2, xAxis.getZ());
            m_surfaceToWorld.set(0, 3, 0);

            m_surfaceToWorld.set(1, 0, yAxis.getX());
            m_surfaceToWorld.set(1, 1, yAxis.getY());
            m_surfaceToWorld.set(1, 2, yAxis.getZ());
            m_surfaceToWorld.set(1, 3, 0);

            m_surfaceToWorld.set(2, 0, zAxis.getX());
            m_surfaceToWorld.set(2, 1, zAxis.getY());
            m_surfaceToWorld.set(2, 2, zAxis.getZ());
            m_surfaceToWorld.set(2, 3, 0);

            m_surfaceToWorld.set(3, 0, origin.getX());
            m_surfaceToWorld.set(3, 1, origin.getY());
            m_surfaceToWorld.set(3, 2, origin.getZ());
            m_surfaceToWorld.set(3, 3, 1);

            m_surfaceToWorldValid = true;
        } finally {
            Draw3DCache.returnVector3f(xAxis);
            Draw3DCache.returnVector3f(yAxis);
            Draw3DCache.returnVector3f(zAxis);
            Draw3DCache.returnVector3f(origin);
        }
    }

    private void updateWorldToSurface() {

        if (m_worldToSurfaceValid)
            return;

        updateSurfaceToWorld();
        Math3D.invert(m_surfaceToWorld, m_worldToSurface);

        m_worldToSurfaceValid = true;
    }

}