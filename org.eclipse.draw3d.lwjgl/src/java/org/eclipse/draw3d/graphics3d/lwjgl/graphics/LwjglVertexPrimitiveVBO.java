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
package org.eclipse.draw3d.graphics3d.lwjgl.graphics;

import java.nio.FloatBuffer;

import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.VertexPrimitive;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * Abstract base class for VBOs that obtain their vertex data from a
 * {@link PrimitiveSet set} of {@link VertexPrimitive vertex primitives}.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 06.01.2010
 */
public abstract class LwjglVertexPrimitiveVBO extends LwjglVBO {

	private PrimitiveSet m_primitives;

	/**
	 * Creates a new vertex buffer using the given primitive set.
	 * 
	 * @param i_primitives the primitive set
	 * @throws NullPointerException if the given primitive set is
	 *             <code>null</code>
	 * @throws IllegalArgumentException if the given primitive set is empty
	 */
	protected LwjglVertexPrimitiveVBO(PrimitiveSet i_primitives) {

		if (i_primitives == null)
			throw new NullPointerException("i_primitives must not be null");

		if (i_primitives.getSize() == 0)
			throw new IllegalArgumentException(i_primitives
				+ " must not be empty");

		m_primitives = i_primitives;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#cleanup(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void cleanup(Graphics3D i_g3d) {

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#createVertexBuffer
	 */
	@Override
	protected FloatBuffer createVertexBuffer() {

		FloatBuffer vertexBuffer =
			BufferUtils.createFloatBuffer(2 * m_primitives.getVertexCount());

		for (Primitive primitive : m_primitives.getPrimitives()) {
			VertexPrimitive vertexPrimitive = (VertexPrimitive) primitive;
			vertexBuffer.put(vertexPrimitive.getVertices());
		}

		m_primitives = null;
		return vertexBuffer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#doRender(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected abstract void doRender(Graphics3D i_g3d);

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#prepare(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void prepare(Graphics3D i_g3d) {

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getBufferId());
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
	}
}
