/*******************************************************************************
 * Copyright (c) 2008 Kristian Duske and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.ecore.diagram.part;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ExclusionSearch;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.Draw3DCanvas;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef3d.handles.FeedbackFigure3D;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 3D version of the GMF diagram graphical viewer.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 01.12.2008
 *        https://gorgo.fernuni-hagen.de/kduske/de.feu.gef3d.ecoretools/trunk
 *        /src/java
 *        /org/eclipse/gmf/runtime/diagram/ui/parts/DiagramGraphicalViewer3D
 *        .java $
 */
public class DiagramGraphicalViewer3D extends DiagramGraphicalViewer {

    /**
     * Creates a new GL canvas, sets it as this viewer's control and returns it.
     * 
     * @param i_composite
     *            the parent composite
     * @return the GL canvas
     */
    public Control createControl3D(Composite i_composite) {

        GLCanvas canvas = Draw3DCanvas.createCanvas(i_composite, SWT.NONE,
            getLightweightSystem3D());

        setControl(canvas);
        return getControl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer#createLightweightSystem()
     */
    @Override
    protected LightweightSystem createLightweightSystem() {

        LightweightSystem3D lws3D = new LightweightSystem3D();

        UpdateManager updateManager = lws3D.getUpdateManager();
        if (updateManager instanceof PickingUpdateManager3D) {
            PickingUpdateManager3D pickingManager = (PickingUpdateManager3D) updateManager;
            ColorPicker picker = pickingManager.getPicker();

            picker.ignoreSurface(Handle.class);
            picker.ignoreSurface(FeedbackFigure3D.class);
        }

        return lws3D;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This method was copied and subsequently modified.
     * </p>
     * 
     * @author hudsonr (original implementation)
     * @author Kristian Duske
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalViewerImpl#findHandleAt(org.eclipse.draw2d.geometry.Point)
     */
    @Override
    public Handle findHandleAt(Point i_p) {

        LayerManager layermanager = (LayerManager) getEditPartRegistry().get(
            LayerManager.ID);

        if (layermanager == null)
            return null;

        Vector3f rayStart = Math3D.getVector3f();
        Vector3f rayDirection = Math3D.getVector3f();
        try {
            List<IFigure> ignore = new ArrayList<IFigure>(3);
            ignore.add(layermanager.getLayer(LayerConstants.PRIMARY_LAYER));
            ignore.add(layermanager.getLayer(LayerConstants.CONNECTION_LAYER));
            ignore.add(layermanager.getLayer(LayerConstants.FEEDBACK_LAYER));

            LightweightSystem3D lws = getLightweightSystem3D();
            PickingUpdateManager3D updateManager = (PickingUpdateManager3D) lws.getUpdateManager();
            ColorPicker picker = updateManager.getPicker();

            ISurface currentSurface = picker.getCurrentSurface();
            currentSurface.getWorldLocation(i_p, rayDirection);

            lws.getCamera().getPosition(rayStart);

            Math3D.sub(rayDirection, rayStart, rayDirection);
            Math3D.normalise(rayDirection, rayDirection);

            IFigure3D rootFigure = (IFigure3D) lws.getRootFigure();
            ISurface rootSurface = rootFigure.getSurface();

            Point s = rootSurface.getSurfaceLocation2D(rayStart, rayDirection,
                null);
            IFigure handle = rootSurface.findFigureAt(s.x, s.y,
                new ExclusionSearch(ignore));

            if (handle instanceof Handle)
                return (Handle) handle;

            return null;
        } finally {
            Math3D.returnVector3f(rayStart);
            Math3D.returnVector3f(rayDirection);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef.ui.parts.ScrollingGraphicalViewer#getFigureCanvas()
     */
    @Override
    protected FigureCanvas getFigureCanvas() {

        return null;
    }

    /**
     * Returns the 3D lightweight system if there is one.
     * 
     * @return the 3D lightweight system or <code>null</code> if the current
     *         lightweight system is not 3D
     */
    public LightweightSystem3D getLightweightSystem3D() {

        LightweightSystem lws = getLightweightSystem();
        if (!(lws instanceof LightweightSystem3D))
            return null;

        return (LightweightSystem3D) lws;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.IScene#getUpdateManager()
     */
    public UpdateManager getUpdateManager() {

        return getLightweightSystem().getUpdateManager();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef.ui.parts.ScrollingGraphicalViewer#reveal(org.eclipse.gef.EditPart)
     */
    @Override
    public void reveal(EditPart i_part) {

        // TODO: implement this properly
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef.ui.parts.ScrollingGraphicalViewer#setRootFigure(org.eclipse.draw2d.IFigure)
     */
    @Override
    protected void setRootFigure(IFigure i_figure) {

        super.setRootFigure(i_figure);
        getLightweightSystem().setContents(i_figure);
    }
}
