/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.picking;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * Picking buffers consist of a color and a depth buffer.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 16.05.2008
 */
public interface PickingBuffers {

    /**
     * Disposes these picking buffers.
     */
    public  void dispose();

    /**
     * Returns the buffer configuration for these picking buffers.
     * 
     * @return the buffer configuration
     */
    public Graphics3DOffscreenBufferConfig getBufferConfig();

    /**
     * Returns the color buffer.
     * 
     * @return the color buffer
     * @throws IllegalStateException
     *             if the offscreen renderer is not initialized or disposed
     */
    public  ByteBuffer getColorBuffer();

    /**
     * Returns the depth buffer.
     * 
     * @return the depth buffer
     * @throws IllegalStateException
     *             if the offscreen renderer is not initialized or disposed
     */
    public  FloatBuffer getDepthBuffer();

    /**
     * Returns the height of the offscreen buffers, in pixels.
     * 
     * @return the height of the offscreen buffers
     * @throws IllegalStateException
     *             if the offscreen renderer is not initialized or disposed
     */
    public  int getHeight();

    /**
     * Returns the width of the offscreen buffers, in pixels.
     * 
     * @return the width of the offscreen buffers
     * @throws IllegalStateException
     *             if the offscreen renderer is not initialized or disposed
     */
    public  int getWidth();

    /**
     * Repaints the offscreen buffers.
     * 
     * @param i_rootFigure
     *            the root figure to paint
     * @param i_figureManager
     *            the figure manager
     * @param i_canvas
     *            the gl canvas to paint on
     * @return <code>true</code> if the back buffer was destroyed and needs to
     *         be repaired and <code>false</code> otherwise
     */
    public boolean repaint(final IFigure i_rootFigure,
            FigureManager i_figureManager, GLCanvas i_canvas);

}