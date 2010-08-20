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
package org.eclipse.draw3d.camera;

import java.util.logging.Logger;

import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.ILodHelper;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * An implementation of {@link ILodHelper} that uses the camera to determine LOD
 * status.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.01.2010
 */
public class CameraLodHelper implements ILodHelper {

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(CameraLodHelper.class.getName());

	private static final float FACTOR = 100f;

	private ICamera m_camera;

	/**
	 * Creates a new instance that uses the given camera.
	 * 
	 * @param i_camera
	 */
	public CameraLodHelper(ICamera i_camera) {
		m_camera = i_camera;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ILodHelper#getLinearLOD(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public float getLinearLOD(IVector3f i_point) {
		Vector3f c = Draw3DCache.getVector3f();
		try {
			m_camera.getPosition(c);
			float d = Math3D.distanceSquared(c, i_point);
			float f = m_camera.getFar();
			return 1f - d / (f * f);
		} finally {
			Draw3DCache.returnVector3f(c);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ILodHelper#getQuotientLOD(IVector3f)
	 */
	public float getQuotientLOD(IVector3f i_point) {
		Vector3f c = Draw3DCache.getVector3f();
		try {
			m_camera.getPosition(c);
			float d = Math3D.distanceSquared(c, i_point);
			float m = m_camera.getFar() * m_camera.getFar();
			return (m - d) / (FACTOR * d + m);
		} finally {
			Draw3DCache.returnVector3f(c);
		}
	}
}
