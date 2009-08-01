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
package org.eclipse.draw3d.picking;

import java.util.logging.Logger;

import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * GeometryPicker There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 31.07.2009
 */
public class GeometryPicker implements Picker {

	private ISurface m_currentSurface;

	private IFigure3D m_rootFigure;

	private IScene m_scene;

	/**
	 * Creates a new picker for the given scene.
	 * 
	 * @param i_scene the scene in which the pickable figures are displayed
	 * @throws NullPointerException if the given scene is <code>null</code>
	 */
	public GeometryPicker(IScene i_scene) {

		if (i_scene == null)
			throw new NullPointerException("i_scene must not be null");

		m_scene = i_scene;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Picker#getCurrentSurface()
	 */
	public ISurface getCurrentSurface() {

		if (m_currentSurface == null)
			return m_rootFigure.getSurface();

		return m_currentSurface;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Picker#getHit(int, int)
	 */
	public Hit getHit(int i_mx, int i_my) {

		return getHit(i_mx, i_my, null);
	}

	private static final Logger log =
		Logger.getLogger(GeometryPicker.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Picker#getHit(int, int,
	 *      org.eclipse.draw2d.TreeSearch)
	 */
	public Hit getHit(int i_mx, int i_my, TreeSearch i_search) {

		Vector3f rayStart = Draw3DCache.getVector3f();
		Vector3f rayDirection = Draw3DCache.getVector3f();
		try {

			ICamera camera = m_scene.getCamera();
			camera.getPosition(rayStart);

			camera.unProject(i_mx, i_my, 0, null, rayDirection);
			Math3D.sub(rayDirection, rayStart, rayDirection);
			Math3D.normalise(rayDirection, rayDirection);

			Query query =
				new Query(rayStart, rayDirection, m_rootFigure, i_search);
			HitImpl hit = query.execute(m_rootFigure);

			if (hit != null) {
				hit.setMouseLocation(i_mx, i_my);
				m_currentSurface = hit.getFigure().getSurface();

				log.info(hit.toString());
			}

			return hit;
		} finally {
			Draw3DCache.returnVector3f(rayStart);
			Draw3DCache.returnVector3f(rayDirection);
		}
	}

	/**
	 * Sets the root figure.
	 * 
	 * @param i_rootFigure the root figure
	 */
	public void setRootFigure(IFigure3D i_rootFigure) {

		m_rootFigure = i_rootFigure;
	}

	/**
	 * @param i_figure
	 * @return
	 */

}
