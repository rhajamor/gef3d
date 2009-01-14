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
package org.eclipse.draw3d.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.picking.ColorPicker;

/**
 * Utility class to convert coordinates.<br/>
 * <br />
 * There are the following coordinate systems in Gef3D:
 * <ul>
 * <li><b>World coordinates</b> - 3D floating point coordinates in OpenGL world
 * space.</li>
 * <li><b>Screen coordinates</b> - 2D integer coordinates on the screen, the
 * origin being in the top left corner of the viewport. Often, a depth value is
 * associated with screen coordinates to identify a point on the view ray. The
 * depth value at a given screen pixel describes the distance from the view
 * point to a point in 3D world space and can be obtained from the color picker
 * ({@link ColorPicker}).</li>
 * <li><b>Figure coordinates</b></li> - 3D floating point coordinates that are
 * relative to the local coordinate system of a figure. The origin of the figure
 * coordinate system is in its top left front corner.
 * <li><b>Surface coordinates</b></li> - 2D integer coordinates that are
 * relative to the origin of the front surface of a figure, which is usually in
 * its top left corner.
 * </ul>
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 02.03.2008
 */
public class CoordinateConverter {

	private static final FloatBuffer MODELVIEW = BufferUtils
			.createFloatBuffer(16);

	private static final FloatBuffer PROJECTION = BufferUtils
			.createFloatBuffer(16);

	private static final FloatBuffer RESULT = BufferUtils.createFloatBuffer(16);

	/**
	 * Used in {@link #screenToSurface(int, int, float, IFigure3D, Point) and
	 * #worldToSurface(float, float, float, IFigure3D, Point).
	 */
	private static final Vector3fImpl TMP_V3 = new Vector3fImpl();

	private static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer(16);

	/**
	 * Returns the world coordinates of a point given in figure-relative
	 * coordinates.
	 * 
	 * @param i_fX the figure-relative X coordinate
	 * @param i_fY the figure-relative Y coordinate
	 * @param i_fZ the figure-relative Z coordinate
	 * @param i_figure the reference figure
	 * @param io_result the result vector, if <code>null</code>, a new one will
	 *            be created
	 * @return the world coordinates of the given point
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public static Vector3f figureToWorld(float i_fX, float i_fY, float i_fZ,
			IFigure3D i_figure, Vector3f io_result) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		if (io_result == null)
			io_result = new Vector3fImpl();

		IVector3f loc = i_figure.getLocation3D();

		io_result.set(i_fX + loc.getX(), i_fY + loc.getY(), i_fZ + loc.getZ());
		i_figure.transformToAbsolute(io_result);
		return io_result;
	}

	/**
	 * Converts the given screen coordinates to figure coordinates regarding the
	 * given figure.
	 * 
	 * @param i_sX the X screen cordinate
	 * @param i_sY the Y screen coordinate
	 * @param i_d the depth value at the given screen coordinates, this value
	 *            can be retrieved by calling
	 *            {@link ColorPicker#getDepth(int, int)}
	 * @param i_figure the figure to which the resulting coordinates should be
	 *            relative to
	 * @param io_result the result vector, if <code>null</code>, a new one will
	 *            be created
	 * @return the figure coordinates of the given screen coordinates
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public static Vector3f screenToFigure(int i_sX, int i_sY, float i_d,
			IFigure3D i_figure, Vector3f io_result) {

		screenToWorld(i_sX, i_sY, i_d, io_result);
		return worldToFigure(io_result.getX(), io_result.getY(), io_result
				.getZ(), i_figure, io_result);
	}

	/**
	 * Returns the surface coordinates of a given point in screen coordinates
	 * regarding the given figure.
	 * 
	 * @param i_sX the X screen coordinate
	 * @param i_sY the Y screen coordinate
	 * @param i_d the depth value
	 * @param i_figure the figure that contains the surface
	 * @param io_result the result point, if <code>null</code>, a new one will
	 *            be created
	 * @return the screen coordinates of the given point
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public static Point screenToSurface(int i_sX, int i_sY, float i_d,
			IFigure3D i_figure, Point io_result) {

		screenToWorld(i_sX, i_sY, i_d, TMP_V3);
		return worldToSurface(TMP_V3.x, TMP_V3.y, TMP_V3.z, i_figure, io_result);
	}

	/**
	 * Returns the surface coordinates of a point with the given screen
	 * coordinates regarding the given figure. This method determines the
	 * necessary depth value by querying the color picker of the given figure,
	 * so it is assumed that the surface of the given figure is visible in the
	 * current picking buffer and that it contains the given screen coordinates.
	 * In other words, it is assumed that the surface of the given figure is hit
	 * by the picking ray.
	 * 
	 * @param i_sX the X screen coordinate
	 * @param i_sY the Y screen coordinate
	 * @param i_figure the figure that contains the surface
	 * @param io_result the result point, if <code>null</code>, a new one will
	 *            be created
	 * @return the screen coordinates of the given point
	 * @throws NullPointerException if the given figure is <code>null</code>
	 * @throws IllegalArgumentException if no color picker can be retrieved from
	 *             the given figure
	 */
	public static Point screenToSurface(int i_sX, int i_sY, IFigure3D i_figure,
			Point io_result) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		UpdateManager updateManager = i_figure.getUpdateManager();
		if (!(updateManager instanceof PickingUpdateManager3D))
			throw new IllegalArgumentException(
					"unable to get color picker from " + i_figure);

