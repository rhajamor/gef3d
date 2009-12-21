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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * LwjglExecutableVBO2 There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.12.2009
 */
public abstract class LwjglExecutableVBO implements ExecutableGraphics2D {

	private FloatBuffer m_vertexBuffer;

	private int m_vertexBufferId;

	protected LwjglExecutableVBO(FloatBuffer i_vertexBuffer) {

		m_vertexBuffer = i_vertexBuffer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void dispose(Graphics3D i_g3d) {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.put(0, m_vertexBufferId);
			idBuffer.rewind();
			GL15.glDeleteBuffers(idBuffer);
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

	protected abstract void doExecute(Graphics3D i_g3d);

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#execute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void execute(Graphics3D i_g3d) {

		preExecute(i_g3d);
		try {
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_vertexBufferId);
			GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);

			try {
				doExecute(i_g3d);
			} finally {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			}
		} finally {
			postExecute(i_g3d);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void initialize(Graphics3D i_g3d) {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.rewind();
			GL15.glGenBuffers(idBuffer);
			m_vertexBufferId = idBuffer.get(0);

			m_vertexBuffer.rewind();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_vertexBufferId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, m_vertexBuffer,
				GL15.GL_STATIC_DRAW);

			m_vertexBuffer = null;
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

	protected void postExecute(Graphics3D i_g3d) {

	}

	protected void preExecute(Graphics3D i_g3d) {

	}
}
