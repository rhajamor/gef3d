/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others,
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation of 2D version
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw3d.geometry.Vector3f;

/**
 * 3D version of connection, extends from Connection and can be used in a 2D 
 * editor, too.
 *
 * @author IBM Corporation (original comments of 2D version)
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 26.11.2007
 * @see org.eclipse.draw2d.Connection
 */
public interface Connection3D extends IFigure3D, Connection {
	/**
	 * Returns the PointList containing the Points that make up this Connection.
	 * This may be returned by reference.
	 * 
	 * @return The points for this Connection
	 */
	List<Vector3f> getPoints3D();

	/**
	 * Sets the PointList containing the Points that make up this Connection.
	 * 
	 * @param list The points for this Connection
	 */
	void setPoints3D(List<Vector3f> list);

}
