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

/**
 * PointQueue There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.11.2009
 */
public class PointList {

	private int[] m_points;

	private int[] m_order;

	public int getX(int i_index) {

		return m_points[2 * m_order[i_index]];
	}

	public int getY(int i_index) {

		return m_points[2 * m_order[i_index] + 1];
	}

	public int getSize() {

		return m_order.length;
	}
}
