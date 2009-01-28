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
package org.eclipse.gef3d.handles;

import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.PositionConstants3D;
import org.eclipse.draw3d.RelativeLocator3D;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gef.handles.RelativeHandleLocator;


/**
 * RelativeHandleLocator3D,
 * There should really be more documentation here.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Mar 24, 2008
 */
public class RelativeHandleLocator3D extends RelativeLocator3D {

	/**
	 * @param i_reference
	 * @param i_location
	 * @param i_zlocation
	 */
	public RelativeHandleLocator3D(IFigure i_reference, int i_location, PositionConstants3D i_zlocation) {
		super(i_reference, i_location);
	}
	
	/**
	 * Overridden to check for reference figures implementing the
	 * {@link HandleBounds} interface.
	 * 
	 * @see RelativeHandleLocator#getReferenceBox()
	 * @see RelativeLocator3D#getReferenceBox3D()
	 * @return
	 */
	public IBoundingBox getReferenceBox3D() {
		IFigure referenceFig = getReferenceFigure();
		if (referenceFig instanceof IFigure3D) {
			if (referenceFig instanceof HandleBounds3D)
				return ((HandleBounds3D) referenceFig).getHandleBounds3D();
			else
				return ((IFigure3D) referenceFig).getBounds3D();
		} else {
			// do not call super method here, since this method may be
			// overridden causing infinite calls or inconsistent behavior
			// with 3D version
			Rectangle rect = (referenceFig instanceof HandleBounds) ? ((HandleBounds) referenceFig)
					.getHandleBounds()
					: referenceFig.getBounds();

			referenceFig.translateToAbsolute(rect);

			return Figure3DHelper.convertBoundsToBounds3D(referenceFig, rect);
		}
	}

}
