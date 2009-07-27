/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthias Thiele - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.graphics3d.x3d.draw;

import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;

/**
 * A polygon graphic primitive. Depending on whether it is looped or not the
 * polygon closes itself automatically (drawing a line from last to first
 * vertex).
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DPolygon extends X3DLineSet {

	/**
	 * Whether the polygon is looped.
	 */
	private boolean m_bIsLoop = false;

	/**
	 * Constructs a poygon
	 * 
	 * @param i_bIsLoop Whether the polygon shall be looped.
	 */
	public X3DPolygon(boolean i_bIsLoop) {
		super();

		m_bIsLoop = i_bIsLoop;
	}

	/**
	 * Gets whether the polygon is looped.
	 * 
	 * @return
	 */
	public boolean isLooped() {
		return m_bIsLoop;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DLineSet#draw(org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawCommand)
	 */
	@Override
	public boolean draw(X3DDrawCommand i_command) {

		super.draw(i_command);

		if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_BEGIN)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 1);

			m_bIsLoop = ((Integer) parameter[0] == Graphics3DDraw.GL_LINE_LOOP);

		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_VERTEX2F)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 2);
			float x = 0, y = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];

			addVertex(x, y, 0);

		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_VERTEX3F)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 3);
			float x = 0, y = 0, z = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];
			z = (Float) parameter[2];

			addVertex(x, y, z);

		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_END)) {

			if (m_bIsLoop) {
				// Need to close the loop by adding again the last and first
				// vertex.
				closeLoop();
			}
			complete();

			return true;
		}

		return false;

	}

	/**
	 * Closes the loop by adding a copy of the first vertex to the end of the
	 * vertices list.
	 */
	private void closeLoop() {
		// Add first vertex again as end point for the loop-closing line
		Vector3f dup2 = new Vector3fImpl(m_vertices.get(0));
		m_vertices.add(dup2);
	}

	/**
	 * Adds a new vertex to the list of vertices.
	 * 
	 * @param i_x X coordinate of the new vertex.
	 * @param i_y Y coordinate of the new vertex.
	 * @param i_z Z coordinate of the new vertex.
	 */
	private void addVertex(float i_x, float i_y, float i_z) {
		m_vertices.add(new Vector3fImpl(i_x, i_y, i_z));

	}
}
