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
package org.eclipse.draw3d.offscreen;

/**
 * Interface to a framebuffer object.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 28.07.2010
 */
public interface FramebufferObject {

	/**
	 * Activates this FBO by binding it. All subsequent rendering operations are
	 * redirected to this FBO. If no FBO has been created yet, it is lazily
	 * created here.
	 * 
	 * @throws IllegalStateException if this FBO is disposed
	 */
	public void activate();

	/**
	 * Checks the status of this FBO and throws a RuntimeException if it is not
	 * complete.
	 * 
	 * @throws RuntimeException if this FBO is in an invalid state
	 */
	public void checkStatus();

	/**
	 * Deactivates this FBO by unbinding it.
	 */
	public void deactivate();

	/**
	 * Disposes this FBO.
	 */
	public void dispose();

	/**
	 * Indicates whether FBOs are supported on this system.
	 * 
	 * @return <code>true</code> if FBOs are supported or <code>false</code>
	 *         otherwise
	 */
	public boolean isSupported();
}
