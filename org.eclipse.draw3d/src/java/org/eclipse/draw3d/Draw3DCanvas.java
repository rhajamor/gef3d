/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.logging.Logger;

import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;

/**
 * Draw3DCanvas There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 17.12.2007
 */
public class Draw3DCanvas extends GLCanvas {

	private static GLData createGLData() {

		GLData data = new GLData();
		data.doubleBuffer = true;
		data.depthSize = 1; // Add this line to force a depth buffer
		data.sampleBuffers = 1; // enable multisampling (kristian)
		data.samples = 4; // enable multisampling (kristian)

		return data;

	}

	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(Draw3DCanvas.class
			.getName());

	/**
	 * @param i_parent
	 * @param i_style
	 * @param i_data
	 */
	public Draw3DCanvas(Composite i_parent, int i_style) {

		super(i_parent, i_style, createGLData());

		// Set the default renderer to have one in any case. May be exchanged
		// later.
		RenderContext.getContext().setDefaultRenderer(this);
		Graphics3D g3d = RenderContext.getContext().getGraphics3D();

		setCurrent();
		try {
			g3d.useContext(this);
		} catch (Graphics3DException ex) {
			log.severe(ex.toString());
			throw new RuntimeException(
					"caught exception while setting GL context", ex);
		}

		g3d.glEnable(Graphics3DDraw.GL_TEXTURE_2D);
		g3d.glEnable(Graphics3DDraw.GL_MULTISAMPLE);

		g3d.glEnable(Graphics3DDraw.GL_CULL_FACE);
		g3d.glBlendFunc(Graphics3DDraw.GL_SRC_ALPHA,
				Graphics3DDraw.GL_ONE_MINUS_SRC_ALPHA);

		g3d.glShadeModel(Graphics3DDraw.GL_FLAT);
		g3d.glEnable(Graphics3DDraw.GL_BLEND);

		g3d.glEnable(Graphics3DDraw.GL_DEPTH_TEST);
		g3d.glClearDepth(1.0);

		// tell GL to pack pixels as tightly as possible
		g3d.glPixelStorei(Graphics3DDraw.GL_PACK_ALIGNMENT, 1);
		g3d.glPixelStorei(Graphics3DDraw.GL_UNPACK_ALIGNMENT, 1);
	}
}
