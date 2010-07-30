/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d.lwjgl.texture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import org.eclipse.draw3d.graphics3d.lwjgl.offscreen.LwjglFbo;
import org.eclipse.draw3d.util.BufferUtils;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.draw3d.util.ImageConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;

/**
 * Allows rendering to an OpenGL texture using a framebuffer object.
 * <p>
 * We use arbitrary texture sizes for performance reasons, this only works on
 * machines with OpenGL version 2 or later. Some video cards can do it with D3D
 * and some OpenGL 2.0 cards can't. In the first group are nVidia serie 6 and up
 * and in second are ATI Radeon series 200 and 300. (since rev. 361, also see
 * mailing list: Alessandro Borges: NPOT - textures, April 7. 2008)
 * </p>
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.05.2008
 */
public class LwjglFboRenderTexture implements LwjglRenderTexture {

	/**
	 * Indicates which attribute groups must be saved prior to using this.
	 */
	private static final int ATTRIB_MASK =
		GL11.GL_LIGHTING_BIT | GL11.GL_CURRENT_BIT | GL11.GL_TRANSFORM_BIT
			| GL11.GL_LINE_BIT | GL11.GL_POLYGON_BIT | GL11.GL_TEXTURE_BIT
			| GL11.GL_VIEWPORT_BIT | GL11.GL_DEPTH_BUFFER_BIT
			| GL11.GL_COLOR_BUFFER_BIT | GL13.GL_MULTISAMPLE_BIT
			| GL11.GL_ENABLE_BIT;

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(LwjglFboRenderTexture.class.getName());

	private boolean m_disposed = false;

	private LwjglFbo m_fbo;

	private int m_glTexture = 0;

	private int m_height = -1;

	private boolean m_valid = false;

	private int m_width = -1;

	private int m_format;

	/**
	 * Creates a new texture with the given initial dimensions.
	 * 
	 * @param i_width the width of the texture
	 * @param i_height the height of the texture
	 * @param i_format the texture type, e.g. {@link GL11#GL_RGBA}
	 * @throws IllegalArgumentException if the given width or height is not
	 *             positive
	 */
	public LwjglFboRenderTexture(int i_width, int i_height, int i_format) {
		setDimensions(i_width, i_height);
		m_format = i_format;
		m_fbo = new LwjglFbo();

		if (!m_fbo.isSupported())
			throw new RuntimeException("FBO is not supported on this system");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglRenderTexture#activate()
	 */
	public void activate() {
		if (m_disposed)
			throw new IllegalStateException("texture is disposed");

		GL11.glFlush();
		m_fbo.activate();

		if (!m_valid) {
			deleteTexture(m_glTexture);
			m_glTexture = createTexture(m_width, m_height);
			m_valid = true;
		}

		EXTFramebufferObject.glFramebufferTexture2DEXT(
			EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
			EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D,
			m_glTexture, 0);

		m_fbo.checkStatus();

		// save all state variables that may be changed by the graphics object
		GL11.glPushAttrib(ATTRIB_MASK);

		GL11.glDisable(GL13.GL_MULTISAMPLE);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		GLU.gluOrtho2D(0, m_width, m_height, 0);
		GL11.glViewport(0, 0, m_width, m_height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglRenderTexture#clear(org.eclipse.swt.graphics.Color,
	 *      int)
	 */
	public void clear(Color i_color, int i_alpha) {
		if (i_color == null)
			throw new NullPointerException("i_color must not be null");

		float[] color = ColorConverter.toFloatArray(i_color, i_alpha, null);
		clear(color[0], color[1], color[2], color[3]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglRenderTexture#clear(float,
	 *      float, float, float)
	 */
	public void clear(float i_red, float i_green, float i_blue, float i_alpha) {
		GL11.glClearColor(i_red, i_green, i_blue, i_alpha);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	private int createTexture(int i_width, int i_height) {
		IntBuffer buffer = Draw3DCache.getIntBuffer(1);
		try {
			buffer.rewind();
			GL11.glGenTextures(buffer);
			int glTexture = buffer.get(0);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
				GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
				GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_CLAMP);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, m_format, i_width,
				i_height, 0, m_format, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

			return glTexture;
		} finally {
			Draw3DCache.returnIntBuffer(buffer);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglRenderTexture#deactivate()
	 */
	public void deactivate() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		GL11.glFlush();

		// calculate mipmaps
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_glTexture);
		EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);

		// restore OpenGL state
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();

		GL11.glPopAttrib();

		m_fbo.deactivate();
	}

	private void deleteTexture(int i_glTexture) {
		if (i_glTexture > 0) {
			IntBuffer buffer = Draw3DCache.getIntBuffer(1);
			try {
				buffer.rewind();
				buffer.put(i_glTexture);
				GL11.glDeleteTextures(buffer);
			} finally {
				Draw3DCache.returnIntBuffer(buffer);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglRenderTexture#dispose(boolean)
	 */
	public void dispose(boolean i_deleteTexture) {
		if (m_disposed)
			return;

		if (i_deleteTexture)
			deleteTexture(m_glTexture);
		m_glTexture = 0;

		m_fbo.dispose();
		m_fbo = null;
	}

	@SuppressWarnings("unused")
	private void dump() {
		ByteBuffer buffer =
			BufferUtils.createByteBuffer(m_width * m_height * 4);
		GL11.glReadPixels(0, 0, m_width, m_height, GL11.GL_RGBA,
			GL11.GL_UNSIGNED_BYTE, buffer);

		ImageData imageData =
			ImageConverter.colorBufferToImage(buffer, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, m_width, m_height);
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { imageData };

		String path =
			"/Users/kristian/Temp/texture" + m_glTexture + "_"
				+ System.currentTimeMillis() + ".png";
		imageLoader.save(path, SWT.IMAGE_PNG);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglRenderTexture#getTextureId()
	 */
	public int getTextureId() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		return m_glTexture;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglRenderTexture#setDimensions(int,
	 *      int)
	 */
	public void setDimensions(int i_width, int i_height) {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		if (i_width <= 0 || i_height <= 0)
			throw new IllegalArgumentException(
				"texture dimensions must be positive (" + i_width + " * "
					+ i_height + ")");

		m_valid = m_valid && m_width == i_width && m_height == i_height;

		m_width = i_width;
		m_height = i_height;
	}
}
