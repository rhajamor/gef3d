/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API
 ******************************************************************************/
package org.eclipse.draw3d;

/**
 * Listens to certain renderer events.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 22.05.2008
 */
public interface RenderListener {

	/**
	 * Called once a render pass was finished. It is guaranteed that the render
	 * context is still valid when this method is called.
	 */
	public void renderPassFinished();

	/**
	 * Called once a render pass was started. It is guaranteed that a render
	 * context has been created and initialized when this method is called.
	 * 
	 * @see RenderContext
	 */
	public void renderPassStarted();
}
