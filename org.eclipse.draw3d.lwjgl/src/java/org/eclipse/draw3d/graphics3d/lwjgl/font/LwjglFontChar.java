/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.graphics3d.lwjgl.font;

import org.lwjgl.opengl.GL11;

/**
 * Holds information about a single GL font character.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.06.2008
 */
public class LwjglFontChar {

	/**
	 * The font height.
	 */
	private final int m_height;

	/**
	 * The S texture coordinate of the upper left corner of this character.
	 */
	private float m_s1;

	/**
	 * The S texture coordinate of the lower right corner of this character.
	 */
	private float m_s2;

	/**
	 * The T texture coordinate of the upper left corner of this character.
	 */
	private float m_t1;

	/**
	 * The T texture coordinate of the lower right corner of this character.
	 */
	private float m_t2;

	/**
	 * The width of this character.
	 */
	private final int m_width;

	/**
	 * Creates a new character with the given width and height.
	 * 
	 * @param i_width the width of this character
	 * @param i_height the height of the font this character belongs to
	 */
	public LwjglFontChar(int i_width, int i_height) {

		m_width = i_width;
		m_height = i_height;
	}

	/**
	 * Renders this character.
	 * 
	 * @param i_width the width of the character texture
	 * @param i_height the height of the character texture
	 */
	public void render(int i_width, int i_height) {

		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(m_s1, m_t1);
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(m_s1, m_t2);
		GL11.glVertex2f(0, m_height);
		GL11.glTexCoord2f(m_s2, m_t2);
		GL11.glVertex2f(m_width, m_height);
		GL11.glTexCoord2f(m_s2, m_t1);
		GL11.glVertex2f(m_width, 0);

		GL11.glEnd();
		GL11.glEndList();
	}

	/**
	 * Returns the width of this character.
	 * 
	 * @return the width of this character
	 */
	public int getWidth() {

		return m_width;
	}

	/**
	 * Sets the texture coordinates of this character.
	 * 
	 * @param i_s1 the S coordinate of the upper left corner
	 * @param i_t1 the T coordinate of the upper left corner
	 * @param i_s2 the S coordinate of the lower right corner
	 * @param i_t2 the T coordinate of the lower right corner
	 */
	public void setTextureCoords(float i_s1, float i_t1, float i_s2, float i_t2) {

		m_s1 = i_s1;
		m_t1 = i_t1;
		m_s2 = i_s2;
		m_t2 = i_t2;
	}
}
