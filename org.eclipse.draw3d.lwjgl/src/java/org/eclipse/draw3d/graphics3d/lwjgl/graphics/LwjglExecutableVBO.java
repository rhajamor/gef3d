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

import org.eclipse.draw3d.graphics3d.ExecutableGraphics2D;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.Draw3DCache;
import org.lwjgl.opengl.GL15;

/**
 * Abstract base class for executable graphics objects that use a vertex buffer
 * object.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.12.2009
 */
public abstract class LwjglExecutableVBO implements ExecutableGraphics2D {

	private int m_bufferId;

	/**
	 * Called after this object was executed.
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
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void dispose(Graphics3D i_g3d) {

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
	 * Executes the drawing code for this executable.
	 * 
	 * @param i_g3d the Graphics3D instance
	 */
	protected abstract void doExecute(Graphics3D i_g3d);

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#execute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void execute(Graphics3D i_g3d) {

		prepare(i_g3d);
		try {
			doExecute(i_g3d);
		} finally {
			cleanup(i_g3d);
		}
	}

	protected int getBufferId() {

		return m_bufferId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void initialize(Graphics3D i_g3d) {

		FloatBuffer vertexBuffer = createVertexBuffer();
		uploadBuffer(vertexBuffer);
	}

	/**
	 * Called before this object was executed.
	 * 
	 * @param i_g3d
	 */
	protected abstract void prepare(Graphics3D i_g3d);

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
