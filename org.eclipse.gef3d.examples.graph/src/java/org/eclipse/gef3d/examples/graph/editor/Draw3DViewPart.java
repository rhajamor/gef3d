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
package org.eclipse.gef3d.examples.graph.editor;

import org.eclipse.draw3d.Draw3DCanvas;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.geometry.Position3DImpl;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef3d.examples.graph.editor.figures.GraphFigure3D;
import org.eclipse.gef3d.examples.graph.editor.figures.VertexFigure3D;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * This is a very small example demonstrating how to use Draw3D outside
 * GEF3D. There is no camera tool available, so you cannot move around, but
 * at least you can draw a figure.
 * <p>
 * Note: Currently, we use Draw3D only from GEF3D, so the initialization
 * code might be a little bit weird.
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 13, 2009
 */
public class Draw3DViewPart extends ViewPart {
	
	Draw3DCanvas canvas;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// some composite
		Composite c = new Composite(parent,SWT.NONE);
		c.setLayout(new FillLayout());
		
		// create a Draw3DCanvas inside:
		LightweightSystem3D lws3D = new LightweightSystem3D();
		lws3D.setDrawAxes(true);
		canvas = Draw3DCanvas.createCanvas(c, SWT.NONE,
				lws3D);
		lws3D.setControl(canvas);
		
		// create some figures:
		GraphFigure3D graphFigure = new GraphFigure3D();
		VertexFigure3D vertexFigure = new VertexFigure3D();
		
		// we have to set a size for the vertex, otherwise
		// its size is zero leading to an exception since its
		// surface will have a negative size then (the graph
		// sample is a quick example w/o much error handling)
		Position3D pos = new Position3DImpl();
		pos.setSize3D(new Vector3fImpl(100,100,100));
		pos.setLocation3D(new Vector3fImpl(10,10,0));
		vertexFigure.getPosition3D().setPosition(pos);
		
		graphFigure.add(vertexFigure);
		
		// set content of lightweight system:
		lws3D.setContents(graphFigure);
		
	
	}
	
	
	
	

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		}

}
