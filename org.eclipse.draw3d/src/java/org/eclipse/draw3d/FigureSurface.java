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

import java.util.List;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.geometry.Math3DCache;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * A surface that belongs to a 3D figure.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 10.07.2009
 */
public class FigureSurface extends AbstractSurface {

    private FigureListener m_figureListener = new FigureListener() {

        public void figureMoved(IFigure i_source) {

            coordinateSystemChanged();
        }
    };

    private IFigure3D m_host;

    /**
     * Creates a new surface for the given figure.
     * 
     * @param i_host
     *            the host figure of this surface
     */
    public FigureSurface(IFigure3D i_host) {

        if (i_host == null)
            throw new NullPointerException("i_host must not be null");

        m_host = i_host;
        m_host.addFigureListener(m_figureListener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#findFigureAt(int, int,
     *      org.eclipse.draw2d.TreeSearch)
     */
    @SuppressWarnings("unchecked")
    public IFigure findFigureAt(int i_sx, int i_sy, TreeSearch i_search) {

        // host pruned?
        if (i_search != null && i_search.prune(m_host))
            return null;

        IFigure hit = null;
        List<IFigure> children = m_host.getChildren();
        for (IFigure child : children) {
            if (!(child instanceof IFigure3D)) {
                if (i_search != null)
                    hit = child.findFigureAt(i_sx, i_sy, i_search);
                else
                    hit = child.findFigureAt(i_sx, i_sy);

                if (hit != null)
                    return hit;
            }
        }

        // now we have only found a 3D figure, and we must check whether it
        // is accepted by the search
        if (i_search == null || i_search.accept(m_host))
            return m_host;

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.ISurface#getHost()
     */
    public IFigure3D getHost() {

        return m_host;
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

        result.set(m_host.getPosition3D().getLocation3D());
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

        result.set(1, 0, 0);
        rotateVector(result);

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

        result.set(0, 1, 0);
        rotateVector(result);

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

        result.set(0, 0, 1);
        rotateVector(result);

        return result;
    }

    private void rotateVector(Vector3f i_vector) {

        Matrix4f rot = Math3DCache.getMatrix4f();
        try {
            rot.setIdentity();

            IVector3f angles = m_host.getPosition3D().getRotation3D();
            Math3D.rotate(angles, rot, rot);

            i_vector.transform(rot);
        } finally {
            Math3DCache.returnMatrix4f(rot);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        b.append("Figure surface for host [");
        b.append(m_host);
        b.append("]");

        return b.toString();
    }
}
