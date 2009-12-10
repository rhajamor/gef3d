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
package org.eclipse.draw3d.graphics.optimizer;

/**
 * PrimitiveBounds There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 02.12.2009
 */
public class PrimitiveBounds {

	private float m_x, m_y, m_width, m_height, m_xPlusWidth, m_yPlusHeight;

	public PrimitiveBounds(float[] i_points) {

		m_x = i_points[0];
		m_y = i_points[1];
		m_width = 0;
		m_height = 0;
		m_xPlusWidth = m_x;
		m_yPlusHeight = m_y;

		float x, y;
		for (int i = 1; i < i_points.length / 2; i++) {
			x = i_points[2 * i];
			y = i_points[2 * i + 1];

			if (x < m_x) {
				m_x = x;
				m_xPlusWidth = m_x + m_width;
			} else if (x > m_xPlusWidth) {
				m_width = x - m_x;
				m_xPlusWidth = m_x + m_width;
			}

			if (y < m_y) {
				m_y = y;
				m_yPlusHeight = y + m_height;
			} else if (y > m_yPlusHeight) {
				m_height = y - m_y;
				m_yPlusHeight = m_y + m_height;
			}
		}
	}

	public boolean contains(PrimitiveBounds i_bounds) {

		if (i_bounds.getX() <= m_x)
			return false;

		if (i_bounds.getXPlusWidth() >= getXPlusWidth())
			return false;

		if (i_bounds.getY() <= m_y)
			return false;

		if (i_bounds.getYPlusHeight() >= getYPlusHeight())
			return false;

		return true;
	}

	public float getHeight() {

		return m_height;
	}

	public float getWidth() {

		return m_width;
	}

	public float getX() {

		return m_x;
	}

	public float getXPlusWidth() {

		return m_xPlusWidth;
	}

	public float getY() {

		return m_y;
	}

	public float getYPlusHeight() {

		return m_yPlusHeight;
	}

	public boolean intersects(PrimitiveBounds i_bounds) {

		if (i_bounds.getX() > getXPlusWidth())
			return false;

		if (i_bounds.getXPlusWidth() < m_x)
			return false;

		if (i_bounds.getY() > getYPlusHeight())
			return false;

		if (i_bounds.getYPlusHeight() < m_y)
			return false;

		return true;
	}
}
