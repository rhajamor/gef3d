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
 * QuadPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class QuadPrimitive extends PolygonPrimitive {

	protected QuadPrimitive(float[] i_points, PrimitiveType i_type) {

		super(i_points, i_type);

		if (i_points.length != 8)
			throw new IllegalArgumentException(
				"a quad can only contain 4 vertices");
	}

	public QuadPrimitive(float[] i_points, boolean i_filled) {

		this(i_points, i_filled ? PrimitiveType.SOLID_QUAD
			: PrimitiveType.OUTLINE_QUAD);
	}
}
