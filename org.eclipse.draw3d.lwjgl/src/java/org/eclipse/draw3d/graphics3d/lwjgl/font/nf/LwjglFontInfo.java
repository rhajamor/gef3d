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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * This class contains meta-information for a given Draw3D font.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 20.07.2010
 */
public class LwjglFontInfo {

	private boolean m_antialiased;

	private boolean m_bold;

	private int m_hashCode;

	private int m_height;

	private boolean m_italic;

	private String m_name;

	/**
	 * Creates a new font info object for the given SWT font.
	 * 
	 * @param i_font the SWT font
	 * @param i_antialiased whether the font is antialiased
	 * @throws NullPointerException if the given SWT font is <code>null</code>
	 */
	public LwjglFontInfo(Font i_font, boolean i_antialiased) {

		this(i_font.getFontData()[0].getName(),
			i_font.getFontData()[0].getHeight(),
			(i_font.getFontData()[0].getStyle() & SWT.BOLD) != 0,
			(i_font.getFontData()[0].getStyle() & SWT.ITALIC) != 0,
			i_antialiased);

	}

	/**
	 * Creates a new font info object for the given data.
	 * 
	 * @param i_name the font name
	 * @param i_height the font height
	 * @param i_bold whether the font is bold
	 * @param i_italic whether the font is italic
	 * @param i_antialiased whether the font is antialiased
	 */
	public LwjglFontInfo(String i_name, int i_height, boolean i_bold,
			boolean i_italic, boolean i_antialiased) {

		if (i_name == null)
			throw new NullPointerException("i_name must not be null");

		m_name = i_name;
		m_height = i_height;
		m_bold = i_bold;
		m_italic = i_italic;
		m_antialiased = i_antialiased;

		m_hashCode = 17;
		m_hashCode = 37 * m_height;
		m_hashCode = 37 * m_hashCode + m_name.hashCode();
		m_hashCode = 37 * m_hashCode + (m_antialiased ? 1 : 0);
		m_hashCode = 37 * m_hashCode + (m_bold ? 1 : 0);
		m_hashCode = 37 * m_hashCode + (m_italic ? 1 : 0);
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

		if (!(i_obj instanceof LwjglFontInfo))
			return false;

		LwjglFontInfo info = (LwjglFontInfo) i_obj;
		if (getHeight() != info.getHeight())
			return false;

		if (!getName().equalsIgnoreCase(info.getName()))
			return false;

		if (isAntialiased() != info.isAntialiased())
			return false;

		if (isBold() != info.isBold())
			return false;

		if (isItalic() != info.isItalic())
			return false;

		return true;
	}

	/**
	 * Returns the font height.
	 * 
	 * @return the font height
	 */
	public int getHeight() {
		return m_height;
	}

	/**
	 * Returns the font name.
	 * 
	 * @return the font name
	 */
	public String getName() {
		return m_name;
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
	 * Indicates whether the font is antialiased.
	 * 
	 * @return <code>true</code> if the font is antialiased and
	 *         <code>false</code> otherwise
	 */
	public boolean isAntialiased() {
		return m_antialiased;
	}

	/**
	 * Indicates whether the font is bold.
	 * 
	 * @return <code>true</code> if the font is bold and <code>false</code>
	 *         otherwise
	 */
	public boolean isBold() {
		return m_bold;
	}

	/**
	 * Indicates whether the font is italic.
	 * 
	 * @return <code>true</code> if the font is italic and <code>false</code>
	 *         otherwise
	 */
	public boolean isItalic() {
		return m_italic;
	}
}
