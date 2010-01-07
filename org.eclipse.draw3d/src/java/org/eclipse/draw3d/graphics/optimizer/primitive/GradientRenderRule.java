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
 * GradientRenderRule There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class GradientRenderRule extends AbstractRenderRule {

	private int m_alpha;

	private int m_fillRule;

	private Color m_fromColor;

	private Color m_toColor;

	private boolean m_xorMode;

	public GradientRenderRule(GraphicsState i_state) {

		m_alpha = i_state.getAlpha();
		m_fillRule = i_state.getFillRule();
		m_fromColor = i_state.getForegroundColor();
		m_toColor = i_state.getBackgroundColor();
		m_xorMode = i_state.getXORMode();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#asGradient()
	 */
	@Override
	public GradientRenderRule asGradient() {
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
		GradientRenderRule other = (GradientRenderRule) obj;
		if (m_alpha != other.m_alpha)
			return false;
		if (m_fillRule != other.m_fillRule)
			return false;
		if (m_fromColor == null) {
			if (other.m_fromColor != null)
				return false;
		} else if (!m_fromColor.equals(other.m_fromColor))
			return false;
		if (m_toColor == null) {
			if (other.m_toColor != null)
				return false;
		} else if (!m_toColor.equals(other.m_toColor))
			return false;
		if (m_xorMode != other.m_xorMode)
			return false;
		return true;
	}

	public int getAlpha() {
		return m_alpha;
	}

	public int getFillRule() {
		return m_fillRule;
	}

	public Color getFromColor() {
		return m_fromColor;
	}

	public Color getToColor() {
		return m_toColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_alpha;
		result = prime * result + m_fillRule;
		result =
			prime * result
				+ ((m_fromColor == null) ? 0 : m_fromColor.hashCode());
		result =
			prime * result + ((m_toColor == null) ? 0 : m_toColor.hashCode());
		result = prime * result + (m_xorMode ? 1231 : 1237);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#isGradient()
	 */
	@Override
	public boolean isGradient() {
		return true;
	}

	public boolean isXorMode() {
		return m_xorMode;
	}

	@Override
	public String toString() {
		return "GradientRenderRule [m_alpha=" + m_alpha + ", m_fillRule="
			+ m_fillRule + ", m_fromColor=" + m_fromColor + ", m_toColor="
			+ m_toColor + ", m_xorMode=" + m_xorMode + "]";
	}
}
