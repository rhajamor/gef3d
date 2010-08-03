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
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.text.CharacterIterator;

/**
 * AWTBasedFont There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public abstract class AWTBasedFont implements IDraw3DFont {

	private static class RangeCharacterIterator implements CharacterIterator {

		private char m_beginIndex;

		private int m_currentIndex;

		private int m_endIndex;

		public RangeCharacterIterator(char i_firstChar, char i_lastChar) {
			m_beginIndex = i_firstChar;
			m_endIndex = i_lastChar;
			m_currentIndex = m_beginIndex;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#current()
		 */
		public char current() {
			if (m_currentIndex < getBeginIndex()
				|| m_currentIndex >= getEndIndex())
				return DONE;

			return (char) m_currentIndex;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#first()
		 */
		public char first() {
			m_currentIndex = getBeginIndex();
			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#getBeginIndex()
		 */
		public int getBeginIndex() {
			return m_beginIndex;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#getEndIndex()
		 */
		public int getEndIndex() {
			return m_endIndex;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#getIndex()
		 */
		public int getIndex() {
			return m_currentIndex;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#last()
		 */
		public char last() {
			m_currentIndex = getEndIndex() - 1;
			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#next()
		 */
		public char next() {
			m_currentIndex++;
			if (m_currentIndex >= getEndIndex())
				return DONE;

			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#previous()
		 */
		public char previous() {
			m_currentIndex--;
			if (m_currentIndex < getBeginIndex())
				return DONE;

			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#setIndex(int)
		 */
		public char setIndex(int i_index) {
			if (i_index < getBeginIndex() || i_index > getEndIndex())
				throw new IllegalArgumentException("index is out of bounds: "
					+ i_index);

			m_currentIndex = i_index;
			return current();
		}
	}

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

		char l = i_string.charAt(0);
		char h = l;
		for (int i = 1; i < i_string.length(); i++) {
			char c = i_string.charAt(i);
			if (c < l)
				l = c;
			else if (c > h)
				h = c;
		}

		updateFontData(l, h);
		return doCreateGlyphVector(i_string);
	}

	protected abstract IDraw3DGlyphVector doCreateGlyphVector(String i_string);

	protected abstract void doUpdateFontData(GlyphVector i_glyphs,
		char i_newFirst, char i_newLast, char i_oldFirst, char i_oldLast);

	protected char getFirstChar() {
		return m_firstChar;
	}

	protected char getLastChar() {
		return m_lastChar;
	}

	protected Font getAwtFont() {
		return m_font;
	}

	public void initialize() {
		char lastChar = (char) (DEF_FIRST_CHAR + DEF_NUM_CHARS);
		updateFontData(DEF_FIRST_CHAR, lastChar);
	}

	private void updateFontData(char i_firstChar, char i_lastChar) {

		char f = (char) Math.min(m_firstChar, i_firstChar);
		char l = (char) Math.max(m_lastChar, i_lastChar);

		if (f >= m_firstChar && l <= m_lastChar)
			return;

		FontRenderContext ctx = new FontRenderContext(null, true, true);
		RangeCharacterIterator ci = new RangeCharacterIterator(f, l);
		GlyphVector glyphs = m_font.createGlyphVector(ctx, ci);

		doUpdateFontData(glyphs, f, l, m_firstChar, m_lastChar);

		m_firstChar = f;
		m_lastChar = l;
	}
}
