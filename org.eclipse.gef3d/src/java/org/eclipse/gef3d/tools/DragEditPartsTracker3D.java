/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - refactoring and optimizations
 ******************************************************************************/
package org.eclipse.gef3d.tools;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef3d.handles.FeedbackFigure3D;
import org.eclipse.gef3d.requests.ChangeBounds3DRequest;
import org.eclipse.draw3d.geometry.IVector3f;


/**
 * DragEditPartsTracker3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 15, 2008
 */
public class DragEditPartsTracker3D extends DragEditPartsTracker {

	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(DragEditPartsTracker3D.class.getName());

	protected TrackState m_trackState;

	/**
	 * @param i_sourceEditPart
	 */
	public DragEditPartsTracker3D(EditPart i_sourceEditPart) {
		super(i_sourceEditPart);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.DragEditPartsTracker#createTargetRequest()
	 */
	@Override
	protected Request createTargetRequest() {
		if (isCloneActive())
			return new ChangeBounds3DRequest(REQ_CLONE);
		else
			return new ChangeBounds3DRequest(REQ_MOVE);
	}

	private ColorPicker getSnapshotPicker() {

		ColorPicker picker = Tracker3DHelper.getPicker(getCurrentViewer());
		if (picker == null)
			throw new IllegalStateException("no color picker available");

		picker.ignoreType(FeedbackFigure3D.class);
		picker.ignoreType(Handle.class);

		EditPartViewer viewer = getCurrentViewer();
		List<?> selectedEditParts = viewer.getSelectedEditParts();

		for (Object object : selectedEditParts) {
			if (object instanceof GraphicalEditPart) {
				GraphicalEditPart editPart = (GraphicalEditPart) object;
				IFigure figure = editPart.getFigure();

				if (figure instanceof IFigure3D) {
					IFigure3D figure3D = (IFigure3D) figure;
					picker.ignoreFigure(figure3D);
				}
			}
		}

		ColorPicker snapshot = picker.createSnapshot();
		picker.clearIgnored();

		return snapshot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.DragEditPartsTracker#performDrag()
	 */
	@Override
	protected void performDrag() {

		super.performDrag();
		m_trackState = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.SelectEditPartTracker#setSourceEditPart(org.eclipse.gef.EditPart)
	 */
	@Override
	protected void setSourceEditPart(EditPart i_part) {

		super.setSourceEditPart(i_part);

		if (i_part != getSourceEditPart())
			m_trackState = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.DragEditPartsTracker#updateTargetRequest()
	 */
	@Override
	protected void updateTargetRequest() {
		// behave like 2D version
		super.updateTargetRequest();

		// and add 3D information if possible
		Request request1 = getTargetRequest();
		if (request1 instanceof ChangeBounds3DRequest) {

			repairStartLocation();
			ChangeBounds3DRequest req3D = (ChangeBounds3DRequest) request1;

			if (m_trackState == null) {
				ColorPicker snapshot = getSnapshotPicker();
				m_trackState = new TrackState(getStartLocation(), snapshot);
			}

			m_trackState.setLocation(getLocation());
			IVector3f delta = m_trackState.getMoveDelta3D(null);
			
			// TODO handle modifier keys for constrained movement
			// constrains the move to dx=0, dy=0, or dx=dy if shift is depressed
			// if (getCurrentInput().isShiftKeyDown()) {
			// request.setConstrainedMove(true);
			// float ratio = 0;
			//
			// if (delta.width != 0)
			// ratio = (float) delta.height / (float) delta.width;
			//
			// ratio = Math.abs(ratio);
			// if (ratio > 0.5 && ratio < 1.5) {
			// if (Math.abs(delta.height) > Math.abs(delta.width)) {
			// if (delta.height > 0)
			// delta.height = Math.abs(delta.width);
			// else
			// delta.height = -Math.abs(delta.width);
			// } else {
			// if (delta.width > 0)
			// delta.width = Math.abs(delta.height);
			// else
			// delta.width = -Math.abs(delta.height);
			// }
			// } else {
			// if (Math.abs(delta.width) > Math.abs(delta.height))
			// delta.height = 0;
			// else
			// delta.width = 0;
			// }
			// }

			// GEF: Point moveDelta = new Point(delta.width, delta.height);
			// GEF: request.setMoveDelta(moveDelta);

			// We use vector for both dimensin and point, hence
			// we don't have to convert between dimension and point:
			req3D.setMoveDelta3D(delta);

			// TODO implement snap to helper
			// if (snapToHelper != null
			// && !getCurrentInput().isModKeyDown(MODIFIER_IGNORE_SNAP)) {
			// PrecisionRectangle baseRect = sourceRectangle.getPreciseCopy();
			// PrecisionRectangle jointRect = compoundSrcRect.getPreciseCopy();
			// baseRect.translate(moveDelta);
			// jointRect.translate(moveDelta);
			//
			// PrecisionPoint preciseDelta = new PrecisionPoint(moveDelta);
			// snapToHelper.snapPoint(request, PositionConstants.HORIZONTAL
			// | PositionConstants.VERTICAL, new PrecisionRectangle[] {
			// baseRect, jointRect }, preciseDelta);
			// request.setMoveDelta(preciseDelta);
			// }

		}
	}
}
