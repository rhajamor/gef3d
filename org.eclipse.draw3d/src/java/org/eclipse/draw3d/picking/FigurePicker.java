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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * FigurePicker There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.07.2009
 */
public class FigurePicker implements Picker {

    private GLCanvas m_canvas;

    private ISurface m_currentSurface;

    private boolean m_disposed = false;

    private FigureManager m_figureManager;

    private PickingBuffers m_pickingBuffers;

    private IFigure3D m_rootFigure;

    private TreeSearch m_search;

    private boolean m_valid = false;

    /**
     * Creates a new picker with the given root figure and canvas. The picker
     * will only use figures which are accepted and not pruned by the given tree
     * search if it is not <code>null</code>.
     * 
     * @param i_rootFigure
     *            the root figure
     * @param i_canvas
     *            the canvas
     * @param i_search
     *            the tree search
     */
    public FigurePicker(IFigure3D i_rootFigure, GLCanvas i_canvas,
            TreeSearch i_search) {

        if (i_rootFigure == null)
            throw new NullPointerException("i_rootFigure must not be null");

        if (i_canvas == null)
            throw new NullPointerException("i_canvas must not be null");

        m_rootFigure = i_rootFigure;
        m_canvas = i_canvas;
        m_search = i_search;

        m_figureManager = new FigureManager(m_search);
        m_pickingBuffers = new OffscreenBuffers();
    }

    /**
     * Disposes all resources associated with this picker.
     */
    public void dispose() {

        m_pickingBuffers.dispose();
        m_disposed = true;
    }

    /**
     * Returns the color value of the pixel at the given position. The returned
     * integer contains the RGB byte values in reverse order as it's least
     * significant bytes: 0x00BBGGRR.
     * 
     * @param i_x
     *            the x coordinate of the pixel
     * @param i_y
     *            the y coordinate of the pixel
     * @return the color of the pixel at the given position
     * @throws IllegalArgumentException
     *             if the specified position coordinates exceed the buffer
     *             dimensions
     */
    private int getColorValue(int i_x, int i_y) {

        ByteBuffer colorBuffer = m_pickingBuffers.getColorBuffer();

        int width = m_pickingBuffers.getWidth();
        int height = m_pickingBuffers.getHeight();

        if (i_x < 0 || i_x >= width || i_y < 0 || i_y >= height)
            return 0;

        int index = ((height - i_y - 1) * width + i_x) * 3;

        int value = 0;
        value = colorBuffer.get(index) & 0xFF;
        value |= (colorBuffer.get(index + 1) & 0xFF) << 8;
        value |= (colorBuffer.get(index + 2) & 0xFF) << 16;

        return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#getCurrentSurface()
     */
    public ISurface getCurrentSurface() {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        if (m_currentSurface == null)
            return m_rootFigure.getSurface();

        return m_currentSurface;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#getDepth(int, int)
     */
    public float getDepth(int i_mx, int i_my) {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        validate();

        FloatBuffer depthBuffer = m_pickingBuffers.getDepthBuffer();

        int width = m_pickingBuffers.getWidth();
        int height = m_pickingBuffers.getHeight();

        if (i_mx < 0 || i_mx >= width || i_my < 0 || i_my >= height)
            return 0;

        int index = (height - i_my - 1) * width + i_mx;
        return depthBuffer.get(index);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#getFigure3D(int, int)
     */
    public IFigure3D getFigure3D(int i_mx, int i_my) {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        validate();

        int color = getColorValue(i_mx, i_my);
        return m_figureManager.getFigure(color);
    }

    /**
     * Informs this figure picker that it needs to validate itself.
     */
    public void invalidate() {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        m_valid = false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#updateCurrentSurface(int, int)
     */
    public void updateCurrentSurface(int i_mx, int i_my) {

        IFigure3D figure = getFigure3D(i_mx, i_my);

        if (figure != null) {
            ISurface surface = figure.getSurface();
            if (surface != null)
                m_currentSurface = figure.getSurface();
        }
    }

    /**
     * Triggers a validation of this figure picker.
     * 
     * @return <code>true</code> if the back buffer needs to be repaired after
     *         the validation and <code>false</code> otherwise
     */
    public boolean validate() {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        if (m_valid)
            return false;

        if (m_canvas == null)
            throw new IllegalStateException("canvas has not been initialized");

        if (m_rootFigure == null)
            throw new IllegalStateException(
                "root figure has not been initialized");

        boolean repairBackbuffer = m_pickingBuffers.repaint(m_rootFigure,
            m_figureManager, m_canvas);
        
        m_valid = true;
        return repairBackbuffer;
    }
}
