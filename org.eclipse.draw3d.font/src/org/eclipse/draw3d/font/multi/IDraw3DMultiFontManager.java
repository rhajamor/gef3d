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
 * Manages {@link IDraw3DMultiFont} instances.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public interface IDraw3DMultiFontManager {
	/**
	 * Disposes all resources associated with this font manager.
	 */
	public void dispose();

	/**
	 * Returns an {@link IDraw3DMultiFont} instance with the given parameters.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_flags the font flags
	 * @return the {@link IDraw3DMultiFont} instance
	 * @throws NullPointerException if the given name or the given flag array is
	 *             <code>null</code>
	 * @throws IllegalArgumentException if the given font size is not positive
	 * @throws IllegalStateException if this font manager is disposed
	 */
	public IDraw3DMultiFont getFont(String i_name, int i_size, Flag... i_flags);
}
