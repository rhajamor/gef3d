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

import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * A simple line graphic primitive.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DLine extends X3DLineSet {

	/**
	 * The standard constructor.
	 */
	public X3DLine() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawTarget#draw(X3DDrawCommand)
	 */
	@Override
	public boolean draw(X3DDrawCommand i_command) {

		super.draw(i_command);

		if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_VERTEX2F)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 2);
			float x = 0, y = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];

			m_vertices.add(new Vector3fImpl(x, y, 0));

		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_VERTEX3F)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 3);
			float x = 0, y = 0, z = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];
			z = (Float) parameter[2];

			m_vertices.add(new Vector3fImpl(x, y, z));
		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_END)) {
			// Nothing special to do before finalizing
			complete();
		}

		if (m_vertices.size() == 2) {
			// Complete after each second vertex.
			return true;
		}

		return false;
	}

}
