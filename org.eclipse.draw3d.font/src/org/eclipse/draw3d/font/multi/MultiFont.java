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

import org.eclipse.draw3d.font.simple.IDraw3DFont.Flag;

/**
 * An implementation of {@link IDraw3DMultiFont} that creates instances of
 * {@link MultiText}.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public class MultiFont implements IDraw3DMultiFont {

	private boolean m_disposed = false;

	private Flag[] m_flags;

	private MultiFontManager m_fontManager;

	private String m_name;

	private int m_size;

	/**
	 * Creates a new instance with the given parameters.
	 * 
	 * @param i_fontManager the multi font manager to use
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_flags the font flags
	 * @throws NullPointerException if the given font manager, the given name or
	 *             the given flag array is <code>null</code>
	 * @throws IllegalArgumentException if the given size is not positive
	 */
	public MultiFont(MultiFontManager i_fontManager, String i_name, int i_size,
			Flag[] i_flags) {
		if (i_fontManager == null)
			throw new NullPointerException("i_fontManager must not be null");
		if (i_name == null)
			throw new NullPointerException("i_name must not be null");
		if (i_flags == null)
			throw new NullPointerException("i_flags must not be null");
		if (i_size <= 0)
			throw new IllegalArgumentException("font size must be positive");

		m_fontManager = i_fontManager;
		m_name = i_name;
		m_size = i_size;
		m_flags = i_flags;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiFont#createText(java.lang.String)
	 */
	public IDraw3DMultiText createText(String i_string) {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		return new MultiText(i_string, m_fontManager, m_name, m_size, m_flags);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.IDraw3DMultiFont#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		m_fontManager = null;
		m_name = null;
		m_flags = null;

		m_disposed = true;
	}

}
