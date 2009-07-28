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
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.XYZAnchor;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.Picker;
import org.eclipse.gef.editpolicies.FeedbackHelper;

/**
 * FeedbackHelper3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 31, 2009
 */
public class FeedbackHelper3D extends FeedbackHelper {

    /**
     * The color picker, which can be <code>null</code>.
     */
    protected Picker m_colorPicker;

    /**
     * The default figure.
     */
    protected IFigure3D m_defaultFigure;

    /**
     * A dummy anchor.
     */
    protected XYZAnchor m_dummyAnchor;

    /**
     * Creates a new feedback helper. The given default figure is used to
     * convert world coordinates to surface coordinates and vice versa.
     * 
     * @param i_defaultFigure
     *            the default figure
     */
    public FeedbackHelper3D(IFigure3D i_defaultFigure) {

        m_defaultFigure = i_defaultFigure;
        m_dummyAnchor = createDummyAnchor();

        UpdateManager updateManager = m_defaultFigure.getUpdateManager();
        if (updateManager instanceof PickingUpdateManager3D)
            m_colorPicker = ((PickingUpdateManager3D) updateManager).getMainPicker();
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
            ISurface surface = null;
            if (m_colorPicker != null)
                surface = m_colorPicker.getCurrentSurface();

            if (surface == null)
                surface = m_defaultFigure.getSurface();

            Vector3f w = Math3D.getVector3f();
            try {
                surface.getWorldLocation(p, w);
                m_dummyAnchor.setLocation3D(w);
                setAnchor(m_dummyAnchor);
            } finally {
                Math3D.returnVector3f(w);
            }
        }
    }

}
