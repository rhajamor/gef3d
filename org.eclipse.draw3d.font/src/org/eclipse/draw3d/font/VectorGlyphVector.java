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
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.draw3d.util.BufferUtils;
import org.eclipse.draw3d.util.Draw3DCache;
import org.lwjgl.opengl.GL15;

/**
 * VectorText There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public class VectorGlyphVector implements IDraw3DGlyphVector {

	private boolean m_disposed = false;

	private IntBuffer m_fanIdx;

	private IntBuffer m_fanCnt;

	private IntBuffer m_stripCnt;

	private IntBuffer m_triCnt;

	private IntBuffer m_stripIdx;

	private IntBuffer m_triIdx;

	private int m_bufferId;

	public VectorGlyphVector(VectorChar[] i_chars) {
		if (i_chars == null)
			throw new NullPointerException("i_chars must not be null");

		// create vertex buffer
		int numFans = 0;
		int numStrips = 0;
		int numTris = 0;
		int numVertices = 0;
		for (int i = 0; i < i_chars.length; i++) {
			numFans += i_chars[i].getNumFans();
			numStrips += i_chars[i].getNumStrips();
			numTris += i_chars[i].getNumTris();
			numVertices += i_chars[i].getNumVertices();
		}

		if (numFans > 0) {
			m_fanIdx = BufferUtils.createIntBuffer(numFans);
			m_fanCnt = BufferUtils.createIntBuffer(numFans);
		}
		if (numStrips > 0) {
			m_stripIdx = BufferUtils.createIntBuffer(numStrips);
			m_stripCnt = BufferUtils.createIntBuffer(numStrips);
		}
		if (numTris > 0) {
			m_triIdx = BufferUtils.createIntBuffer(numTris);
			m_triCnt = BufferUtils.createIntBuffer(numTris);
		}

		float x = 0;
		float y = 0;

		int i = 0;
		FloatBuffer buf = BufferUtils.createFloatBuffer(2 * numVertices);
		for (int j = 0; j < i_chars.length; j++) {
			i = i_chars[j].compileFans(buf, i, m_fanIdx, m_fanCnt, x, y);
			i = i_chars[j].compileStrips(buf, i, m_stripIdx, m_stripCnt, x, y);
			i = i_chars[j].compileTriangles(buf, i, m_triIdx, m_triCnt, x, y);
			x += i_chars[j].getAdvanceX();
			y += i_chars[j].getAdvanceY();
		}

		// upload vertex buffer
		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.rewind();
			glGenBuffers(idBuffer);

			m_bufferId = idBuffer.get(0);
			buf.rewind();

			glBindBuffer(GL_ARRAY_BUFFER, m_bufferId);
			glBufferData(GL_ARRAY_BUFFER, buf, GL_STREAM_READ);
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DGlyphVector#render()
	 */
	public void render() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		glBindBuffer(GL15.GL_ARRAY_BUFFER, m_bufferId);
		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(2, GL_FLOAT, 0, 0);
		try {
			if (m_fanIdx != null)
				glMultiDrawArrays(GL_TRIANGLE_FAN, m_fanIdx, m_fanCnt);
			if (m_stripIdx != null)
				glMultiDrawArrays(GL_TRIANGLE_STRIP, m_stripIdx, m_stripCnt);
			if (m_triIdx != null)
				glMultiDrawArrays(GL_TRIANGLES, m_triIdx, m_triCnt);
		} finally {
			glDisableClientState(GL_VERTEX_ARRAY);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DGlyphVector#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		if (m_bufferId != 0) {
			IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
			try {
				BufferUtils.put(idBuffer, m_bufferId);
				glDeleteBuffers(idBuffer);
				m_bufferId = 0;
			} finally {
				Draw3DCache.returnIntBuffer(idBuffer);
			}
		}

		m_disposed = true;
	}
}
