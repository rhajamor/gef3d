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
package org.eclipse.draw3d.graphics3d.lwjgl.offscreen;

import static org.lwjgl.opengl.EXTFramebufferObject.*;

import java.nio.IntBuffer;

import org.eclipse.draw3d.offscreen.FramebufferObject;
import org.eclipse.draw3d.util.Draw3DCache;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GLContext;

/**
 * Wraps an LWJGL framebuffer object (FBO).
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 28.07.2010
 */
public class LwjglFbo implements FramebufferObject {

	private boolean m_disposed = false;

	private int m_glFrameBuffer = 0;

	/**
	 * {@inheritDoc}
	 * 
	 * @see FramebufferObject#activate()
	 */
	public void activate() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		if (m_glFrameBuffer == 0)
			m_glFrameBuffer = create();

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, m_glFrameBuffer);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.offscreen.FramebufferObject#checkStatus()
	 */
	public void checkStatus() {
		int status = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
		if (status != GL_FRAMEBUFFER_COMPLETE_EXT)
			throw new RuntimeException(getStatus(status));
	}

	private int create() {
		IntBuffer buffer = Draw3DCache.getIntBuffer(1);
		try {
			buffer.rewind();
			EXTFramebufferObject.glGenFramebuffersEXT(buffer);
			return buffer.get(0);
		} finally {
			Draw3DCache.returnIntBuffer(buffer);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see FramebufferObject#deactivate()
	 */
	public void deactivate() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	private void delete(int i_glFrameBuffer) {
		if (i_glFrameBuffer > 0) {
			IntBuffer buffer = Draw3DCache.getIntBuffer(1);
			try {
				buffer.rewind();
				buffer.put(i_glFrameBuffer);
				glDeleteFramebuffersEXT(buffer);
			} finally {
				Draw3DCache.returnIntBuffer(buffer);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see FramebufferObject#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			return;

		if (m_glFrameBuffer != 0) {
			delete(m_glFrameBuffer);
			m_glFrameBuffer = 0;
		}

		m_disposed = true;
	}

	private String getStatus(int i_status) {
		switch (i_status) {
		case GL_FRAMEBUFFER_COMPLETE_EXT:
			return "GL_FRAMEBUFFER_COMPLETE_EXT";
		case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
			return "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT";
		case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			return "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT";
		case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
			return "GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT";
		case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
			return "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT";
		case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
			return "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT";
		case GL_FRAMEBUFFER_UNSUPPORTED_EXT:
			return "GL_FRAMEBUFFER_UNSUPPORTED_EXT";
		default:
			return "unknown status code " + i_status;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see FBO#isSupported()
	 */
	public boolean isSupported() {
		ContextCapabilities caps = GLContext.getCapabilities();
		return caps != null && caps.GL_EXT_framebuffer_object;
	}
}
