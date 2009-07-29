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
 * Basic vector operations.
 * 
 * @author Jens von Pilgrim, Kristian Duske
 * @version $Revision$
 * @since 19.10.2008
 */
public class Math3DVector3f extends Math3DVector2f {

    /**
     * Calculates the absolute value for each component of the given vector. The
     * source and result vector may be the same object.
     * 
     * @param i_source
     *            the source vector
     * @param io_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            created and returned
     * @return the result vector
     */
    public static Vector3f abs(IVector3f i_source, Vector3f io_result) {

        if (io_result == null)
            io_result = new Vector3fImpl(Math.abs(i_source.getX()),
                Math.abs(i_source.getY()), Math.abs(i_source.getZ()));
        else
            io_result.set(Math.abs(i_source.getX()), Math.abs(i_source.getY()),
                Math.abs(i_source.getZ()));

        return io_result;
    }

    /**
     * Adds two vectors.
     * 
     * @param i_leftVector3f
     * @param i_rightVector3f
     * @param o_resultVector3f
     * @return
     */
    public static Vector3f add(IVector3f i_left, IVector3f i_right,
            Vector3f o_result) {

        if (o_result == null) {
            return new Vector3fImpl(i_left.getX() + i_right.getX(),
                i_left.getY() + i_right.getY(), i_left.getZ() + i_right.getZ());
        } else {
            o_result.set(i_left.getX() + i_right.getX(), i_left.getY()
                    + i_right.getY(), i_left.getZ() + i_right.getZ());
            return o_result;
        }
    }

    /**
     * Returns the angle of the two vectors in the range of 0.0 through
     * <i>pi</i>.
     * 
     * @param i_left
     * @param i_right
     * @return alpha [0..pi]
     */
    public static float angle(IVector3f i_left, IVector3f i_right) {

        float cosAlpha = dot(i_left, i_right)
                / (i_left.length() * i_right.length());
        if (cosAlpha <= -1)
            return (float) Math.PI;
        else if (cosAlpha >= 1) {
            return 0;
        }
        return (float) Math.acos(cosAlpha);
    }

    /**
     * Returns the cross product of the two vectors, i.e. <i>left x right</i>
     * 
     * @param i_left
     * @param i_right
     * @param o_result
     * @return
     * @see http://en.wikipedia.org/wiki/Cross_product
     */
    public static Vector3f cross(IVector3f i_left, IVector3f i_right,
            Vector3f o_result) {

        // a x b = (a2b3 − a3b2, a3b1 − a1b3, a1b2 − a2b1)

        float x = i_left.getY() * i_right.getZ() // 
                - i_left.getZ() * i_right.getY();
        float y = i_left.getZ() * i_right.getX() // 
                - i_left.getX() * i_right.getZ();
        float z = i_left.getX() * i_right.getY() // 
                - i_left.getY() * i_right.getX();

        if (o_result == null) {
            return new Vector3fImpl(x, y, z);
        } else {
            o_result.set(x, y, z);
            return o_result;
        }

    }

    /**
     * Calculates the dot product (scalar product) of the two vectors.
     * 
     * @param i_left
     * @param i_right
     * @return
     */
    public static float dot(IVector3f i_left, IVector3f i_right) {

        return i_left.getX() * i_right.getX() + i_left.getY() * i_right.getY()
                + i_left.getZ() * i_right.getZ();
    }

