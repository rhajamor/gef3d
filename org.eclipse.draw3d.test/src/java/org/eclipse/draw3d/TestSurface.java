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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * TestSurface There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 22.07.2009
 */
public class TestSurface extends AbstractSurface {

    private Vector3f m_origin = new Vector3fImpl();

    private Vector3f m_xAxis = new Vector3fImpl();

    private Vector3f m_yAxis = new Vector3fImpl();

    private Vector3f m_zAxis = new Vector3fImpl();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#findFigureAt(int, int,
     *      org.eclipse.draw2d.TreeSearch)
     */
    public IFigure findFigureAt(int i_sx, int i_sy, TreeSearch i_search) {

        // this will not be tested
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getOwner()
     */
    public IFigure2DHost3D getOwner() {

        // this will not be tested
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.AbstractSurface#getOrigin(org.eclipse.draw3d.geometry.Vector3f)
     */
    @Override
    protected Vector3f getOrigin(Vector3f io_result) {

        Vector3f result = io_result;
        if (result == null)
            result = new Vector3fImpl();

        result.set(m_origin);
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.AbstractSurface#getXAxis(org.eclipse.draw3d.geometry.Vector3f)
     */
    @Override
    protected Vector3f getXAxis(Vector3f io_result) {

        Vector3f result = io_result;
        if (result == null)
            result = new Vector3fImpl();

        result.set(m_xAxis);
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.AbstractSurface#getYAxis(org.eclipse.draw3d.geometry.Vector3f)
     */
    @Override
    protected Vector3f getYAxis(Vector3f io_result) {

        Vector3f result = io_result;
        if (result == null)
            result = new Vector3fImpl();

        result.set(m_yAxis);
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.AbstractSurface#getZAxis(org.eclipse.draw3d.geometry.Vector3f)
     */
    @Override
    protected Vector3f getZAxis(Vector3f io_result) {

        Vector3f result = io_result;
        if (result == null)
            result = new Vector3fImpl();

        result.set(m_zAxis);
        return result;
    }

    /**
     * Sets the origin and the X, Y and Z axes of this surface.
     * 
     * @param i_origin
     *            the origin of this surface
     * @param i_xAxis
     *            the X axis of this surface
     * @param i_yAxis
     *            the Y axis of this surface
     * @param i_zAxis
     *            the Z axis of this surface
     */
    public void set(IVector3f i_origin, IVector3f i_xAxis,
            IVector3f i_yAxis, IVector3f i_zAxis) {

        m_origin.set(i_origin);
        m_xAxis.set(i_xAxis);
        m_yAxis.set(i_yAxis);
        m_zAxis.set(i_zAxis);
    }
}
