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
public class FillAttributes extends Attributes {

	private Color m_color;

	private int m_fillRule;

	private boolean m_xorMode;

	public Color getColor() {
		return m_color;
	}

	public int getFillRule() {
		return m_fillRule;
	}

	public boolean isXorMode() {
		return m_xorMode;
	}

	public FillAttributes(GraphicsState i_state) {

		super(i_state);
		m_color = i_state.getBackgroundColor();
		m_fillRule = i_state.getFillRule();
		m_xorMode = i_state.getXORMode();
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

		FillAttributes other = (FillAttributes) i_obj;

		if (m_fillRule != other.m_fillRule)
			return false;

		if (m_xorMode != other.m_xorMode)
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
		result = prime * result + m_fillRule;
		result = prime * result + (m_xorMode ? 1231 : 1237);
		return result;
	}

}
