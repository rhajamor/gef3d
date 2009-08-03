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
package org.eclipse.gef3d.editpolicies;

import java.util.logging.Logger;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.XYZAnchor;
import org.eclipse.draw3d.geometry.BoundingBox;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.Picker;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.gef.editpolicies.FeedbackHelper;

/**
 * FeedbackHelper3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 31, 2009
 */
public class FeedbackHelper3D extends FeedbackHelper {

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(FeedbackHelper3D.class.getName());

	/**
	 * A dummy anchor.
	 */
	protected XYZAnchor m_dummyAnchor;

	/**
	 * The 3D host figure.
	 */
	protected IFigure m_hostFigure;

	/**
	 * The picker, which can be <code>null</code>.
	 */
	protected Picker m_picker;

	/**
	 * Creates a new feedback helper.
	 */
	public FeedbackHelper3D() {

		m_dummyAnchor = createDummyAnchor();
	}

	/**
	 * Creates a dummy anchor.
	 * 
	 * @return a dummy anchor
	 */
	protected XYZAnchor createDummyAnchor() {

		return new XYZAnchor(new Vector3fImpl(10, 10, 10));
	}

	/**
	 * Sets the bounds of the given feedback figure to the given location and
	 * size. The given coordinates and dimension are in relation to the current
	 * surface.
	 * 
	 * @param i_feedback the figure to update
	 * @param i_sLocation the location to set
	 * @param i_surfaceSize the size to set
	 */
	public void setBounds(IFigure3D i_feedback, Point i_sLocation,
		Dimension i_surfaceSize) {

		BoundingBox bounds = Draw3DCache.getBoundingBox();
		Vector3f wLocation = Draw3DCache.getVector3f();
		Vector3f worldSize = Draw3DCache.getVector3f();
		try {
			update(bounds, i_sLocation, i_surfaceSize, null, null);

			bounds.expand(0.01f);
			bounds.getPosition(wLocation);
			bounds.getSize(worldSize);

			i_feedback.getPosition3D().setLocation3D(wLocation);
			i_feedback.getPosition3D().setSize3D(worldSize);
		} finally {
			Draw3DCache.returnBoundingBox(bounds);
			Draw3DCache.returnVector3f(wLocation);
			Draw3DCache.returnVector3f(worldSize);
		}
	}

	/**
	 * Sets the host figure of this feedback helper.
	 * 
	 * @param i_hostFigure the host figure
	 */
	public void setHostFigure(IFigure i_hostFigure) {

		m_hostFigure = i_hostFigure;

		UpdateManager updateManager = m_hostFigure.getUpdateManager();
		if (updateManager instanceof PickingUpdateManager3D)
			m_picker = ((PickingUpdateManager3D) updateManager).getPicker();
	}

	private void update(BoundingBox i_bounds, Point i_surfaceLocation,
		Dimension i_surfaceSize, Point i_surfaceMoveDelta,
		Dimension i_surfaceSizeDelta) {

		Point sLocation = Draw3DCache.getPoint();
		Dimension surfaceSize = Draw3DCache.getDimension();
		Vector3f wLocation = Draw3DCache.getVector3f();
		Vector3f worldSize = Draw3DCache.getVector3f();
		try {
			sLocation.setLocation(i_surfaceLocation);
			surfaceSize.setSize(i_surfaceSize);

			if (i_surfaceMoveDelta != null)
				sLocation.translate(i_surfaceMoveDelta);

			if (i_surfaceSizeDelta != null)
				surfaceSize.expand(i_surfaceSizeDelta);

			ISurface surface = m_picker.getCurrentSurface();

			surface.getWorldLocation(sLocation, wLocation);
			surface.getWorldDimension(surfaceSize, worldSize);
			worldSize.setZ(1);

			i_bounds.setLocation(wLocation);
			i_bounds.setSize(worldSize);

			// i_bounds.translate(0, 0, -1);
		} finally {
			Draw3DCache.returnPoint(sLocation);
			Draw3DCache.returnDimension(surfaceSize);
			Draw3DCache.returnVector3f(wLocation);
			Draw3DCache.returnVector3f(worldSize);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method is a duplicate of the original one, using the newly defined
	 * anchor here.
	 * 
	 * @see org.eclipse.gef.editpolicies.FeedbackHelper#update(org.eclipse.draw2d.ConnectionAnchor,
	 *      org.eclipse.draw2d.geometry.Point)
	 */
	@Override
	public void update(ConnectionAnchor anchor, Point p) {

		if (anchor != null)
			setAnchor(anchor);
		else {
			ISurface surface = m_picker.getCurrentSurface();

			Vector3f w = Draw3DCache.getVector3f();
			try {
				surface.getWorldLocation(p, w);
				m_dummyAnchor.setLocation3D(w);
				setAnchor(m_dummyAnchor);
			} finally {
				Draw3DCache.returnVector3f(w);
			}
		}
	}

	/**
	 * Moves the given feedback figure by the given move delta and resizes it by
	 * the given size delta. The given deltas are relative to the original
	 * bounds of the host figure and are in relation to the current surface.
	 * 
	 * @param i_feedback the feedback figure
	 * @param i_surfaceMoveDelta the move delta
	 * @param i_surfaceSizeDelta the size delta
	 */
	public void update(IFigure3D i_feedback, Point i_surfaceMoveDelta,
		Dimension i_surfaceSizeDelta) {

		if (i_feedback == null)
			throw new NullPointerException("i_feedback must not be null");

		BoundingBox bounds = Draw3DCache.getBoundingBox();
		Vector3f wLocation = Draw3DCache.getVector3f();
		Vector3f worldSize = Draw3DCache.getVector3f();
		try {
			if (m_hostFigure instanceof IFigure3D) {
				IFigure3D figure3D = (IFigure3D) m_hostFigure;
				bounds.set(figure3D.getBounds3D());

				ISurface surface = m_picker.getCurrentSurface();

				if (i_surfaceMoveDelta != null) {
					surface.getWorldLocation(i_surfaceMoveDelta, wLocation);
					bounds.translate(wLocation);
				}

				if (i_surfaceSizeDelta != null) {
					surface.getWorldDimension(i_surfaceSizeDelta, worldSize);
					bounds.resize(worldSize);
				}
			} else {
				Point sLocation = m_hostFigure.getBounds().getLocation();
				Dimension surfaceSize = m_hostFigure.getBounds().getSize();

				update(bounds, sLocation, surfaceSize, i_surfaceMoveDelta,
					i_surfaceSizeDelta);
			}

			bounds.expand(0.01f);
			bounds.getPosition(wLocation);
			bounds.getSize(worldSize);

			i_feedback.getPosition3D().setLocation3D(wLocation);
			i_feedback.getPosition3D().setSize3D(worldSize);
		} finally {
			Draw3DCache.returnBoundingBox(bounds);
			Draw3DCache.returnVector3f(wLocation);
			Draw3DCache.returnVector3f(worldSize);
		}
	}

}
