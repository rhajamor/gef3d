/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others,
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation of 2D version
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - refactoring and optimizations
 ******************************************************************************/
package org.eclipse.gef3d.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef3d.handles.MoveHandle3D;
import org.eclipse.gef3d.requests.ChangeBounds3DRequest;
import org.eclipse.swt.events.MouseEvent;

/**
 * Created in {@link MoveHandle3D#createDragTracker()}.
 * 
 * @author IBM Corporation (original 2D version)
 * @author Jens von Pilgrim
 * @author Kristian Duske
 * @version $Revision$
 * @since Apr 15, 2008
 */
public class DragEditPartsTracker3D extends DragEditPartsTracker {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(DragEditPartsTracker3D.class.getName());

	private TrackState m_trackState;

	/**
	 * Creates a new tracker for the given source edit part.
	 * 
	 * @param i_sourceEditPart the source edit part
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#getDragMoveDelta()
	 */
	@Override
	protected Dimension getDragMoveDelta() {

		return new Dimension(getTrackState().getMoveDelta2D());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#getLocation()
	 */
	@Override
	protected Point getLocation() {

		return new Point(getTrackState().getLocation2D());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#getStartLocation()
	 */
	@Override
	protected Point getStartLocation() {

		return new Point(getTrackState().getStartLocation2D());
	}

	/**
	 * Returns the current dragstate. If the drag has just begun, a new
	 * trackstate will be created.
	 * 
	 * @return
	 */
	protected TrackState getTrackState() {

		Point location = getCurrentInput().getMouseLocation();
		if (m_trackState == null) {
			if (log.isLoggable(Level.FINER))
				log.finer("creating track state");

			m_trackState =
				Tracker3DHelper.getTrackState(location, getCurrentViewer());
		}

		m_trackState.setScreenLocation(getCurrentInput().getMouseLocation());
		return m_trackState;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.DragEditPartsTracker#performDrag()
	 */
	@Override
	protected void performDrag() {

		try {
			super.performDrag();
		} finally {
			m_trackState = null;
			Tracker3DHelper.getPicker(getCurrentViewer()).clearIgnored();
		}
		if (log.isLoggable(Level.FINER))
			log.finer("drag finished");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.DragEditPartsTracker#setTargetEditPart(org.eclipse.gef.EditPart)
	 */
	@Override
	protected void setTargetEditPart(EditPart i_editpart) {

		if (i_editpart != getTargetEditPart() && log.isLoggable(Level.FINER))
			log.finer("new target edit part: " + i_editpart);

		super.setTargetEditPart(i_editpart);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.DragEditPartsTracker#updateTargetRequest()
	 */
	@Override
	protected void updateTargetRequest() {

		// if (log.isLoggable(Level.FINER))
		// log.finer("updating target request");

		// behave like 2D version
		super.updateTargetRequest();

		// and add 3D information if possible
		Request request1 = getTargetRequest();
		if (request1 instanceof ChangeBounds3DRequest) {

			ChangeBounds3DRequest req3D = (ChangeBounds3DRequest) request1;
			IVector3f delta =
				new Vector3fImpl(getTrackState().getMoveDelta3D());

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

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.TargetingTool#updateTargetUnderMouse()
	 */
	@Override
	protected boolean updateTargetUnderMouse() {

		// if (log.isLoggable(Level.FINER))
		// log.finer("updating target under mouse");

		if (!isTargetLocked()) {
			EditPart editPart =
				getCurrentViewer().findObjectAtExcluding(
					getCurrentInput().getMouseLocation(), getExclusionSet(),
					getTargetingConditional());
			if (editPart != null)
				editPart = editPart.getTargetEditPart(getTargetRequest());
			boolean changed = getTargetEditPart() != editPart;
			setTargetEditPart(editPart);
			return changed;
		} else
			return false;
	}

	/**
	 * 1:1 {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#mouseDrag(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseDrag(MouseEvent me, EditPartViewer viewer) {

		// if (log.isLoggable(Level.INFO)) {
		//			log.info("MouseEvent, EditPartViewer - me=" + me + ", viewer=" + viewer); //$NON-NLS-1$ //$NON-NLS-2$
		// }

		if (!isViewerImportant(viewer))
			return;
		setViewer(viewer);
		boolean wasDragging = movedPastThreshold();
		getCurrentInput().setInput(me);
		handleDrag();
		if (movedPastThreshold()) {
			if (!wasDragging)
				handleDragStarted();
			handleDragInProgress();
		}
	}
}
