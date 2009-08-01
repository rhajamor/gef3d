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
 * operations all over the 3D geometry classes holding the data, they are
 * bundled here. This makes it easier to create subclasses of the existing
 * geometry classes or provide adapter interfaces, since only the data has to be
 * provided but not the logic.
 * 
 * @author Jens von Pilgrim, Kristian Duske
 * @version $Revision$
 * @since 19.10.2008
 */
public class Math3D extends Math3DMatrixOps {

	/**
	 * Enumerates the sides of a plane.
	 * 
	 * @author Kristian Duske
	 * @version $Revision$
	 * @since 31.07.2009
	 */
	public enum Side {
		/**
		 * The back side.
		 */
		BACK,
		/**
		 * The front side.
		 */
		FRONT;
	}

	/**
	 * Indicates whether the given point is on the front or back of the given
	 * plane. If the given point is contained within the given plane,
	 * <code>null</code> is returned.
	 * 
	 * @param i_planePoint a point on the plane
	 * @param i_planeNormal the plane normal
	 * @param i_point the point to check
	 * @return {@link Side#FRONT} if the given point is on the front of,
	 *         {@link Side#BACK} if it is on the back of or <code>null</code> if
	 *         it is contained within the given plane
	 * @throws NullPointerException if any of the given arguments is
	 *             <code>null</code>
	 */
	public static Side getSideOfPoint(IVector3f i_planePoint,
		IVector3f i_planeNormal, IVector3f i_point) {

		if (i_planePoint == null)
			throw new NullPointerException("i_planePoint must not be null");

		if (i_planeNormal == null)
			throw new NullPointerException("i_planeNormal must not be null");

		if (i_point == null)
			throw new NullPointerException("i_point must not be null");

		if (i_planePoint.equals(i_point))
			return null;

		Vector3f tmp = Math3DCache.getVector3f();
		try {
			Math3D.sub(i_point, i_planePoint, tmp);
			float cos = Math3D.dot(i_planeNormal, tmp);
			if (cos > 0)
				return Side.FRONT;
			else if (cos < 0)
				return Side.BACK;

			return null;
		} finally {
			Math3DCache.returnVector3f(tmp);
		}
	}

	/**
	 * Determines the point of intersection between this plane and a line
	 * represented by two distinct points contained in that line.
	 * 
	 * @param i_linePoint1 the first point contained in the line
	 * @param i_linePoint2 the second point contained in the line
	 * @param i_planePoint a point contained in the plane
	 * @param i_planeNormal the plane normal
	 * @param o_result the result vector, if <code>null</code>, a new vector
	 *            will be created
	 * @return the point of intersection or <code>null</code> if the given line
	 *         does not intersect with this plane or if it is entirely contained
	 *         in this plane
	 * @throws NullPointerException if any of the given arguments is
	 *             <code>null</code>
	 * @see "http://en.wikipedia.org/wiki/Line-plane_intersection"
	 */
	public static Vector3f lineIntersectsPlane(IVector3f i_linePoint1,
		IVector3f i_linePoint2, IVector3f i_planePoint,
		IVector3f i_planeNormal, Vector3f o_result) {

		if (i_linePoint1 == null)
			throw new NullPointerException("i_linePoint1 must not be null");

		if (i_linePoint2 == null)
			throw new NullPointerException("i_linePoint2 must not be null");

		if (i_planePoint == null)
			throw new NullPointerException("i_planePoint must not be null");

		if (i_planeNormal == null)
			throw new NullPointerException("i_planeNormal must not be null");

		Vector3f result = o_result;
		if (result == null)
			result = new Vector3fImpl();

		Vector3f tmp = Math3DCache.getVector3f();
		try {
			Math3D.sub(i_linePoint2, i_linePoint1, tmp);

			float d = Math3D.dot(i_planePoint, i_planeNormal);

			float numerator =
				d - i_planeNormal.getX() * i_linePoint1.getX()
					- i_planeNormal.getY() * i_linePoint1.getY()
					- i_planeNormal.getZ() * i_linePoint1.getZ();

			float denominator =
				i_planeNormal.getX() * tmp.getX() + i_planeNormal.getY()
					* tmp.getY() + i_planeNormal.getZ() * tmp.getZ();

			// line is parallel to or contained within the plane
			if (denominator == 0)
				return null;

			float t = numerator / denominator;

			tmp.scale(t);
			Math3D.add(i_linePoint1, tmp, result);

			return result;
		} finally {
			Math3DCache.returnVector3f(tmp);
		}
	}

