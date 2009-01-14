/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - refactoring and optimizations
 ******************************************************************************/
package org.eclipse.gef3d.ui.parts;

import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.swt.graphics.Color;


/**
 * IScene is a graphical viewer which has a camera attached.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 03.01.2008
 */
public interface IScene {

	/**
	 * Returns the camera that is attached to this scene.
	 * 
	 * @return the attached camera
	 */
	public ICamera getCamera();

	/**
	 * Returns the update manager of the viewer.
	 * 
	 * @return the update manager
	 * @todo move this to a more sensible place
	 */
	public UpdateManager getUpdateManager();

	/**
	 * Instructs the scene to render itself.
	 */
	public void render();

	/**
	 * Sets the background color of this scene.
	 * 
	 * @param i_backgroundColor the background color
	 */
	public void setBackgroundColor(Color i_backgroundColor);

	/**
	 * Sets the camera for this scene.
	 * 
	 * @param i_camera the camera to set
	 */
	public void setCamera(ICamera i_camera);

	/**
	 * Specifies whether coordinate axes should be drawn.
	 * 
	 * @param i_drawAxes <code>true</code> if coordinate axes should be drawn
	 *            or <code>false</code> otherwise
	 */
	public void setDrawAxes(boolean i_drawAxes);
}