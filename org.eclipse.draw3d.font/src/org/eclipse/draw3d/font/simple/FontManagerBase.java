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
package org.eclipse.draw3d.font.simple;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw3d.font.simple.IDraw3DFont.Flag;

/**
 * A base class for simple font managers.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public abstract class FontManagerBase implements IDraw3DFontManager {

	private boolean m_disposed = false;

	private Map<Object, IDraw3DFont> m_fonts =
		new HashMap<Object, IDraw3DFont>();

	/**
	 * Creates a new font with the given parameters.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_precision the precision
	 * @param i_flags the font flags
	 * @return the newly created font
	 */
	protected abstract IDraw3DFont createFont(String i_name, int i_size,
		float i_precision, Flag... i_flags);

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.IDraw3DFontManager#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		for (IDraw3DFont font : m_fonts.values())
			font.dispose();

		m_fonts = null;
		m_disposed = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.IDraw3DFontManager#getFont(java.lang.String,
	 *      int, float, org.eclipse.draw3d.font.simple.IDraw3DFont.Flag[])
	 */
	public IDraw3DFont getFont(String i_name, int i_size, float i_precision,
		Flag... i_flags) {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		if (i_name == null)
			throw new NullPointerException("i_name must not be null");
		if (i_flags == null)
			throw new NullPointerException("i_flags must not be null");
		if (i_size <= 0)
			throw new IllegalArgumentException(
				"font size must be a positive integer");
		if (i_precision < 0 || i_precision > 1)
			throw new IllegalArgumentException(
				"precision must be between 0 and 1");

		Object key = getKey(i_name, i_size, i_precision, i_flags);
		IDraw3DFont font = m_fonts.get(key);
		if (font == null) {
			font = createFont(i_name, i_size, i_precision, i_flags);
			m_fonts.put(key, font);
		}

		return font;
	}

	/**
	 * Returns a key for a font with the given parameters.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_precision the precision value
	 * @param i_flags the font flags
	 * @return the font key
	 */
	protected abstract Object getKey(String i_name, int i_size,
		float i_precision, Flag... i_flags);
}
