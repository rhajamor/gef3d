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
package org.eclipse.draw3d.graphics.optimizer.primitive;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw3d.graphics.GraphicsState;

/**
 * PolylinePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 25.12.2009
 */
public class PolylinePrimitive extends AbstractVertexPrimitive {

	public PolylinePrimitive(GraphicsState i_state, PointList i_vertices) {

		super(i_state.getTransformation(), new OutlineRenderRule(i_state),
			i_vertices);

		if (i_vertices.size() < 3)
			throw new IllegalArgumentException(
				"a polyline must contain at least three vertices");
	}

	public PolylinePrimitive(GraphicsState i_state, float[] i_vertices) {

		super(i_state.getTransformation(), new OutlineRenderRule(i_state),
			i_vertices);

		if (i_vertices.length < 6)
			throw new IllegalArgumentException(
				"a polyline must contain at least three vertices");
	}
}
