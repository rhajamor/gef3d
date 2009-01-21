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
package org.eclipse.draw3d.geometryext;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * SyncHost3D
 * There should really be more documentation here.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Jan 21, 2009
 */
public interface SyncHost3D extends IHost3D {
	
	/**
	 * Returns rectangle representing the bounds of a 2D figure.
	 * @return
	 */
	public Rectangle getBounds();
	
	/**
	 * Sets the bounds of this host.
	 */
	public void setBounds(Rectangle rect);

}
