/*
 * Type:    org.eclipse.emf.ecoretools.diagram.edit.parts.FeedbackLayer3D
 * File:  	FeedbackLayer3D.java
 * Project:	de.feu.gef3d.ecoretools
 * Date: 	06.12.2008
 * Author: 	Kristian Duske
 * Version:	$Revision$
 * Changed: $Date$ by $Author$ 
 * URL:     $HeadURL$
 *
 * Copyright 2007, FernUniversitaet in Hagen
 */

package org.eclipse.draw3d;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;

/**
 * FeedbackLayer3D
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	Apr 7, 2009
 */	
public class FeedbackLayer3D extends FreeformLayer {

	public FeedbackLayer3D() {
		setEnabled(false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.FreeformLayer#add(org.eclipse.draw2d.IFigure,
	 *      java.lang.Object, int)
	 */
	@Override
	public void add(IFigure i_child, Object i_constraint, int i_index) {

		if (i_child instanceof Connection) {
			Connection conn = (Connection) i_child;
			super.add(conn);
		} else {
			super.add(i_child, i_constraint, i_index);
		}
	}

}