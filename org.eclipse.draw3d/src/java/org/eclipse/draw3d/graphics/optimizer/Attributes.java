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

import org.eclipse.draw3d.graphics.GraphicsState;

/**
 * CommonAttributes There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.12.2009
 */
public abstract class Attributes {

	protected int m_alpha;

	public int getAlpha() {
		return m_alpha;
	}

	public Attributes(GraphicsState i_state) {

		m_alpha = i_state.getAlpha();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Attributes other = (Attributes) obj;

		if (m_alpha != other.m_alpha)
			return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + m_alpha;
		return result;
	}
}
