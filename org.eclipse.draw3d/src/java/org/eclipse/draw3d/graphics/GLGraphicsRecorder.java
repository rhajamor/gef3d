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
package org.eclipse.draw3d.graphics;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.LineAttributes;

/**
 * Saves fonts and custom dash patterns so that they can be initialized before
 * the recorded method calls are played back.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 06.11.2009
 */
public class GLGraphicsRecorder extends GraphicsRecorder {

	private Set<int[]> m_dashes = new HashSet<int[]>();

	private Set<Font> m_fonts = new HashSet<Font>();

	private void saveLineDash() {

		int[] lineDash = getState().getLineDash();
		if (lineDash != null)
			m_dashes.add(lineDash);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.GraphicsRecorder#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(Font i_f) {

		super.setFont(i_f);

		if (i_f != null)
			m_fonts.add(i_f);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.GraphicsRecorder#setLineAttributes(org.eclipse.swt.graphics.LineAttributes)
	 */
	@Override
	public void setLineAttributes(LineAttributes i_attributes) {

		super.setLineAttributes(i_attributes);
		saveLineDash();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.GraphicsRecorder#setLineDash(float[])
	 */
	@Override
	public void setLineDash(float[] i_dash) {

		super.setLineDash(i_dash);
		saveLineDash();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.GraphicsRecorder#setLineDash(int[])
	 */
	@Override
	public void setLineDash(int[] i_dash) {

		super.setLineDash(i_dash);
		saveLineDash();
	}
}
