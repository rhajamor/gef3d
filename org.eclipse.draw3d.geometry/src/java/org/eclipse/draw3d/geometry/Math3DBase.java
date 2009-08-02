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
	 * Indicates whether the absolute difference of given float values is at
	 * most the given epsilon value.
	 * 
	 * @param left the left value
	 * @param right the right value
	 * @param epsilon the maximum difference
	 * @return <code>true</code> if <i>|left-right|<=epsilon</i> or
	 *         <code>false</code> otherwise
	 */
	public static boolean equals(float left, float right, float epsilon) {

		return Math.abs(left - right) <= epsilon;
	}

	/**
	 * Indicates whether all elements in the given arrays are equal in the sense
	 * of {@link #equals(float, float, float)}. This is the case if the two
	 * arrays contain the same number of elements and for each element pair
	 * <code>(afleft[i],afright[j])</code> the following statement is true:
	 * <code> 0 <= i=j < size</code> implies
	 * <code>equals(afleft[i], afleft[j], epsilon) == true</code>.
	 * 
	 * @param afleft the left array
	 * @param afright the right array
	 * @param epsilon the maximum difference
	 * @return <code>true</code> if the two arrays are equal, <code>false</code>
	 *         otherwise
	 */
	public static boolean equals(float[] afleft, float[] afright, float epsilon) {

		if (afleft.length != afright.length)
			return false;

		for (int i = 0; i < afleft.length; i++)
			if (Math.abs(afleft[i] - afright[i]) > epsilon)
				return false;

		return true;
	}

	/**
	 * Indicates whether the given value is inside the interval defined by the
	 * given boundaries. The check is inclusive, so
	 * 
	 * <pre>
	 * in(x, x, x)
	 * </pre>
	 * 
	 * is always <code>true</code>. The boundaries don't have to be ordered in
	 * any way.
	 * 
	 * @param i_boundary1 one interval boundary
	 * @param i_boundary2 the other interval boundary
	 * @param i_value the value to check
	 * @return <code>true</code> if the given value is in the given interval or
	 *         <code>false</code> otherwise
	 */
	public static boolean in(float i_boundary1, float i_boundary2, float i_value) {

		float min, max;
		if (i_boundary1 < i_boundary2) {
			min = i_boundary1;
			max = i_boundary2;
		} else {
			min = i_boundary2;
			max = i_boundary1;
		}

		return i_value >= min && i_value <= max;
	}

}
