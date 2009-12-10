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

import java.nio.FloatBuffer;

import org.eclipse.draw3d.graphics.optimizer.Attributes;
import org.eclipse.draw3d.graphics3d.ExecutableGraphics2D;
import org.eclipse.draw3d.graphics3d.Graphics3D;

/**
 * LwjglExecutableGraphics2D
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	10.12.2009
 */
public class LwjglExecutableGraphics2D implements ExecutableGraphics2D {

	private static class VBOExecutable {

		private int m_id;

		public void initialize() {

		}

		public void execute() {

		}

		public void dispose() {

		}
	}

	public void add(Attributes i_attributes, FloatBuffer i_vertexBuffer) {

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void dispose(Graphics3D i_g3d) {
		// TODO implement method LwjglExecutableGraphics2D.dispose

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#execute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void execute(Graphics3D i_g3d) {
		// TODO implement method LwjglExecutableGraphics2D.execute

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void initialize(Graphics3D i_g3d) {
		// TODO implement method LwjglExecutableGraphics2D.initialize

	}

}
