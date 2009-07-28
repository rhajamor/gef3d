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
package org.eclipse.gef3d.ui.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTEventDispatcher3D;
import org.eclipse.draw3d.IHostFigure3D;
import org.eclipse.draw3d.IScene;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.DomainEventDispatcher;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;

/**
 * DomainEventDispatcher3D There should really be more documentation here.
 * 
 * @author hudsonr
 * @author Kristian Duske
 * @version $Revision$
 * @since 27.07.2009
 */
public class DomainEventDispatcher3D extends SWTEventDispatcher3D {

    protected EditDomain m_domain;

    protected EditPartViewer m_viewer;

    protected boolean m_editorCaptured = false;

    /**
     * Creates a new event dispatcher for the given scene, edit domain and
     * viewer.
     * 
     * @param i_scene
     *            the scene
     * @param i_domain
     *            the edit domain
     * @param i_viewer
     *            the viewer
     * 
     * @throws NullPointerException
     *             if any of the given arguments is <code>null</code>
     */
    public DomainEventDispatcher3D(IScene i_scene, EditDomain i_domain,
            EditPartViewer i_viewer) {

        super(i_scene);

        if (i_domain == null)
            throw new NullPointerException("i_domain must not be null");

        if (i_viewer == null)
            throw new NullPointerException("i_viewer must not be null");

        m_domain = i_domain;
        m_viewer = i_viewer;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from {@link DomainEventDispatcher}.
     * </p>
     * 
     * @see org.eclipse.draw2d.SWTEventDispatcher3D#dispatchFocusGained(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void dispatchFocusGained(FocusEvent i_e) {

        super.dispatchFocusGained(i_e);
        m_domain.focusGained(i_e, m_viewer);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from {@link DomainEventDispatcher}.
     * </p>
     * 
     * @see org.eclipse.draw2d.SWTEventDispatcher3D#dispatchFocusLost(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void dispatchFocusLost(FocusEvent i_e) {

        super.dispatchFocusLost(i_e);
        m_domain.focusLost(i_e, m_viewer);

        setRouteEventsToEditor(false);
    }

    /**
     * <p>
     * Copied from {@link DomainEventDispatcher}.
     * </p>
     * 
     * @return <code>true</code> if the drawing subsystem is busy
     * 
     * @see DomainEventDispatcher
     */
    protected boolean isBusy() {

        if (getCurrentEvent() != null && getCurrentEvent().isConsumed())
            return true;

        return isCaptured();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from {@link DomainEventDispatcher}.
     * </p>
     * 
     * @see org.eclipse.draw2d.SWTEventDispatcher3D#dispatchKeyPressed(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void dispatchKeyPressed(KeyEvent i_e) {

        if (!m_editorCaptured) {
            super.dispatchKeyPressed(i_e);
            if (isBusy())
                return;
        }

        m_domain.keyDown(i_e, m_viewer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.SWTEventDispatcher3D#dispatchKeyReleased(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void dispatchKeyReleased(KeyEvent i_e) {

        if (!m_editorCaptured) {
            super.dispatchKeyReleased(i_e);
            if (isBusy())
                return;
        }

        m_domain.keyUp(i_e, m_viewer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.SWTEventDispatcher3D#dispatchKeyTraversed(org.eclipse.swt.events.TraverseEvent)
     */
    @Override
    public void dispatchKeyTraversed(TraverseEvent i_e) {

        if (!m_editorCaptured) {
            super.dispatchKeyTraversed(i_e);
            if (!i_e.doit)
                return;
        }

        m_domain.keyTraversed(i_e, m_viewer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.SWTEventDispatcher3D#dispatchMouseDoubleClicked(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseDoubleClicked(MouseEvent i_me) {

        if (!m_editorCaptured) {
            super.dispatchMouseDoubleClicked(i_me);
            if (isBusy())
                return;
        }

        EditPartViewer viewer = m_viewer;
        IFigure owner = getPicker().getCurrentSurface().getOwner();
        if (owner instanceof IHostFigure3D) {
            IHostFigure3D host = (IHostFigure3D) owner;
        }
        
        domain.mouseDoubleClick(me, viewer);
    }

    /**
     * <p>
     * Copied from {@link DomainEventDispatcher}.
     * </p>
     * 
     * @param i_routeEvents
     *            specifies whether events should be routed to the editor
     * 
     * @see DomainEventDispatcher#setRouteEventsToEditor(boolean)
     */
    private void setRouteEventsToEditor(boolean i_routeEvents) {

        m_editorCaptured = i_routeEvents;
    }
}
