/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

/**
 * Transparent objects must implement this interface so that they can be
 * rendered correctly.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 23.01.2008
 */
public interface TransparentObject {

	/**
	 * Returns the distance between the camera and the object.
	 * 
	 * @return the distance
	 */
	public float getTransparencyDepth();

	/**
	 * Actually renders the transparent object.
	 */
	public void renderTransparent();
}
