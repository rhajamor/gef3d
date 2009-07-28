/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Math3DMatrix3fTest There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Dec 17, 2008
 */
public class Math3DMatrix3fTest {

	public static float PREC = 0.00001f;

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#add(org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testAddIMatrix3fIMatrix3fMatrix3f() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#sub(org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testSubIMatrix3fIMatrix3fMatrix3f() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#mul(org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testMulIMatrix3fIMatrix3fMatrix3f() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#transpose(org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testTransposeIMatrix3fMatrix3f() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#determinant(org.eclipse.draw3d.geometry.IMatrix3f)}
	 * .
	 */
	@Test
	public void testDeterminantIMatrix3f() {
		float[] mf = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Matrix3f m = new Matrix3fImpl(mf, false);
		assertEquals(0, Math3D.determinant(m));
		mf = new float[] { 1, 3, 9, 5, 7, 9, 1, 2, 3 };
		m = new Matrix3fImpl(mf, false);
		assertEquals(12, Math3D.determinant(m));
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#invert(org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testInvertIMatrix3fMatrix3f() {
		float[] mf = { 1, 3, 9, 5, 7, 9, 1, 2, 3 };
		Matrix3f m = new Matrix3fImpl(mf, false);
		mf = new float[] { 0.25f, 0.75f, -3, -0.5f, -0.5f, 3, 0.25f,
				0.0833333f, -0.666667f };
		Matrix3f e = new Matrix3fImpl(mf, false);
		Matrix3f inv = Math3D.invert(m, null);

		if (!Math3D.equals(e, inv, PREC)) {
			fail("Invert failed, expected" + e + "was" + inv);
		}

	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#adjugate(org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testAdjugateIMatrix3fMatrix3f() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#mul(float, org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testMulFloatIMatrix3fMatrix3f() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#negate(org.eclipse.draw3d.geometry.IMatrix3f, org.eclipse.draw3d.geometry.Matrix3f)}
	 * .
	 */
	@Test
	public void testNegateIMatrix3fMatrix3f() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#det(float, float, float, float, float, float, float, float, float)}
	 * .
	 */
	@Test
	public void testDetFloatFloatFloatFloatFloatFloatFloatFloatFloat() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.draw3d.geometry.Math3DMatrix3f#negdet(float, float, float, float, float, float, float, float, float)}
	 * .
	 */
	@Test
	public void testNegdetFloatFloatFloatFloatFloatFloatFloatFloatFloat() {
		fail("Not yet implemented");
	}

}
