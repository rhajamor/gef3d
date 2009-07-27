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

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.XYZAnchor;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.draw3d.util.CoordinateConverter;
import org.eclipse.gef.editpolicies.FeedbackHelper;

/**
 * FeedbackHelper3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 31, 2009
 */
public class FeedbackHelper3D extends FeedbackHelper {

	protected XYZAnchor m_dummyAnchor;

	protected ColorPicker m_colorPicker;

	protected IFigure3D m_defaultFigure;

	/**
	 * @param colorPicker
	 */
	public FeedbackHelper3D(IFigure3D defaultFigure) {
		m_dummyAnchor = createDummyAnchor();
		if (defaultFigure.getUpdateManager() instanceof PickingUpdateManager3D) {
			m_colorPicker =
				((PickingUpdateManager3D) defaultFigure.getUpdateManager())
					.getPicker();
		} else {
			m_colorPicker = null;
		}
		m_defaultFigure = defaultFigure;
	}

	/**
	 * @return
	 */
	protected XYZAnchor createDummyAnchor() {
		return new XYZAnchor(new Vector3fImpl(10, 10, 10));
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
			IFigure3D last3DFigure =
				(m_colorPicker != null) ? m_colorPicker.getLastValidFigure()
					: null;
			if (last3DFigure == null) {
				last3DFigure = m_defaultFigure;
			}
			Vector3fImpl v = new Vector3fImpl();
			Point surface = new Point();

			CoordinateConverter
				.screenToSurface(p.x, p.y, last3DFigure, surface);
			CoordinateConverter.surfaceToWorld(surface.x, surface.y,
				last3DFigure, v);
			m_dummyAnchor.setLocation3D(v);
			setAnchor(m_dummyAnchor);
		}
	}

}
