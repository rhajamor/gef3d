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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig;
import org.eclipse.draw3d.util.BufferUtils;
import org.eclipse.draw3d.util.StopWatch;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * Picking buffers that contain a snapshot.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 16.05.2008
 */
public class SnapshotBuffers implements PickingBuffers {

	private static final Logger log = Logger.getLogger(SnapshotBuffers.class
			.getName());

	private final Graphics3DOffscreenBufferConfig m_bufferConfig;

	private ByteBuffer m_colorBuffer;

	private FloatBuffer m_depthBuffer;

	private boolean m_disposed;

	private final int m_height;

	private final int m_width;

	/**
	 * Creates a new snapshot of the given buffers. The given buffers will be
	 * copied so that changes to those buffers are not reflected in this
	 * snapshot.
	 * 
	 * @param i_colorBuffer the color buffer to preserve
	 * @param i_depthBuffer the depth buffer to preserve
	 * @param i_width the width of the buffers, in pixels
	 * @param i_height the height of the buffers, in pixels
	 * @param i_bufferConfig the buffer configuration
	 * @throws NullPointerException if any of the given buffers is
	 *             <code>null</code>
	 */
	public SnapshotBuffers(ByteBuffer i_colorBuffer, FloatBuffer i_depthBuffer,
			int i_width, int i_height,
			Graphics3DOffscreenBufferConfig i_bufferConfig) {

		if (i_colorBuffer == null)
			throw new NullPointerException("i_colorBuffer must not be null");

		if (i_depthBuffer == null)
			throw new NullPointerException("i_depthBuffer must not be null");

		if (i_bufferConfig == null)
			throw new NullPointerException("i_bufferConfig must not be null");

		if (log.isLoggable(Level.FINE))
			log.fine(StopWatch.start("color buffer snapshot"));

		m_colorBuffer = BufferUtils.createByteBuffer(i_colorBuffer.capacity());
		i_colorBuffer.rewind();
		m_colorBuffer.put(i_colorBuffer);

		m_depthBuffer = BufferUtils.createFloatBuffer(i_depthBuffer.capacity());
		m_depthBuffer.rewind();
		m_depthBuffer.put(i_depthBuffer);

		m_width = i_width;
		m_height = i_height;
		m_bufferConfig = i_bufferConfig;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#createSnapshot()
	 */
	public PickingBuffers createSnapshot() {

		return new SnapshotBuffers(getColorBuffer(), getDepthBuffer(),
				getWidth(), getHeight(), getBufferConfig());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#dispose()
	 */
	public void dispose() {

		if (m_disposed)
			return;

		m_colorBuffer = null;
		m_depthBuffer = null;

		m_disposed = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getBufferConfig()
	 */
	public Graphics3DOffscreenBufferConfig getBufferConfig() {

		return m_bufferConfig;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getColorBuffer()
	 */
	public ByteBuffer getColorBuffer() {

		if (m_disposed)
			throw new IllegalStateException("picking buffers are disposed");

		return m_colorBuffer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getDepthBuffer()
	 */
	public FloatBuffer getDepthBuffer() {

		if (m_disposed)
			throw new IllegalStateException("picking buffers are disposed");

		return m_depthBuffer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getHeight()
	 */
	public int getHeight() {

		if (m_disposed)
			throw new IllegalStateException("picking buffers are disposed");

		return m_height;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#getWidth()
	 */
	public int getWidth() {

		if (m_disposed)
			throw new IllegalStateException("picking buffers are disposed");

		return m_width;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.PickingBuffers#repaint(org.eclipse.draw2d.IFigure,
	 *      org.eclipse.draw3d.picking.FigureManager,
	 *      org.eclipse.swt.opengl.GLCanvas)
	 */
	public void repaint(IFigure i_rootFigure, FigureManager i_figureManager,
			GLCanvas i_canvas) {

		// nothing to do
	}
}
