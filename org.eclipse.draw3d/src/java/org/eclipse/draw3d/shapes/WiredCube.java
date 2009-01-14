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

import org.eclipse.draw3d.DisplayListManager;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.swt.graphics.Color;

/**
 * A wireframe cube.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 27.02.2008
 */
public class WiredCube extends AbstractModelShape {

	private static final String DL_CUBE = "wired_cube";

	private final float[] m_color = new float[] { 0, 0, 0, 1 };

	private void initDisplayLists(DisplayListManager i_displayListManager) {

		if (i_displayListManager.isDisplayList(DL_CUBE))
			return;

		i_displayListManager.createDisplayList(DL_CUBE, new Runnable() {

			public void run() {
				Graphics3D g3d = RenderContext.getContext().getGraphics3D();
				g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
				g3d.glVertex3f(0, 0, 0);
				g3d.glVertex3f(1, 0, 0);
				g3d.glVertex3f(1, 0, 1);
				g3d.glVertex3f(0, 0, 1);
				g3d.glEnd();

				g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
				g3d.glVertex3f(0, 1, 0);
				g3d.glVertex3f(1, 1, 0);
				g3d.glVertex3f(1, 1, 1);
				g3d.glVertex3f(0, 1, 1);
				g3d.glEnd();

				g3d.glBegin(Graphics3DDraw.GL_LINES);
				g3d.glVertex3f(0, 0, 0);
				g3d.glVertex3f(0, 1, 0);
				g3d.glVertex3f(1, 0, 0);
				g3d.glVertex3f(1, 1, 0);
				g3d.glVertex3f(0, 0, 1);
				g3d.glVertex3f(0, 1, 1);
				g3d.glVertex3f(1, 0, 1);
				g3d.glVertex3f(1, 1, 1);
				g3d.glEnd();
			}
		});
	}

	@Override
	protected void performRender() {

		RenderContext renderContext = RenderContext.getContext();
		DisplayListManager displayListManager = renderContext
				.getDisplayListManager();

		initDisplayLists(displayListManager);
		displayListManager.executeDisplayList(DL_CUBE);

	}

	/**
	 * Sets the color of this cube.
	 * 
	 * @param i_color the color of the face
	 * @param i_alpha the alpha value of the face
	 * @throws NullPointerException if the given face or the given color is
	 *             <code>null</code>
	 */
	public void setColor(Color i_color, int i_alpha) {

		if (i_color == null)
			throw new NullPointerException("i_color must not be null");

		ColorConverter.toFloatArray(i_color, i_alpha, m_color);
	}

	/**
	 * Sets the color of this cube.
	 * 
	 * @param i_red the red component
	 * @param i_green the green component
	 * @param i_blue the blue component
	 * @param i_alpha the alpha value
	 */
	public void setColor(int i_red, int i_green, int i_blue, int i_alpha) {

		ColorConverter.toFloatArray(i_red, i_green, i_blue, i_alpha, m_color);
	}

	@Override
	protected void setup() {

		float red = m_color[0];
		float green = m_color[1];
		float blue = m_color[2];
		float alpha = m_color[3];
		Graphics3D g3d = RenderContext.getContext().getGraphics3D();
		g3d.glColor4f(red, green, blue, alpha);
	}
}
