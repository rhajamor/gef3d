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
package org.eclipse.draw3d.graphics3d.lwjgl;

import java.nio.FloatBuffer;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.IMatrix3f;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Vector2fImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics.optimizer.primitive.TextPrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.TextRenderRule;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.ILodHelper;
import org.eclipse.draw3d.graphics3d.RenderImage;
import org.eclipse.draw3d.graphics3d.lwjgl.font.LwjglFont;
import org.eclipse.draw3d.graphics3d.lwjgl.font.LwjglVectorFont;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.draw3d.util.Draw3DCache;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * TextRenderImage There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.01.2010
 */
public class TextRenderImage implements RenderImage {

	private float[] m_color = new float[4];

	private String m_text;

	private IVector2f m_size;

	private LwjglVectorFont m_vectorFont;

	private LwjglFont m_textureFont;

	private FloatBuffer m_transformationBuffer;

	private Vector3f m_absPos = new Vector3fImpl();

	private Vector3f m_normal = new Vector3fImpl(IVector3f.Z_AXIS_NEG);

	public TextRenderImage(TextPrimitive i_primitive,
			LwjglVectorFont i_vectorFont, LwjglFont i_textureFont,
			IPosition3D i_position) {

		m_text = i_primitive.getText();
		m_vectorFont = i_vectorFont;
		m_textureFont = i_textureFont;

		TextRenderRule textRule = i_primitive.getRenderRule().asText();

		ColorConverter.toFloatArray(textRule.getTextColor(),
			textRule.getAlpha(), m_color);

		Dimension extent = i_primitive.getExtent();
		m_size = new Vector2fImpl(extent.width, extent.height);

		IMatrix3f t3f = i_primitive.getTransformation();
		Point p = i_primitive.getPosition();

		boolean hasTrans = t3f != null && !IMatrix3f.IDENTITY.equals(t3f);
		boolean hasPos = p.x != 0 || p.y != 0;

		if (hasTrans || hasPos) {
			Matrix4f t4f = Draw3DCache.getMatrix4f();
			try {
				if (hasTrans)
					Math3D.convert(t3f, t4f);

				if (hasPos) {
					Vector3f v = Draw3DCache.getVector3f();
					try {
						if (!hasTrans)
							t4f.setIdentity();

						v.set(p.x, p.y, 0);
						Math3D.translate(t4f, v, t4f);
					} finally {
						Draw3DCache.returnVector3f(v);
					}
				}

				m_transformationBuffer = BufferUtils.createFloatBuffer(16);
				t4f.toBufferRowMajor(m_transformationBuffer);
				m_transformationBuffer.rewind();

				Math3D.mul(t4f, i_position.getTransformationMatrix(), t4f);
				Math3D.transform(m_absPos, t4f, m_absPos);
			} finally {
				Draw3DCache.returnMatrix4f(t4f);
			}
		} else {
			Math3D.transform(m_absPos, i_position.getTransformationMatrix(),
				m_absPos);
		}

		Math3D.transform(m_normal, i_position.getAbsoluteRotationMatrix(),
			m_normal);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.RenderImage#dispose()
	 */
	public void dispose() {
		// TODO implement method RenderImage.dispose

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.RenderImage#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void initialize(Graphics3D i_g3d) {

	}

	private static final float LOD_VF = 1100f;

	private static final float LOD_TF = 10000f;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.RenderImage#render(org.eclipse.draw3d.graphics3d.Graphics3D,
	 *      org.eclipse.draw3d.graphics3d.ILodHelper)
	 */
	public void render(Graphics3D i_g3d, ILodHelper i_lodHelper) {

		float nd =
			i_lodHelper.getNormalizedDistance(m_absPos, m_size, m_normal);

		if (nd <= LOD_TF) {

			GL11.glColor4f(m_color[0], m_color[1], m_color[2], m_color[3]);
			if (m_transformationBuffer != null) {
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPushMatrix();
				try {
					GL11.glMultMatrix(m_transformationBuffer);
					if (nd <= LOD_VF) {
						// GL11.glColor4f(1, 0, 0, 1);
						m_vectorFont.render(m_text);
					} else {
						// GL11.glColor4f(0, 1, 0, 1);
						m_textureFont.renderString(m_text, 0, 0, false);
					}
				} finally {
					GL11.glPopMatrix();
				}
			} else {
				if (nd <= LOD_VF) {
					// GL11.glColor4f(1, 0, 0, 1);
					m_vectorFont.render(m_text);
				} else {
					// GL11.glColor4f(0, 1, 0, 1);
					m_textureFont.renderString(m_text, 0, 0, false);
				}
			}
		}
	}
}
