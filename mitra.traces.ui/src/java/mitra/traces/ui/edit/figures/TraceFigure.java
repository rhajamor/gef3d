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

package mitra.traces.ui.edit.figures;

import java.util.logging.Logger;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.Shape;

/**
 * TraceFigure There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 18.01.2008
 * 
 */
public class TraceFigure extends Figure3D {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(TraceFigure.class.getName());

	private Shape m_shape = new CuboidFigureShape(this);

	public TraceFigure() {

		// embed 2D components into this figure
		this.setLayoutManager(new ToolbarLayout());

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#postrender()
	 */
	@Override
	public void postrender(RenderContext renderContext) {
		if (isVisible())
			m_shape.render(renderContext);
	}

	public void setTag(String strTag) {
		Label label;
		for (String strLine : strTag.split(",")) {
			label = new Label(strLine);
			add(label);
		}

	}
}
