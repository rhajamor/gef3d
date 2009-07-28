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
package org.eclipse.draw3d;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.EventDispatcher;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTEventDispatcher;

/**
 * An implementation of {@link IHostFigure3D} that extends {@link Figure3D}. Any
 * subclass of this inherits the ability to host 2D children.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 25.07.2009
 */
public class HostFigure3D extends Figure3D implements IHostFigure3D {

    /**
     * The texture needs to be invalidated every time a child is moved so that
     * the changes are drawn on the screen.
     */
    private FigureListener childMovedListener = new FigureListener() {

        public void figureMoved(IFigure i_source) {

            m_dirty = true;
        }
    };

    /**
     * The connection layer for the 2D children.
     */
    protected ConnectionLayer m_connectionLayer;

    /**
     * Indicates whether the 2D children of this figure need to be repainted.
     */
    protected boolean m_dirty = true;

    /**
     * The event dispatcher for the 2D children.
     */
    protected EventDispatcher m_eventDispatcher;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.Figure3D#invalidateTree()
     */
    @Override
    public void invalidateTree() {

        m_dirty = true;
        super.invalidateTree();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.Figure3D#paintChildren(org.eclipse.draw2d.Graphics)
     */
    @Override
    protected void paintChildren(Graphics i_graphics) {

        super.paintChildren(i_graphics);

        RenderContext renderContext = getRenderContext();
        if (renderContext.getMode().isPaint())
            m_dirty = false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.Figure#remove(org.eclipse.draw2d.IFigure)
     */
    @Override
    public void remove(IFigure i_figure) {

        super.remove(i_figure);

        if (!(i_figure instanceof IFigure3D))
            i_figure.removeFigureListener(childMovedListener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.Figure#validate()
     */
    @Override
    public void validate() {

        super.validate();
        m_dirty = true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.Figure#add(org.eclipse.draw2d.IFigure,
     *      java.lang.Object, int)
     */
    @Override
    public void add(IFigure i_figure, Object i_constraint, int i_index) {

        super.add(i_figure, i_constraint, i_index);

        // register as figure listener with 2D children so that we know when
        // they move
        if (!(i_figure instanceof IFigure3D))
            i_figure.addFigureListener(childMovedListener);
    }

    /**
     * Creates the event dispatcher for the 2D children. The default
     * implementation creates an instance of {@link SWTEventDispatcher}.
     * 
     * @return the event dispatcher
     */
    protected EventDispatcher createEventDispatcher() {

        return new SWTEventDispatcher();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.IHostFigure3D#getConnectionLayer(org.eclipse.draw3d.ConnectionLayerFactory)
     */
    public ConnectionLayer getConnectionLayer(ConnectionLayerFactory i_clfactory) {

        if (m_connectionLayer == null && i_clfactory != null) {
            m_connectionLayer = i_clfactory.createConnectionLayer(this);
            // add(connectionLayer); // or else it doesn't have an update
            // manager
        }
        return m_connectionLayer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.IHostFigure3D#getEventDispatcher()
     */
    public EventDispatcher getEventDispatcher() {

        if (m_eventDispatcher == null)
            m_eventDispatcher = createEventDispatcher();

        return m_eventDispatcher;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.IHostFigure3D#isDirty()
     */
    public boolean isDirty() {

        return m_dirty;
    }
}
