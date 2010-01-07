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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.TextPrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.TextRenderRule;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.lwjgl.font.LwjglFont;
import org.eclipse.draw3d.graphics3d.lwjgl.font.LwjglFontManager;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * LwjglExecutableImages There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.01.2010
 */
public class LwjglExecutableText extends LwjglExecutableVBO {

	private float[] m_color;

	private LwjglFontManager m_fontManager;

	private PrimitiveSet m_primitives;

	private int m_textureId;

	private int m_vertexCount;

	private int m_vertexSize;

	public LwjglExecutableText(PrimitiveSet i_primitives,
			LwjglFontManager i_fontManager) {

		m_fontManager = i_fontManager;
		m_primitives = i_primitives;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#cleanup(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void cleanup(Graphics3D i_g3d) {

		GL15.glBindBuffer(GL11.GL_TEXTURE_COORD_ARRAY, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		if (m_color == null) {
			GL15.glBindBuffer(GL11.GL_COLOR_ARRAY, 0);
			GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		}

		GL11.glPopAttrib();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#doExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void doExecute(Graphics3D i_g3d) {

		if (m_color != null)
			i_g3d.glColor4f(m_color);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, m_vertexCount);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVertexBuffer#createVertexBuffer()
	 */
	@Override
	protected FloatBuffer createVertexBuffer() {

		PrimitiveClass clazz = m_primitives.getPrimitiveClass();
		TextRenderRule renderRule = clazz.getRenderRule().asText();

		Font font = renderRule.getFont();
		boolean fontAntialias = renderRule.isFontAntialias();

		LwjglFont glFont =
			m_fontManager.getFont(font, (char) 32, (char) 127, fontAntialias);

		m_textureId = glFont.getTextureId();

		boolean first = true;
		Color constColor = null;
		int constAlpha = -1;

		m_vertexCount = 0;
		for (Primitive primitive : m_primitives.getPrimitives()) {
			TextPrimitive textPrimitive = (TextPrimitive) primitive;
			TextRenderRule textRenderRule =
				textPrimitive.getRenderRule().asText();

			String text = textPrimitive.getText();
			Color color = textRenderRule.getTextColor();
			int alpha = textRenderRule.getAlpha();

			if (first) {
				constColor = color;
				constAlpha = alpha;
				first = false;
			} else if (constColor != null
				&& (!constColor.equals(color) || constAlpha != alpha)) {
				constColor = null;
				constAlpha = -1;
			}

			m_vertexCount += 4 * glFont.getLength(text);
		}

		if (constColor != null)
			m_color = ColorConverter.toFloatArray(constColor, constAlpha, null);
		m_vertexSize = (m_color == null ? 2 + 2 + 4 : 2 + 2) * 4;

		FloatBuffer buffer =
			BufferUtils.createFloatBuffer(m_vertexSize * m_vertexCount);

		float[] c = new float[4];
		for (Primitive primitive : m_primitives.getPrimitives()) {
			TextPrimitive textPrimitive = (TextPrimitive) primitive;
			String text = textPrimitive.getText();
			boolean expand = textPrimitive.isExpand();
			Point position = textPrimitive.getPosition();
			IMatrix4f transformation = textPrimitive.getTransformation();

			glFont.renderString(text, transformation, position.x, position.y,
				expand, buffer, buffer);

			if (m_color == null) {
				TextRenderRule textRenderRule =
					textPrimitive.getRenderRule().asText();

				Color color = textRenderRule.getTextColor();
				int alpha = textRenderRule.getAlpha();

				buffer.put(ColorConverter.toFloatArray(color, alpha, c));
			}
		}

		m_primitives = null;
		m_fontManager = null;

		return buffer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#prepare(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void prepare(Graphics3D i_g3d) {

		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getBufferId());

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, m_vertexSize, 0);

		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, m_vertexSize, 2 * 4);

		if (m_color == null) {
			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
			GL11.glColorPointer(4, GL11.GL_FLOAT, m_vertexSize, 4 * 4);
		}
	}
}
