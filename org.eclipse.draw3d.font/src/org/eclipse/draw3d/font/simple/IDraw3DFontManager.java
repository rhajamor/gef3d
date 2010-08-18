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

import org.eclipse.draw3d.font.simple.IDraw3DFont.Flag;

/**
 * Manages simple fonts.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public interface IDraw3DFontManager {
	/**
	 * Disposes all resources associated with this font manager.
	 */
	public void dispose();

	/**
	 * Returns a font with the given parameters.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_precision the precision value
	 * @param i_flags the font flags
	 * @return the font
	 * @throws IllegalStateException if this font manager is disposed
	 */
	public IDraw3DFont getFont(String i_name, int i_size, float i_precision,
		Flag... i_flags);
}
