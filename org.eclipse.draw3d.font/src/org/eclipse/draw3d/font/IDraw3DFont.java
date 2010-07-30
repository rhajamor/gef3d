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

/**
 * IDraw3DFont There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public interface IDraw3DFont {
	public enum Flags {
		BOLD, ITALIC;

		public static int getAWTStyle(Flags... i_flags) {
			int style = 0;
			for (Flags flag : i_flags) {
				switch (flag) {
				case BOLD:
					style |= Font.BOLD;
					break;
				case ITALIC:
					style |= Font.ITALIC;
					break;
				default:
					throw new IllegalArgumentException("unknown flag: " + flag);
				}
			}
			return style;
		}
	}

	public IDraw3DGlyphVector createGlyphVector(String i_string);

	public void dispose();
}
