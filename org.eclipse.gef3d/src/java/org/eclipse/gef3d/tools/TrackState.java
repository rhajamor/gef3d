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


import org.eclipse.draw2d.geometry.Point;
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

	private Vector3f m_location3D = new Vector3fImpl();

	private ColorPicker m_picker;

	private Vector3f m_startLocation3D = new Vector3fImpl();

	/**
	 * Creates a new instance and initializes it with the given arguments.
	 * 
	 * @param i_startLocation the start location of the drag operation
	 * @param i_picker the picker to use for calculating 3D coordinates
	 * @throws NullPointerException if any of the given arguments is null
	 */
	public TrackState(Point i_startLocation, ColorPicker i_picker) {

		if (i_startLocation == null)
			throw new NullPointerException("i_startLocation must not be null");

		if (i_picker == null)
			throw new NullPointerException("i_picker must not be null");

		m_picker = i_picker;
		m_picker.getVirtualCoordinates(i_startLocation.x, i_startLocation.y,
				m_startLocation3D);
	}

	/**
	 * Returns the current 3D drag location.
	 * 
	 * @return the current 3D drag location or <code>null</code> if no
	 *         location has been set
	 */
	public IVector3f getLocation3D() {

		return m_location3D;
	}

	/**
	 * Returns the delta vector of the start and the current drag location.
	 * 
	 * @param o_result the result vector, if <code>null</code>, a new one
	 *            will be created
	 * @return the result delta vector
	 */
	public Vector3f getMoveDelta3D(Vector3f o_result) {

		return Math3D.sub(m_location3D, m_startLocation3D, o_result);
	}

	/**
	 * Sets the current 2D location of the drag.
	 * 
	 * @param i_location the current 2D location
	 * @throws NullPointerException if the given location is <code>null</code>
	 */
	public void setLocation(Point i_location) {

		if (i_location == null)
			throw new NullPointerException("i_location must not be null");

		m_picker
				.getVirtualCoordinates(i_location.x, i_location.y, m_location3D);
	}
}
