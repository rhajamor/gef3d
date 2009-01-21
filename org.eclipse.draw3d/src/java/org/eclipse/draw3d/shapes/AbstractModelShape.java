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
import org.eclipse.draw3d.geometryext.Position3D;
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

	private Position3D position3D = null;
	
	private Object cachedRawPosition;

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

				
				if (! g3d.isPositionRawCompatible(cachedRawPosition)) {
					cachedRawPosition = g3d.createRawPosition(position3D);
				}
				g3d.setPosition(cachedRawPosition);
				
//				cachedRawPosition.rewind();				
//				g3d.glMultMatrix(cachedRawPosition);
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
	 * Sets the position of this shape. 
	 * 
	 * @param io_positionAsRef the position, this parameter is directly set and
	 * not copied here
	 */
	public void setPosition(Position3D io_positionAsRef) {

		cachedRawPosition = null; 
		
		if (io_positionAsRef == null) {
			m_useModelMatrix = false;
			return;
		}

//		if (cachedRawPosition == null)
//			cachedRawPosition = BufferUtils.createFloatBuffer(16);
//		else
//			cachedRawPosition.rewind();
//
//		i_modelMatrix.toBufferRowMajor(cachedRawPosition);
		position3D = io_positionAsRef;
		m_useModelMatrix = true;
	}
	// was:
//	  Sets the model matrix for this shape. If the specified model matrix is
//	  <code>null</code>, the identity matrix is used as the model matrix.
//	public void setModelMatrix(IMatrix4f i_modelMatrix) {


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
		builder.append(cachedRawPosition);
		builder.append("]");

		return builder.toString();
	}

}
