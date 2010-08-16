/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.font;

import java.awt.Font;

/**
 * AWTBasedFont There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public abstract class AWTBasedFont implements IDraw3DFont {

	private static final char DEF_FIRST_CHAR = ' ';

	private static final int DEF_NUM_CHARS = 96;

	private char m_firstChar = Character.MAX_VALUE;

	private Font m_font;

	private char m_lastChar = Character.MIN_VALUE;

	public AWTBasedFont(String i_name, int i_size, Flag... i_flags) {
		if (i_name == null)
			throw new NullPointerException("i_name must not be null");

		m_font = new Font(i_name, Flag.getAWTStyle(i_flags), i_size);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DFont#createGlyphVector(java.lang.String)
	 */
	public IDraw3DGlyphVector createGlyphVector(String i_string) {
		if (i_string == null)
			throw new NullPointerException("i_string must not be null");

		if (i_string.length() == 0)
			return null;

		return doCreateGlyphVector(i_string);
	}

	protected abstract IDraw3DGlyphVector doCreateGlyphVector(String i_string);

	protected Font getAwtFont() {
		return m_font;
	}
}
