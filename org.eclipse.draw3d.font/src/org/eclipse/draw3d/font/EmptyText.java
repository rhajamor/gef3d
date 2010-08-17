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

/**
 * An implementation of {@link IDraw3DText} that does nothing. This is useful
 * for empty strings and such.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 17.08.2010
 */
public class EmptyText implements IDraw3DText {

	/**
	 * A single instance.
	 */
	public static final EmptyText INSTANCE = new EmptyText();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DText#dispose()
	 */
	public void dispose() {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DText#render()
	 */
	public void render() {
		// nothing to do
	}
}
