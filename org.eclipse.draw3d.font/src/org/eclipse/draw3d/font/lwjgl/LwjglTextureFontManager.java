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
package org.eclipse.draw3d.font.lwjgl;

import java.util.Arrays;

import org.eclipse.draw3d.font.simple.FontManagerBase;
import org.eclipse.draw3d.font.simple.IDraw3DFont;
import org.eclipse.draw3d.font.simple.IDraw3DFont.Flag;

/**
 * Manages LWJGL texture fonts.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public class LwjglTextureFontManager extends FontManagerBase {

	private static class Key {
		private String m_name;

		private int m_size;

		private Flag[] m_flags;

		private int m_hashCode;

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
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return m_hashCode;
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
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.FontManagerBase#createFont(java.lang.String,
	 *      int, float, org.eclipse.draw3d.font.simple.IDraw3DFont.Flag[])
	 */
	@Override
	protected IDraw3DFont createFont(String i_name, int i_size,
		float i_precision, Flag... i_flags) {
		return new LwjglTextureFont(i_name, i_size, i_flags);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.FontManagerBase#getKey(java.lang.String,
	 *      int, float, org.eclipse.draw3d.font.simple.IDraw3DFont.Flag[])
	 */
	@Override
	protected Object getKey(String i_name, int i_size, float i_precision,
		Flag... i_flags) {
		return new Key(i_name, i_size, i_flags);
	}

}
