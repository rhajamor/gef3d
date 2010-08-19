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

import org.eclipse.swt.SWT;

/**
 * A font that can be used to render text in the Draw3D subsystem. Fonts are
 * used to create instances of {@link IDraw3DText} which are then used to render
 * the actual text.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public interface IDraw3DFont {
	/**
	 * The font face flags.
	 * 
	 * @author Kristian Duske
	 * @version $Revision$
	 * @since 17.08.2010
	 */
	public enum Flag {
		/**
		 * The bold font face.
		 */
		BOLD,
		/**
		 * The italic font face.
		 */
		ITALIC;

		/**
		 * Returns the AWT style value for the given flags.
		 * 
		 * @param i_flags the flags
		 * @return the AWT style
		 * @see Font#getStyle()
		 */
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

		/**
		 * Returns an array containing the flags that represent the given
		 * parameters.
		 * 
		 * @param i_bold whether the flags should include {@link #BOLD}
		 * @param i_italic whether the flags should include {@link #ITALIC}
		 * @return the flag array
		 */
		public static Flag[] getFlags(boolean i_bold, boolean i_italic) {
			if (i_bold && i_italic)
				return new Flag[] { BOLD, ITALIC };
			else if (i_bold)
				return new Flag[] { BOLD };
			else if (i_italic)
				return new Flag[] { ITALIC };
			return new Flag[0];
		}

		/**
		 * Returns an array containing the flags that represent the given SWT
		 * style mask.
		 * 
		 * @param i_swtStyle the SWT style mask
		 * @return the flag array
		 * @see org.eclipse.swt.graphics.Font
		 */
		public static Flag[] getFlags(int i_swtStyle) {
			boolean bold = (i_swtStyle & SWT.BOLD) != 0;
			boolean italic = (i_swtStyle & SWT.ITALIC) != 0;

			return getFlags(bold, italic);
		}
	}

	/**
	 * Creates an instance of {@link IDraw3DText} for the given string.
	 * 
	 * @param i_string the string to render
	 * @return an instance of {@link IDraw3DText} that renders the given string
	 * @throws IllegalStateException if this font is disposed
	 */
	public IDraw3DText createText(String i_string);

	/**
	 * Disposes all resources associated with this font.
	 */
	public void dispose();
}
