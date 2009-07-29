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

import java.util.LinkedList;
import java.util.Queue;

/**
 * Math3D provides common 3D math operations. Instead of spreading all these
 * operations all over the 3D geometry classes, they are bundled here. This
 * makes it easier to create subclasses of the existing geometry classes or
 * provide adapter interfaces, since only the data has to be provided but not
 * the logic. Apart from that, it also serves as a cache for temporary math
 * objects like vectors and matrices which can be retrieved and returned to it
 * using appropriate methods.
 * 
 * @author Jens von Pilgrim, Matthias Thiele
 * @version $Revision$
 * @since 19.10.2008
 */
public class Math3DBase {

	private static final Queue<Matrix2f> m_matrix2f = new LinkedList<Matrix2f>();

	private static final Queue<Matrix3f> m_matrix3f = new LinkedList<Matrix3f>();

	private static final Queue<Matrix4f> m_matrix4f = new LinkedList<Matrix4f>();

	private static final Queue<Vector2f> m_vector2f = new LinkedList<Vector2f>();

	private static final Queue<Vector3f> m_vector3f = new LinkedList<Vector3f>();

	private static final Queue<Vector4f> m_vector4f = new LinkedList<Vector4f>();

	/**
	 * Indicates whether the absolute difference of given float values is at
	 * most the given epsilon value.
	 * 
	 * @param left
	 *            the left value
	 * @param right
	 *            the right value
	 * @param epsilon
	 *            the maximum difference
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
	 * 
	 * <code>(afleft[i],afright[j])</code> the following statement is true:
	 * <code> 0 <= i=j < size</code> implies
	 * <code>equals(afleft[i], afleft[j], epsilon) == true</code>.
	 * 
	 * @param afleft
	 *            the left array
	 * @param afright
	 *            the right array
	 * @param epsilon
	 *            the maximum difference
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
	 * Returns a cached {@link Matrix2f}.
	 * 
	 * @return a cached matrix
	 */
	public static Matrix2f getMatrix2f() {

		synchronized (m_matrix2f) {
			if (m_matrix2f.isEmpty())
				return new Matrix2fImpl();
			else
				return m_matrix2f.remove();
		}
	}

	/**
	 * Returns a cached {@link Matrix3f}.
	 * 
	 * @return a cached matrix
	 */
	public static Matrix3f getMatrix3f() {

		synchronized (m_matrix3f) {
			if (m_matrix3f.isEmpty())
				return new Matrix3fImpl();
			else
				return m_matrix3f.remove();
		}
	}

	/**
	 * Returns a cached {@link Matrix4f}.
	 * 
	 * @return a cached matrix
	 */
	public static Matrix4f getMatrix4f() {

		synchronized (m_matrix4f) {
			if (m_matrix4f.isEmpty())
				return new Matrix4fImpl();
			else
				return m_matrix4f.remove();
		}
	}

	/**
	 * Returns a cached {@link Vector2f}.
	 * 
	 * @return a cached vector
	 */
	public static Vector2f getVector2f() {

		synchronized (m_vector2f) {
			if (m_vector2f.isEmpty())
				return new Vector2fImpl();
			else
				return m_vector2f.remove();
		}
	}

	/**
	 * Returns a cached {@link Vector3f}.
	 * 
	 * @return a cached vector
	 */
	public static Vector3f getVector3f() {

		synchronized (m_vector3f) {
			if (m_vector3f.isEmpty())
				return new Vector3fImpl();
			else
				return m_vector3f.remove();
		}
	}

	/**
	 * Returns a cached {@link Vector4f}.
	 * 
	 * @return a cached vector
	 */
	public static Vector4f getVector4f() {

		synchronized (m_vector4f) {
			if (m_vector4f.isEmpty())
				return new Vector4fImpl();
			else
				return m_vector4f.remove();
		}
	}

	/**
	 * Returns the given matrix to the cache. If the given matrix is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_m
	 *            the matrix to return
	 */
	public static void returnMatrix2f(Matrix2f i_m) {

		if (i_m == null)
			return;

		synchronized (m_matrix2f) {
			m_matrix2f.offer(i_m);
		}
	}

	/**
	 * Returns the given matrix to the cache. If the given matrix is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_m
	 *            the matrix to return
	 */
	public static void returnMatrix3f(Matrix3f i_m) {

		if (i_m == null)
			return;

		synchronized (m_matrix3f) {
			m_matrix3f.offer(i_m);
		}
	}

	/**
	 * Returns the given matrix to the cache. If the given matrix is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_m
	 *            the matrix to return
	 */
	public static void returnMatrix4f(Matrix4f i_m) {

		if (i_m == null)
			return;

		synchronized (m_matrix4f) {
			m_matrix4f.offer(i_m);
		}
	}

	/**
	 * Returns the given vector to the cache. If the given vector is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_v
	 *            the vector to return
	 */
	public static void returnVector2f(Vector2f i_v) {

		if (i_v == null)
			return;

		synchronized (m_vector2f) {
			m_vector2f.offer(i_v);
		}
	}

	/**
	 * Returns the given vector to the cache. If the given vector is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_v
	 *            the vector to return
	 */
	public static void returnVector3f(Vector3f i_v) {

		if (i_v == null)
			return;

		synchronized (m_vector3f) {
			m_vector3f.offer(i_v);
		}
	}

	/**
	 * Returns the given vector to the cache. If the given vector is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_v
	 *            the vector to return
	 */
	public static void returnVector4f(Vector4f i_v) {

		if (i_v == null)
			return;

		synchronized (m_vector4f) {
			m_vector4f.offer(i_v);
		}
	}

}
