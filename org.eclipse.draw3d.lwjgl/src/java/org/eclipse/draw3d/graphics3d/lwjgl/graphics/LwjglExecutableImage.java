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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.eclipse.draw3d.graphics3d.DisplayListManager;
import org.eclipse.draw3d.graphics3d.ExecutableGraphics2D;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.draw3d.util.ImageConverter.ConversionSpecs;
import org.eclipse.draw3d.util.converter.BufferInfo;
import org.eclipse.draw3d.util.converter.ImageConverter;
import org.eclipse.swt.graphics.Image;
import org.lwjgl.opengl.GL11;

/**
 * LwjglExecutableImage There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 16.12.2009
 */
public class LwjglExecutableImage implements ExecutableGraphics2D {

	private Image m_image;

	private int m_x;

	private int m_y;

	private int m_w;

	private int m_h;

	private int m_textureId;

	private float[] m_vertices;

	public LwjglExecutableImage(float[] i_vertices, Image i_image, int i_sX,
			int i_sY, int i_sW, int i_sH) {

		m_vertices = i_vertices;
		m_image = i_image;
		m_x = i_sX;
		m_y = i_sY;
		m_w = i_sW;
		m_h = i_sH;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void dispose(Graphics3D i_g3d) {

		if (m_textureId != 0) {
			IntBuffer nameBuffer = Draw3DCache.getIntBuffer(1);
			try {
				nameBuffer.put(0, m_textureId);
				nameBuffer.rewind();
				GL11.glDeleteTextures(nameBuffer);
				m_textureId = 0;
			} finally {
				Draw3DCache.returnIntBuffer(nameBuffer);
			}
		}

		DisplayListManager manager = i_g3d.getDisplayListManager();
		if (manager.isDisplayList(this))
			manager.deleteDisplayLists(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#execute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void execute(Graphics3D i_g3d) {

		DisplayListManager manager = i_g3d.getDisplayListManager();
		manager.executeDisplayList(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void initialize(Graphics3D i_g3d) {

		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		try {
			ConversionSpecs specs = new ConversionSpecs();
			specs.foregroundAlpha = 255;
			specs.textureWidth = m_w;
			specs.textureHeight = m_h;
			specs.clip =
				new org.eclipse.swt.graphics.Rectangle(m_x, m_y, m_w, m_h);

			BufferInfo info =
				new BufferInfo(m_w, m_h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 1);

			ByteBuffer buffer = Draw3DCache.getByteBuffer(info.getSize());
			IntBuffer nameBuffer = Draw3DCache.getIntBuffer(1);
			try {
				ImageConverter converter = ImageConverter.getInstance();
				buffer = converter.imageToBuffer(m_image, info, buffer, false);

				nameBuffer.rewind();
				GL11.glGenTextures(nameBuffer);

				m_textureId = nameBuffer.get(0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureId);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, m_w,
					m_h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

				GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
					GL11.GL_REPLACE);
			} finally {
				Draw3DCache.returnIntBuffer(nameBuffer);
				Draw3DCache.returnByteBuffer(buffer);
			}
		} finally {
			GL11.glPopAttrib();
		}

		DisplayListManager manager = i_g3d.getDisplayListManager();
		manager.createDisplayList(this, new Runnable() {

			public void run() {
				GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
				try {
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureId);

					GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2f(m_vertices[0], m_vertices[1]);

					GL11.glTexCoord2f(0, 1);
					GL11.glVertex2f(m_vertices[2], m_vertices[3]);

					GL11.glTexCoord2f(1, 1);
					GL11.glVertex2f(m_vertices[4], m_vertices[5]);

					GL11.glTexCoord2f(1, 0);
					GL11.glVertex2f(m_vertices[6], m_vertices[7]);
					GL11.glEnd();
				} finally {
					GL11.glPopAttrib();
				}
			}
		});
	}
}
