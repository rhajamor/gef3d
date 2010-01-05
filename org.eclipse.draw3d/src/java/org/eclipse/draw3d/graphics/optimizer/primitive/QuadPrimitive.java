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

import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.graphics.GraphicsState;

/**
 * PolylinePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 25.12.2009
 */
public class QuadPrimitive extends AbstractVertexPrimitive {

	public static QuadPrimitive createGradientQuad(GraphicsState i_state,
		int i_x, int i_y, int i_width, int i_height, boolean i_vertical) {

		return new QuadPrimitive(i_state.getTransformation(),
			new GradientRenderRule(i_state),
			getVertices(i_x, i_y, i_width, i_height, i_vertical));
	}

	public static QuadPrimitive createOutlineQuad(GraphicsState i_state,
		int i_x, int i_y, int i_width, int i_height) {

		return new QuadPrimitive(i_state.getTransformation(),
			new OutlineRenderRule(i_state),
			getVertices(i_x, i_y, i_width, i_height, false));
	}

	public static QuadPrimitive createSolidQuad(GraphicsState i_state, int i_x,
		int i_y, int i_width, int i_height) {

		return new QuadPrimitive(i_state.getTransformation(),
			new SolidRenderRule(i_state),
			getVertices(i_x, i_y, i_width, i_height, false));
	}

	protected static float[] getVertices(int i_x, int i_y, int i_w, int i_h,
		boolean i_vertical) {

		if (i_vertical)
			return new float[] { i_x + i_w, i_y, i_x, i_y, i_x, i_y + i_h,
				i_x + i_w, i_y + i_h };

		return new float[] { i_x, i_y, i_x, i_y + i_h, i_x + i_w, i_y + i_h,
			i_x + i_w, i_y };
	}

	protected QuadPrimitive(IMatrix4f i_transformation,
			RenderRule i_renderRule,
			float[] i_vertices) {

		super(i_transformation, i_renderRule, i_vertices);

		if (i_vertices.length != 8)
			throw new IllegalArgumentException(
				"a quad must contain exactly four vertices");
	}
}
