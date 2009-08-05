/*
 * Type:    org.eclipse.emf.ecoretools.diagram.edit.parts.DiagramFigure3D
 * File:  	DiagramFigure3D.java
 * Project:	de.feu.gef3d.ecoretools
 * Date: 	06.12.2008
 * Author: 	Kristian Duske
 * Version:	$Revision$
 * Changed: $Date$ by $Author$ 
 * URL:     $HeadURL$
 *
 * Copyright 2007, FernUniversitaet in Hagen
 */

package org.eclipse.gef3d.examples.uml2.figures;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw3d.ShapeFigure3D;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.Shape;

public class DiagramFigure3D extends ShapeFigure3D {

	protected int headerStyle;

	public DiagramFigure3D() {

		setLayoutManager(new FreeformLayout());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ShapeFigure3D#createShape()
	 */
	@Override
	protected Shape createShape() {

		return new CuboidFigureShape(this);
	}
}