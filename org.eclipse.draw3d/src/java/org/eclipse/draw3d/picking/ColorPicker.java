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

import static org.eclipse.draw3d.util.CoordinateConverter.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderMode;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.geometryext.Plane;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig;
import org.eclipse.draw3d.util.CoordinateConverter;
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
	private static final Logger log = Logger.getLogger(ColorPicker.class
			.getName());

	/**
	 * The standard distance for a virtual pick when no virtual picking plane
	 * can be computed or if the current virtual picking plane is not hit by the
	 * pick ray. This value should be between 0 and 1.
	 */
	private static final float STD_DEPTH = 0.1f;

	private static Vector3fImpl TMP_V3_1 = new Vector3fImpl();

	private static Vector3fImpl TMP_V3_2 = new Vector3fImpl();

	private static Vector3fImpl TMP_V3_3 = new Vector3fImpl();

	private ICamera m_camera;

	private GLCanvas m_canvas;

	private boolean m_disposed = false;

	private final FigureManager m_figureManager;

	private IFigure3D m_lastFigure;

	private float m_lastZ;

	private final PickingBuffers m_pickingBuffers;

	private IFigure m_rootFigure;

	private boolean m_valid = false;

	private final Plane m_virtualPlane = new Plane();

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
		Graphics3DOffscreenBufferConfig bufferConfig = m_pickingBuffers
				.getBufferConfig();

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
	 * @param i_x the x coordinate of the pixel
	 * @param i_y the y coordinate of the pixel
	 * @return the color of the pixel at the given position
	 * @throws IllegalArgumentException if the specified position coordinates
	 *             exceed the buffer dimensions
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
	 * Returns the depth value for the foremost figure at the given coordinates.
	 * 
	 * @param i_x the screen X coordinate
	 * @param i_y the screen Y coordinate
	 * @return the depth value
	 * @throws IllegalStateException if this figure picker is invalid
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
	 * Returns the figure at the given screen coordinates or <code>null</code>
	 * if no figure can be found at the given coordinates that satisfies the
	 * given tree search.
	 * 
	 * @param i_x the X screen coordinate
	 * @param i_y the Y screen coordinate
	 * @param i_search the tree search
	 * @return the figure at the given coordinates or <code>null</code>
	 */
	public IFigure getFigure(int i_x, int i_y, TreeSearch i_search) {

		IFigure3D figure3D = getFigure3D(i_x, i_y);
		if (figure3D == null)
			return null;

		if (i_search != null) {
			IFigure currentFigure = figure3D;
			do {
				if (i_search.prune(currentFigure))
					return null;

				currentFigure = currentFigure.getParent();
			} while (currentFigure != null);
		}

		List<IFigure> children2D = figure3D.getChildren2D();
		if (!children2D.isEmpty()) {
			float depth = getDepth(i_x, i_y);

			Point coords = Point.SINGLETON;
			CoordinateConverter.screenToSurface(i_x, i_y, depth, figure3D,
					coords);

			int sX = coords.x;
			int sY = coords.y;
			for (IFigure child2D : children2D) {
				IFigure result = null;
				if (i_search != null) {
					if (i_search.prune(child2D))
						continue;

					result = child2D.findFigureAt(sX, sY, i_search);
				} else {
					result = child2D.findFigureAt(sX, sY);
				}

				if (result != null)
					return result;
			}
		}

		// now we have only found a 3D figure, and we must check whether it
		// is accepted by the search

		if (i_search == null)
			return figure3D;

		IFigure currentFigure = figure3D;
		do {
			if (i_search.accept(currentFigure))
				return currentFigure;

			currentFigure = currentFigure.getParent();
		} while (currentFigure != null);

		return null;
	}

	/**
	 * Returns the foremost 3D figure at the given screen coordinates.
	 * 
	 * @param i_x the screen X coordinate
	 * @param i_y the screen Y coordinate
	 * @return the 3D figure at the given coordinates or <code>null</code> if
	 *         the pixel at the given coordinates is void
	 * @throws IllegalStateException if this figure picker is invalid
	 */
	public IFigure3D getFigure3D(int i_x, int i_y) {

		if (m_disposed)
			throw new IllegalStateException("color picker is disposed");

		validate();

		int color = getColorValue(i_x, i_y);

		IFigure3D figure = m_figureManager.getFigure(color);
		if (figure != null)
			m_lastFigure = figure;

		return figure;
	}

	/**
	 * Returns virtual world coordinates for the given screen coordinates.
	 * Virtual coordinates are calculated using the given screen coordinates and
	 * a depth value that is determined in the following way:
	 * <ul>
	 * <li>If there is a figure at the given screen coordinates, use the depth
	 * value of the figure's intersection with the pick ray. This is the same
	 * value as returned by {@link #getDepth(int, int)}.</li>
	 * <li>If the given screen coordinates point to the void, a virtual picking
	 * plane is constructed by determining the last figure that was hit by the
	 * pick ray before it entered the void and using the depth value of the
	 * intersection of the pick ray with the figure before it entered the void.
	 * The virtual plane is parallel to the last figure's surface. Then, a depth
	 * value is calculated by intersecting the pick ray with this virtual plane.
	 * </li>
	 * </ul>
	 * 
	 * @param i_x the screen X coordinate
	 * @param i_y the screen Y coordinate
	 * @param o_result the result vector, if <code>null</code>, a new vector
	 *            will be created
	 * @return the virtual world coordinates for the given screen coordinates
	 * @throws IllegalStateException if this figure picker is invalid
	 */
	public Vector3f getVirtualCoordinates(int i_x, int i_y, Vector3f o_result) {

		if (m_disposed)
			throw new IllegalStateException("color picker is disposed");

		if (m_camera == null)
			throw new IllegalStateException("camera has not been initialized");

		validate();

		Vector3f result = o_result;
		if (result == null)
			result = new Vector3fImpl();

		IFigure3D figure = getFigure3D(i_x, i_y);
		if (figure != null) {
			float depth = getDepth(i_x, i_y);
			screenToWorld(i_x, i_y, depth, result);

			updateVirtualPlane(i_x, i_y);
		} else if (m_lastFigure != null) {
			m_camera.getPosition(TMP_V3_1);
			screenToWorld(i_x, i_y, 0, TMP_V3_2);

			m_virtualPlane.intersectionWithRay(TMP_V3_1, TMP_V3_2, result);
		} else {
			screenToWorld(i_x, i_y, STD_DEPTH, result);
		}

		return result;
	}

	/**
	 * Ignore the given figure and all its children.
	 * 
	 * @param i_figure the figure to ignore
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public void ignoreFigure(IFigure3D i_figure) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		if (m_disposed)
			throw new IllegalStateException("color picker is disposed");

		m_valid = !m_figureManager.ignoreFigure(i_figure) && m_valid;

		if (log.isLoggable(Level.FINE))
			log.fine("ignoring " + i_figure + " and all children");
	}

	/**
	 * Ignore any figure that is an instance of the given type including the
	 * figures children.
	 * 
	 * @param i_type the type to ignore
	 * @throws NullPointerException if the given class is <code>null</code>
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
	 * @param i_figure the figure to check
	 * @return <code>true</code> if the given figure is ignored or
	 *         <code>false</code> otherwise
	 * @throws NullPointerException if the given figure is <code>null</code>
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

	/**
	 * Sets the camera to use when calculating the virtual picking plane.
	 * 
	 * @param i_camera the camera
	 */
	public void setCamera(ICamera i_camera) {

		if (m_disposed)
			throw new IllegalStateException("color picker is disposed");

		m_camera = i_camera;
	}

	/**
	 * Sets the canvas to use when repainting the color buffer.
	 * 
	 * @param i_canvas the canvas
	 */
	public void setCanvas(GLCanvas i_canvas) {

		if (m_disposed)
			throw new IllegalStateException("color picker is disposed");

		m_canvas = i_canvas;
	}

	/**
	 * Sets the root figure.
	 * 
	 * @param i_rootFigure the root figure
	 */
	public void setRootFigure(IFigure i_rootFigure) {

		if (m_disposed)
			throw new IllegalStateException("color picker is disposed");

		m_rootFigure = i_rootFigure;
	}

	private void updateVirtualPlane(int i_x, int i_y) {

		float depth = getDepth(i_x, i_y);
		if (depth == 1.0f)
			return;

		if (m_lastFigure == null)
			throw new IllegalStateException("no reference figure has been set");

		screenToFigure(i_x, i_y, depth, m_lastFigure, TMP_V3_1);
		if (Math.abs(m_lastZ - TMP_V3_1.z) < 0.1f)
			return;

		m_lastZ = TMP_V3_1.z;

		TMP_V3_2.set(TMP_V3_1);
		TMP_V3_2.x += 1000;

		TMP_V3_3.set(TMP_V3_1);
		TMP_V3_3.y += 1000;

		m_lastFigure.transformToParent(TMP_V3_1);
		m_lastFigure.transformToAbsolute(TMP_V3_1);

		m_lastFigure.transformToParent(TMP_V3_2);
		m_lastFigure.transformToAbsolute(TMP_V3_2);

		m_lastFigure.transformToParent(TMP_V3_3);
		m_lastFigure.transformToAbsolute(TMP_V3_3);

		// if the three points are not colinear (non-invertible model
		// matrix), they will be ignored
		m_virtualPlane.set(TMP_V3_1, TMP_V3_2, TMP_V3_3);
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
