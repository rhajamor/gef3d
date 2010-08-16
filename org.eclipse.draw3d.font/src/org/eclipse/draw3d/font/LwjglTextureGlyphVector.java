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
package org.eclipse.draw3d.font;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.nio.IntBuffer;

import org.eclipse.draw3d.util.BufferUtils;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * LwjglTextureGlyphVector There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 16.08.2010
 */
public class LwjglTextureGlyphVector implements IDraw3DGlyphVector {

	private enum State {
		UNINITIALIZED, INITIALIZED, DISPOSED
	}

	private int m_textureId = 0;

	private State m_state = State.UNINITIALIZED;

	private BufferedImage m_image;

	public LwjglTextureGlyphVector(BufferedImage i_image) {
		if (i_image == null)
			throw new NullPointerException("i_image must not be null");
		m_image = i_image;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DGlyphVector#dispose()
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

	private void initialize() {
		if (m_state == State.INITIALIZED)
			return;

		Raster data = m_image.getData();
		DataBufferByte dataBuffer = (DataBufferByte) data.getDataBuffer();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DGlyphVector#render()
	 */
	public void render() {
		if (m_state == State.DISPOSED)
			throw new IllegalStateException(this + " is disposed");
	}

}
