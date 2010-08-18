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

import java.awt.Font;
import java.awt.font.FontRenderContext;

/**
 * Base class for all fonts that use an AWT font to create their font data.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public abstract class AwtBasedFont implements IDraw3DFont {

	private Font m_font;

	/**
	 * Creates a new instance with the given parameters.
	 * 
	 * @param i_name the name of the font
	 * @param i_size the font size
	 * @param i_flags the {@link Flag} array
	 */
	public AwtBasedFont(String i_name, int i_size, Flag... i_flags) {
		if (i_name == null)
			throw new NullPointerException("i_name must not be null");

		m_font = new Font(i_name, Flag.getAWTStyle(i_flags), i_size);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.IDraw3DFont#createText(java.lang.String)
	 */
	public IDraw3DText createText(String i_string) {
		if (i_string == null || i_string.trim().length() == 0)
			return EmptyText.INSTANCE;

		FontRenderContext ctx = new FontRenderContext(null, true, true);
		return doCreateText(i_string, ctx);
	}

	/**
	 * Creates an instanceof {@link IDraw3DText} that renders the given string,
	 * which is guaranteed to not be <code>null</code> or empty.
	 * 
	 * @param i_string the string, which is neither <code>null</code> or empty
	 * @param i_context the font rendering context
	 * @return the {@link IDraw3DText} instance
	 */
	protected abstract IDraw3DText doCreateText(String i_string,
		FontRenderContext i_context);

	/**
	 * Returns the AWT font which this font is based on.
	 * 
	 * @return the AWT font
	 */
	protected Font getAwtFont() {
		return m_font;
	}
}
