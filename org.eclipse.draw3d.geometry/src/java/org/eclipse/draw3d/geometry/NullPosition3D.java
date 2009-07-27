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
package org.eclipse.draw3d.geometry;

import javax.naming.OperationNotSupportedException;


/**
 * NullPosition3D can be used to create a dummy parent position, for example
 * if the host hierarchy requires invisible elements such as containers.
 * <p>
 * The following values are set, this class is immutable:
 * <ul>
 * 	<li>Location: (0,0,0)</li>
 *  <li>Size: (1,1,1)</li>
 *  <li>Rotation: (0,0,0)</li>
 * </ul>
 * That is, the transformation matrix created is the identity matrix.
 * </p>
 * 
 * 
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Jan 21, 2009
 */
public class NullPosition3D extends Position3DImpl {

	/**
	 * @param i_host
	 */
	public NullPosition3D(IHost3D i_host) {
		super(i_host);
		init();
	}

	
	protected void init() {
		super.setLocation3D(IVector3f.NULLVEC3f);
		super.setRotation3D(IVector3f.NULLVEC3f);
		super.setSize3D(IVector3f.UNITVEC3f);
	}
	
	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.Position3DImpl#setLocation3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	@Override
	public void setLocation3D(IVector3f i_point) {
		throw new UnsupportedOperationException("this position must not be changed");
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.Position3DImpl#setSize3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	@Override
	public void setSize3D(IVector3f i_size) {
		throw new UnsupportedOperationException("this position must not be changed");
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.AbstractPosition3D#setRotation3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	@Override
	public void setRotation3D(IVector3f i_rotation) {
		throw new UnsupportedOperationException("this position must not be changed");
	}
	
	

}
