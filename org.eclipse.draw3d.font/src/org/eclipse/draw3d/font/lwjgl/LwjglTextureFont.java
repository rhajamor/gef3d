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

import java.awt.image.BufferedImage;

import org.eclipse.draw3d.font.simple.AwtBasedTextureFont;
import org.eclipse.draw3d.font.simple.IDraw3DText;

/**
 * A font that renders text using textures created by rendering AWT font onto an
 * image.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 17.08.2010
 */
public class LwjglTextureFont extends AwtBasedTextureFont {

	/**
	 * Creates a new instance.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_flags the flags
	 */
	public LwjglTextureFont(String i_name, int i_size, Flag[] i_flags) {
		super(i_name, i_size, i_flags);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.AwtBasedTextureFont#doCreateText(java.awt.image.BufferedImage)
	 */
	@Override
	protected IDraw3DText doCreateText(BufferedImage i_image) {
		return new LwjglTextureText(i_image);
	}
}
