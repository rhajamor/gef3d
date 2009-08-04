/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.TransparentObject;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.Query;

/**
 * Adapter for shapes with transparency. This class can be used for simply
 * adapting {@link Shape} objects to enable transparent shapes used by
 * {@link IFigure3D} objects. The figure is used to calculate the transparency
 * depth of the shape, it is simply the center of the figure's bound. This is a
 * not very accurate method, but it works in most cases.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Aug 16, 2008
 */
public class TransparencyAdapter implements TransparentObject {

	private static final Vector3fImpl TMP_V3 = new Vector3fImpl();

	protected IFigure3D containerFigure;

	protected Shape transparentShape;

	/**
	 * @param i_containerFigure
	 * @param i_transparentShape
	 */
	public TransparencyAdapter(IFigure3D i_containerFigure,
			Shape i_transparentShape) {
		containerFigure = i_containerFigure;
		transparentShape = i_transparentShape;
	}

	/**
	 * Returns the distance of the point of intersection between the picking ray
	 * that is stored in the given query and this shape at the given position.
	 * 
	 * @param i_query the query
	 * @param i_position the position of this shape
	 * @return the distance or {@link Float#NaN} if this shape is not hit by the
	 *         picking ray
	 */
	public float getDistance(Query i_query, Position3D i_position) {

		return transparentShape.getDistance(i_query, i_position);
	}

	/**
	 * Returns center of figure's bounds. This method calculates the center of
	 * the container figures {@link IFigure3D#getBounds()}.
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#getTransparencyDepth()
	 */
	public float getTransparencyDepth(RenderContext renderContext) {
		ICamera camera = renderContext.getScene().getCamera();
		containerFigure.getBounds3D().getCenter(TMP_V3);
		float dist = camera.getDistance(TMP_V3);
		return dist;
	}

	/**
	 * Calls figure's {@link Shape#render()} method. {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#renderTransparent()
	 */
	public void renderTransparent(RenderContext renderContext) {
		transparentShape.render(renderContext);
	}

}