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
package org.eclipse.draw3d.font.lwjgl;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.eclipse.draw3d.font.simple.IDraw3DText;
import org.eclipse.draw3d.util.BufferUtils;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * Renders a
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 16.08.2010
 */
public class LwjglTextureText implements IDraw3DText {

	private enum State {
		DISPOSED, INITIALIZED, UNINITIALIZED
	}

	private int m_height;

	private BufferedImage m_image;

	private State m_state = State.UNINITIALIZED;

	private int m_textureId = 0;

	private int m_width;

	/**
	 * Creates a new instance that renders a texture created from the given
	 * image.
	 * 
	 * @param i_image the image to render
	 * @throws NullPointerException if the given image is <code>null</code>
	 */
	public LwjglTextureText(BufferedImage i_image) {
		if (i_image == null)
			throw new NullPointerException("i_image must not be null");
		m_image = i_image;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.IDraw3DText#dispose()
	 */
	public void dispose() {
		if (m_state == State.DISPOSED)
			throw new IllegalStateException(this + " + is disposed");

		if (m_textureId > 0) {
			IntBuffer idBuf = Draw3DCache.getIntBuffer(1);
			try {
				BufferUtils.put(idBuf, m_textureId);
				glDeleteTextures(idBuf);
				m_textureId = 0;
			} finally {
				Draw3DCache.returnIntBuffer(idBuf);
			}
		}

		if (m_image != null)
			m_image = null;

		m_state = State.DISPOSED;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.IDraw3DText#getHeight()
	 */
	public float getHeight() {
		return m_height;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.IDraw3DText#getWidth()
	 */
	public float getWidth() {
		return m_width;
	}

	private void initialize() {
		Raster data = m_image.getData();
		m_width = data.getWidth();
		m_height = data.getHeight();
		ByteBuffer buf = BufferUtils.createByteBuffer(m_width * m_height * 2);

		int[] pixel = new int[4];
		for (int y = m_height - 1; y >= 0; y--) {
			for (int x = 0; x < m_width; x++) {
				data.getPixel(x, y, pixel);
				int r = pixel[0];
				int g = pixel[1];
				int b = pixel[2];
				int a = pixel[3];
				int l = (int) (0.3f * r + 0.59f * g + 0.11f * b);
				buf.put((byte) (l & 0xFF));
				buf.put((byte) (a & 0xFF));
			}
		}

		buf.rewind();

		IntBuffer idBuf = Draw3DCache.getIntBuffer(1);
		glPushAttrib(GL_TEXTURE_BIT);
		try {
			glGenTextures(idBuf);
			m_textureId = idBuf.get(0);

			glBindTexture(GL_TEXTURE_2D, m_textureId);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE_ALPHA, m_width,
				m_height, 0, GL_LUMINANCE_ALPHA, GL_UNSIGNED_BYTE, buf);

			// GLU.gluBuild2DMipmaps(m_textureId, 2, m_width,
			// m_height,
			// GL_LUMINANCE_ALPHA, GL11.GL_UNSIGNED_BYTE,
			// buffer);
		} finally {
			Draw3DCache.returnIntBuffer(idBuf);
			glPopAttrib();
		}

		m_image = null;
		m_state = State.INITIALIZED;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.simple.IDraw3DText#render()
	 */
	public void render() {
		if (m_state == State.DISPOSED)
			throw new IllegalStateException(this + " is disposed");
		else if (m_state == State.UNINITIALIZED)
			initialize();

		glBindTexture(GL_TEXTURE_2D, m_textureId);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(0, m_height);
		glTexCoord2f(1, 0);
		glVertex2f(m_width, m_height);
		glTexCoord2f(1, 1);
		glVertex2f(m_width, 0);
		glTexCoord2f(0, 1);
		glVertex2f(0, 0);
		glEnd();
	}
}
