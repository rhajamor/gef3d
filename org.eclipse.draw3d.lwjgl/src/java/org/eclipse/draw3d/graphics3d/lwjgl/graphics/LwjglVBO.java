/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d.lwjgl.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.Draw3DCache;
import org.lwjgl.opengl.GL15;

/**
 * Abstract base class for vertex buffer objects.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.12.2009
 */
public abstract class LwjglVBO {

	private int m_bufferId;

	/**
	 * Called after this VBO was executed.
	 * 
	 * @param i_g3d the Graphics3D instance
	 */
	protected abstract void cleanup(Graphics3D i_g3d);

	/**
	 * Creates the vertex buffer.
	 * 
	 * @return a float buffer containing the vertex data of the given primitives
	 * @throws NullPointerException if the given primitive set is
	 *             <code>null</code>
	 */
	protected FloatBuffer createVertexBuffer() {

		throw new AssertionError(
			"createVertexBuffer() was called, but not implemented");
	}

	/**
	 * Disposes the ressources associated with this VBO.
	 */
	public void dispose() {

		if (m_bufferId != 0) {
			disposeBuffer(m_bufferId);
			m_bufferId = 0;
		}
	}

	/**
	 * Dispose the buffer with the given ID.
	 * 
	 * @param id the ID of the buffer to dispose
	 */
	protected void disposeBuffer(int id) {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.rewind();
			idBuffer.put(id);
			idBuffer.rewind();
			GL15.glDeleteBuffers(idBuffer);
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

	/**
	 * Executes the drawing code for this VBO.
	 * 
	 * @param i_g3d the Graphics3D instance
	 */
	protected abstract void doRender(Graphics3D i_g3d);

	/**
	 * Returns the ID of the OpenGL VBO.
	 * 
	 * @return the ID
	 */
	protected int getBufferId() {

		return m_bufferId;
	}

	/**
	 * Initializes this VBO.
	 * 
	 * @param i_g3d the Graphics3D instance
	 */
	public void initialize(Graphics3D i_g3d) {

		FloatBuffer vertexBuffer = createVertexBuffer();
		uploadBuffer(vertexBuffer);
	}

	/**
	 * Called before this VBO is rendered.
	 * 
	 * @param i_g3d the Graphics3D instance.
	 */
	protected abstract void prepare(Graphics3D i_g3d);

	/**
	 * Renders this VBO.
	 * 
	 * @param i_g3d the Graphics3D instance
	 */
	public void render(Graphics3D i_g3d) {

		prepare(i_g3d);
		try {
			doRender(i_g3d);
		} finally {
			cleanup(i_g3d);
		}
	}

	/**
	 * Uploads the given buffer and sets the buffer ID.
	 * 
	 * @param i_buffer the buffer to upload
	 */
	protected void uploadBuffer(FloatBuffer i_buffer) {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.rewind();
			GL15.glGenBuffers(idBuffer);
			m_bufferId = idBuffer.get(0);

			i_buffer.rewind();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_bufferId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, i_buffer,
				GL15.GL_STATIC_DRAW);
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}
}
