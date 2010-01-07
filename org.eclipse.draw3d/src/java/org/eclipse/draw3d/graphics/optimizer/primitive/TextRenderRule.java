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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * TextRenderRule There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class TextRenderRule extends AbstractRenderRule {

	private int m_alpha;

	private Font m_font;

	private boolean m_fontAntialias;

	private Color m_textColor;

	public TextRenderRule(GraphicsState i_state) {

		m_font = i_state.getFont();
		m_textColor = i_state.getForegroundColor();
		m_alpha = i_state.getAlpha();
		m_fontAntialias = i_state.getAntialias() == SWT.ON;
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
		if (m_font == null) {
			if (other.m_font != null)
				return false;
		} else if (!m_font.equals(other.m_font))
			return false;
		if (m_fontAntialias != other.m_fontAntialias)
			return false;
		return true;
	}

	public int getAlpha() {
		return m_alpha;
	}

	public Font getFont() {

		return m_font;
	}

	public Color getTextColor() {
		return m_textColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_font == null) ? 0 : m_font.hashCode());
		result = prime * result + (m_fontAntialias ? 1231 : 1237);
		return result;
	}

	public boolean isFontAntialias() {

		return m_fontAntialias;
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

	@Override
	public String toString() {
		return "TextRenderRule [m_alpha=" + m_alpha + ", m_font=" + m_font
			+ ", m_fontAntialias=" + m_fontAntialias + ", m_textColor="
			+ m_textColor + "]";
	}
}
