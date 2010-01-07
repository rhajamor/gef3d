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

import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass;
import org.eclipse.draw3d.graphics.optimizer.primitive.GradientRenderRule;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.QuadPrimitive;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.swt.graphics.Color;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * LwjglExecutableQuads There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.12.2009
 */
public class LwjglExecutableGradientQuads extends LwjglExecutableVBO {

	private static final int VERTEX_SIZE = (2 + 4) * 4;

	private int m_vertexCount;

	private PrimitiveSet m_primitives;

	public LwjglExecutableGradientQuads(PrimitiveSet i_primitives) {

		if (i_primitives == null)
			throw new NullPointerException("i_primitives must not be null");

		if (i_primitives.getSize() == 0)
			throw new IllegalArgumentException(i_primitives + " is empty");

		PrimitiveClass primitiveClass = i_primitives.getPrimitiveClass();
		if (!primitiveClass.isGradient() || !primitiveClass.isQuad())
			throw new IllegalArgumentException(i_primitives
				+ " does not contain gradient quads");

		m_primitives = i_primitives;
		m_vertexCount = i_primitives.getVertexCount();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#cleanup(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void cleanup(Graphics3D i_g3d) {

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);

		GL11.glPopAttrib();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#createVertexBuffer
	 */
	@Override
	protected FloatBuffer createVertexBuffer() {

		FloatBuffer buffer =
			BufferUtils.createFloatBuffer(VERTEX_SIZE
				* m_primitives.getVertexCount());

		float[] c = new float[4];
		for (Primitive primitive : m_primitives.getPrimitives()) {
			QuadPrimitive quad = (QuadPrimitive) primitive;
			float[] vertices = quad.getTransformedVertices();

			GradientRenderRule renderRule = quad.getRenderRule().asGradient();
			Color fromColor = renderRule.getFromColor();
			Color toColor = renderRule.getToColor();
			int alpha = renderRule.getAlpha();

			buffer.put(vertices[0]);
			buffer.put(vertices[1]);
			buffer.put(ColorConverter.toFloatArray(fromColor, alpha, c));

			buffer.put(vertices[2]);
			buffer.put(vertices[3]);
			buffer.put(ColorConverter.toFloatArray(fromColor, alpha, c));

			buffer.put(vertices[4]);
			buffer.put(vertices[5]);
			buffer.put(ColorConverter.toFloatArray(toColor, alpha, c));

			buffer.put(vertices[6]);
			buffer.put(vertices[7]);
			buffer.put(ColorConverter.toFloatArray(toColor, alpha, c));
		}

		m_primitives = null;
		return buffer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#doExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void doExecute(Graphics3D i_g3d) {

		GL11.glDrawArrays(GL11.GL_QUADS, 0, m_vertexCount);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#prepare(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void prepare(Graphics3D i_g3d) {

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getBufferId());

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, VERTEX_SIZE, 0);

		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glColorPointer(4, GL11.GL_FLOAT, VERTEX_SIZE, 2 * 4);
	}
}
