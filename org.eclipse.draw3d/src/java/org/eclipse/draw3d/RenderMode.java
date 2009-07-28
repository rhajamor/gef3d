/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

/**
 * Indicates the mode of a render pass.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 13.12.2007
 */
public enum RenderMode {

	/**
	 * Indicates that the current render pass is used to paint the picking
	 * buffer.
	 */
	COLOR,
	/**
	 * Indicates that the current render pass is used to paint the framebuffer.
	 */
	PAINT;

	/**
	 * Indicates that the current render mode is {@link #COLOR}.
	 * 
	 * @return <code>true</code> if the current render mode is {@link #COLOR}
	 */
	public boolean isColor() {

		return this == COLOR;
	}

	/**
	 * Indicates that the current render mode is {@link #PAINT}.
	 * 
	 * @return <code>true</code> if the current render mode is {@link #PAINT}
	 */
	public boolean isPaint() {

		return this == PAINT;
	}
}
