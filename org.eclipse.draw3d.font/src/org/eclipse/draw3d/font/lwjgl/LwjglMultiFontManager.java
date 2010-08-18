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

import org.eclipse.draw3d.font.multi.MultiFontManager;
import org.eclipse.draw3d.font.simple.IDraw3DFontManager;

/**
 * Manages LWJGL multi fonts.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public class LwjglMultiFontManager extends MultiFontManager {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.MultiFontManager#createTextureFontManager()
	 */
	@Override
	protected IDraw3DFontManager createTextureFontManager() {
		return new LwjglTextureFontManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.multi.MultiFontManager#createVectorFontManager()
	 */
	@Override
	protected IDraw3DFontManager createVectorFontManager() {
		return new LwjglVectorFontManager();
	}
}
