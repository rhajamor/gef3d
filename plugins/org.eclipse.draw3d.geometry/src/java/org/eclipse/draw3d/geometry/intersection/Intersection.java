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
 * Intersection There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.11.2009
 */
public class Intersection {

	public enum Type {
		OVERLAP, POINT
	}

	private Segment m_first;

	private Segment m_overlap;

	private IVector2f m_point;

	private Segment m_second;

	private Type m_type;

	public Intersection(Segment i_first, Segment i_second, IVector2f i_point) {

		m_type = Type.POINT;
		m_first = i_first;
		m_second = i_second;
		m_point = i_point;
	}

	public Intersection(Segment i_first, Segment i_second, Segment i_overlap) {

		m_type = Type.OVERLAP;
		m_first = i_first;
		m_second = i_second;
		m_overlap = i_overlap;
	}

	public Segment getFirst() {

		return m_first;
	}

	public Segment getOverlap() {

		if (m_type != Type.OVERLAP)
			throw new AssertionError();

		return m_overlap;
	}

	public IVector2f getPoint() {

		if (m_type != Type.POINT)
			throw new AssertionError();

		return m_point;
	}

	public Segment getSecond() {

		return m_second;
	}

	public Type getType() {

		return m_type;
	}
}
