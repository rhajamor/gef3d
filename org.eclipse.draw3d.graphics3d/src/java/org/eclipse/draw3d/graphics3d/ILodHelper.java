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
package org.eclipse.draw3d.graphics3d;

import org.eclipse.draw3d.geometry.IVector3f;

/**
 * Instances of this class are used to calculate a LOD value for an object at a
 * given position. This value is based on the distance between an object and the
 * camera, but it is not linear. A high LOD value means that the object is close
 * to the camera and thus should be rendered with a lot of detail.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.01.2010
 */
public interface ILodHelper {
	/**
	 * Returns an LOD value that is calculated by the following function:
	 * 
	 * <pre>
	 *           m<sup>2</sup> - d<sup>2</sup>
	 * f(d) = ------------
	 *        100 * d<sup>2</sup> + m<sup>2</sup>
	 * </pre>
	 * 
	 * where
	 * <ul>
	 * <li><b>d</b> is the distance between the given point and the camera</li>
	 * <li><b>m</b> is the distance after which an object is no longer visible
	 * at all</li>
	 * </ul>
	 * The function has the following invariants:
	 * <ul>
	 * <li>f(0) = 1</li>
	 * <li>f(m) = 0</li>
	 * <li>f(d) is in [0, 1] for every d >= 0</li>
	 * </ul>
	 * The following graphic shows the curve for m = 10000: <br />
	 * <img src="doc-files/ILodHelper-1.jpg" />
	 * 
	 * @param i_point the point
	 * @return the LOD value
	 */
	public float getQuotientLOD(IVector3f i_point);
}
