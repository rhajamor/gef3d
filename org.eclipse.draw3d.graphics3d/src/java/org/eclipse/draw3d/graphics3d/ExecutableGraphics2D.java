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

/**
 * ExecutableGraphics2D
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	10.12.2009
 */
public interface ExecutableGraphics2D {

	public void execute(Graphics3D i_g3d);

	public void initialize(Graphics3D i_g3d);

	public void dispose(Graphics3D i_g3d);
}
