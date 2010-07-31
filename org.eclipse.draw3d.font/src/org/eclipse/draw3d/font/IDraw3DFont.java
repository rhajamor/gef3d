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
	public enum Flag {
		BOLD, ITALIC;

		public static int getAWTStyle(Flag... i_flags) {
			int style = 0;
			for (Flag flag : i_flags) {
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

		public static Flag[] getFlags(boolean i_bold, boolean i_italic) {
			if (i_bold && i_italic)
				return new Flag[] { BOLD, ITALIC };
			else if (i_bold)
				return new Flag[] { BOLD };
			else if (i_italic)
				return new Flag[] { ITALIC };
			return new Flag[0];
		}
	}

	public IDraw3DGlyphVector createGlyphVector(String i_string);

	public void dispose();
}
