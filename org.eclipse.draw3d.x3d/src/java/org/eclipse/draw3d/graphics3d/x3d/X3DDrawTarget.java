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

package org.eclipse.draw3d.graphics3d.x3d;

import java.util.ListIterator;

import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * ...
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public interface X3DDrawTarget {

	public void draw(/* X3DDrawCommand i_command */);

	public ListIterator<X3DNode> getNodeIterator();

	public void addGraphics2D(String i_strImageFile, String i_strExportPath);

}
