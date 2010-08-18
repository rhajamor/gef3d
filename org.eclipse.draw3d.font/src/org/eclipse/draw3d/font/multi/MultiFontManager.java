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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw3d.font.simple.IDraw3DFont;
import org.eclipse.draw3d.font.simple.IDraw3DFontManager;
import org.eclipse.draw3d.font.simple.IDraw3DFont.Flag;

/**
 * An implementation of {@link IDraw3DMultiFontManager} that manages instances
 * of {@link MultiFont}.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public abstract class MultiFontManager implements IDraw3DMultiFontManager {

	private static class Key {
		private Flag[] m_flags;

		private int m_hashCode;

		private String m_name;

		private int m_size;

		public Key(String i_name, int i_size, Flag... i_flags) {
			m_name = i_name.trim().toLowerCase();
			m_size = i_size;
			m_flags = i_flags;

			m_hashCode = 17;
			m_hashCode = 37 * m_hashCode + m_name.hashCode();
			m_hashCode = 37 * m_hashCode + m_size;
			m_hashCode = 37 * m_hashCode + Arrays.hashCode(m_flags);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object i_obj) {
			if (i_obj == null)
				return false;
			if (!(i_obj instanceof Key))
				return false;

			Key key = (Key) i_obj;
			if (!m_name.equals(key.m_name))
				return false;
			if (m_size != key.m_size)
				return false;
			if (!Arrays.equals(m_flags, key.m_flags))
				return false;

			return true;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return m_hashCode;
		}
	}

	private boolean m_disposed = false;

	private Map<Object, IDraw3DMultiFont> m_fonts =
		new HashMap<Object, IDraw3DMultiFont>();

	private IDraw3DFontManager m_textureFontManager =
		createTextureFontManager();

	private IDraw3DFontManager m_vectorFontManager = createVectorFontManager();

	/**
	 * Creates a new font manager for texture fonts.
	 * 
	 * @return the newly created font manager
	 */
	protected abstract IDraw3DFontManager createTextureFontManager();

	/**
	 * Creates a new font manager for vertex fonts.
	 * 
	 * @return the newly created font manager
	 */
	protected abstract IDraw3DFontManager createVectorFontManager();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiFontManager#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		m_disposed = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiFontManager#getFont(java.lang.String,
	 *      int, org.eclipse.draw3d.font.simple.IDraw3DFont.Flag[])
	 */
	public IDraw3DMultiFont getFont(String i_name, int i_size, Flag... i_flags) {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");
		if (i_name == null)
			throw new NullPointerException("i_name must not be null");
		if (i_flags == null)
			throw new NullPointerException("i_flags must not be null");
		if (i_size <= 0)
			throw new IllegalArgumentException("font size must be positive");

		Key key = new Key(i_name, i_size, i_flags);
		IDraw3DMultiFont font = m_fonts.get(key);
		if (font == null) {
			font = new MultiFont(this, i_name, i_size, i_flags);
			m_fonts.put(key, font);
		}

		return font;
	}

	/**
	 * Returns a texture font with the given parameters.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_flags the font flags
	 * @return the texture font
	 */
	IDraw3DFont getTextureFont(String i_name, int i_size, Flag... i_flags) {
		return m_textureFontManager.getFont(i_name, i_size, 1, i_flags);
	}

	/**
	 * Returns a vector font with the given parameters.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_precision the vector data precision value
	 * @param i_flags the font flags
	 * @return the vector font
	 */
	IDraw3DFont getVectorFont(String i_name, int i_size, float i_precision,
		Flag... i_flags) {
		return m_vectorFontManager.getFont(i_name, i_size, i_precision, i_flags);
	}
}
