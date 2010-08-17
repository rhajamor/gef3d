/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.font;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

/**
 * This instance of {@link GLUtessellatorCallbackAdapter} collects vertex data
 * that occurs during the tesselation of a string. After each character, the
 * vertex data, which consists of triangle strips, triangle fans and triangle
 * sets, can be collected by calling {@link #createVectorChar(float, float)}. To
 * reset the internal state of this callback object, the {@link #reset()}
 * function must be called.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 17.08.2010
 */
public class LwjglAWTGlyphCallback extends GLUtessellatorCallbackAdapter {

	private float[][] m_fans = new float[3][];

	private int m_index;

	private int m_numFans = 0;

	private int m_numStrips = 0;

	private int m_numSets = 0;

	private float[][] m_strips = new float[3][];

	private float[][] m_sets = new float[3][];

	private int m_type;

	private float[] m_vertices = new float[16];

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#begin(int)
	 */
	@Override
	public void begin(int i_type) {
		m_type = i_type;
		m_index = 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#combine(double[],
	 *      java.lang.Object[], float[], java.lang.Object[])
	 */
	@Override
	public void combine(double[] i_coords, Object[] i_data, float[] i_weight,
		Object[] i_outData) {
		i_outData[0] = new float[] { (float) i_coords[0], (float) i_coords[1] };
	}

	/**
	 * Creates an instance of {@link VectorChar} that represents the current
	 * character data.
	 * 
	 * @param i_advX the value by which the X position must be advanced after
	 *            this character was rendered
	 * @param i_advY the value by which the Y position must be advanced after
	 *            this character was rendered
	 * @return the {@link VectorChar} instance that represents the current
	 *         character data
	 */
	public VectorChar createVectorChar(float i_advX, float i_advY) {
		VectorChar result = new VectorChar(i_advX, i_advY);
		if (m_numFans > 0)
			result.setTriangleFans(m_fans, m_numFans);
		if (m_numStrips > 0)
			result.setTriangleStrips(m_strips, m_numStrips);
		if (m_numSets > 0)
			result.setTriangleSets(m_sets, m_numSets);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#end()
	 */
	@Override
	public void end() {
		switch (m_type) {
		case GL_TRIANGLE_FAN:
			if (m_numFans == m_fans.length)
				m_fans = resize(m_fans);
			float[] fan = new float[m_index];
			System.arraycopy(m_vertices, 0, fan, 0, fan.length);
			m_fans[m_numFans++] = fan;
			break;
		case GL_TRIANGLE_STRIP:
			if (m_numStrips == m_strips.length)
				m_strips = resize(m_strips);
			float[] strip = new float[m_index];
			System.arraycopy(m_vertices, 0, strip, 0, strip.length);
			m_strips[m_numStrips++] = strip;
			break;
		case GL_TRIANGLES:
			if (m_numSets == m_sets.length)
				m_sets = resize(m_sets);
			float[] tris = new float[m_index];
			System.arraycopy(m_vertices, 0, tris, 0, tris.length);
			m_sets[m_numSets++] = tris;
			break;
		default:
			throw new IllegalStateException(
				"unknown tesselation primitive type: " + m_type);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#error(int)
	 */
	@Override
	public void error(int i_errnum) {
		throw new RuntimeException("caught error during polygon tesselation: "
			+ i_errnum);
	}

	/**
	 * Resets the internal state of this callback object.
	 */
	public void reset() {
		m_numFans = 0;
		m_numStrips = 0;
		m_numSets = 0;
	}

	private float[][] resize(float[][] i_array) {
		float[][] resized = new float[2 * i_array.length][];
		System.arraycopy(i_array, 0, resized, 0, i_array.length);
		return resized;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#vertex(java.lang.Object)
	 */
	@Override
	public void vertex(Object i_vertexData) {
		if (m_index == m_vertices.length) {
			float[] temp = m_vertices;
			m_vertices = new float[temp.length * 2];
			System.arraycopy(temp, 0, m_vertices, 0, temp.length);
		}

		float[] vertex = (float[]) i_vertexData;
		m_vertices[m_index++] = vertex[0];
		m_vertices[m_index++] = vertex[1];
	}
}