	/**
	 * Calculcates the point of intersection between a ray and a plane. The ray
	 * is specified by a starting point and a direction and the plane is
	 * specified in Hessian normal form, e.g. by a contained point and a normal
	 * vector. This method returns a scalar x so that
	 * 
	 * <pre>
	 * p = i_rayStart + x * i_rayDirection
	 * </pre>
	 * 
	 * in which p is the point of intersection of the given ray and the given
	 * plane, if any. A result of <code>0</code> indicates that the ray starting
	 * point is contained within the plane.
	 * 
	 * @param i_rayStart the starting point of the ray
	 * @param i_rayDirection the direction vector of the ray, which must be
	 *            normalised
	 * @param i_planePoint a point that is contained in the plane
	 * @param i_planeNormal the normal vector of the plane
	 * @return the scalar factor for the ray direction vector or
	 *         {@link Float#NaN} if the given ray does not intersect with or is
	 *         contained entirely in the given plane
	 */
	public static float rayIntersectsPlane(IVector3f i_rayStart,
		IVector3f i_rayDirection, IVector3f i_planePoint,
		IVector3f i_planeNormal) {

		float d = Math3D.dot(i_planePoint, i_planeNormal);

		float numerator = d - Math3D.dot(i_rayStart, i_planeNormal);
		float denominator = Math3D.dot(i_rayDirection, i_planeNormal);

		if (denominator == 0)
			return Float.NaN;

		float t = numerator / denominator;
		if (t < 0)
			return Float.NaN;

		return t;
	}

	/**
	 * Calculcates the point of intersection between a ray and a plane. The ray
	 * is specified by a starting point and a direction and the plane is
	 * specified in Hessian normal form, e.g. by a contained point and a normal
	 * vector. If the result is equal to the ray starting point, the ray
	 * starting point is contained within the plane.
	 * 
	 * @param i_rayStart the starting point of the ray
	 * @param i_rayDirection the direction vector of the ray, which must be
	 *            normalised
	 * @param i_planePoint a point that is contained in the plane
	 * @param i_planeNormal the normal vector of the plane
	 * @param io_result the result vector, if <code>null</code>, a new vector
	 *            will be returned
	 * @return the point of intersection between the given ray and plane or
	 *         <code>null</code> if the given ray either does not intersect with
	 *         or is contained entirely in the given plane
	 */
	public static Vector3f rayIntersectsPlane(IVector3f i_rayStart,
		IVector3f i_rayDirection, IVector3f i_planePoint,
		IVector3f i_planeNormal, Vector3f io_result) {

		float t =
			rayIntersectsPlane(i_rayStart, i_rayDirection, i_planePoint,
				i_planeNormal);
		if (Float.isNaN(t))
			return null;

		Vector3f result = io_result;
		if (result == null)
			result = new Vector3fImpl();

		if (t == 0)
			result.set(i_rayStart);
		else {
			result.set(i_rayDirection);
			result.scale(t);

			Math3D.add(i_rayStart, result, result);
		}

		return result;
	}

	/**
	 * Calculates the point of intersection between a given line segment
	 * specified its boundaries and a plane.
	 * 
	 * @param i_segmentPoint1 the first boundary point of the segment
	 * @param i_segmentPoint2 the second boundary point of the segment
	 * @param i_planePoint a point on the plane
	 * @param i_planeNormal the plane normal
	 * @param o_result the result vector, if <code>null</code>, a new vector
	 *            will be returned
	 * @return the point of intersection or <code>null</code> if the given
	 *         segment does not intersect with the given plane
	 * @throws NullPointerException if any of the given argumentents except
	 *             <code>o_result</code> is <code>null</code>
	 */
	public static Vector3f segmentIntersectsPlane(IVector3f i_segmentPoint1,
		IVector3f i_segmentPoint2, IVector3f i_planePoint,
		IVector3f i_planeNormal, Vector3f o_result) {

		if (i_segmentPoint1 == null)
			throw new NullPointerException("i_segmentPoint1 must not be null");

		if (i_segmentPoint2 == null)
			throw new NullPointerException("i_segmentPoint2 must not be null");

		Vector3f dir = Math3DCache.getVector3f();
		try {
			Math3D.sub(i_segmentPoint2, i_segmentPoint1, dir);

			float max = dir.length();
			Math3D.normalise(dir, dir);

			float t =
				rayIntersectsPlane(i_segmentPoint1, dir, i_planePoint,
					i_planeNormal);

			if (Float.isNaN(t))
				return null;

			if (t > max)
				return null;

			Vector3f result = o_result;
			if (result == null)
				result = new Vector3fImpl();

			result.set(dir);
			result.scale(t);

			Math3D.add(i_segmentPoint1, result, result);
			return result;
		} finally {
			Math3DCache.returnVector3f(dir);
		}
	}
}
