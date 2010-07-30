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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VectorChar {

	private float m_advX;

	private float m_advY;

	private float[][] m_fans;

	private int m_numVertices = 0;

	private float[][] m_strips;

	private float[][] m_tris;

	public VectorChar(float i_advX, float i_advY) {
		m_advX = i_advX;
		m_advY = i_advY;
	}

	public int compileFans(FloatBuffer i_buffer, int i_index,
		IntBuffer i_indices, IntBuffer i_counts, float i_x, float i_y) {
		if (m_fans == null || m_fans.length == 0)
			return i_index;

		int index = i_index;
		for (int i = 0; i < m_fans.length; i++) {
			for (int j = 0; i < m_fans[i].length / 2; j++) {
				i_buffer.put(m_fans[i][2 * j] + i_x);
				i_buffer.put(m_fans[i][2 * j + 1] + i_y);
			}
			i_indices.put(index);
			i_counts.put(m_fans[i].length);
			index += m_fans[i].length;
		}
		return index;
	}

	public int compileStrips(FloatBuffer i_buffer, int i_index,
		IntBuffer i_indices, IntBuffer i_counts, float i_x, float i_y) {
		if (m_strips == null || m_strips.length == 0)
			return i_index;

		int index = i_index;
		for (int i = 0; i < m_strips.length; i++) {
			for (int j = 0; i < m_strips[i].length / 2; j++) {
				i_buffer.put(m_strips[i][2 * j] + i_x);
				i_buffer.put(m_strips[i][2 * j + 1] + i_y);
			}
			i_indices.put(index);
			i_counts.put(m_strips[i].length);
			index += m_strips[i].length;
		}
		return index;
	}

	public int compileTriangles(FloatBuffer i_buffer, int i_index,
		IntBuffer i_indices, IntBuffer i_counts, float i_x, float i_y) {
		if (m_tris == null || m_tris.length == 0)
			return i_index;

		int index = i_index;
		for (int i = 0; i < m_tris.length; i++) {
			for (int j = 0; i < m_tris[i].length / 2; j++) {
				i_buffer.put(m_tris[i][2 * j] + i_x);
				i_buffer.put(m_tris[i][2 * j + 1] + i_y);
			}
			i_indices.put(index);
			i_counts.put(m_tris[i].length);
			index += m_tris[i].length;
		}
		return index;
	}

	public float getAdvanceX() {
		return m_advX;
	}

	public float getAdvanceY() {
		return m_advY;
	}

	public int getNumFans() {
		return m_fans == null ? 0 : m_fans.length;
	}

	public int getNumStrips() {
		return m_strips == null ? 0 : m_strips.length;
	}

	public int getNumTris() {
		return m_tris == null ? 0 : m_tris.length;
	}

	public int getNumVertices() {
		return m_numVertices;
	}

	public void setTriangleFans(float[][] i_data, int i_num) {
		m_fans = new float[i_num][];
		for (int i = 0; i < i_num; i++) {
			m_fans[i] = i_data[i];
			m_numVertices += m_fans[i].length / 2;
		}
	}

	public void setTriangles(float[][] i_data, int i_num) {
		m_tris = new float[i_num][];
		for (int i = 0; i < i_num; i++) {
			m_tris[i] = i_data[i];
			m_numVertices += m_tris[i].length / 2;
		}
	}

	public void setTriangleStrips(float[][] i_data, int i_num) {
		m_strips = new float[i_num][];
		for (int i = 0; i < i_num; i++) {
			m_strips[i] = i_data[i];
			m_numVertices += m_strips[i].length / 2;
		}
	}
}