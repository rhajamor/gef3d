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
package org.eclipse.draw3d.geometry.intersection;

import org.eclipse.draw3d.geometry.IVector2f;

/**
 * Segment There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.11.2009
 */
public class Segment {

	private IVector2f m_end;

	private IVector2f m_start;

	private IVector2f m_left;

	private IVector2f m_right;

	private IVector2f m_upper;

	private IVector2f m_lower;

	private float m_c;

	private float m_g;

	public Segment(IVector2f i_start, IVector2f i_end) {

		m_start = i_start;
		m_end = i_end;

		float dX = getEnd().getX() - getStart().getX();
		float dY = getEnd().getY() - getStart().getY();

		if (dX != 0) {
			m_g = Math.abs(dY / dX);
			m_c = m_g * getStart().getX() - getStart().getY();
		} else {
			m_g = Float.POSITIVE_INFINITY;
			m_c = Float.NaN;
		}
	}

	public float getC() {

		return m_c;
	}

	public float getG() {

		return m_g;
	}

	public IVector2f getEnd() {

		return m_end;
	}

	public IVector2f getStart() {

		return m_start;
	}
}
