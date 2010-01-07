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

import java.util.Arrays;

import org.eclipse.draw3d.graphics.GraphicsState;
import org.eclipse.swt.graphics.Color;

/**
 * OutlineRenderRule There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class OutlineRenderRule extends AbstractRenderRule {

	private int m_alpha;

	private Color m_color;

	private int m_lineCap;

	private int[] m_lineDash;

	private int m_lineJoin;

	private int m_lineStyle;

	private float m_lineWidth;

	public OutlineRenderRule(GraphicsState i_state) {

		m_alpha = i_state.getAlpha();
		m_color = i_state.getForegroundColor();
		m_lineCap = i_state.getLineCap();
		m_lineDash = i_state.getLineDash();
		m_lineJoin = i_state.getLineJoin();
		m_lineStyle = i_state.getLineStyle();
		m_lineWidth = i_state.getLineWidth();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#asOutline()
	 */
	@Override
	public OutlineRenderRule asOutline() {
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
		OutlineRenderRule other = (OutlineRenderRule) obj;
		if (m_alpha != other.m_alpha)
			return false;
		if (m_color == null) {
			if (other.m_color != null)
				return false;
		} else if (!m_color.equals(other.m_color))
			return false;
		if (m_lineCap != other.m_lineCap)
			return false;
		if (!Arrays.equals(m_lineDash, other.m_lineDash))
			return false;
		if (m_lineJoin != other.m_lineJoin)
			return false;
		if (m_lineStyle != other.m_lineStyle)
			return false;
		if (Float.floatToIntBits(m_lineWidth) != Float.floatToIntBits(other.m_lineWidth))
			return false;
		return true;
	}

	public int getAlpha() {

		return m_alpha;
	}

	public Color getColor() {

		return m_color;
	}

	public int getLineCap() {

		return m_lineCap;
	}

	public int[] getLineDash() {

		return m_lineDash;
	}

	public int getLineJoin() {

		return m_lineJoin;
	}

	public int getLineStyle() {

		return m_lineStyle;
	}

	public float getLineWidth() {

		return m_lineWidth;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_alpha;
		result = prime * result + ((m_color == null) ? 0 : m_color.hashCode());
		result = prime * result + m_lineCap;
		result = prime * result + Arrays.hashCode(m_lineDash);
		result = prime * result + m_lineJoin;
		result = prime * result + m_lineStyle;
		result = prime * result + Float.floatToIntBits(m_lineWidth);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#isOutline()
	 */
	@Override
	public boolean isOutline() {
		return true;
	}

	@Override
	public String toString() {
		return "OutlineRenderRule [m_alpha=" + m_alpha + ", m_color=" + m_color
			+ ", m_lineCap=" + m_lineCap + ", m_lineDash="
			+ Arrays.toString(m_lineDash) + ", m_lineJoin=" + m_lineJoin
			+ ", m_lineStyle=" + m_lineStyle + ", m_lineWidth=" + m_lineWidth
			+ "]";
	}
}
