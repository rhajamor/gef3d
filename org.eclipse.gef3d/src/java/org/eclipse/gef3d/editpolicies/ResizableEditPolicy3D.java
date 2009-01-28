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

import static org.eclipse.draw2d.PositionConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.geometry.BoundingBox;
import org.eclipse.draw3d.geometry.BoundingBoxImpl;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef3d.handles.FeedbackFigure3D;
import org.eclipse.gef3d.handles.HandleBounds3D;
import org.eclipse.gef3d.handles.IHandleFactory;
import org.eclipse.gef3d.handles.MoveHandle3DFactory;
import org.eclipse.gef3d.handles.NonResizableHandle3DFactory;
import org.eclipse.gef3d.handles.ResizableHandle3DFactory;
import org.eclipse.gef3d.requests.ChangeBounds3DRequest;

/**
 * 3D version of {@link ResizableEditPolicy}, creates 3D handles if used within
 * a 3D editor, otherwise it behaves such like its super class. I.e. this class
 * can be used instead of its superclass to enable controllers which can be used
 * in both, 2D and 3D modes.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 24, 2008
 */
public class ResizableEditPolicy3D extends ResizableEditPolicy {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(ResizableEditPolicy3D.class.getName());

	private static Vector3fImpl TEMP_V_1 = new Vector3fImpl();

	/**
	 * {@inheritDoc} This method is responsible for creating selection handles.
	 * <p>
	 * Copied from {@link ResizableEditPolicy}. Instead of handler kits,
	 * factories are used. If no 3D ancestor is found, the original method is
	 * used.
	 * </p>
	 * 
	 * @see org.eclipse.gef.editpolicies.ResizableEditPolicy#createSelectionHandles()
	 */
	@Override
	protected List createSelectionHandles() {
		// use 2D implementation if only 2D figures are displayed
		if (Figure3DHelper.getAncestor3D(getHostFigure()) == null) {
			return super.createSelectionHandles();
		} else { // use 3D implementation otherwise

			IHandleFactory nonResizableHF = NonResizableHandle3DFactory.INSTANCE;
			IHandleFactory resizableHF = ResizableHandle3DFactory.INSTANCE;
			IHandleFactory moveHF = MoveHandle3DFactory.INSTANCE;

			int directions = getResizeDirections();

			List<Handle> list = new ArrayList<Handle>();
			GraphicalEditPart host = (GraphicalEditPart) getHost();

			if (directions == 0)
				nonResizableHF.addHandles(host, list);
			else if (directions != -1) {
				moveHF.addHandles(host, list);
				if ((directions & EAST) != 0)
					resizableHF.addHandle(host, list, EAST);
				else
					nonResizableHF.addHandle(host, list, EAST);
				if ((directions & SOUTH_EAST) == SOUTH_EAST)
					resizableHF.addHandle(host, list, SOUTH_EAST);
				else
					nonResizableHF.addHandle(host, list, SOUTH_EAST);
				if ((directions & SOUTH) != 0)
					resizableHF.addHandle(host, list, SOUTH);
				else
					nonResizableHF.addHandle(host, list, SOUTH);
				if ((directions & SOUTH_WEST) == SOUTH_WEST)
					resizableHF.addHandle(host, list, SOUTH_WEST);
				else
					nonResizableHF.addHandle(host, list, SOUTH_WEST);
				if ((directions & WEST) != 0)
					resizableHF.addHandle(host, list, WEST);
				else
					nonResizableHF.addHandle(host, list, WEST);
				if ((directions & NORTH_WEST) == NORTH_WEST)
					resizableHF.addHandle(host, list, NORTH_WEST);
				else
					nonResizableHF.addHandle(host, list, NORTH_WEST);
				if ((directions & NORTH) != 0)
					resizableHF.addHandle(host, list, NORTH);
				else
					nonResizableHF.addHandle(host, list, NORTH);
				if ((directions & NORTH_EAST) == NORTH_EAST)
					resizableHF.addHandle(host, list, NORTH_EAST);
				else
					nonResizableHF.addHandle(host, list, NORTH_EAST);
			} else
				resizableHF.addHandles(host, list);

			return list;
		}
	}

	/**
	 * Creates the figure used for feedback.
	 * <p>
	 * Copied and modified for 3D from
	 * {@link NonResizableEditPolicy#createDragSourceFeedbackFigure()}.
	 * </p>
	 * 
	 * @return the new feedback figure
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
	 * Shows or updates feedback for a change bounds request.
	 * <p>
	 * Copied and modified for 3D from
	 * {@link NonResizableEditPolicy#showChangeBoundsFeedback(ChangeBoundsRequest)}
	 * .
	 * </p>
	 * 
	 * @param request the request
	 */
	@Override
	protected void showChangeBoundsFeedback(ChangeBoundsRequest request) {
		// if (log.isLoggable(Level.INFO)) {
		// log.info("showChangeBoundsFeedback"); //$NON-NLS-1$
		// }

		// GEF: IFigure feedback = getDragSourceFeedbackFigure();
		IFigure feedback = getDragSourceFeedbackFigure();

		if (!(feedback instanceof IFigure3D)) {
			super.showChangeBoundsFeedback(request);
		} else {
			// GEF: PrecisionRectangle rect = new
			// PrecisionRectangle(getInitialFeedbackBounds().getCopy());
			BoundingBox bb = getInitialFeedbackBounds3D();

			// GEF: getHostFigure().translateToAbsolute(rect);
			if (getHostFigure() instanceof IFigure3D)
				((IFigure3D) getHostFigure()).transformToAbsolute(bb);

			// GEF: rect.translate(request.getMoveDelta());
			if (request instanceof ChangeBounds3DRequest) {
				ChangeBounds3DRequest req3D = (ChangeBounds3DRequest) request;
				bb.translate(req3D.getMoveDelta3D());
				// GEF: rect.resize(request.getSizeDelta());
				bb.resize(req3D.getSizeDelta3D());

				// GEF: feedback.translateToRelative(rect);
				IFigure3D feedback3D = (IFigure3D) feedback;
				feedback3D.transformToRelative(bb);

				// GEF: feedback.setBounds(rect);
				feedback3D.setLocation3D(bb.getPosition(TEMP_V_1));

				// Why is this in GEF not necessary:
				IVector3f rv = bb.getSize(TEMP_V_1);
				bb.setSize((rv.getX() < 0) ? 0 : rv.getX(), //
						(rv.getY() < 0) ? 0 : rv.getY(), //
						(rv.getZ() < 0) ? 0 : rv.getZ());

				feedback3D.setSize3D(bb.getSize(TEMP_V_1));
			} else {
				log
						.warning("ChangeBoundsRequest - ChangeBoundsRequest3D expected, received " + request); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
