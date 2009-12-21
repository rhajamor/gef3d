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
import org.eclipse.swt.graphics.Color;

/**
 * FillAttributes There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.12.2009
 */
public class SolidAttributes extends FillAttributes {

	protected Color m_color;

	public SolidAttributes(GraphicsState i_state) {

		super(i_state);
		m_color = i_state.getBackgroundColor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object i_obj) {

		if (!super.equals(i_obj))
			return false;

		SolidAttributes other = (SolidAttributes) i_obj;

		if (m_color == null) {
			if (other.m_color != null)
				return false;
		} else if (!m_color.equals(other.m_color))
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
		int result = super.hashCode();
		result = prime * result + ((m_color == null) ? 0 : m_color.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "FillAttributes [color=" + m_color + ", alpha=" + m_alpha
			+ ", fillRule=" + m_fillRule + ", xorMode=" + m_xorMode + "]";
	}

	public Color getColor() {
		return m_color;
	}

}
