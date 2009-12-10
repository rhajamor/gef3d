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
package org.eclipse.draw3d.graphics.optimizer;

/**
 * LinePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class LinePrimitive extends PolylinePrimitive {

	public LinePrimitive(float[] i_points) {

		super(i_points, PrimitiveType.LINE);

		if (i_points.length != 4)
			throw new IllegalArgumentException(
				"a point must contain exactly two vertices");
	}

	public LinePrimitive(float i_x1, float i_y1, float i_x2, float i_y2) {

		this(new float[] { i_x1, i_y1, i_x2, i_y2 });
	}
}
