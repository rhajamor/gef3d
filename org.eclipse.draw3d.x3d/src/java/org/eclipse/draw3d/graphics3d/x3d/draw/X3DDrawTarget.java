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

import java.util.ListIterator;

import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * An interface which is implemented by the X3D graphic primitives. Its target
 * is to implement the drawing itself transparently in the graphic primitives.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public interface X3DDrawTarget {

	/**
	 * Send a draw command for execution to this draw target.
	 * 
	 * @param i_command The command to execute.
	 * @return <code>True</code> if this draw target is completed and needs to
	 *         be exchanged against a new instance of the same type.
	 *         <code>False</code> otherwise.
	 */
	public boolean draw(X3DDrawCommand i_command);

	/**
	 * Get an iterator over the nodes of this draw target.
	 * 
	 * @return The node iterator
	 */
	public ListIterator<X3DNode> getNodeIterator();

	/**
	 * Assigns a texture (graphics2d) to this draw target.
	 * 
	 * @param i_strImageFile At this path, the texture file can be found.
	 * @param i_strExportPath This is the destination path of the export.
	 */
	public void addGraphics2D(String i_strImageFile, String i_strExportPath);

}
