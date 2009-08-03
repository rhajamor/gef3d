/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.picking.Query;

/**
 * A shape is a geometric object that can render itself using OpenGL commands.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 27.02.2008
 */
public interface Shape {

	/**
	 * Returns the distance of the point of intersection between the picking ray
	 * that is stored in the given query and this shape at the given position.
	 * 
	 * @param i_query the query
	 * @param i_position the position of this shape
	 * @return the distance or {@link Float#NaN} if this shape is not hit by the
	 *         picking ray
	 */
	public float getDistance(Query i_query, Position3D i_position);

	/**
	 * Render the shape.
	 * 
	 * @param i_renderContext the current render context
	 */
	public void render(RenderContext i_renderContext);
}
