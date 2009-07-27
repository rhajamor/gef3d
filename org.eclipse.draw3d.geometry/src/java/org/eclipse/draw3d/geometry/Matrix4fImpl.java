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

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Implementation of a {@link Matrix4f} backed by single float fields.
 * <p>
 * For an explanation of row- and column-major format, see interface
 * {@link IMatrix4f}.
 * </p>
 * 
 * <p>The matrix is internally stored using float fields. This is how these
 * fields are stored:
 * <table>
 * <tr><td>a11</td><td>a12</td><td>a13</td><td>a14</td></tr>
 * <tr><td>a21</td><td>a22</td><td>a23</td><td>a24</td></tr>
 * <tr><td>a31</td><td>a32</td><td>a33</td><td>a34</td></tr>
 * <tr><td>a41</td><td>a42</td><td>a43</td><td>a44</td></tr>
 * </table>
 * This is how matrix algorithms are usually explained.
 * </p>
 * 
 * @author Jens von Pilgrim, Matthias Thiele
 * @version $Revision$
 * @since Dec 16, 2008
 */
public class Matrix4fImpl implements Matrix4f, Serializable, Cloneable {

	public float a11;

	public float a12;

	public float a13;

	public float a14;

	public float a21;

	public float a22;

	public float a23;

	public float a24;

	public float a31;

	public float a32;

	public float a33;

	public float a34;

	public float a41;

	public float a42;

	public float a43;

	public float a44;

	/**
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Casts any {@link IMatrix4f} matrix into a {@link Matrix4fImpl},#
	 * either by casting or copying. The returned object is of type
	 * {@link Matrix4fImpl} and thus it is mutable. Since the object may
	 * be identical to the given, immutable one, the client of this method
	 * must ensure not to modify the returned object. This method is
	 * heavily used in {@link Math3D} for performance issues.
	 * 
	 * @param i_sourceMatrix4f The source matrix.
	 * @return Matrix4fImpl which is equals to given IMatrix4f
	 */
	static Matrix4fImpl cast(IMatrix4f i_sourceMatrix4f) {
		if (i_sourceMatrix4f instanceof Matrix4fImpl) {
			return (Matrix4fImpl) i_sourceMatrix4f;
		} else {
			return new Matrix4fImpl(i_sourceMatrix4f);
		}
	}

	/**
	 * Empty constructor, creates this matrix as an identity matrix. This
	 * constructor is pretty fast.
	 */
	public Matrix4fImpl() {
		// all values are initialized with 0f, see Java Spec
		a11 = 1;
		a22 = 1;
		a33 = 1;
		a44 = 1;
	}

	/**
	 * Constructs a matrix4f from a source matrix.
	 * 
	 * @param i_sourceMatrix4f The source matrix.
	 */
	public Matrix4fImpl(final IMatrix4f i_sourceMatrix4f) {
		set(i_sourceMatrix4f);
	}

	/**
	 * Creates this matrix and sets its values. The values are read in row-major
	 * or column-major (OpenGL) format, depending on parameter
	 * <code>i_bColumnMajor</code>.
	 * 
	 * @param i_buffer the buffer with the source values
	 * @param i_bColumnMajor the matrix is stored in column major (OpenGL)
	 *            format
	 */
	public Matrix4fImpl(final FloatBuffer i_buffer, final boolean i_bColumnMajor) {
		if (!i_bColumnMajor) {
			setRowMajor(i_buffer);
		} else {
			setColumnMajor(i_buffer);
		}
	}

	/**
	 * Constructs a matrix4f from an array of float, starting at offset 0. The
	 * values are read in row-major or column-major (OpenGL) format, depending
	 * on parameter <code>i_bColumnMajor</code>.
	 * 
	 * @param i_arrayOfFloat Values are read from this array
	 * @param i_bColumnMajor the matrix is stored in column major (OpenGL)
	 *            format
	 */
	public Matrix4fImpl(final float[] i_arrayOfFloat, final boolean i_bColumnMajor) {
		this(i_arrayOfFloat, i_bColumnMajor, 0);
	}