    /**
     * Returns the Euler angles for a rotation that orients a given vector into
     * the direction specified by a given reference vector.<br />
     * The result vector contains the rotations about the x, y and z axes. The
     * rotations must be applied in the following order: Y first, then Z, and
     * finally X.
     * 
     * @param i_vector
     *            the vector that is to be oriented
     * @param i_reference
     *            the reference vector
     * @param o_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            created
     * @return the rotation angles
     * @throws NullPointerException
     *             if the given vector or reference vector is <code>null</code>
     * 
     * @see "http://www.euclideanspace.com/maths/algebra/vectors/angleBetween/index.htm"
     * @see "http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm"
     * @see "http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/index.htm"
     */
    public static Vector3f getEulerAngles(IVector3f i_vector,
            IVector3f i_reference, Vector3f o_result) {

        if (i_vector == null)
            throw new NullPointerException("i_reference must not be null");

        if (i_reference == null)
            throw new NullPointerException("i_vector must not be null");

        Vector3f result = o_result;
        if (result == null)
            result = new Vector3fImpl();

        Vector3f vecNorm = getVector3f();
        Vector3f refNorm = getVector3f();
        Vector3f axis = getVector3f();
        try {

            // calculate axis / angle representation of the rotation
            Math3D.normalise(i_vector, vecNorm);
            Math3D.normalise(i_reference, refNorm);

            Math3D.cross(vecNorm, refNorm, axis);
            float length = axis.length();
            if (length == 0) { // vector is already oriented to reference
                result.set(0, 0, 0);
                return result;
            }

            axis.scale(1.0f / length);

            double angle = Math.acos(Math3D.dot(vecNorm, refNorm));
            double halfAngle = angle / 2;
            double s = Math.sin(halfAngle);

            // convert to quaternion representation
            double qx = axis.getX() * s;
            double qy = axis.getY() * s;
            double qz = axis.getZ() * s;
            double qw = Math.cos(halfAngle);

            // convert to euler axis representation
            double t = qx * qy + qz * qw;
            double h;
            double a;
            double b;
            if (t > 0.499) {
                h = 2 * Math.atan2(qx, qw);
                a = Math.PI / 2;
                b = 0;
            } else if (t < -0.499) {
                h = -2 * Math.atan2(qx, qw);
                a = -Math.PI / 2;
                b = 0;
            } else {
                double sqx = qx * qx;
                double sqy = qy * qy;
                double sqz = qz * qz;
                h = Math.atan2(2 * qy * qw - 2 * qx * qz, 1 - 2 * sqy - 2 * sqz);
                a = Math.asin(2 * t);
                b = Math.atan2(2 * qx * qw - 2 * qy * qz, 1 - 2 * sqx - 2 * sqz);
            }

            result.set((float) b, (float) h, (float) a);
            return result;
        } finally {
            returnVector3f(vecNorm);
            returnVector3f(refNorm);
            returnVector3f(axis);
        }
    }

    /**
     * Calculates the negative value for each component of the given vector. The
     * source and result vector may be the same object.
     * 
     * @param i_source
     *            the source vector
     * @param o_result
     *            the result vector, if <code>null</code>, a new vector will be
     *            created and returned
     * @return the result vector
     */
    public static Vector3f negate(IVector3f i_source, Vector3f o_result) {

        if (o_result == null) {
            return new Vector3fImpl(-i_source.getX(), -i_source.getY(),
                -i_source.getZ());
        } else {
            o_result.set(-i_source.getX(), -i_source.getY(), -i_source.getZ());
            return o_result;
        }
    }

    /**
     * Returns the normalized vector, that is the vector divided by its length.
     * 
     * @param i_vec
     * @param o_result
     * @return
     * @see http://en.wikipedia.org/wiki/Unit_vector
     */
    public static Vector3f normalise(IVector3f i_source, Vector3f o_result) {

        float lengthInv = 1 / i_source.length();
        return scale(lengthInv, i_source, o_result);
    }

    /**
     * Multiplies all elements of the vector with the given scale value.
     * 
     * @param scale
     * @param i_sourceVector3f
     * @param o_resultVector3f
     * @return
     */
    public static Vector3f scale(float scale, IVector3f i_source,
            Vector3f o_result) {

        if (o_result == null) {
            return new Vector3fImpl(scale * i_source.getX(), scale
                    * i_source.getY(), scale * i_source.getZ());
        } else {
            o_result.set(scale * i_source.getX(), scale * i_source.getY(),
                scale * i_source.getZ());
            return o_result;
        }
    }

    /**
     * Subtracts vector right from left, i.e. returns left-right.
     * 
     * @param i_left
     * @param i_right
     * @param o_result
     * @return
     */
    public static Vector3f sub(IVector3f i_left, IVector3f i_right,
            Vector3f o_result) {

        if (o_result == null) {
            return new Vector3fImpl(i_left.getX() - i_right.getX(),
                i_left.getY() - i_right.getY(), i_left.getZ() - i_right.getZ());
        } else {
            o_result.set(i_left.getX() - i_right.getX(), i_left.getY()
                    - i_right.getY(), i_left.getZ() - i_right.getZ());
            return o_result;
        }
    }

    /**
     * Translates a vector by given x, y, and z value. Basically it's the same
     * as adding a vector (x,y,z) to the vector.
     * 
     * @param i_source
     * @param x
     * @param y
     * @param z
     * @param o_result
     * @return
     */
    public static Vector3f translate(IVector3f i_source, float x, float y,
            float z, Vector3f o_result) {

        if (o_result == null) {
            return new Vector3fImpl(x + i_source.getX(), y + i_source.getY(), z
                    + i_source.getZ());
        } else {
            o_result.set(x + i_source.getX(), y + i_source.getY(), z
                    + i_source.getZ());
            return o_result;
        }
    }

}
