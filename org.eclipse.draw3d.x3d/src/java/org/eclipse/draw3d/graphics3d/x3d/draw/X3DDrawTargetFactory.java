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

import org.eclipse.draw3d.graphics3d.Graphics3DException;

/**
 * This factory creates draw targets.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DDrawTargetFactory {

	/**
	 * A line graphics primitive which contains exactly two vertices.
	 */
	public static final int DRAW_TARGET_LINE = 1;

	/**
	 * A polygon primitive which may contain any number of vertices. Every two
	 * vertices define one edge of the polygon.
	 */
	public static final int DRAW_TARGET_POLYGON = 2;

	/**
	 * Similar to {@link #DRAW_TARGET_POLYGON} which the difference that the
	 * last and the first vertex define a closing edge.
	 */
	public static final int DRAW_TARGET_POLYGON_LOOP = 3;

	/**
	 * A quad graphics primitive, which may also get a texture assigned or
	 * filled with any diffuse color.
	 */
	public static final int DRAW_TARGET_QUAD = 4;

	/**
	 * Creates a draw target of the specified type.
	 * 
	 * @param i_iType The type of draw target to create.
	 * @return The created draw target.
	 */
	public static X3DDrawTarget createDrawTarget(int i_iType) {

		X3DDrawTarget dt = null;

		switch (i_iType) {

		case DRAW_TARGET_LINE:
			dt = new X3DLine();
			break;

		case DRAW_TARGET_QUAD:
			dt = new X3DQuad();
			break;

		case DRAW_TARGET_POLYGON:
			dt = new X3DPolygon(false);
			break;

		case DRAW_TARGET_POLYGON_LOOP:
			dt = new X3DPolygon(true);
			break;

		default:
			throw new Graphics3DException("Unknown draw target type: "
					+ i_iType);

		}

		return dt;
	}

	/**
	 * This method can be used to create a new instance of an existing draw
	 * target.
	 * 
	 * @param original The original draw target
	 * @return A new draw target instance of the same type as the original.
	 */
	public static X3DDrawTarget createNewInstance(X3DDrawTarget original) {
		if (original instanceof X3DLine) {
			return new X3DLine();
		} else if (original instanceof X3DQuad) {
			return new X3DQuad();
		} else if (original instanceof X3DPolygon) {
			X3DPolygon poly = (X3DPolygon) original;
			return new X3DPolygon(poly.isLooped());
		} else {
			throw new Graphics3DException("Unknown original: "
					+ original.getClass());
		}
	}

}
