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

import static org.eclipse.draw3d.util.CoordinateConverter.worldToSurface;

import java.text.MessageFormat;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IFigure3D;
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

	private boolean m_valid = false;

	private Vector3f m_location3D = new Vector3fImpl();

	private ColorPicker m_picker;

	private Vector3f m_startLocation3D = new Vector3fImpl();

	private IFigure3D m_startFigure;

	private IFigure3D m_currentFigure;

	private Point m_screenLocation2D = new Point();

	private Point m_location2D = new Point();

	private Dimension m_moveDelta2D = new Dimension();

	private Point m_startLocation2D = new Point();

	private Vector3f m_moveDelta3D = new Vector3fImpl();

	/**
	 * Postcondition: moveDeltas (2D and 3D) != null
	 */
	private void validate() {

		if (!m_valid) {

			int x = m_screenLocation2D.x;
			int y = m_screenLocation2D.y;

			m_picker.getVirtualCoordinates(x, y, m_location3D);
			m_currentFigure = m_picker.getFigure3D(x, y);

			IFigure3D surfaceFigure = m_picker.getLastValidFigure();

			worldToSurface(m_location3D.getX(), m_location3D.getY(),
				m_location3D.getZ(), surfaceFigure, m_location2D);

			Math3D.sub(m_location3D, m_startLocation3D, m_moveDelta3D);

			// if (m_startFigure == m_currentFigure) {
			if (m_moveDelta2D == null)
				m_moveDelta2D = new Dimension();

			m_moveDelta2D.width = m_location2D.x - m_startLocation2D.x;
			m_moveDelta2D.height = m_location2D.y - m_startLocation2D.y;
			// } else {
			// m_moveDelta2D = null;
			// }

			m_valid = true;
		}
		// postcondition:
		if (!(m_moveDelta2D != null && m_moveDelta3D != null)) {
			throw new IllegalStateException(MessageFormat.format(
				"Postcondition failed, "
						+ "at least one move delta is null (2D: {0}, 3D: {1})",
				m_moveDelta2D, m_moveDelta3D));

		}
	}

	/**
	 * Returns the 2D move delta.
	 * 
	 * @return the 2D move delta.
	 */
	public Dimension getMoveDelta2D() {

		validate();
		return m_moveDelta2D;
	}

	/**
	 * Returns the 3D start location.
	 * 
	 * @return
	 */
	public IVector3f getStartLocation3D() {

		return m_startLocation3D;
	}

	/**
	 * Creates a new instance and initializes it with the given arguments.
	 * 
	 * @param i_screenLocation2D the start location of the drag operation in
	 *            screen coordinates
	 * @param i_picker the picker to use for calculating 3D coordinates
	 * @throws NullPointerException if any of the given arguments is null
	 */
	public TrackState(Point i_screenLocation2D, ColorPicker i_picker) {

		if (i_screenLocation2D == null)
			throw new NullPointerException(
				"i_screenLocation2D must not be null");

		if (i_picker == null)
			throw new NullPointerException("i_picker must not be null");

		m_picker = i_picker;
		setScreenLocation(i_screenLocation2D);

		m_picker.getVirtualCoordinates(m_screenLocation2D.x,
			m_screenLocation2D.y, m_startLocation3D);
		m_startFigure =
			m_picker.getFigure3D(m_screenLocation2D.x, m_screenLocation2D.y);
		worldToSurface(m_startLocation3D.getX(), m_startLocation3D.getY(),
			m_startLocation3D.getZ(), m_startFigure, m_startLocation2D);
	}

	/**
	 * Returns the current 3D drag location.
	 * 
	 * @return the current 3D drag location or <code>null</code> if no location
	 *         has been set
	 */
	public IVector3f getLocation3D() {

		validate();
		return m_location3D;
	}

	/**
	 * Returns the delta vector of the start and the current drag location.
	 * 
	 * @param o_result the result vector, if <code>null</code>, a new one will
	 *            be created
	 * @return the result delta vector
	 */
	public Vector3f getMoveDelta3D() {

		validate();
		return m_moveDelta3D;
	}

	public Point getLocation2D() {

		validate();
		return m_location2D;
	}

	/**
	 * Sets the current 2D location of the drag.
	 * 
	 * @param i_location the current 2D location
	 * @throws NullPointerException if the given location is <code>null</code>
	 */
	public void setScreenLocation(Point i_location) {

		if (i_location == null)
			throw new NullPointerException("i_location must not be null");

		if (i_location.equals(m_screenLocation2D))
			return;

		m_screenLocation2D.setLocation(i_location);
		m_valid = false;
	}

	public Point getStartLocation2D() {

		return m_startLocation2D;
	}
}
