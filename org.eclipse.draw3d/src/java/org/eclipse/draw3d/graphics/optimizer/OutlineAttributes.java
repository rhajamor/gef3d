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

import java.util.Arrays;

import org.eclipse.draw3d.graphics.GraphicsState;

/**
 * OutlineAttributes There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.12.2009
 */
public class OutlineAttributes extends Attributes {

	private int m_lineCap;

	private int[] m_lineDash;

	private int m_lineJoin;

	private int m_lineStyle;

	private float m_lineWidth;

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "OutlinerAttributes [color=" + m_color + ", alpha=" + m_alpha
			+ ", lineWidth=" + m_lineWidth + ", lineJoin=" + m_lineJoin
			+ ", lineCap=" + m_lineCap + ", lineDash=" + m_lineDash + "]";
	}

	public OutlineAttributes(GraphicsState i_state) {

		super(i_state);
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object i_obj) {

		if (!super.equals(i_obj))
			return false;

		OutlineAttributes other = (OutlineAttributes) i_obj;

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

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + m_lineCap;
		result = prime * result + Arrays.hashCode(m_lineDash);
		result = prime * result + m_lineJoin;
		result = prime * result + m_lineStyle;
		result = prime * result + Float.floatToIntBits(m_lineWidth);
		return result;
	}

}
