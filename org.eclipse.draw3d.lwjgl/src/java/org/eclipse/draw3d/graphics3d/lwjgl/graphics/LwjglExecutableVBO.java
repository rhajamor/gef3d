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

import org.eclipse.draw3d.graphics.optimizer.Attributes;
import org.eclipse.draw3d.graphics.optimizer.FillAttributes;
import org.eclipse.draw3d.graphics.optimizer.OutlineAttributes;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveType;
import org.eclipse.draw3d.graphics3d.ExecutableGraphics2D;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.Draw3DCache;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * LwjglExecutableVBO There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 10.12.2009
 */
public class LwjglExecutableVBO implements ExecutableGraphics2D {

	private FloatBuffer m_vertexBuffer;

	private int m_id;

	private Attributes m_attributes;

	private PrimitiveType m_type;

	private int m_numPrimitives;

	private int[] m_numVertices;

	public LwjglExecutableVBO(PrimitiveType i_type, Attributes i_attributes,
			FloatBuffer i_vertexBuffer) {

		m_type = i_type;
		m_attributes = i_attributes;
		m_vertexBuffer = i_vertexBuffer;

		switch (i_type) {
		case FILLED_QUAD:
		case OUTLINED_QUAD:
			m_numPrimitives = m_vertexBuffer.limit() / 4;
			break;
		case LINE:
			m_numPrimitives = m_vertexBuffer.limit() / 2;
			break;
		default:
			throw new IllegalArgumentException(
				"must supply vertex counts for polygons and polylines");
		}
	}

	public LwjglExecutableVBO(PrimitiveType i_type, Attributes i_attributes,
			FloatBuffer i_vertexBuffer, int[] i_numVertices) {

		m_type = i_type;
		m_attributes = i_attributes;
		m_vertexBuffer = i_vertexBuffer;
		switch (i_type) {
		case FILLED_POLYGON:
		case OUTLINED_POLYGON:
		case POLYLINE:
			m_numPrimitives = i_numVertices.length;
			m_numVertices = i_numVertices;
			break;
		default:
			throw new IllegalArgumentException(
				"must not supply vertex counts for quads and lines");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void dispose(Graphics3D i_g3d) {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.put(0, m_id);
			idBuffer.rewind();
			GL15.glDeleteBuffers(idBuffer);
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#execute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void execute(Graphics3D i_g3d) {

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_id);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);

		switch (m_type) {
		case FILLED_POLYGON:
			FillAttributes fa = (FillAttributes) m_attributes;
			i_g3d.glColor(fa.getColor(), fa.getAlpha());

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			for (int i = 0, index = 0; i < m_numPrimitives; i++) {
				GL11.glDrawArrays(GL11.GL_POLYGON, index, m_numVertices[i]);
				index += m_numVertices[i];
			}
			break;
		case FILLED_QUAD:
			fa = (FillAttributes) m_attributes;
			i_g3d.glColor(fa.getColor(), fa.getAlpha());

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, 4 * m_numPrimitives);
			break;
		case OUTLINED_POLYGON:
			OutlineAttributes oa = (OutlineAttributes) m_attributes;
			i_g3d.glColor(oa.getColor(), oa.getAlpha());

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			for (int i = 0, index = 0; i < m_numPrimitives; i++) {
				GL11.glDrawArrays(GL11.GL_POLYGON, index, m_numVertices[i]);
				index += m_numVertices[i];
			}
			break;
		case OUTLINED_QUAD:
			oa = (OutlineAttributes) m_attributes;
			i_g3d.glColor(oa.getColor(), oa.getAlpha());

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, 4 * m_numPrimitives);
			break;
		case POLYLINE:
			oa = (OutlineAttributes) m_attributes;
			i_g3d.glColor(oa.getColor(), oa.getAlpha());
			for (int i = 0, index = 0; i < m_numPrimitives; i++) {
				GL11.glDrawArrays(GL11.GL_LINE_STRIP, index, m_numVertices[i]);
				index += m_numVertices[i];
			}
			break;
		case LINE:
			oa = (OutlineAttributes) m_attributes;
			i_g3d.glColor(oa.getColor(), oa.getAlpha());
			GL11.glDrawArrays(GL11.GL_LINES, 0, 2 * m_numPrimitives);
			break;

		default:
			break;
		}

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
			m_id = idBuffer.get(0);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_id);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, m_vertexBuffer,
				GL15.GL_STATIC_DRAW);

			m_vertexBuffer = null;
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

}
