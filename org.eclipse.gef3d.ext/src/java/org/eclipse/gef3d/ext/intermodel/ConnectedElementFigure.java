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

package org.eclipse.gef3d.ext.intermodel;

import java.util.logging.Logger;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.Shape;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


/**
 * ConnectedElementFigure There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 18.01.2008
 */
public class ConnectedElementFigure extends Figure3D {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(ConnectedElementFigure.class.getName());

	private Shape m_shape = new CuboidFigureShape(this);

	Label tagLabel;

	public ConnectedElementFigure() {

		// embed 2D components into this figure
		this.setLayoutManager(new FlowLayout());
		Color c = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
		setAlpha(200);
		setBackgroundColor(c);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#postrender()
	 */
	@Override
	public void postrender() {

		m_shape.render();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#render()
	 */
	@Override
	public void render() {

		// nothing to do
	}

	public void setTag(String strTag) {
		tagLabel.setText(strTag);
	}

}
