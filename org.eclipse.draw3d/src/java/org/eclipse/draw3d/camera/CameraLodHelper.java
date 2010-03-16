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

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.ILodHelper;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * CameraLodHelper There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.01.2010
 */
public class CameraLodHelper implements ILodHelper {

	private ICamera m_camera;

	private Vector3f m_cDir = new Vector3fImpl();

	private Vector3f m_ncDir = new Vector3fImpl();

	public CameraLodHelper(ICamera i_camera) {

		m_camera = i_camera;
	}

	private IVector3f getNormalizedCameraDirection() {

		Vector3f cDir = Draw3DCache.getVector3f();
		try {
			m_camera.getViewDirection(cDir);
			if (!m_cDir.equals(cDir)) {
				m_cDir.set(cDir);
				Math3D.normalise(m_cDir, m_ncDir);
			}

			return m_ncDir;
		} finally {
			Draw3DCache.returnVector3f(cDir);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ILodHelper#getNormalizedArea(IVector3f,
	 *      IVector2f, IVector3f)
	 */
	public float getNormalizedArea(IVector3f i_position, IVector2f i_size,
		IVector3f i_normal) {

		Vector3f cPos = Draw3DCache.getVector3f();
		Vector3f v = Draw3DCache.getVector3f();
		try {
			m_camera.getPosition(cPos);
			Math3D.sub(i_position, cPos, v);

			float d2 = v.lengthSquared();
			float sa = i_size.getX() * i_size.getY() / d2;

			float cosa = Math3D.dot(getNormalizedCameraDirection(), i_normal);
			return sa * Math.abs(cosa);
		} finally {
			Draw3DCache.returnVector3f(cPos, v);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ILodHelper#getNormalizedDistance(IVector3f,
	 *      IVector2f, IVector3f)
	 */
	public float getNormalizedDistance(IVector3f i_position, IVector2f i_size,
		IVector3f i_normal) {

		Vector3f cPos = Draw3DCache.getVector3f();
		Vector3f v = Draw3DCache.getVector3f();
		try {
			v.setX(i_position.getX() + i_size.getX() / 2);
			v.setY(i_position.getY() + i_size.getY() / 2);
			v.setZ(i_position.getZ());

			m_camera.getPosition(cPos);
			Math3D.sub(v, cPos, v);

			float d = v.length();

			float cosa = Math3D.dot(getNormalizedCameraDirection(), i_normal);
			if (cosa == 0)
				return Float.MAX_VALUE;

			return d / Math.abs(cosa);
		} finally {
			Draw3DCache.returnVector3f(cPos, v);
		}
	}

}