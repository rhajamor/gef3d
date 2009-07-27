/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.geometryext;

import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * A plane that allows some simple geometric operations. Internally, the plane
 * is stored in hessian normal form.
 * 
 * @author Kristian Duske
 * @version $Revision:628 $
 * @since 21.04.2008
 */
public class Plane {

	private static Vector3fImpl TMP_V3_1 = new Vector3fImpl();

	private static Vector3fImpl TMP_V3_2 = new Vector3fImpl();

	private static Vector3fImpl TMP_V3_3 = new Vector3fImpl();

	private float d;

	private Vector3fImpl m_n = new Vector3fImpl();

	private Vector3fImpl m_p = new Vector3fImpl();

	/**
	 * Determines the point of intersection between this plane and a line
	 * represented by two distinct points contained in that line.
	 * <p>
	 * See {@link http://en.wikipedia.org/wiki/Line-plane_intersection}
	 * </p>
	 * 
	 * @param i_la the first point
	 * @param i_lb the second point
	 * @param o_result the result vector, if <code>null</code>, a new vector
	 *            will be created
	 * @return the point of intersection or <code>null</code> if the given
	 *         line does not intersect with this plane or if it is entirely
	 *         contained in this plane
	 * @throws NullPointerException if i_la or i_lb is <code>null</code>
	 */
	public Vector3f intersectionWithLine(IVector3f i_la, IVector3f i_lb,
			Vector3f o_result) {

		if (i_la == null)
			throw new NullPointerException("i_la must not be null");

		if (i_lb == null)
			throw new NullPointerException("i_lb must not be null");

		Vector3f result = o_result;
		if (result == null)
			result = new Vector3fImpl();

		Math3D.sub(i_lb, i_la, TMP_V3_1);

		double numerator = d - m_n.x * i_la.getX() - m_n.y * i_la.getY() - m_n.z * i_la.getZ();
		double denominator = m_n.x * TMP_V3_1.x + m_n.y * TMP_V3_1.y + m_n.z
				* TMP_V3_1.z;

		// line is parallel to or contained within the plane
		if (denominator == 0)
			return null;

		float t = (float) (numerator / denominator);

		TMP_V3_1.scale(t);
		Math3D.add(i_la, TMP_V3_1, result);

		return result;
	}

	/**
	 * Determines the point of intersection of this plane and a given ray.
	 * 
	 * @param i_rs the start point of the ray
	 * @param i_rd the direction of the ray
	 * @param o_result the result vector, if <code>null</code>, a new vector
	 *            will be created
	 * @return the point of intersection or <code>null</code> if the given
	 *         line does not intersect with this plane or if it is entirely
	 *         contained in this plane
	 * @throws NullPointerException if i_la or i_lb is <code>null</code>
	 */
	public Vector3f intersectionWithRay(IVector3f i_rs, IVector3f i_rd,
			Vector3f o_result) {

		if (i_rs == null)
			throw new NullPointerException("i_rs must not be null");

		if (i_rd == null)
			throw new NullPointerException("i_rd must not be null");

		Vector3f result = intersectionWithLine(i_rs, i_rd, o_result);
		if (result == null)
			return null;

		// check whether the point of intersection is contained in the ray
		// this is true if (result - i_rs) equals lambda * (i_rd - i_rs), where
		// lambda is a non negative scalar
		float x1 = result.getX() - i_rs.getX();
		float x2 = i_rd.getX() - i_rs.getX();

		float lambda = x1 / x2;
		if (lambda < 0)
			return null;

		return result;
	}

	/**
	 * Sets the parameters of this plane using the given vectors which describe
	 * three non-collinear points.
	 * 
	 * @param i_p0 the first point
	 * @param i_p1 the second point
	 * @param i_p2 the third point
	 * @return <code>true</code> if the given points are non-collinear,
	 *         <code>false</code> otherwise
	 * @throws NullPointerException if any of the given vectors is
	 *             <code>null</code>
	 */
	public boolean set(IVector3f i_p0, IVector3f i_p1, IVector3f i_p2) {

		if (i_p0 == null)
			throw new NullPointerException("i_p0 must not be null");

		if (i_p1 == null)
			throw new NullPointerException("i_p1 must not be null");

		if (i_p2 == null)
			throw new NullPointerException("i_p2 must not be null");

		// convert to hessian normal form
		Math3D.sub(i_p1, i_p0, TMP_V3_1);
		Math3D.sub(i_p2, i_p0, TMP_V3_2);

		Math3D.cross(TMP_V3_1, TMP_V3_2, TMP_V3_3);
		if (TMP_V3_3.lengthSquared() == 0)
			return false;

		Math3D.normalise(TMP_V3_3, TMP_V3_3);

		m_n.set(TMP_V3_3);
		m_p.set(i_p0);
		d = Math3D.dot(m_p, m_n);

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder buf = new StringBuilder();

		buf.append("Plane[position: ");
		buf.append(m_p);
		buf.append(", normal: ");
		buf.append(m_n);
		buf.append(", d: ");
		buf.append(d);
		buf.append("]");

		return buf.toString();
	}
}
