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
 * General interface for objects to be rendered. Rendering is performed three
 * pass:
 * <ul>
 * <li>Firstly render is called</li>
 * <li>Secondly postrender is called, this is usually done after possible
 * children have been rendered, i.e. after textures were updated</li>
 * <li>Transparent parts of the object are stored in renderState and are
 * rendered after everything else has been rendered</li>
 * </ul>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 23.01.2008
 */
public interface Renderable {

	/**
	 * This method is responsible of rendering to the screen. It is called
	 * <strong>before</strong> children are rendered, i.e. textures created by
	 * children may not be created yet.
	 * 
	 * @see #postrender()
	 */
	public void render();

	/**
	 * This method is responsible of rendering to the screen. It is called (via
	 * the figure hierarchy) by {@link DeferredUpdateManager3D}. It is called
	 * <strong>after</strong> children were rendered, i.e. textures created by
	 * children are created.
	 * 
	 * @see #render()
	 */
	public void postrender();
}
