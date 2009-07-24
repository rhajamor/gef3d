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
     * Calculcates the point of intersection between a ray and a plane. The ray
     * is specified by a starting point and a direction and the plane is
     * specified in Hessian normal form, e.g. by a contained point and a normal
     * vector.
     * 
     * @param i_rayStart
     *            the starting point of the ray
     * @param i_rayDirection
     *            the direction vector of the ray, which must be normalised
     * @param i_planePoint
     *            a point that is contained in the plane
     * @param i_planeNormal
     *            the normal vector of the plane
     * @param io_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            returned
     * @return the point of intersection between the given ray and plane or
     *         <code>null</code> if the given ray either does not intersect with
     *         or is contained entirely in the given plane
     */
    public static Vector3f rayIntersectsPlane(IVector3f i_rayStart,
            IVector3f i_rayDirection, IVector3f i_planePoint,
            IVector3f i_planeNormal, Vector3f io_result) {

        float d = Math3D.dot(i_planePoint, i_planeNormal);

        float numerator = -1 * Math3D.dot(i_rayStart, i_planeNormal) + d;
        float denominator = Math3D.dot(i_rayDirection, i_planeNormal);

        if (denominator == 0)
            return null;

        float t = numerator / denominator;
        if (t < 0)
            return null;

        Vector3f result = io_result;
        if (result == null)
            result = new Vector3fImpl();

        result.set(i_rayDirection);
        result.scale(t);

        Math3D.add(i_rayStart, result, result);
        return result;
    }
}
