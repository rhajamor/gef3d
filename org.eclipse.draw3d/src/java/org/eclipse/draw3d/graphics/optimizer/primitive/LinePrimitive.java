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

import org.eclipse.draw3d.graphics.GraphicsState;

/**
 * LinePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 25.12.2009
 */
public class LinePrimitive extends AbstractVertexPrimitive {

	public LinePrimitive(GraphicsState i_state, int i_x1, int i_y1, int i_x2,
			int i_y2) {

		super(i_state.getTransformation(), new OutlineRenderRule(i_state),
			new float[] { i_x1, i_y1, i_x2, i_y2 });
	}
}
