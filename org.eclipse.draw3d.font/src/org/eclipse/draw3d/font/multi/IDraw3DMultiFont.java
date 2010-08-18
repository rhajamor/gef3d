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

import org.eclipse.draw3d.font.simple.IDraw3DText;

/**
 * A font that creates {@link IDraw3DMultiText} instances instead of
 * {@link IDraw3DText} instances.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.08.2010
 */
public interface IDraw3DMultiFont {

	/**
	 * Creates an instance of {@link IDraw3DMultiText} for the given string.
	 * 
	 * @param i_string the string to render
	 * @return an instance of {@link IDraw3DMultiText} that renders the given
	 *         string
	 * @throws IllegalStateException if this font is disposed
	 */
	public IDraw3DMultiText createText(String i_string);

	/**
	 * Disposes all resources associated with this font.
	 */
	public void dispose();
}
