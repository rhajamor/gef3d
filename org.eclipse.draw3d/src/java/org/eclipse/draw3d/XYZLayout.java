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
package org.eclipse.draw3d;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * 3D version of the {@link XYLayout}. This lays out the components using
 * the layout constraints as defined by each component. The constraints are
 * expected to be either {@link IVector3f} (location only), {@link IPosition3D}
 * (full 3D position information), {@link Rectangle} (2D with location and
 * size), or only {@link Point}. If constraints are null or another type, the
 * position of the child is not changed by this manager. Also, the surface of the
 * parent is ignored here, if you want to ensure that children are painted on
 * top of their parent's surface, use the XYZSurfaceLayout layout manager.
 * <p>
 * If child is not a 3D figure, only its 2D location components are set, other
 * values maybe present in the constraint are ignored. Particularly no projection
 * is performed. 
 * </p>
 * 
 * @todo class not implemented yet
 * @todo preferred size not supported yet
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 14.01.2008
 */
public class XYZLayout extends XYLayout {
	
	/**
	 * Implements the algorithm to layout the components of the given container
	 * figure. Each component is laid out using its own layout constraint
	 * specifying its size and position.
	 * 
	 * @see LayoutManager#layout(IFigure)
	 */
	public void layout(IFigure parent) {
		super.layout(parent);
//		Point offset = getOrigin(parent);
		
//		for (Object f: parent.getChildren()) {
//			if (f instanceof IFigure3D) {
//				IFigure3D fig3D = (IFigure3D) f;
//				Object constraint = getConstraint(fig3D);
//				
//				if (constraint instanceof IPosition3D) {
//					TEMP_POS.setPosition((IPosition3D) constraint);
//					if (parent)
//					
//					
//				}
//				
//			}
//			
//			
//			
//			Object constraint = getConstraint(f);
//			if (constraint == null)
//				continue;
//			
//			
//			
//			
//			bounds = bounds.getTranslated(offset);
//			f.setBounds(bounds);
//		}
	}

}
