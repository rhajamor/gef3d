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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.RenderMode;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig;
import org.eclipse.draw3d.util.ImageConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * Allows picking of 3D figures by using color picking buffers. Color picking
 * works by rendering the scene with flat shading, giving every pickable object
 * a single distinct color. This color is later used as an index to retrieve the
 * associated figure. Picking is then performed by determining the color of the
 * pixel that was hit by the picking ray and looking up the picked object (if
 * any).
 * <p>
 * From old picking documentation (may not be uptodate): Picking is done by
 * rendering all 3D figures in flat color mode. Every figure has a unique RGB
 * color which is used to identify it. This color is also used as the index of
 * the {@link #coloredFigures} array: <br>
 * <br>
 * <code>index = RR | GG << 8 | BB << 16 == 0x00BBGGRR</code>.<br>
 * <br>
 * The picking process has the following steps:
 * <ol>
 * <li>Recreate the picking data structures if neccessary. For every figure:
 * <ol>
 * <li>Obtain a unique color using
 * {@link #nextColorIndex(RenderMode, IFigure3D)}. This also adds the figure to
 * the {@link #coloredFigures} array with the color as the index as described
 * above.</li>
 * <li>Render the figure in flat color mode using the unique color.</li>
 * </ol>
 * Finally, read the color and depth buffers.</li>
 * <li>Find the color at the given coordinates (this is done by reading the
 * color buffer).</li>
 * <li>Calculate an integer index from the RGB values of the color (see above).</li>
 * <li>If the color is 0xFFFFFF (white), then no figure was hit (white is the
 * background color). Otherwise, find the figure in the {@link #coloredFigures}
 * array and return it.</li>
 * </ol>
 * </p>
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 08.05.2008
 */
public class ColorPicker {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ColorPicker.class.getName());

    private GLCanvas m_canvas;

    private ISurface m_currentSurface;

    private boolean m_disposed = false;

    private final FigureManager m_figureManager;

    private Set<Class<?>> m_ignoredHosts = new HashSet<Class<?>>();

    private final PickingBuffers m_pickingBuffers;

    private IFigure3D m_rootFigure;

    private boolean m_valid = false;

    /**
     * Creates a new instance.
     */
    public ColorPicker() {

        m_figureManager = new FigureManager();
        m_pickingBuffers = new OffscreenBuffers();
    }

    /**
     * Clears the ignored figures and types.
     */
    public void clearIgnored() {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        if (log.isLoggable(Level.FINE))
            log.fine("clearing all ignored figures and types");

        m_valid = !m_figureManager.clearIgnored() && m_valid;
    }

    /**
     * Disposes this color picker.
     */
    public void dispose() {

        if (m_disposed)
            return;

        m_pickingBuffers.dispose();
        m_disposed = true;
    }

    /**
     * Dumps the color buffer of this picker.
     */
    public void dump() {

        ByteBuffer buffer = m_pickingBuffers.getColorBuffer();
        Graphics3DOffscreenBufferConfig bufferConfig = m_pickingBuffers.getBufferConfig();

        int pixelFormat = bufferConfig.getColorPixelFormat();
        int dataType = bufferConfig.getColorDataType();

        int width = m_pickingBuffers.getWidth();
        int height = m_pickingBuffers.getHeight();

        ImageData imageData = ImageConverter.colorBufferToImage(buffer,
            pixelFormat, dataType, width, height);

        ImageLoader imageLoader = new ImageLoader();
        imageLoader.data = new ImageData[] { imageData };

        String path = "/Users/kristian/Temp/colorBuffer"
                + System.currentTimeMillis() + ".png";
        imageLoader.save(path, SWT.IMAGE_PNG);
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
     * Returns the current surface.
     * 
     * @return the current surface
     */
    public ISurface getCurrentSurface() {

        if (m_currentSurface == null)
            return m_rootFigure.getSurface();

        return m_currentSurface;
    }

    /**
     * Returns the depth value for the foremost figure at the given mouse
     * coordinates.
     * 
     * @param i_x
     *            the mouse X coordinate
     * @param i_y
     *            the mouse Y coordinate
     * @return the depth value
     * @throws IllegalStateException
     *             if this figure picker is invalid
     */
    public float getDepth(int i_x, int i_y) {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        validate();

        FloatBuffer depthBuffer = m_pickingBuffers.getDepthBuffer();

        int width = m_pickingBuffers.getWidth();
        int height = m_pickingBuffers.getHeight();

        if (i_x < 0 || i_x >= width || i_y < 0 || i_y >= height)
            return 0;

        int index = (height - i_y - 1) * width + i_x;
        return depthBuffer.get(index);
    }

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
     *             if this figure picker is invalid
     */
    public IFigure3D getFigure3D(int i_mx, int i_my) {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        validate();

        int color = getColorValue(i_mx, i_my);
        IFigure3D figure = m_figureManager.getFigure(color);

        if (figure != null && !isIgnoredHost(figure)) {
            ISurface surface = figure.getSurface();
            if (surface != null)
                m_currentSurface = figure.getSurface();
        }

        return figure;
    }

    /**
     * Ignore the given figure and all its children.
     * 
     * @param i_figure
     *            the figure to ignore
     * @throws NullPointerException
     *             if the given figure is <code>null</code>
     */
    public void ignoreFigure(IFigure3D i_figure) {

        if (i_figure == null)
            throw new NullPointerException("i_figure must not be null");

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        m_valid &= !m_figureManager.ignoreFigure(i_figure);

        if (log.isLoggable(Level.FINE))
            log.fine("ignoring " + i_figure + " and all children");
    }

    /**
     * Ignores all figures in the given collection and their children.
     * 
     * @param i_figures
     *            the figures to ignore
     * 
     * @throws NullPointerException
     *             if the given collection is <code>null</code>
     */
    public void ignoreFigures(Collection<IFigure3D> i_figures) {

        if (i_figures == null)
            throw new NullPointerException("i_figures must not be null");

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        for (IFigure3D figure : i_figures) {
            m_valid &= !m_figureManager.ignoreFigure(figure);

            if (log.isLoggable(Level.FINE))
                log.fine("ignoring " + figure + " and all children");
        }
    }

    /**
     * Ignores any surface that belongs to a host that is an instance of the
     * given type or any of the hosts children.
     * 
     * @param i_hostType
     *            the type to ignore
     */
    public void ignoreSurface(Class<?> i_hostType) {

        if (i_hostType == null)
            throw new NullPointerException("i_hostType must not be null");

        if (m_ignoredHosts.add(i_hostType) && log.isLoggable(Level.FINE))
            log.fine("ignoring surface type " + i_hostType);
    }

    /**
     * Ignore any figure that is an instance of the given type including the
     * figures children.
     * 
     * @param i_type
     *            the type to ignore
     * @throws NullPointerException
     *             if the given class is <code>null</code>
     */
    public void ignoreType(Class<?> i_type) {

        if (i_type == null)
            throw new NullPointerException("i_type must not be null");

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        m_valid = !m_figureManager.ignoreType(i_type) && m_valid;

        if (log.isLoggable(Level.FINE))
            log.fine("ignoring " + i_type + " and all children");
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
     * Indicates whether the given figure is ignored.
     * 
     * @param i_figure
     *            the figure to check
     * @return <code>true</code> if the given figure is ignored or
     *         <code>false</code> otherwise
     * @throws NullPointerException
     *             if the given figure is <code>null</code>
     */
    public boolean isIgnored(IFigure3D i_figure) {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        IFigure3D currentFigure = i_figure;
        do {
            if (m_figureManager.isIgnored(currentFigure)) {
                if (log.isLoggable(Level.FINE)) {
                    StringBuilder message = new StringBuilder();
                    message.append(i_figure);
                    message.append(" is ignored");
                    if (i_figure != currentFigure) {
                        message.append(" because of ancestor ");
                        message.append(currentFigure);
                    }

                    log.fine(message.toString());
                }
                return true;
            }

            currentFigure = Figure3DHelper.getAncestor3D(currentFigure);
        } while (currentFigure != null);

        return false;
    }

    private boolean isIgnoredHost(IFigure3D i_figure) {

        for (Class<?> type : m_ignoredHosts)
            if (type.isInstance(i_figure))
                return true;

        return false;
    }

    /**
     * Sets the canvas to use when repainting the color buffer.
     * 
     * @param i_canvas
     *            the canvas
     */
    public void setCanvas(GLCanvas i_canvas) {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        m_canvas = i_canvas;
    }

    /**
     * Sets the root figure.
     * 
     * @param i_rootFigure
     *            the root figure
     */
    public void setRootFigure(IFigure3D i_rootFigure) {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        m_rootFigure = i_rootFigure;
    }

    /**
     * Triggers a validation of this figure picker.
     */
    public void validate() {

        if (m_disposed)
            throw new IllegalStateException("color picker is disposed");

        if (m_valid)
            return;

        if (m_canvas == null)
            throw new IllegalStateException("canvas has not been initialized");

        if (m_rootFigure == null)
            throw new IllegalStateException(
                "root figure has not been initialized");

        m_pickingBuffers.repaint(m_rootFigure, m_figureManager, m_canvas);
        m_valid = true;
    }
}
