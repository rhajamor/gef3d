/*******************************************************************************
 * Copyright (c) 2008 Kristian Duske and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.ecore.diagram.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.emf.ecoretools.diagram.edit.parts.EcoreEditPartFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef3d.examples.ecore.figures.DiagramFigure3D;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


/**
 * EcoreEditPartFactory3D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 06.12.2008
 */
public class EcoreEditPartFactory3D extends EcoreEditPartFactory {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecoretools.diagram.edit.parts.EcoreEditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 *      java.lang.Object)
	 */
	@Override
	public EditPart createEditPart(EditPart i_context, Object i_model) {

		if (i_model instanceof Diagram) {
			Diagram diagram = (Diagram) i_model;
			return new DiagramEditPart(diagram) {
				/**
				 * {@inheritDoc}
				 * 
				 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart#createFigure()
				 */
				@Override
				protected IFigure createFigure() {
					Figure3D f = new DiagramFigure3D();
					// Figure3D f = new ClassDiagramFigure3DEmbedded();

					f.setLocation3D(new Vector3fImpl(0, 300, 0));
					f.setSize3D(new Vector3fImpl(1400, 1400, 60));

					f.setBackgroundColor(new Color(Display.getCurrent(), 255,
							255, 255));
					f.setAlpha((byte) (255 / 2));

					return f;
				}
			};
		}

		return super.createEditPart(i_context, i_model);
	}
}
