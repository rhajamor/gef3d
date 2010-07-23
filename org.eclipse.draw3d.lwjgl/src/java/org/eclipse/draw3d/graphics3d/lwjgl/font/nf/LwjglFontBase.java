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
package org.eclipse.draw3d.graphics3d.lwjgl.font.nf;

import java.awt.Font;

/**
 * Base implementation for LWJGL fonts.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.07.2010
 */
public abstract class LwjglFontBase implements ILwjglFont {

	private LwjglFontInfo m_fontInfo;

	/**
	 * Creates a new glyph vector that renders the given string.
	 * 
	 * @param i_string the string to render
	 * @return the glyph vector
	 */
	protected abstract ILwjglGlyphVector createGlyphVector(String i_string);

	/**
	 * Returns an AWT font that corresponds to this font.
	 * 
	 * @return the AWT font
	 */
	protected Font getAwtFont() {
		String name = getFontInfo().getName();
		int height = getFontInfo().getHeight();
		int style =
			(getFontInfo().isBold() ? Font.BOLD : 0)
				& (getFontInfo().isItalic() ? Font.ITALIC : 0);

		return new Font(name, style, height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.font.nf.ILwjglFont#getFontInfo()
	 */
	public LwjglFontInfo getFontInfo() {
		return m_fontInfo;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.font.nf.ILwjglFont#render(java.lang.String)
	 */
	public void render(String i_string) {
		if (i_string == null)
			throw new NullPointerException("i_string must not be null");

		ILwjglGlyphVector glyphVector = createGlyphVector(i_string);
		glyphVector.render();
	}

}
