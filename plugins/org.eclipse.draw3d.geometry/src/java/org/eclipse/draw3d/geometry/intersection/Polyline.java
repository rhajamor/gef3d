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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw3d.geometry.IVector2f;

/**
 * Polyline There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.11.2009
 */
public class Polyline {

	private List<Segment> m_segments = new LinkedList<Segment>();

	private IVector2f m_last = null;

	public List<Segment> getSegments() {

		return Collections.unmodifiableList(m_segments);
	}

	public void addPoint(IVector2f i_point) {

		if (i_point == null)
			throw new NullPointerException("i_point must not be null");

		if (i_point.equals(m_last))
			return;

		if (m_last != null)
			m_segments.add(new Segment(m_last, i_point));

		m_last = i_point;
	}
}
