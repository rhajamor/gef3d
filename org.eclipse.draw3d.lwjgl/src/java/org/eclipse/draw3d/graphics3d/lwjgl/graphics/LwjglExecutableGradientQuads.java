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

import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass;
import org.eclipse.draw3d.graphics.optimizer.primitive.GradientRenderRule;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.draw3d.util.Draw3DCache;
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

	private FloatBuffer m_colorBuffer;

	private int m_colorBufferId;

	private int m_numQuads;

	public LwjglExecutableGradientQuads(PrimitiveSet i_primitives) {

		super(i_primitives);

		PrimitiveClass primitiveClass = i_primitives.getPrimitiveClass();
		if (!primitiveClass.isGradient() || !primitiveClass.isQuad())
			throw new IllegalArgumentException(i_primitives
				+ " does not contain gradient quads");

		m_numQuads = i_primitives.getSize();
		m_colorBuffer = BufferUtils.createFloatBuffer(4 * 4 * m_numQuads);

		float[] c = new float[4];
		for (Primitive primitive : i_primitives.getPrimitives()) {

			GradientRenderRule renderRule =
				primitive.getRenderRule().asGradient();

			int alpha = renderRule.getAlpha();
			Color fromColor = renderRule.getFromColor();
			Color toColor = renderRule.getToColor();

			ColorConverter.toFloatArray(fromColor, alpha, c);
			m_colorBuffer.put(c);
			m_colorBuffer.put(c);

			ColorConverter.toFloatArray(toColor, alpha, c);
			m_colorBuffer.put(c);
			m_colorBuffer.put(c);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	public void dispose(Graphics3D i_g3d) {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.put(0, m_colorBufferId);
			idBuffer.rewind();
			GL15.glDeleteBuffers(idBuffer);
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}

		super.dispose(i_g3d);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#doExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void doExecute(Graphics3D i_g3d) {

		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4 * m_numQuads);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	public void initialize(Graphics3D i_g3d) {

		super.initialize(i_g3d);

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.rewind();
			GL15.glGenBuffers(idBuffer);
			m_colorBufferId = idBuffer.get(0);

			m_colorBuffer.rewind();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_colorBufferId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, m_colorBuffer,
				GL15.GL_STATIC_DRAW);

			m_colorBuffer = null;
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#postExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void postExecute(Graphics3D i_g3d) {

		GL15.glBindBuffer(GL11.GL_COLOR_ARRAY, 0);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);

		GL11.glPopAttrib();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#preExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void preExecute(Graphics3D i_g3d) {

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_colorBufferId);
		GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
	}
}
