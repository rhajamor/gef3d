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
	 * @see org.eclipse.draw3d.graphics3d.ILodHelper#getLODFactor(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public float getLODFactor(IVector3f i_position) {
		Vector3f cPos = Draw3DCache.getVector3f();
		Vector3f v = Draw3DCache.getVector3f();
		try {
			m_camera.getPosition(cPos);
			Math3D.sub(i_position, cPos, v);
			return v.length() / m_camera.getFar();
		} finally {
			Draw3DCache.returnVector3f(cPos, v);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ILodHelper#getLODFactor(org.eclipse.draw3d.geometry.IVector3f,
	 *      org.eclipse.draw3d.geometry.IVector3f)
	 */
	public float getLODFactor(IVector3f i_position, IVector3f i_normal) {
		Vector3f cPos = Draw3DCache.getVector3f();
		Vector3f v = Draw3DCache.getVector3f();
		Vector3f vDir = Draw3DCache.getVector3f();
		try {
			m_camera.getViewDirection(vDir);

			float cosa = Math3D.dot(vDir, i_normal);
			if (cosa <= 0)
				return 1;

			m_camera.getPosition(cPos);
			Math3D.sub(i_position, cPos, v);
			float l = v.length() / m_camera.getFar();

			return l / cosa;
		} finally {
			Draw3DCache.returnVector3f(cPos, v, vDir);
		}
	}
}
