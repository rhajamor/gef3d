/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.geometry;


/**
 * Math3D provides common 3D math operations. Instead of spreading all these
 * operations all over the 3D geometry classes, they are bundled here. This
 * makes it easier to create subclasses of the existing geometry classes or
 * provide adapter interfaces, since only the data has to be provided but not
 * the logic.
 * 
 * @author Jens von Pilgrim, Matthias Thiele
 * @version $Revision$
 * @since 19.10.2008
 */
public class Math3DBase {

	/**
	 * Returns true if <i>|left-right|<=epsilon</i>
	 * 
	 * @param left
	 * @param right
	 * @param epsilon
	 * @return
	 */
	public static boolean equals(float left, float right, float epsilon) {
		return Math.abs(left - right) <= epsilon;
	}

	/**
	 * Returns true if size of both array is equal and a_i in afleft and b_i in
	 * afright holds <i>|a_i-b_i|<=epsilon</i> for all i=0..size.
	 * 
	 * @param left
	 * @param right
	 * @param epsilon
	 * @return
	 */
	public static boolean equals(float[] afleft, float[] afright, float epsilon) {
		if (afleft.length != afright.length)
			return false;
		for (int i = 0; i < afleft.length; i++) {
			if (Math.abs(afleft[i] - afright[i]) > epsilon)
				return false;
		}
		return true;
	}

}
