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
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.draw3d.util.Cache;
import org.eclipse.gef.editpolicies.FeedbackHelper;

/**
 * FeedbackHelper3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 31, 2009
 */
public class FeedbackHelper3D extends FeedbackHelper {

    private static void update(BoundingBox i_bounds, ISurface i_surface,
            Point i_surfaceLocation, Dimension i_surfaceSize,
            Point i_surfaceMoveDelta, Dimension i_surfaceSizeDelta) {

        Point surfaceLocation = Cache.getPoint();
        Dimension surfaceSize = Cache.getDimension();
        Vector3f worldLocation = Cache.getVector3f();
        Vector3f worldSize = Cache.getVector3f();
        try {
            surfaceLocation.setLocation(i_surfaceLocation);
            surfaceSize.setSize(i_surfaceSize);

            if (i_surfaceMoveDelta != null)
                surfaceLocation.translate(i_surfaceMoveDelta);

            if (i_surfaceSizeDelta != null)
                surfaceSize.expand(i_surfaceSizeDelta);

            i_surface.getWorldLocation(surfaceLocation, worldLocation);
            i_surface.getWorldDimension(surfaceSize, worldSize);
            worldSize.setZ(1);

            i_bounds.setLocation(worldLocation);
            i_bounds.setSize(worldSize);

            i_bounds.translate(0, 0, -1);
        } finally {
            Cache.returnPoint(surfaceLocation);
            Cache.returnDimension(surfaceSize);
            Cache.returnVector3f(worldLocation);
            Cache.returnVector3f(worldSize);
        }
    }

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(FeedbackHelper3D.class.getName());

    public static void update(IFigure3D i_feedback, ISurface i_surface,
            Point i_surfaceLocation, Dimension i_surfaceSize) {

        BoundingBox bounds = Cache.getBoundingBox();
        Vector3f worldLocation = Cache.getVector3f();
        Vector3f worldSize = Cache.getVector3f();
        try {
            update(bounds, i_surface, i_surfaceLocation, i_surfaceSize, null,
                null);

            bounds.expand(0.01f);
            bounds.getPosition(worldLocation);
            bounds.getSize(worldSize);

            i_feedback.getPosition3D().setLocation3D(worldLocation);
            i_feedback.getPosition3D().setSize3D(worldSize);
        } finally {
            Cache.returnBoundingBox(bounds);
            Cache.returnVector3f(worldLocation);
            Cache.returnVector3f(worldSize);
        }
    }

    public static void update(IFigure3D i_feedback, ISurface i_surface,
            IFigure i_figure, Point i_surfaceMoveDelta,
            Dimension i_surfaceSizeDelta) {

        if (i_feedback == null)
            throw new NullPointerException("i_feedback must not be null");

        if (i_surface == null)
            throw new NullPointerException("i_surface must not be null");

        if (i_figure == null)
            throw new NullPointerException("i_figure must not be null");

        BoundingBox bounds = Cache.getBoundingBox();
        Vector3f worldLocation = Cache.getVector3f();
        Vector3f worldSize = Cache.getVector3f();
        try {
            if (i_figure instanceof IFigure3D) {
                IFigure3D figure3D = (IFigure3D) i_figure;
                bounds.set(figure3D.getBounds3D());

                if (i_surfaceMoveDelta != null) {
                    i_surface.getWorldLocation(i_surfaceMoveDelta,
                        worldLocation);
                    bounds.translate(worldLocation);
                }

                if (i_surfaceSizeDelta != null) {
                    i_surface.getWorldDimension(i_surfaceSizeDelta, worldSize);
                    bounds.resize(worldSize);
                }
            } else {
                Point surfaceLocation = i_figure.getBounds().getLocation();
                Dimension surfaceSize = i_figure.getBounds().getSize();

                update(bounds, i_surface, surfaceLocation, surfaceSize,
                    i_surfaceMoveDelta, i_surfaceSizeDelta);
            }

            bounds.expand(0.01f);
            bounds.getPosition(worldLocation);
            bounds.getSize(worldSize);

            i_feedback.getPosition3D().setLocation3D(worldLocation);
            i_feedback.getPosition3D().setSize3D(worldSize);
        } finally {
            Cache.returnBoundingBox(bounds);
            Cache.returnVector3f(worldLocation);
            Cache.returnVector3f(worldSize);
        }
    }

    /**
     * The color picker, which can be <code>null</code>.
     */
    protected ColorPicker m_colorPicker;

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
            m_colorPicker = ((PickingUpdateManager3D) updateManager).getPicker();
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

            Vector3f w = Cache.getVector3f();
            try {
                surface.getWorldLocation(p, w);
                m_dummyAnchor.setLocation3D(w);
                setAnchor(m_dummyAnchor);
            } finally {
                Cache.returnVector3f(w);
            }
        }
    }

}
