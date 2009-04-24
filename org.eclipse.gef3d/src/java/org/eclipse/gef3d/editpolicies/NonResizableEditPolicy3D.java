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

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.geometry.BoundingBox;
import org.eclipse.draw3d.geometry.BoundingBoxImpl;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gef3d.handles.FeedbackFigure3D;
import org.eclipse.gef3d.handles.HandleBounds3D;
import org.eclipse.gef3d.handles.IHandleFactory;
import org.eclipse.gef3d.handles.NonResizableHandle3DFactory;


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
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(NonResizableEditPolicy3D.class.getName());

	private static Vector3fImpl TEMP_V_1 = new Vector3fImpl();
	
	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#createDragSourceFeedbackFigure()
	 */
	@Override
	protected IFigure createDragSourceFeedbackFigure() {
		// use 2D implementation if only 2D figures are displayed
		if (Figure3DHelper.getAncestor3D(getHostFigure()) == null) {
			return super.createDragSourceFeedbackFigure();
		} else { // use 3D implementation otherwise

			// Use a ghost rectangle for feedback
			// GEF:
			// RectangleFigure r = new RectangleFigure();
			// FigureUtilities.makeGhostShape(r);
			// r.setLineStyle(Graphics.LINE_DOT);
			// r.setForegroundColor(ColorConstants.white);

			FeedbackFigure3D feedbackFigure = new FeedbackFigure3D();

			// GEF: r.setBounds(getInitialFeedbackBounds());
			IBoundingBox bounds = getInitialFeedbackBounds3D();
			feedbackFigure.setLocation3D(bounds.getPosition(TEMP_V_1));
			feedbackFigure.setSize3D(bounds.getSize(TEMP_V_1));

			// GEF:
			addFeedback(feedbackFigure);
			return feedbackFigure;
		}
	}
	
	/**
	 * Returns the bounds of the host's figure to be used to calculate the
	 * initial location of the feedback.Uses handle bounds if available.
	 * <p>
	 * Here, the returned bound may be modified since they are a copy.
	 * <p>
	 * Copied and modified for 3D from
	 * {@link NonResizableEditPolicy#getInitialFeedbackBounds()}.
	 * </p>
	 * 
	 * @return the host figure's bounding Rectangle
	 */
	protected BoundingBox getInitialFeedbackBounds3D() {

		GraphicalEditPart graphicalEditPart = (GraphicalEditPart) getHost();
		IFigure ref = graphicalEditPart.getFigure();

		BoundingBox bounds3D = new BoundingBoxImpl();
		if (ref instanceof IFigure3D) {
			IFigure3D ref3D = (IFigure3D) ref;

			if (ref3D instanceof HandleBounds3D) {
				HandleBounds3D handleBounds = (HandleBounds3D) ref3D;
				bounds3D.set(handleBounds.getHandleBounds3D());
			} else {
				bounds3D.set(ref3D.getBounds3D());
			}
		} else {
			// do not call super method here, since this method may be
			// overridden causing infinite calls or inconsistent behavior
			// with 3D version

			Rectangle rect;
			if (ref instanceof HandleBounds) {
				HandleBounds handleBounds = (HandleBounds) ref;
				rect = handleBounds.getHandleBounds();
			} else {
				rect = ref.getBounds();
			}

			ref.translateToAbsolute(rect);

			bounds3D.set(Figure3DHelper.convertBoundsToBounds3D(ref, rect, 1));
			bounds3D.translate(0, 0, -1);
		}

		bounds3D.expand(0.01f);
		return bounds3D;
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
		if (log.isLoggable(Level.INFO)) {
			log.info("showSourceFeedback - Not implemented yet"); //$NON-NLS-1$
		}

		// TODO implement method NonResizableEditPolicy3D.showSourceFeedback
		super.showSourceFeedback(i_request);
	}
	
	

}
