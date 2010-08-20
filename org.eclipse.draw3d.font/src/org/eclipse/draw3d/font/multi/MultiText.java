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

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(MultiText.class.getName());

	/**
	 * If the LOD value is smaller than {@link #VECTOR_FONT_TH} and greater or
	 * equal to this value, texture fonts are used.
	 */
	private static final float TEXTURE_FONT_TH = 0.20f;

	private static final float VECTOR_FONT_PREC = 1f;

	/**
	 * If the LOD value is greater or equal to this value, vector fonts are
	 * used.
	 */
	private static final float VECTOR_FONT_TH = 0.87f;

	private boolean m_disposed = false;

	private IDraw3DText m_textureText;

	private IDraw3DText m_vectorText;

	/**
	 * Creates a new instance with the given parameters that renders the given
	 * string.
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

		IDraw3DFont textureFont =
			i_fontManager.getTextureFont(i_fontName, i_fontSize, i_fontFlags);
		m_textureText = textureFont.createText(i_string);

		IDraw3DFont vectorFont =
			i_fontManager.getVectorFont(i_fontName, i_fontSize,
				VECTOR_FONT_PREC, i_fontFlags);
		m_vectorText = vectorFont.createText(i_string);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiText#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		if (m_textureText != null) {
			m_textureText.dispose();
			m_textureText = null;
		}

		if (m_vectorText != null) {
			m_vectorText.dispose();
			m_vectorText = null;
		}

		m_disposed = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiText#getHeight()
	 */
	public float getHeight() {
		return m_textureText.getHeight();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiText#getWidth()
	 */
	public float getWidth() {
		return m_vectorText.getWidth();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiText#render(float)
	 */
	public void render(float i_lod) {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		if (i_lod >= VECTOR_FONT_TH)
			m_vectorText.render();
		else if (i_lod >= TEXTURE_FONT_TH)
			m_textureText.render();
	}
}
