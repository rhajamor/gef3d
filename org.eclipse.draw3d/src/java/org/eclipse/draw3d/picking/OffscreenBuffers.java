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
package org.eclipse.draw3d.picking;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.DummyGraphics;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.GLd3d;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.RenderMode;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig;
import org.eclipse.draw3d.offscreen.OffscreenRenderer;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * A picking buffer that is provided by an offscreen renderer.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 15.05.2008
 */
public class OffscreenBuffers implements PickingBuffers {

	private OffscreenRenderer m_offscreenRenderer;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#dispose()
	 */
	public void dispose() {

		if (m_offscreenRenderer != null) {
			m_offscreenRenderer.dispose();
			m_offscreenRenderer = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getBufferConfig()
	 */
	public Graphics3DOffscreenBufferConfig getBufferConfig() {

		return m_offscreenRenderer.getBufferConfig();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getColorBuffer()
	 */
	public ByteBuffer getColorBuffer() {

		if (m_offscreenRenderer == null)
			throw new IllegalStateException(
					"offscreen renderer not initialized or disposed");

		return m_offscreenRenderer.getColorBuffer();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getDepthBuffer()
	 */
	public FloatBuffer getDepthBuffer() {

		if (m_offscreenRenderer == null)
			throw new IllegalStateException(
					"offscreen renderer not initialized or disposed");

		return m_offscreenRenderer.getDepthBuffer();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getHeight()
	 */
	public int getHeight() {

		if (m_offscreenRenderer == null)
			throw new IllegalStateException(
					"offscreen renderer not initialized or disposed");

		return m_offscreenRenderer.getHeight();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getWidth()
	 */
	public int getWidth() {

		if (m_offscreenRenderer == null)
			throw new IllegalStateException(
					"offscreen renderer not initialized or disposed");

		return m_offscreenRenderer.getWidth();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#repaint(IFigure, FigureManager, GLCanvas)
	 */
	public boolean repaint(final IFigure i_rootFigure,
			FigureManager i_figureManager, GLCanvas i_canvas) {

		RenderContext renderContext = Figure3DHelper
				.getAncestor3D(i_rootFigure).getRenderContext();
		renderContext.setMode(RenderMode.COLOR);
		renderContext.activate(); // set context to current context
		renderContext.setColorProvider(i_figureManager);
		Graphics3D g3d = renderContext.getGraphics3D();

		// lazy initialized because now GL is set up
		if (m_offscreenRenderer == null) {
			int buffers = Graphics3DDraw.GL_COLOR_BUFFER_BIT
					| Graphics3DDraw.GL_DEPTH_BUFFER_BIT;
			Graphics3DOffscreenBufferConfig bufferConfig = g3d
					.getGraphics3DOffscreenBufferConfig(buffers,
							Graphics3DDraw.GL_RGB,
							Graphics3DDraw.GL_UNSIGNED_BYTE,
							Graphics3DDraw.GL_FLOAT);

			m_offscreenRenderer = new OffscreenRenderer(bufferConfig);
		}

		int width = GLd3d.getAlignedWidth(g3d, i_canvas.getSize().x);
		int height = i_canvas.getSize().y;

		m_offscreenRenderer.setDimensions(width, height);
		i_figureManager.clear();

		boolean dither = g3d.glIsEnabled(Graphics3DDraw.GL_DITHER);
		if (dither)
			g3d.glDisable(Graphics3DDraw.GL_DITHER);

		boolean multisample = g3d.glIsEnabled(Graphics3DDraw.GL_MULTISAMPLE);
		if (multisample)
			g3d.glDisable(Graphics3DDraw.GL_MULTISAMPLE);

		boolean texture = g3d.glIsEnabled(Graphics3DDraw.GL_TEXTURE_2D);
		if (texture)
			g3d.glDisable(Graphics3DDraw.GL_TEXTURE_2D);

		try {
			m_offscreenRenderer.render(new Runnable() {
				public void run() {
					i_rootFigure.paint(new DummyGraphics());
				}
			});
			
			return m_offscreenRenderer.isBackBufferEnabled();
		} finally {
			if (dither)
				g3d.glEnable(Graphics3DDraw.GL_DITHER);

			if (multisample)
				g3d.glEnable(Graphics3DDraw.GL_MULTISAMPLE);

			if (texture)
				g3d.glEnable(Graphics3DDraw.GL_TEXTURE_2D);
		}
	}
}
