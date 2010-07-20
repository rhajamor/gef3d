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

import org.eclipse.draw3d.graphics3d.ILodHelper;
import org.eclipse.swt.graphics.Font;

/**
 * Manages fonts for the Draw3D subsystem.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 20.07.2010
 */
public interface IDraw3DFontManager {

	/**
	 * Returns a Draw3D font that emulates the given SWT font. Depending on the
	 * rendering platform, there may be several Draw3D fonts that can render a
	 * given SWT font.
	 * 
	 * @param i_swtFont the SWT font that is emulated by the returned font
	 * @param i_antialiased whether the returned font is antialiased
	 * @param i_lodHelper the LOD helper which can be used to select fonts
	 * @return the Draw3D font
	 * @throws NullPointerException if the given SWT font is <code>null</code>
	 */
	public IDraw3DFont getFont(Font i_swtFont, boolean i_antialiased,
		ILodHelper i_lodHelper);
}
