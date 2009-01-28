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
package org.eclipse.gef3d.requests;

import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * ChangeBounds3DRequest There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 31, 2008
 */
public class ChangeBounds3DRequest extends ChangeBoundsRequest {

	IVector3f sizeDelta3D = IVector3f.NULLVEC3f;

	IVector3f moveDelta3D = IVector3f.NULLVEC3f;

	IVector3f location3D = IVector3f.NULLVEC3f;

	/**
	 * @param i_type
	 */
	public ChangeBounds3DRequest(Object i_type) {
		super(i_type);
	}

	/**
	 * @return the sizeDelta3D
	 */
	public IVector3f getSizeDelta3D() {
		return sizeDelta3D;
	}

	/**
	 * @param i_sizeDelta3D the sizeDelta3D to set
	 */
	public void setSizeDelta3D(IVector3f i_sizeDelta3D) {
		sizeDelta3D = i_sizeDelta3D;
	}

	/**
	 * @return the moveDelta3D
	 */
	public IVector3f getMoveDelta3D() {
		return moveDelta3D;
	}

	/**
	 * @param i_moveDelta3D the moveDelta3D to set
	 */
	public void setMoveDelta3D(IVector3f i_moveDelta3D) {
		moveDelta3D = i_moveDelta3D;
	}

	/**
	 * @return the location3D
	 */
	public IVector3f getLocation3D() {
		return location3D;
	}

	/**
	 * @param i_location3D the location3D to set
	 */
	public void setLocation3D(IVector3f i_location3D) {
		location3D = i_location3D;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer strb = new StringBuffer("ChangeBounds3DReq. [");

		strb.append("\n  Location: ").append(getLocation()).append("=").append(
				getLocation3D());
		strb.append("\n  Move delta: ").append(getMoveDelta()).append("=")
				.append(getMoveDelta3D());
		strb.append("\n  Size delta: ").append(getSizeDelta()).append("=")
				.append(getSizeDelta3D());

		strb.append("]");
		return strb.toString();

	}

}