	/**
	 * Constructs a matrix4f from an array of float, starting at given offset.
	 * The values are read in row-major or column-major (OpenGL) format,
	 * depending on parameter <code>i_bColumnMajor</code>.
	 * 
	 * @param i_arrayOfFloat Values are read from this array
	 * @param i_bColumnMajor the matrix is stored in column major (OpenGL)
	 *            format
	 * @param i_iOffset Get values from array with this offset.
	 */
	public Matrix4fImpl(final float[] i_arrayOfFloat, final boolean i_bColumnMajor,
			int i_iOffset) {
		if (i_bColumnMajor) {
			this.a11 = i_arrayOfFloat[i_iOffset++];
			this.a21 = i_arrayOfFloat[i_iOffset++];
			this.a31 = i_arrayOfFloat[i_iOffset++];
			this.a41 = i_arrayOfFloat[i_iOffset++];
			this.a12 = i_arrayOfFloat[i_iOffset++];
			this.a22 = i_arrayOfFloat[i_iOffset++];
			this.a32 = i_arrayOfFloat[i_iOffset++];
			this.a42 = i_arrayOfFloat[i_iOffset++];
			this.a13 = i_arrayOfFloat[i_iOffset++];
			this.a23 = i_arrayOfFloat[i_iOffset++];
			this.a33 = i_arrayOfFloat[i_iOffset++];
			this.a43 = i_arrayOfFloat[i_iOffset++];
			this.a14 = i_arrayOfFloat[i_iOffset++];
			this.a24 = i_arrayOfFloat[i_iOffset++];
			this.a34 = i_arrayOfFloat[i_iOffset++];
			this.a44 = i_arrayOfFloat[i_iOffset];
		} else {
			this.a11 = i_arrayOfFloat[i_iOffset++];
			this.a12 = i_arrayOfFloat[i_iOffset++];
			this.a13 = i_arrayOfFloat[i_iOffset++];
			this.a14 = i_arrayOfFloat[i_iOffset++];
			this.a21 = i_arrayOfFloat[i_iOffset++];
			this.a22 = i_arrayOfFloat[i_iOffset++];
			this.a23 = i_arrayOfFloat[i_iOffset++];
			this.a24 = i_arrayOfFloat[i_iOffset++];
			this.a31 = i_arrayOfFloat[i_iOffset++];
			this.a32 = i_arrayOfFloat[i_iOffset++];
			this.a33 = i_arrayOfFloat[i_iOffset++];
			this.a34 = i_arrayOfFloat[i_iOffset++];
			this.a41 = i_arrayOfFloat[i_iOffset++];
			this.a42 = i_arrayOfFloat[i_iOffset++];
			this.a43 = i_arrayOfFloat[i_iOffset++];
			this.a44 = i_arrayOfFloat[i_iOffset];
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.Matrix4f#set(org.eclipse.draw3d.geometry.IMatrix4f)
	 */
	public void set(final IMatrix4f i_sourceMatrix4f) {
		Matrix4fImpl src = cast(i_sourceMatrix4f);

		this.a11 = src.a11;
		this.a12 = src.a12;
		this.a13 = src.a13;
		this.a14 = src.a14;
		this.a21 = src.a21;
		this.a22 = src.a22;
		this.a23 = src.a23;
		this.a24 = src.a24;
		this.a31 = src.a31;
		this.a32 = src.a32;
		this.a33 = src.a33;
		this.a34 = src.a34;
		this.a41 = src.a41;
		this.a42 = src.a42;
		this.a43 = src.a43;
		this.a44 = src.a44;
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.Matrix4f#setRowMajor(java.nio.FloatBuffer)
	 */
	public void setRowMajor(final FloatBuffer i_floatBuffer) {
		this.a11 = i_floatBuffer.get();
		this.a12 = i_floatBuffer.get();
		this.a13 = i_floatBuffer.get();
		this.a14 = i_floatBuffer.get();
		this.a21 = i_floatBuffer.get();
		this.a22 = i_floatBuffer.get();
		this.a23 = i_floatBuffer.get();
		this.a24 = i_floatBuffer.get();
		this.a31 = i_floatBuffer.get();
		this.a32 = i_floatBuffer.get();
		this.a33 = i_floatBuffer.get();
		this.a34 = i_floatBuffer.get();
		this.a41 = i_floatBuffer.get();
		this.a42 = i_floatBuffer.get();
		this.a43 = i_floatBuffer.get();
		this.a44 = i_floatBuffer.get();
	}
	
	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.Matrix#setRowMajor(float[])
	 */
	public void setRowMajor(float[] i_arrayOfFloat) {
		a11 = i_arrayOfFloat[0];
		a12 = i_arrayOfFloat[1];
		a13 = i_arrayOfFloat[2];
		a14 = i_arrayOfFloat[3];
		
		a21 = i_arrayOfFloat[4];
		a22 = i_arrayOfFloat[5];
		a23 = i_arrayOfFloat[6];
		a24 = i_arrayOfFloat[7];
		
		a31 = i_arrayOfFloat[8];
		a32 = i_arrayOfFloat[9];
		a33 = i_arrayOfFloat[10];
		a34 = i_arrayOfFloat[11];
		
		a41 = i_arrayOfFloat[12];
		a42 = i_arrayOfFloat[13];
		a43 = i_arrayOfFloat[14];
		a44 = i_arrayOfFloat[15];
		
	}


	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.Matrix4f#setColumnMajor(java.nio.FloatBuffer)
	 */
	public void setColumnMajor(final FloatBuffer i_floatBuffer) {
		this.a11 = i_floatBuffer.get();
		this.a21 = i_floatBuffer.get();
		this.a31 = i_floatBuffer.get();
		this.a41 = i_floatBuffer.get();
		this.a12 = i_floatBuffer.get();
		this.a22 = i_floatBuffer.get();
		this.a32 = i_floatBuffer.get();
		this.a42 = i_floatBuffer.get();
		this.a13 = i_floatBuffer.get();
		this.a23 = i_floatBuffer.get();
		this.a33 = i_floatBuffer.get();
		this.a43 = i_floatBuffer.get();
		this.a14 = i_floatBuffer.get();
		this.a24 = i_floatBuffer.get();
		this.a34 = i_floatBuffer.get();
		this.a44 = i_floatBuffer.get();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.Matrix4f#setIdentity()
	 */
	public void setIdentity() {
		this.a11 = 1.0f;
		this.a12 = 0.0f;
		this.a13 = 0.0f;
		this.a14 = 0.0f;
		this.a21 = 0.0f;
		this.a22 = 1.0f;
		this.a23 = 0.0f;
		this.a24 = 0.0f;
		this.a31 = 0.0f;
		this.a32 = 0.0f;
		this.a33 = 1.0f;
		this.a34 = 0.0f;
		this.a41 = 0.0f;
		this.a42 = 0.0f;
		this.a43 = 0.0f;
		this.a44 = 1.0f;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.Matrix4f#setZero()
	 */
	public void setZero() {
		this.a11 = 0.0f;
		this.a12 = 0.0f;
		this.a13 = 0.0f;
		this.a14 = 0.0f;
		this.a21 = 0.0f;
		this.a22 = 0.0f;
		this.a23 = 0.0f;
		this.a24 = 0.0f;
		this.a31 = 0.0f;
		this.a32 = 0.0f;
		this.a33 = 0.0f;
		this.a34 = 0.0f;
		this.a41 = 0.0f;
		this.a42 = 0.0f;
		this.a43 = 0.0f;
		this.a44 = 0.0f;
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.IMatrix4f#toBufferRowMajor(java.nio.FloatBuffer)
	 */
	public void toBufferRowMajor(final FloatBuffer o_floatBuffer) {
		o_floatBuffer.put(a11);
		o_floatBuffer.put(a12);
		o_floatBuffer.put(a13);
		o_floatBuffer.put(a14);
		o_floatBuffer.put(a21);
		o_floatBuffer.put(a22);
		o_floatBuffer.put(a23);
		o_floatBuffer.put(a24);
		o_floatBuffer.put(a31);
		o_floatBuffer.put(a32);
		o_floatBuffer.put(a33);
		o_floatBuffer.put(a34);
		o_floatBuffer.put(a41);
		o_floatBuffer.put(a42);
		o_floatBuffer.put(a43);
		o_floatBuffer.put(a44);
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.IMatrix4f#toBufferColumnMajor(java.nio.FloatBuffer)
	 */
	public void toBufferColumnMajor(final FloatBuffer o_floatBuffer) {
		o_floatBuffer.put(a11);
		o_floatBuffer.put(a21);
		o_floatBuffer.put(a31);
		o_floatBuffer.put(a41);
		o_floatBuffer.put(a12);
		o_floatBuffer.put(a22);
		o_floatBuffer.put(a32);
		o_floatBuffer.put(a42);
		o_floatBuffer.put(a13);
		o_floatBuffer.put(a23);
		o_floatBuffer.put(a33);
		o_floatBuffer.put(a43);
		o_floatBuffer.put(a14);
		o_floatBuffer.put(a24);
		o_floatBuffer.put(a34);
		o_floatBuffer.put(a44);
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.IMatrix4f#toArrayRowMajor(float[])
	 */
	public void toArrayRowMajor(final float[] o_arrayOfFloat)  {
		this.toArrayRowMajor(o_arrayOfFloat, 0);
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.IMatrix4f#toArrayRowMajor(float[], int)
	 */
	public void toArrayRowMajor(final float[] o_arrayOfFloat, int i_iOffset) {
		o_arrayOfFloat[i_iOffset++] = a11;
		o_arrayOfFloat[i_iOffset++] = a12;
		o_arrayOfFloat[i_iOffset++] = a13;
		o_arrayOfFloat[i_iOffset++] = a14;
		o_arrayOfFloat[i_iOffset++] = a21;
		o_arrayOfFloat[i_iOffset++] = a22;
		o_arrayOfFloat[i_iOffset++] = a23;
		o_arrayOfFloat[i_iOffset++] = a24;
		o_arrayOfFloat[i_iOffset++] = a31;
		o_arrayOfFloat[i_iOffset++] = a32;
		o_arrayOfFloat[i_iOffset++] = a33;
		o_arrayOfFloat[i_iOffset++] = a34;
		o_arrayOfFloat[i_iOffset++] = a41;
		o_arrayOfFloat[i_iOffset++] = a42;
		o_arrayOfFloat[i_iOffset++] = a43;
		o_arrayOfFloat[i_iOffset] = a44;
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.IMatrix4f#toArrayColumnMajor(float[])
	 */
	public void toArrayColumnMajor(final float[] o_arrayOfFloat) {
		this.toArrayColumnMajor(o_arrayOfFloat, 0);
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.geometry.IMatrix4f#toArrayColumnMajor(float[], int)
	 */
	public void toArrayColumnMajor(final float[] o_arrayOfFloat, int i_iOffset) {
		o_arrayOfFloat[i_iOffset++] = a11;
		o_arrayOfFloat[i_iOffset++] = a21;
		o_arrayOfFloat[i_iOffset++] = a31;
		o_arrayOfFloat[i_iOffset++] = a41;
		o_arrayOfFloat[i_iOffset++] = a12;
		o_arrayOfFloat[i_iOffset++] = a22;
		o_arrayOfFloat[i_iOffset++] = a32;
		o_arrayOfFloat[i_iOffset++] = a42;
		o_arrayOfFloat[i_iOffset++] = a13;
		o_arrayOfFloat[i_iOffset++] = a23;
		o_arrayOfFloat[i_iOffset++] = a33;
		o_arrayOfFloat[i_iOffset++] = a43;
		o_arrayOfFloat[i_iOffset++] = a14;
		o_arrayOfFloat[i_iOffset++] = a24;
		o_arrayOfFloat[i_iOffset++] = a34;
		o_arrayOfFloat[i_iOffset++] = a44;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.IMatrix4f#equals(org.eclipse.draw3d.geometry.IMatrix4f)
	 */
	public boolean equals(final IMatrix4f i_anotherMatrix4f) {
		if (this == i_anotherMatrix4f)
			return true;
		if (i_anotherMatrix4f == null)
			return false;
		Matrix4fImpl sm = cast(i_anotherMatrix4f);
		return a11 == sm.a11 && a12 == sm.a12 && a13 == sm.a13 && a14==sm.a14 && // 
			   a21 == sm.a21 && a22 == sm.a22 && a23 == sm.a23 && a24==sm.a24 && //
			   a31 == sm.a31 && a32 == sm.a32 && a33 == sm.a33 && a34==sm.a34 && //
			   a41 == sm.a41 && a42 == sm.a42 && a43 == sm.a43 && a44==sm.a44;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Matrix4fImpl(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(TO_STRING_FORMAT, String.valueOf(a11), String
				.valueOf(a12), String.valueOf(a13), String.valueOf(a14), String
				.valueOf(a21), String.valueOf(a22), String.valueOf(a23), String
				.valueOf(a24), String.valueOf(a31), String.valueOf(a32), String
				.valueOf(a33), String.valueOf(a34),
				String.valueOf(a41), String.valueOf(a42), String
				.valueOf(a43), String.valueOf(a44));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		float[] af = new float[16];
		toArrayRowMajor(af);
		return Arrays.hashCode(af);
	}
	
	/** 
	 * {@inheritDoc}
	 * Returns 16.
	 * 
	 * @see org.eclipse.draw3d.geometry.IMatrix#size()
	 */
	public int size() {
		return 16;
	}

}
