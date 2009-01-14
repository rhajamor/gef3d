/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import java.nio.FloatBuffer;

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.util.BufferUtils;

/**
 * An abstract base class for shapes.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 27.02.2008
 */
public abstract class AbstractModelShape implements Shape {

	private FloatBuffer m_modelMatrixBuffer;

	private boolean m_useModelMatrix = false;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render()
	 */
	public final void render() {

		setup();
		Graphics3D g3d = RenderContext.getContext().getGraphics3D();

		g3d.glMatrixMode(Graphics3DDraw.GL_MODELVIEW);
		if (m_useModelMatrix)
			g3d.glPushMatrix();

		try {
			if (m_useModelMatrix) {
				m_modelMatrixBuffer.rewind();
				g3d.glMultMatrix(m_modelMatrixBuffer);
			}

			performRender();
		} finally {
			if (m_useModelMatrix)
				g3d.glPopMatrix();
		}
	}

	/**
	 * Perform the actual rendering. Extenders of this class must override and
	 * implement this method.
	 */
	protected abstract void performRender();

	/**
	 * Do some setup. Extenders of this class can override and implement this
	 * method.
	 */
	protected void setup() {
		// nothing to setup
	}

	/**
	 * Sets the model matrix for this shape. If the specified model matrix is
	 * <code>null</code>, the identity matrix is used as the model matrix.
	 * 
	 * @param i_modelMatrix the model matrix
	 */
	public void setModelMatrix(IMatrix4f i_modelMatrix) {

		if (i_modelMatrix == null) {
			m_useModelMatrix = false;
			return;
		}

		if (m_modelMatrixBuffer == null)
			m_modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
		else
			m_modelMatrixBuffer.rewind();

		i_modelMatrix.toBufferRowMajor(m_modelMatrixBuffer);
		m_useModelMatrix = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		String className = getClass().getName();
		className = className.substring(className.lastIndexOf("."));

		builder.append(className);
		builder.append("[modelMatrix: ");
		builder.append(m_modelMatrixBuffer);
		builder.append("]");

		return builder.toString();
	}

}
