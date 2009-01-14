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

package org.eclipse.draw3d;

import java.util.List;

import org.eclipse.draw2d.DelegatingLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * Locator used to place a {@link RotatableDecoration3D} on a
 * {@link Connection3D}. The decoration can be placed at the source or target
 * end of the connection figure. The default connection implementation uses a
 * {@link DelegatingLayout} which requires locators.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 17.05.2008
 */
public class ArrowLocator3D implements Locator {

	/**
	 * Enumerates the alignment options for a 3D locator.
	 * 
	 * @author Kristian Duske
	 * @version $Revision$
	 * @since 19.05.2008
	 * @see $HeadURL$
	 */
	public static enum Alignment {
		/**
		 * The middle of the connection.
		 */
		MIDDLE,
		/**
		 * The source of the connection.
		 */
		SOURCE,
		/**
		 * The target of the connection.
		 */
		TARGET
	}

	private Alignment m_alignment;

	private Connection3D m_connection;

	/**
	 * Creates a new locator for the given connection and with the given
	 * alignment.
	 * 
	 * @param i_connection the connection associated with the locator
	 * @param i_alignment the alignment of the locator
	 * @throws NullPointerException if either of the given arguments is
	 *             <code>null</code>
	 */
	public ArrowLocator3D(Connection3D i_connection, Alignment i_alignment) {

		if (i_connection == null)
			throw new NullPointerException("i_connection must not be null");

		if (i_alignment == null)
			throw new NullPointerException("i_alignment must not be null");

		m_connection = i_connection;
		m_alignment = i_alignment;
	}

	/**
	 * Returns the alignment of this locator.
	 * 
	 * @return the alignment of this locator
	 */
	public Alignment getAlignment() {

		return m_alignment;
	}

	/**
	 * Returns the connection associated with this locator.
	 * 
	 * @return the connection associated with this locator
	 */
	public Connection3D getConnection() {

		return m_connection;
	}

	/**
	 * Returns the location of a decoration depending on the locator's
	 * alignment.
	 * 
	 * @param i_points the point list
	 * @param io_result the result vector, can be <code>null</code>
	 * @return the location
	 * @throws NullPointerException if the given point list is <code>null</code>
	 */
	protected IVector3f getLocation(List<Vector3f> i_points, Vector3f io_result) {

		if (i_points == null)
			throw new NullPointerException("i_points must not be null");

		if (io_result == null)
			io_result = new Vector3fImpl();

		int size = i_points.size();
		switch (m_alignment) {
		case SOURCE:
			io_result.set(i_points.get(0));
			break;
		case TARGET:
			io_result.set(i_points.get(size - 1));
			break;
		case MIDDLE:
			if ((size % 2) == 0) {
				int i = size / 2;
				Vector3f p1 = i_points.get(i - 1);
				Vector3f p2 = i_points.get(i);
				Math3D.sub(p2, p1, io_result);
				io_result.scale(0.5f);
				Math3D.add(p1, io_result, io_result);
			} else {
				io_result.set(i_points.get((size - 1) / 2));
			}
			break;
		default:
			throw new IllegalStateException("unknown location: " + m_alignment);
		}

		return io_result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Locator#relocate(org.eclipse.draw2d.IFigure)
	 */
	public void relocate(IFigure i_target) {

		List<Vector3f> points = m_connection.getPoints3D();
		RotatableDecoration3D arrow = (RotatableDecoration3D) i_target;

		IVector3f location = getLocation(points, null);
		arrow.setLocation3D(location);

		if (m_alignment == Alignment.SOURCE)
			arrow.setReferencePoint3D(points.get(1));
		else if (m_alignment == Alignment.TARGET)
			arrow.setReferencePoint3D(points.get(points.size() - 2));
	}
}
