/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.camera;

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * General camera interface used by LightweightSystem. A camera can be used in
 * two modes, which are working very differently. In absolute mode, moving,
 * rotating, or orbiting the camera results in directly modifying the given
 * parameters. In timed mode, the given values are interpreted as velocities. In
 * timed mode, render must be periodically called in order to update the values.
 * <p>
 * This interface is not supposed to be implemented directly, instead subclass
 * {@link AbstractCamera}.
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 19.11.2007
 */
public interface ICamera {

	public static final IVector3f RIGHT_REF = new Vector3fImpl(1, 0, 0);

	public static final IVector3f UP_REF = new Vector3fImpl(0, -1, 0);

	public static final IVector3f VIEW_REF = new Vector3fImpl(0, 0, 1);

	/**
	 * Registers the given listener with this camera. If the listener is already
	 * registered, it will be registered again.
	 * 
	 * @param i_listener
	 *            the listener to register
	 */
	public void addCameraListener(ICameraListener i_listener);

	/**
	 * Calculates the distance from this camera to a given point.
	 * 
	 * @param i_point
	 *            the point
	 * @return the distance from this camera to the given point
	 * @throws NullPointerException
	 *             if the given point is <code>null</code>
	 */
	public float getDistance(Vector3f i_point);

	/**
	 * Returns the distance between the viewpoint and the var clipping plane.
	 * 
	 * @return the distance
	 */
	public int getFar();

	/**
	 * Returns the distance between the viewpoint and the near clipping plane.
	 * 
	 * @return the distance
	 */
	public int getNear();

	/**
	 * Returns the camera's current position.
	 * 
	 * @param io_result
	 *            the result vector, if <code>null</code>, a new vector will be
	 *            returned
	 * @return the current position
	 */
	public Vector3f getPosition(Vector3f io_result);

	/**
	 * Look at the given position. If the up vector is not <code>null</code>,
	 * the camera is adjusted accordingly.
	 * 
	 * @param i_position
	 *            the position to look at
	 * @param i_upvector
	 *            the new up vector, which may be <code>null</code>
	 */
	public void lookAt(IVector3f i_position, IVector3f i_upvector);

	/**
	 * Moves camera by given distances.
	 * 
	 * @param i_dForward
	 *            the distance in the view direction
	 * @param i_dStrafe
	 *            the sideways distance in the right direction
	 * @param i_dUp
	 *            the distance on the Z axis
	 */
	public void moveBy(float i_dForward, float i_dStrafe, float i_dUp);

	/**
	 * Moves the camera to the given position.
	 * 
	 * @param i_x
	 *            the X coordinate of the new position
	 * @param i_y
	 *            the Y coordinate of the new position
	 * @param i_z
	 *            the Z coordinate of the new position
	 */
	public void moveTo(float i_x, float i_y, float i_z);

	/**
	 * Moves the camera around a location (called orbit center) by a given
	 * angle.
	 * 
	 * @param i_center
	 *            the orbit center
	 * @param i_hAngle
	 *            the horizontal orbit angle, in radians
	 * @param i_vAngle
	 *            the vertical orbit angle, in radians
	 */
	public void orbit(Vector3f i_center, float i_hAngle, float i_vAngle);

	/**
	 * Removes the given camera listener from this camera. If the given listener
	 * is not registered, nothing happens. If the given listener has been
	 * registered more than once, the oldest registration will be removed.
	 * 
	 * @param i_listener
	 *            the listener to remove
	 */
	public void removeCameraListener(ICameraListener i_listener);

	/**
	 * Renders the camera.
	 */
	public void render(RenderContext renderContext);

	/**
	 * Resets the camera to its default position, view direction and up vector.
	 */
	public void reset();

	/**
	 * Rotates camera by the given angles.
	 * 
	 * @param i_roll
	 *            the roll angle (rotates about the view direction)
	 * @param i_pitch
	 *            the pitch angle (nods the camera)
	 * @param i_yaw
	 *            the yaw angle (looks sideways)
	 */
	public void rotate(float i_roll, float i_pitch, float i_yaw);

	/**
	 * Sets the currently visible viewport dimensions.
	 * 
	 * @param i_width
	 *            the width of the viewport
	 * @param i_height
	 *            the height of the viewport
	 */
	public void setViewport(int i_width, int i_height);
}
