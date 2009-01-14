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
 * TempVectors is a utility class with temporary vectors. Other static methods
 * can be found in Math3D. only static methods for vector3f and other 3D related
 * operation or constants not found in {@link org.eclipse.draw3d.geometry}.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 08.11.2007
 */
public final class TempVectors
{

	private static final Vector3f TMP_V3_1 = new Vector3fImpl();

	private static final Vector3f TMP_V3_2 = new Vector3fImpl();

	private static final Vector3f TMP_V3_3 = new Vector3fImpl();

	/**
	 * Returns the Euler angles for a rotation that orients a given vector into
	 * the direction specified by a given reference vector.<br /> The result
	 * vector contains the rotations about the x, y and z axes. The rotations
	 * must be applied in the following order: Y first, then Z, and finally X.
	 * 
	 * @param i_vector the vector that is to be oriented
	 * @param i_reference the reference vector
	 * @param o_result the result vector, if <code>null</code>, a new vector
	 *            will be created
	 * @return the rotation angles
	 * @throws NullPointerException if the given vector or reference vector is
	 *             <code>null</code>
	 * @see http 
	 *      ://www.euclideanspace.com/maths/algebra/vectors/angleBetween/index
	 *      .htm
	 * @see http://www.euclideanspace.com/maths/geometry/rotations/conversions/
	 *      angleToQuaternion/index.htm
	 * @see http://www.euclideanspace.com/maths/geometry/rotations/conversions/
	 *      quaternionToEuler/index.htm
	 */
	public static Vector3f getEulerAngles(IVector3f i_vector,
			IVector3f i_reference, Vector3f o_result)
	{

		if (i_vector == null)
			throw new NullPointerException("i_reference must not be null");

		if (i_reference == null)
			throw new NullPointerException("i_vector must not be null");

		if (o_result == null)
			o_result = new Vector3fImpl();

		// calculate axis / angle representation of the rotation
		TMP_V3_1.set(i_vector);
		TMP_V3_2.set(i_reference);

		Math3D.normalise(TMP_V3_1, TMP_V3_1);
		Math3D.normalise(TMP_V3_2, TMP_V3_2);

		Vector3f axis = Math3D.cross(TMP_V3_1, TMP_V3_2, TMP_V3_3);
		float length = axis.length();
		if (length == 0)
		{ // vector is already oriented to reference
			o_result.set(0, 0, 0);
			return o_result;
		}

		axis.scale(1.0f / length);

		double angle = Math.acos(Math3D.dot(TMP_V3_1, TMP_V3_2));
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
		if (t > 0.499)
		{
			h = 2 * Math.atan2(qx, qw);
			a = Math.PI / 2;
			b = 0;
		}
		else if (t < -0.499)
		{
			h = -2 * Math.atan2(qx, qw);
			a = -Math.PI / 2;
			b = 0;
		}
		else
		{
			double sqx = qx * qx;
			double sqy = qy * qy;
			double sqz = qz * qz;
			h = Math.atan2(2 * qy * qw - 2 * qx * qz, 1 - 2 * sqy - 2 * sqz);
			a = Math.asin(2 * t);
			b = Math.atan2(2 * qx * qw - 2 * qy * qz, 1 - 2 * sqx - 2 * sqz);
		}

		o_result.set((float) b, (float) h, (float) a);

		return o_result;
	}

	private TempVectors()
	{

		// non-instantiable class
	}

}
