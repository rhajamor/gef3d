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
 * PolygonPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class PolygonPrimitive extends PolylinePrimitive {

	public PolygonPrimitive(float[] i_points, boolean i_filled) {

		super(i_points, i_filled ? PrimitiveType.FILLED_POLYGON
			: PrimitiveType.OUTLINED_POLYGON);
	}

	protected PolygonPrimitive(float[] i_points, PrimitiveType i_type) {

		super(i_points, i_type);
	}
}
