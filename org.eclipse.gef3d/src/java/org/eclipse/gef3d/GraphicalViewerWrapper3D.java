/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d;

import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.gef3d.ui.parts.GraphicalViewer3D;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * GraphicalViewerWrapper3D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.07.2009
 */
public class GraphicalViewerWrapper3D extends GraphicalViewerWrapper implements
        GraphicalViewer3D {

    /**
     * Creates a new wrapper that delegates to the given graphical viewer.
     * 
     * @param i_viewer
     *            the graphical viewer to delegate to
     * @param i_scene
     *            the scene
     * 
     * @throws NullPointerException
     *             if any of the given arguments is <code>null</code>
     */
    public GraphicalViewerWrapper3D(GraphicalViewer3D i_viewer, IScene i_scene) {

        super(i_viewer, i_scene);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef3d.ui.parts.GraphicalViewer3D#createControl3D(org.eclipse.swt.widgets.Composite)
     */
    public Control createControl3D(Composite i_parent) {

        return ((GraphicalViewer3D) m_viewer).createControl3D(i_parent);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef3d.ui.parts.GraphicalViewer3D#getLightweightSystem3D()
     */
    public LightweightSystem3D getLightweightSystem3D() {

        return ((GraphicalViewer3D) m_viewer).getLightweightSystem3D();
    }
}
