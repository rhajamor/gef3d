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
 * GradientAttributes There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.12.2009
 */
public class GradientAttributes extends FillAttributes {

	private Color m_gradientColor;

	public GradientAttributes(GraphicsState i_state) {

		super(i_state);
		m_gradientColor = i_state.getForegroundColor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.FillAttributes#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object i_obj) {

		if (!super.equals(i_obj))
			return false;

		GradientAttributes other = (GradientAttributes) i_obj;
		if (m_gradientColor == null) {
			if (other.m_gradientColor != null)
				return false;
		} else if (!m_gradientColor.equals(other.m_gradientColor))
			return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.FillAttributes#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result =
			prime * result
				+ ((m_gradientColor == null) ? 0 : m_gradientColor.hashCode());
		return result;
	}
}
