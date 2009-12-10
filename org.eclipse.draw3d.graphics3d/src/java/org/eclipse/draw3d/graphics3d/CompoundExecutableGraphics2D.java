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
package org.eclipse.draw3d.graphics3d;

import java.util.List;

/**
 * CompoundExecutableGraphics2D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 10.12.2009
 */
public class CompoundExecutableGraphics2D implements ExecutableGraphics2D {

	private List<ExecutableGraphics2D> m_exetuables;

	public CompoundExecutableGraphics2D(List<ExecutableGraphics2D> i_exetuables) {

		m_exetuables = i_exetuables;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void dispose(Graphics3D i_g3d) {

		for (ExecutableGraphics2D executable : m_exetuables)
			executable.dispose(i_g3d);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#execute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void execute(Graphics3D i_g3d) {

		for (ExecutableGraphics2D executable : m_exetuables)
			executable.execute(i_g3d);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.ExecutableGraphics2D#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	public void initialize(Graphics3D i_g3d) {

		for (ExecutableGraphics2D executable : m_exetuables)
			executable.initialize(i_g3d);
	}
}
