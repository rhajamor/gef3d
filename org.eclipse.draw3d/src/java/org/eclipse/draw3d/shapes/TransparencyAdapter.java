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
import org.eclipse.draw3d.geometry.Vector3fImpl;


/**
 * Adapter for shapes with transparency. This class can be used 
 * for simply adapting {@link Shape} objects to enable transparent 
 * shapes used by {@link IFigure3D} objects. 
 * 
 * The figure is used to calculate the transparency depth of the shape, 
 * it is simply the center of the figure's bound. This is a not very 
 * accurate method, but it works in most cases.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Aug 16, 2008
 */	
public class TransparencyAdapter implements TransparentObject {

	protected Shape transparentShape;
	protected IFigure3D containerFigure;

	private static final Vector3fImpl TMP_V3 = new Vector3fImpl();

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
	 * Returns center of figure's bounds. This method calculates the
	 * center of the container figures {@link IFigure3D#getBounds()}.
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#getTransparencyDepth()
	 */
	public float getTransparencyDepth() {
		RenderContext renderContext = RenderContext.getContext();
		ICamera camera = renderContext.getCamera();
		containerFigure.getBounds3D().getCenter(TMP_V3);
		float dist = camera.getDistance(TMP_V3);
		return dist;
	}

	/**
	 * Calls figure's {@link Shape#render()} method.
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.TransparentObject#renderTransparent()
	 */
	public void renderTransparent() {
		transparentShape.render();
	}

}