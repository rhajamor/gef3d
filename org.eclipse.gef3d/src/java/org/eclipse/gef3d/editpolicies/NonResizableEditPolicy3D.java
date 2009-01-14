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
package org.eclipse.gef3d.editpolicies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.Request;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.NonResizableHandleKit;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.gef3d.handles.IHandleFactory;
import org.eclipse.gef3d.handles.MoveHandle3DFactory;
import org.eclipse.gef3d.handles.NonResizableHandle3DFactory;
import org.eclipse.gef3d.handles.ResizableHandle3DFactory;


/**
 * NonResizableEditPolicy3D creates 3D handles if used within a 3D editor,
 * otherwise it behaves such like its super class. I.e. this class can be used
 * instead of its superclass to enable controllers which can be used in both,
 * 2D and 3D modes.
 * 
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Apr 14, 2008
 */
public class NonResizableEditPolicy3D extends NonResizableEditPolicy {

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#createDragSourceFeedbackFigure()
	 */
	@Override
	protected IFigure createDragSourceFeedbackFigure() {
		// TODO implement method NonResizableEditPolicy3D.createDragSourceFeedbackFigure
		return super.createDragSourceFeedbackFigure();
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#createSelectionHandles()
	 */
	@Override
	protected List createSelectionHandles() {
		// use 2D implementation if only 2D figures are displayed
		if (Figure3DHelper.getAncestor3D(getHostFigure()) == null) {
			return super.createSelectionHandles();
		} else { // use 3D implementation otherwise
			IHandleFactory nonResizableHF = NonResizableHandle3DFactory.INSTANCE;
			
			List<Handle> list = new ArrayList<Handle>();
			
			// TODO implement tracker method
//		 	if (isDragAllowed())
		 		nonResizableHF.addHandles((GraphicalEditPart)getHost(), list);
//		 	else
//		 		NonResizableHandleKit.addHandles((GraphicalEditPart)getHost(), list, 
//		 				new SelectEditPartTracker(getHost()), SharedCursors.ARROW);
		 	return list;
		}
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#showSourceFeedback(org.eclipse.gef.Request)
	 */
	@Override
	public void showSourceFeedback(Request i_request) {
		// TODO implement method NonResizableEditPolicy3D.showSourceFeedback
		super.showSourceFeedback(i_request);
	}
	
	

}
