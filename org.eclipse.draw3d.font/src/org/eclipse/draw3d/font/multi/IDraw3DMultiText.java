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

/**
 * Renderable text that uses a level of detail value to adapt its complexity.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public interface IDraw3DMultiText {
	/**
	 * Disposes all resources associated with this text.
	 */
	public void dispose();

	/**
	 * Returns the height of the text block.
	 * 
	 * @return the height
	 */
	public float getHeight();

	/**
	 * Returns the width of the text block.
	 * 
	 * @return the width
	 */
	public float getWidth();

	/**
	 * Renders this text and adapts the visual complexity to the given LOD
	 * value. A low value indicates that little detail should be used while a
	 * high value indicates that lots of detail should be used.
	 * 
	 * @param i_lod the LOD value, which must be between 0 and 1 inclusive
	 * @throws IllegalArgumentException if the given LOD value is not between 0
	 *             and 1 inclusive
	 * @throws IllegalStateException if this text is disposed
	 */
	public void render(float i_lod);
}
