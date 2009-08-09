/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.ParaxialBoundingBox;
import org.eclipse.draw3d.picking.Query;

/**
 * An invisible shape which cannot be picked. This shape is used mainly during
 * development or for debugging, it is rarely used in real applications.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Aug 7, 2009
 */
public class NullShape implements Shape {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getDistance(org.eclipse.draw3d.picking.Query)
	 */
	public float getDistance(Query iQuery) {

		return Float.NaN;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getParaxialBoundingBox(org.eclipse.draw3d.geometry.ParaxialBoundingBox)
	 */
	public ParaxialBoundingBox getParaxialBoundingBox(
		ParaxialBoundingBox o_result) {

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render(org.eclipse.draw3d.RenderContext)
	 */
	public void render(RenderContext iRenderContext) {

		// nothing to do
	}
}
