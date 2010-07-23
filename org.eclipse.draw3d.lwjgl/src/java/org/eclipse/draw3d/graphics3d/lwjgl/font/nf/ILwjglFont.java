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

/**
 * A Draw3D font allows rendering of text using the rendering subsystem. The
 * actual implementation depends on the currently used renderer.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.07.2010
 */
public interface ILwjglFont {

	/**
	 * Disposes all resources used by this font.
	 */
	public void dispose();

	/**
	 * Returns the font info object for this font.
	 * 
	 * @return the font info object
	 */
	public LwjglFontInfo getFontInfo();

	/**
	 * Initializes this font.
	 */
	public void initialize();

	/**
	 * Renders the given string.
	 * 
	 * @param string the string to render
	 */
	public void render(String string);
}
