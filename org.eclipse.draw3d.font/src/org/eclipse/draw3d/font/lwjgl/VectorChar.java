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
package org.eclipse.draw3d.font.lwjgl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Stores vector data for a single character.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 17.08.2010
 */
public class VectorChar {

	private float[][] m_fans;

	private int m_numVertices = 0;

	private float[][] m_strips;

	private float[][] m_sets;

	/**
	 * Compiles the triangle fans stored in this character into the given
	 * buffers.
	 * 
	 * @param i_vertices the vertex buffer
	 * @param i_index the current index into the vertex buffer
	 * @param i_indices the index buffer
	 * @param i_counts the vertex count buffer
	 * @return the new index into the vertex buffer
	 */
	public int compileFans(FloatBuffer i_vertices, int i_index,
		IntBuffer i_indices, IntBuffer i_counts) {
		if (m_fans == null || m_fans.length == 0)
			return i_index;

		int index = i_index;
		for (int i = 0; i < m_fans.length; i++) {
			int count = m_fans[i].length / 2;
			for (int j = 0; j < count; j++) {
				i_vertices.put(m_fans[i][2 * j]);
				i_vertices.put(m_fans[i][2 * j + 1]);
			}
			i_indices.put(index);
			i_counts.put(count);
			index += count;
		}
		return index;
	}

	/**
	 * Compiles the triangle strips stored in this character into the given
	 * buffers.
	 * 
	 * @param i_vertices the vertex buffer
	 * @param i_index the current index into the vertex buffer
	 * @param i_indices the index buffer
	 * @param i_counts the vertex count buffer
	 * @return the new index into the vertex buffer
	 */
	public int compileStrips(FloatBuffer i_vertices, int i_index,
		IntBuffer i_indices, IntBuffer i_counts) {
		if (m_strips == null || m_strips.length == 0)
			return i_index;

		int index = i_index;
		for (int i = 0; i < m_strips.length; i++) {
			int count = m_strips[i].length / 2;
			for (int j = 0; j < count; j++) {
				i_vertices.put(m_strips[i][2 * j]);
				i_vertices.put(m_strips[i][2 * j + 1]);
			}
			i_indices.put(index);
			i_counts.put(count);
			index += count;
		}
		return index;
	}

	/**
	 * Compiles the triangle sets stored in this character into the given
	 * buffers.
	 * 
	 * @param i_vertices the vertex buffer
	 * @param i_index the current index into the vertex buffer
	 * @param i_indices the index buffer
	 * @param i_counts the vertex count buffer
	 * @return the new index into the vertex buffer
	 */
	public int compileSets(FloatBuffer i_vertices, int i_index,
		IntBuffer i_indices, IntBuffer i_counts) {
		if (m_sets == null || m_sets.length == 0)
			return i_index;

		int index = i_index;
		for (int i = 0; i < m_sets.length; i++) {
			int count = m_sets[i].length / 2;
			for (int j = 0; j < count; j++) {
				i_vertices.put(m_sets[i][2 * j]);
				i_vertices.put(m_sets[i][2 * j + 1]);
			}
			i_indices.put(index);
			i_counts.put(count);
			index += count;
		}
		return index;
	}

	/**
	 * Returns the number of triangle fans stored in this char.
	 * 
	 * @return the number of triangle fans
	 */
	public int getNumFans() {
		return m_fans == null ? 0 : m_fans.length;
	}

	/**
	 * Returns the number of triangle strips stored in this char.
	 * 
	 * @return the number of triangle strips
	 */
	public int getNumStrips() {
		return m_strips == null ? 0 : m_strips.length;
	}

	/**
	 * Returns the number of triangle sets stored in this char.
	 * 
	 * @return the number of triangle sets
	 */
	public int getNumSets() {
		return m_sets == null ? 0 : m_sets.length;
	}

	/**
	 * Returns the total number of vertices in this char.
	 * 
	 * @return the total number of vertices
	 */
	public int getNumVertices() {
		return m_numVertices;
	}

	/**
	 * Sets the triangle fans of this char. The given two dimensional array
	 * stores the individual fans in the first dimension and the vector data for
	 * each fan in the second dimension. The first dimension may be larger than
	 * the actual number of fans, which is indicated by the second parameter
	 * 
	 * @param i_data the triangle fans
	 * @param i_num the actual number of triangle fans
	 */
	public void setTriangleFans(float[][] i_data, int i_num) {
		m_fans = new float[i_num][];
		for (int i = 0; i < i_num; i++) {
			m_fans[i] = i_data[i];
			m_numVertices += m_fans[i].length / 2;
		}
	}

	/**
	 * Sets the triangle sets of this char. The given two dimensional array
	 * stores the individual sets in the first dimension and the vector data for
	 * each set in the second dimension. The first dimension may be larger than
	 * the actual number of sets, which is indicated by the second parameter
	 * 
	 * @param i_data the triangle sets
	 * @param i_num the actual number of triangle sets
	 */
	public void setTriangleSets(float[][] i_data, int i_num) {
		m_sets = new float[i_num][];
		for (int i = 0; i < i_num; i++) {
			m_sets[i] = i_data[i];
			m_numVertices += m_sets[i].length / 2;
		}
	}

	/**
	 * Sets the triangle strips of this char. The given two dimensional array
	 * stores the individual strips in the first dimension and the vector data
	 * for each strip in the second dimension. The first dimension may be larger
	 * than the actual number of strips, which is indicated by the second
	 * parameter
	 * 
	 * @param i_data the triangle strips
	 * @param i_num the actual number of triangle strips
	 */
	public void setTriangleStrips(float[][] i_data, int i_num) {
		m_strips = new float[i_num][];
		for (int i = 0; i < i_num; i++) {
			m_strips[i] = i_data[i];
			m_numVertices += m_strips[i].length / 2;
		}
	}
}