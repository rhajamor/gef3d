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

/**
 * Represents a renderable chunk of text. Such a text chunk can be rendered
 * repeatedly until it is disposed.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public interface IDraw3DText {
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
	 * Renders this text.
	 * 
	 * @throws IllegalStateException if this text chunk is disposed
	 */
	public void render();
}
