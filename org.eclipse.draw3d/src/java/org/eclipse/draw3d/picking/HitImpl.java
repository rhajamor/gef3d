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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.geometry.IParaxialBoundingBox;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * A mutable implementation of the {@link Hit} interface.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 31.07.2009
 */
public class HitImpl implements Hit {

	private float m_distance;

	private IFigure3D m_figure;

	private Point m_mLocation;

	private Vector3f m_wLocation;

	private IVector3f m_rayStart;

	private IVector3f m_rayDirection;

	/**
	 * Creates a new hit with the given figure and distance.
	 * 
	 * @param i_figure the figure that was hit
	 * @param i_distance the distance of the hit point to the origin of the
	 *            picking ray
	 * @param i_rayStart the origin of the picking ray
	 * @param i_rayDirection the direction of the picking ray
	 * @throws NullPointerException if the given figure or any of the given
	 *             vectors is <code>null</code>
	 */
	public HitImpl(IFigure3D i_figure, float i_distance, IVector3f i_rayStart,
			IVector3f i_rayDirection) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		if (i_rayStart == null)
			throw new NullPointerException("i_rayStart must not be null");

		if (i_rayDirection == null)
			throw new NullPointerException("i_rayDirection must not be null");

		m_figure = i_figure;
		m_distance = i_distance;
		m_rayStart = i_rayStart;
		m_rayDirection = i_rayDirection;
	}

	/**
	 * Returns the best candidate of this hit and the given hit. The best
	 * candidate is calculated as follows:
	 * <ul>
	 * <li>If the given hit is returned, this hit is returned.</li>
	 * <li>If both hits are exact or both hits are not exact, the closest hit is
	 * returned.</li>
	 * <li>If one hit is exact, it is returned if it is not behind the paraxial
	 * bounding box of the approximated hit.</li>
	 * <li>Otherwise, the coarse hit is returned.</li>
	 * </ul>
	 * 
	 * @param i_hit the hit to compare to
	 * @return the best candidate of this and the given hit
	 * @throws NullPointerException if the given hit is <code>null</code>
	 */
	public HitImpl getBestCandidate(HitImpl i_hit) {

		if (i_hit == null)
			return this;

		if ((isExact() == i_hit.isExact())) {
			if (isCloserThan(i_hit))
				return this;
			else
				return i_hit;
		} else {
			Vector3f wLocation = Draw3DCache.getVector3f();
			try {
				HitImpl exact = isExact() ? this : i_hit;
				HitImpl coarse = isExact() ? i_hit : this;

				if (exact.isCloserThan(coarse))
					return exact;
				else {
					IFigure3D figure = coarse.getFigure();
					IParaxialBoundingBox pbBox =
						figure.getParaxialBoundingBox();

					exact.getWorldLocation(wLocation);
					if (pbBox.contains(wLocation))
						return exact;
					else
						return coarse;
				}
			} finally {
				Draw3DCache.returnVector3f(wLocation);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Hit#getDistance()
	 */
	public float getDistance() {

		return m_distance;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Hit#getFigure()
	 */
	public IFigure3D getFigure() {

		return m_figure;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Hit#getMouseLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getMouseLocation(Point o_result) {

		if (m_mLocation == null)
			throw new IllegalStateException("mouse location has not been set");

		Point result = o_result;
		if (result == null)
			result = new Point();

		result.setLocation(m_mLocation);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Hit#getWorldLocation(org.eclipse.draw3d.geometry.Vector3f)
	 */
	public Vector3f getWorldLocation(Vector3f o_result) {

		Vector3f result = o_result;
		if (result == null)
			result = new Vector3fImpl();

		if (m_wLocation == null) {
			m_wLocation = new Vector3fImpl(m_rayDirection);
			m_wLocation.scale(m_distance);
			Math3D.add(m_rayStart, m_wLocation, m_wLocation);
		}

		result.set(m_wLocation);
		return result;
	}

	/**
	 * Indicates whether this hit is closer than the given hit (to the origin of
	 * the picking ray).
	 * 
	 * @param i_hit the hit to compare with
	 * @return <code>true</code> if this hit is closer than the given hit and
	 *         <code>false</code> otherwise
	 * @throws NullPointerException if the given hit is <code>null</code>
	 */
	public boolean isCloserThan(Hit i_hit) {

		if (i_hit == null)
			throw new NullPointerException("i_hit must not be null");

		return m_distance < i_hit.getDistance();
	}

	/**
	 * Indicates whether this hit is exact, or if it was found by some
	 * approximation method.
	 * 
	 * @return <code>true</code> if this hit is exact or <code>false</code>
	 *         otherwise
	 */
	public boolean isExact() {

		return m_figure instanceof Pickable;
	}

	/**
	 * Sets the mouse location.
	 * 
	 * @param i_mx the mouse X coordinate
	 * @param i_my the mouse Y coordinate
	 */
	public void setMouseLocation(int i_mx, int i_my) {

		if (m_mLocation == null)
			m_mLocation = new Point();

		m_mLocation.setLocation(i_mx, i_my);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();

		b.append("Hit[figure=");
		b.append(m_figure);
		b.append(", mouse: ");
		b.append(m_mLocation);
		b.append(", distance: ");
		b.append(m_distance);
		b.append(", world: ");
		b.append(m_wLocation);
		b.append("]");

		return b.toString();
	}

}
