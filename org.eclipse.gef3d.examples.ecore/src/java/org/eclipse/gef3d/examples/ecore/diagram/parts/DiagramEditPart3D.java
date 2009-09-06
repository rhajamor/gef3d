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
package org.eclipse.gef3d.examples.ecore.diagram.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.gef3d.examples.ecore.figures.DiagramFigure3D;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.tools.DragEditPartsTrackerEx;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * DiagramEditPart3D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 02.09.2009
 */
public class DiagramEditPart3D extends DiagramEditPart {

	/**
	 * Creates a new edit part for the given view.
	 * 
	 * @param i_diagramView the view
	 */
	public DiagramEditPart3D(View i_diagramView) {
		super(i_diagramView);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Figure3D f = new DiagramFigure3D();

		f.setBackgroundColor(new Color(Display.getCurrent(), 255, 255, 255));
		f.setAlpha((byte) (255 / 2));

		return f;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	@Override
	public DragTracker getDragTracker(Request req) {
		if (req instanceof SelectionRequest
			&& ((SelectionRequest) req).getLastButtonPressed() == 3)
			return new DeselectAllTracker(this);
		return new DragEditPartsTrackerEx(this);
	}
}
