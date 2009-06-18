/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.ui.viewer;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.swt.graphics.Color;

/**
 * A scene that wraps a lightweight system 3D. This is useful when no GEF viewer
 * is used to contain the lightweight system.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 03.06.2009
 */
public class StandaloneScene implements IScene {

	private LightweightSystem3D m_lightweightSystem;

	/**
	 * Creates a new scene that wraps the given lightweight system.
	 * 
	 * @param i_lightweightSystem
	 *            the lightweight system
	 * 
	 * @throws NullPointerException
	 *             if the given lightweight system is <code>null</code>
	 */
	public StandaloneScene(LightweightSystem3D i_lightweightSystem) {

		if (i_lightweightSystem == null)
			throw new NullPointerException(
					"i_lightweightSystem must not be null");

		m_lightweightSystem = i_lightweightSystem;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IScene#getCamera()
	 */
	public ICamera getCamera() {

		return m_lightweightSystem.getCamera();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IScene#getUpdateManager()
	 */
	public UpdateManager getUpdateManager() {

		return m_lightweightSystem.getUpdateManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IScene#render()
	 */
	public void render() {

		m_lightweightSystem.getRenderContext().activate();

		UpdateManager updateManager = getUpdateManager();
		if (updateManager == null)
			return;

		IFigure root = m_lightweightSystem.getRootFigure();
		updateManager.addDirtyRegion(root, 0, 0, 10000, 10000);
		updateManager.performUpdate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IScene#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBackgroundColor(Color i_backgroundColor) {

		m_lightweightSystem.setBackgroundColor(i_backgroundColor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IScene#setCamera(org.eclipse.draw3d.camera.ICamera)
	 */
	public void setCamera(ICamera i_camera) {

		m_lightweightSystem.setCamera(i_camera);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IScene#setDrawAxes(boolean)
	 */
	public void setDrawAxes(boolean i_drawAxes) {

		m_lightweightSystem.setDrawAxes(i_drawAxes);
	}
}