		PickingUpdateManager3D pickingManager = (PickingUpdateManager3D) updateManager;
		ColorPicker picker = pickingManager.getPicker();

		float depth = picker.getDepth(i_sX, i_sY);
		return screenToSurface(i_sX, i_sY, depth, i_figure, io_result);
	}

	/**
	 * Converts the given screen coordinates to world coordinates using the
	 * given depth value.
	 * 
	 * @param i_sX the X screen coordinate
	 * @param i_sY the Y screen coordinate
	 * @param i_d the depth value, this value can be retrieved by calling
	 *            {@link ColorPicker#getDepth(int, int)}
	 * @param io_result the result vector, if <code>null</code>, a new one will
	 *            be created
	 * @return the world coordinates of the point specified by the given screen
	 *         coordinates and depth
	 */
	public static Vector3f screenToWorld(int i_sX, int i_sY, float i_d,
			Vector3f io_result) {

		if (io_result == null)
			io_result = new Vector3fImpl();

		Graphics3D g3d = RenderContext.getContext().getGraphics3D();
		g3d.glGetInteger(Graphics3DDraw.GL_VIEWPORT, VIEWPORT);
		g3d.glGetFloat(Graphics3DDraw.GL_PROJECTION_MATRIX, PROJECTION);
		g3d.glGetFloat(Graphics3DDraw.GL_MODELVIEW_MATRIX, MODELVIEW);

		// OpenGL screen coordinates expect the origin to be in the lower left
		// corner
		int screenHeight = VIEWPORT.get(3);
		int invertedY = screenHeight - i_sY;

		RESULT.rewind();
		g3d.gluUnProject(i_sX, invertedY, i_d, MODELVIEW, PROJECTION, VIEWPORT,
				RESULT);

		io_result.set(RESULT.get(), RESULT.get(), RESULT.get());
		return io_result;
	}

	/**
	 * Returns the world coordinates of a point on a given figure's front
	 * surface, with the given distance to the surface.
	 * 
	 * @param i_sX the X surface coordinate
	 * @param i_sY the Y surface coordinate
	 * @param i_dist the distance of the resulting 3D location from the front
	 *            surface
	 * @param i_figure the figure that contains the surface
	 * @param io_result the result vector, if <code>null</code>, a new one will
	 *            be created
	 * @return the world coordinates of the given point
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public static Vector3f surfaceToWorld(int i_sX, int i_sY, float i_dist,
			IFigure3D i_figure, Vector3f io_result) {

		if (i_figure == null)
			throw new NullPointerException("figure must not be null");

		IVector3f size = i_figure.getSize3D();
		return figureToWorld(i_sX, i_sY, i_dist + size.getZ(), i_figure,
				io_result);
	}

	/**
	 * Returns the world coordinates of a point on a given figure's front
	 * surface. This is the same as calling
	 * {@link #surfaceToWorld(int, int, float, IFigure3D, Vector3f)} with a
	 * distance of 0.
	 * 
	 * @param i_sX the X surface coordinate
	 * @param i_sY the Y surface coordinate
	 * @param i_figure the figure that contains the surface
	 * @param io_result the result vector, if <code>null</code>, a new one will
	 *            be created
	 * @return the world coordinates of the given point
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public static Vector3f surfaceToWorld(int i_sX, int i_sY,
			IFigure3D i_figure, Vector3f io_result) {

		return surfaceToWorld(i_sX, i_sY, 0, i_figure, io_result);
	}

	/**
	 * Returns the figure-relative coordinates of a given point in world
	 * coordinates.
	 * 
	 * @param i_wX the X world coordinate
	 * @param i_wY the Y world coordinate
	 * @param i_wZ the Z world coordinate
	 * @param i_figure the figure which the returned point should be relative to
	 * @param io_result the result vector, if <code>null</code>, a new one will
	 *            be created
	 * @return the figure-relative coordinates of the given point
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public static Vector3f worldToFigure(float i_wX, float i_wY, float i_wZ,
			IFigure3D i_figure, Vector3f io_result) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		if (io_result == null)
			io_result = new Vector3fImpl();

		io_result.set(i_wX, i_wY, i_wZ);
		i_figure.transformToRelative(io_result);

		IVector3f loc = i_figure.getLocation3D();
		io_result.set(io_result.getX() - loc.getX(), io_result.getY()
				- loc.getY(), io_result.getZ() - loc.getZ());

		return io_result;
	}

	/**
	 * Returns the surface coordinates of a given point in world coordinates.
	 * The surface point is specified by the intersection of a line that is
	 * perpendicular to the figure's surface and that contains the given point.
	 * 
	 * @param i_wX the X world coordinate
	 * @param i_wY the Y world coordinate
	 * @param i_wZ the Z world coordinate
	 * @param i_figure the figure that contains the surface to which the
	 *            returned point should be relative
	 * @param io_result the result point, if <code>null</code>, a new one will
	 *            be created
	 * @return the 2D surface coordinates on the figures surface
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public static Point worldToSurface(float i_wX, float i_wY, float i_wZ,
			IFigure3D i_figure, Point io_result) {

		if (i_figure == null)
			throw new NullPointerException("figure must not be null");

		Point result = io_result;
		if (result == null)
			result = new Point();

		worldToFigure(i_wX, i_wY, i_wZ, i_figure, TMP_V3);
		result.x = Math.round(TMP_V3.x);
		result.y = Math.round(TMP_V3.y);

		return result;
	}

	private CoordinateConverter() {

		// empty constructor
	}
}