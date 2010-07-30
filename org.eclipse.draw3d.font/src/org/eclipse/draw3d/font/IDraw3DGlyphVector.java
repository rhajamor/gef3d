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
 * IDraw3DText
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	30.07.2010
 */
public interface IDraw3DGlyphVector {
	public void render();

	public void dispose();
}
