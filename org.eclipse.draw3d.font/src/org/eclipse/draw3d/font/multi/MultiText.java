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
package org.eclipse.draw3d.font.multi;

import java.util.logging.Logger;

import org.eclipse.draw3d.font.simple.IDraw3DFont;
import org.eclipse.draw3d.font.simple.IDraw3DText;
import org.eclipse.draw3d.font.simple.IDraw3DFont.Flag;

/**
 * A renderable text that encapsulates several fonts for the different degrees
 * of the LOD. The LOD value is quantized to steps of 0.1. If the result is less
 * or equal to the {@link #TEXTURE_FONT_TH} threshold, a texture font is used,
 * otherwise, a vector font is used. The precision parameter for the vector font
 * is used by scaling the LOD value (which is between {@link #TEXTURE_FONT_TH}
 * and 1) to 0 to 1 range.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public class MultiText implements IDraw3DMultiText {

	private static final float TEXTURE_FONT_TH = 0.09f;

	private IDraw3DText[] m_cache = new IDraw3DText[11];

	private boolean m_disposed = false;

	private Flag[] m_fontFlags;

	private MultiFontManager m_fontManager;

	private String m_fontName;

	private int m_fontSize;

	private String m_string;

	/**
	 * Creates a new instance with the given parameters that renders the given
	 * string.f
	 * 
	 * @param i_string the string to render
	 * @param i_fontManager the font manager
	 * @param i_fontName the font name
	 * @param i_fontSize the font size
	 * @param i_fontFlags the font flags
	 * @throws NullPointerException if the given string, font manager, font name
	 *             or flag array is <code>null</code>
	 * @throws IllegalArgumentException if the given font size is not positive
	 */
	public MultiText(String i_string, MultiFontManager i_fontManager,
			String i_fontName, int i_fontSize, Flag... i_fontFlags) {
		if (i_string == null)
			throw new NullPointerException("i_string must not be null");
		if (i_fontManager == null)
			throw new NullPointerException("i_fontManager must not be null");
		if (i_fontName == null)
			throw new NullPointerException("i_fontName must not be null");
		if (i_fontFlags == null)
			throw new NullPointerException("i_fontFlags must not be null");
		if (i_fontSize <= 0)
			throw new IllegalArgumentException("font size must be positive");

		m_string = i_string;
		m_fontManager = i_fontManager;
		m_fontName = i_fontName;
		m_fontSize = i_fontSize;
		m_fontFlags = i_fontFlags;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiText#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		for (IDraw3DText text : m_cache)
			if (text != null)
				text.dispose();

		m_cache = null;
		m_fontFlags = null;
		m_fontName = null;
		m_string = null;
		m_fontManager = null;

		m_disposed = true;
	}

	private int index(float i_scaledLod) {
		return (int) i_scaledLod * 10;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiText#render(float)
	 */
	public void render(float i_lod) {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		float sl = scaleLod(i_lod);
		int i = index(sl);

		IDraw3DText text = m_cache[i];
		if (text == null) {
			IDraw3DFont font = selectFont(sl);
			text = font.createText(m_string);
			m_cache[i] = text;
		}

		text.render();
	}

	private float scaleLod(float i_lod) {
		return (float) Math.floor(i_lod * 10) / 10;
	}

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(MultiText.class.getName());

	private IDraw3DFont selectFont(float i_sl) {
		if (i_sl <= TEXTURE_FONT_TH)
			return m_fontManager.getTextureFont(m_fontName, m_fontSize,
				m_fontFlags);

		float p = 1 - (i_sl - TEXTURE_FONT_TH) / (1 - TEXTURE_FONT_TH);
		return m_fontManager.getVectorFont(m_fontName, m_fontSize, p,
			m_fontFlags);
	}
}
