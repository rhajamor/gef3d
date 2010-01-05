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
 * TextRenderRule There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class TextRenderRule extends AbstractRenderRule {

	private int m_alpha;

	private Color m_textColor;

	public TextRenderRule(GraphicsState i_state) {

		m_textColor = i_state.getForegroundColor();
		m_alpha = i_state.getAlpha();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#asText()
	 */
	@Override
	public TextRenderRule asText() {
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
		TextRenderRule other = (TextRenderRule) obj;
		if (m_alpha != other.m_alpha)
			return false;
		if (m_textColor == null) {
			if (other.m_textColor != null)
				return false;
		} else if (!m_textColor.equals(other.m_textColor))
			return false;
		return true;
	}

	public int getAlpha() {
		return m_alpha;
	}

	public Color getTextColor() {
		return m_textColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_alpha;
		result =
			prime * result
				+ ((m_textColor == null) ? 0 : m_textColor.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#isText()
	 */
	@Override
	public boolean isText() {
		return true;
	}
}
