/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * XYZAnchor There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 31, 2009
 */
public class XYZAnchor extends XYAnchor implements ConnectionAnchor3D {

	Vector3fImpl v3fLocation;

	/**
	 * @param location
	 */
	public XYZAnchor(Vector3fImpl location) {
		super(new Point(location.getX(), location.getY()));
		v3fLocation = new Vector3fImpl();
		setLocation3D(location);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.XYAnchor#setLocation(org.eclipse.draw2d.geometry.Point)
	 */
	@Override
	public void setLocation(Point i_p) {
		v3fLocation.set(i_p.x, i_p.y, 10);
		super.setLocation(i_p);
	}

	public void setLocation3D(IVector3f i_p) {
		v3fLocation.set(i_p);
		fireAnchorMoved();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ConnectionAnchor3D#getLocation3D(org.eclipse.draw3d.geometry.IVector3f,
	 *      org.eclipse.draw3d.geometry.Vector3f)
	 */
	public IVector3f getLocation3D(IVector3f i_reference, Vector3f io_result) {
		if (io_result != null) {
			io_result.set(v3fLocation);
			return io_result;
		} else {
			return v3fLocation;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ConnectionAnchor3D#getReferencePoint3D(org.eclipse.draw3d.geometry.Vector3f)
	 */
	public IVector3f getReferencePoint3D(Vector3f io_result) {
		return getLocation3D(null, io_result);
	}

}
