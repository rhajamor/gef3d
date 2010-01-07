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

import java.nio.IntBuffer;

import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass;
import org.eclipse.draw3d.graphics.optimizer.primitive.OutlineRenderRule;
import org.eclipse.draw3d.graphics.optimizer.primitive.PolylinePrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.ColorConverter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/**
 * LwjglExecutableQuads There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.12.2009
 */
public class LwjglExecutablePolylines extends LwjglExecutableVertexBuffer {

	private float[] m_color = new float[4];

	private IntBuffer m_firstBuffer;

	private IntBuffer m_numBuffer;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	public void dispose(Graphics3D i_g3d) {

		m_firstBuffer = null;
		m_numBuffer = null;

		super.dispose(i_g3d);
	}

	public LwjglExecutablePolylines(PrimitiveSet i_primitives) {

		super(i_primitives);

		PrimitiveClass clazz = i_primitives.getPrimitiveClass();
		if (!clazz.isPolyline())
			throw new IllegalArgumentException(i_primitives
				+ " does not contain polylines");

		int count = i_primitives.getSize();
		m_firstBuffer = BufferUtils.createIntBuffer(count);
		m_numBuffer = BufferUtils.createIntBuffer(count);

		int index = 0;
		for (Primitive primitive : i_primitives.getPrimitives()) {
			PolylinePrimitive polyline = (PolylinePrimitive) primitive;

			int numVertices = polyline.getVertexCount();
			m_numBuffer.put(numVertices);
			m_firstBuffer.put(index);
			index += numVertices;
		}

		OutlineRenderRule rule = clazz.getRenderRule().asOutline();
		ColorConverter.toFloatArray(rule.getColor(), rule.getAlpha(), m_color);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#doExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void doExecute(Graphics3D i_g3d) {

		i_g3d.glColor4f(m_color);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL14.glMultiDrawArrays(GL11.GL_LINE_STRIP, m_firstBuffer, m_numBuffer);
	}
}
