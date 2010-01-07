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

/**
 * ImageRenderRule There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class ImageRenderRule extends AbstractRenderRule {

	private int m_alpha;

	public ImageRenderRule(GraphicsState i_state) {

		m_alpha = i_state.getAlpha();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#asImage()
	 */
	@Override
	public ImageRenderRule asImage() {
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
		ImageRenderRule other = (ImageRenderRule) obj;
		if (m_alpha != other.m_alpha)
			return false;
		return true;
	}

	public int getAlpha() {
		return m_alpha;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_alpha;
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractRenderRule#isImage()
	 */
	@Override
	public boolean isImage() {
		return true;
	}

	@Override
	public String toString() {
		return "ImageRenderRule [m_alpha=" + m_alpha + "]";
	}
}
