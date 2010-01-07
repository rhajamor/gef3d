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
package org.eclipse.draw3d.graphics.optimizer.primitive;

import org.eclipse.draw3d.graphics.GraphicsState;
import org.eclipse.swt.graphics.Color;

/**
 * SolidRenderRule There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class SolidRenderRule extends AbstractRenderRule {

	private int m_alpha;

	private Color m_color;

	private int m_fillRule;

	private boolean m_xorMode;

	public SolidRenderRule(GraphicsState i_state) {

		m_alpha = i_state.getAlpha();
		m_color = i_state.getBackgroundColor();
		m_fillRule = i_state.getFillRule();
		m_xorMode = i_state.getXORMode();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#asSolid()
	 */
	@Override
	public SolidRenderRule asSolid() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolidRenderRule other = (SolidRenderRule) obj;
		if (m_alpha != other.m_alpha)
			return false;
		if (m_color == null) {
			if (other.m_color != null)
				return false;
		} else if (!m_color.equals(other.m_color))
			return false;
		if (m_fillRule != other.m_fillRule)
			return false;
		if (m_xorMode != other.m_xorMode)
			return false;
		return true;
	}

	public int getAlpha() {
		return m_alpha;
	}

	public Color getColor() {
		return m_color;
	}

	public int getFillRule() {
		return m_fillRule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_alpha;
		result = prime * result + ((m_color == null) ? 0 : m_color.hashCode());
		result = prime * result + m_fillRule;
		result = prime * result + (m_xorMode ? 1231 : 1237);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#isSolid()
	 */
	@Override
	public boolean isSolid() {
		return true;
	}

	public boolean isXorMode() {
		return m_xorMode;
	}

	@Override
	public String toString() {
		return "SolidRenderRule [m_alpha=" + m_alpha + ", m_color=" + m_color
			+ ", m_fillRule=" + m_fillRule + ", m_xorMode=" + m_xorMode + "]";
	}
}
