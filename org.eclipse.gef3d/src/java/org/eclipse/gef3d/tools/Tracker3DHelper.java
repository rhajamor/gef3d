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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef3d.handles.FeedbackFigure3D;

/**
 * Contains utility methods used in trackers.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 17, 2008
 */
public class Tracker3DHelper {

	/**
	 * Returns the picker for the given edit part viewer.
	 * 
	 * @param i_viewer the edit part viewer
	 * @return the picker for the given viewer or <code>null</code> if there is
	 *         no picker for the given viewer
	 * @throws NullPointerException if the given viewer is <code>null</code>
	 */
	public static ColorPicker getPicker(EditPartViewer i_viewer) {

		if (i_viewer == null)
			throw new NullPointerException("i_viewer must not be null");

		PickingUpdateManager3D updateManager = getPickingUpdateManager(i_viewer);
		if (updateManager == null)
			return null;

		return updateManager.getPicker();
	}

	/**
	 * Returns the picking update manager for the given edit part viewer.
	 * 
	 * @param i_viewer the edit part viewer
	 * @return the picking update manager or <code>null</code> if the given edit
	 *         part viewer doesn't have one
	 * @throws NullPointerException if the given viewer is <code>null</code>
	 */
	public static PickingUpdateManager3D getPickingUpdateManager(
			EditPartViewer i_viewer) {

		if (i_viewer == null)
			throw new NullPointerException("i_viewer must not be null");

		RootEditPart root = i_viewer.getRootEditPart();

		if (!(root instanceof GraphicalEditPart))
			return null;

		GraphicalEditPart graphicalRoot = (GraphicalEditPart) root;
		IFigure figure = graphicalRoot.getFigure();

		UpdateManager updateManager = figure.getUpdateManager();
		if (!(updateManager instanceof PickingUpdateManager3D))
			return null;

		return (PickingUpdateManager3D) updateManager;
	}

	/**
	 * Returns a new track state for the given start location and viewer. The
	 * color picker is reconfigured to ignore certain figures, so make sure to
	 * clear the ignored figures after tracking is completed.
	 * 
	 * @param i_location the 2D start location in screen coordinates
	 * @param i_viewer the editpart viewer
	 * @return the track state
	 */
	public static TrackState getTrackState(Point i_location,
			EditPartViewer i_viewer) {

		ColorPicker picker = Tracker3DHelper.getPicker(i_viewer);

		picker.ignoreType(FeedbackFigure3D.class);
		picker.ignoreType(Handle.class);

		List<?> selectedEditParts = i_viewer.getSelectedEditParts();

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

		TrackState trackState = new TrackState(picker, i_location);
		return trackState;
	}
}
