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
package org.eclipse.draw2d;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Picker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;

/**
 * A 3D version of {@link SWTEventDispatcher}. This dispatcher works just like
 * SWTEventDispatcher with one difference: Mouse coordinates are converted to
 * surface coordinates before being dispatched to figures. This class was
 * largely copied from the original implementation and adapted here.
 * 
 * @author hudsonr
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.07.2009
 */
public class SWTEventDispatcher3D extends EventDispatcher {

    private static final org.eclipse.swt.graphics.Point TMP_P = new org.eclipse.swt.graphics.Point(
        0, 0);

    /**
     * Indicates whether input is currently captured and routed to
     * {@link #m_mouseTarget}.
     */
    protected boolean m_captured;

    /**
     * The event source.
     */
    protected Control m_control;

    /**
     * The current Draw2D mouse event.
     */
    protected MouseEvent m_currentEvent;

    /**
     * The current cursor.
     */
    protected Cursor m_cursor;

    /**
     * The figure currently under the mouse cursor.
     */
    protected IFigure m_cursorTarget;

    /**
     * Indicates whether figure traversal is allowed.
     */
    protected boolean m_figureTraverse = true;

    /**
     * The focus traverse manager.
     */
    protected FocusTraverseManager m_focusManager;

    /**
     * The currently focused figure.
     */
    protected IFigure m_focusOwner;

    /**
     * The current hover source.
     */
    protected Figure m_hoverSource;

    /**
     * The current mouse target.
     */
    protected IFigure m_mouseTarget;

    /**
     * The root figure.
     */
    protected IFigure m_rootFigure;

    /**
     * The scene.
     */
    protected IScene m_scene;

    /**
     * The tool tip helper.
     */
    protected ToolTipHelper m_toolTipHelper;

    /**
     * Creates a new event dispatcher for the given scene.
     * 
     * @param i_scene
     *            the scene
     * 
     * @throws NullPointerException
     *             if the given scene is <code>null</code>
     */
    public SWTEventDispatcher3D(IScene i_scene) {

        if (i_scene == null)
            throw new NullPointerException("i_scene must not be null");

        m_scene = i_scene;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchFocusGained(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void dispatchFocusGained(org.eclipse.swt.events.FocusEvent i_e) {

        IFigure currentFocusOwner = getFocusTraverseManager().getCurrentFocusOwner();

        /*
         * Upon focus gained, if there is no current focus owner, set focus on
         * first focusable child.
         */
        if (currentFocusOwner == null)
            currentFocusOwner = getFocusTraverseManager().getNextFocusableFigure(
                m_rootFigure, m_focusOwner);

        setFocus(currentFocusOwner);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchFocusLost(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void dispatchFocusLost(org.eclipse.swt.events.FocusEvent i_e) {

        setFocus(null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchKeyPressed(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void dispatchKeyPressed(org.eclipse.swt.events.KeyEvent i_e) {

        if (m_focusOwner != null) {
            KeyEvent event = new KeyEvent(this, m_focusOwner, i_e);
            m_focusOwner.handleKeyPressed(event);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchKeyReleased(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void dispatchKeyReleased(org.eclipse.swt.events.KeyEvent i_e) {

        if (m_focusOwner != null) {
            KeyEvent event = new KeyEvent(this, m_focusOwner, i_e);
            m_focusOwner.handleKeyReleased(event);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchKeyTraversed(org.eclipse.swt.events.TraverseEvent)
     */
    @Override
    public void dispatchKeyTraversed(TraverseEvent i_e) {

        if (!m_figureTraverse)
            return;

        IFigure nextFigure = null;

        if (i_e.detail == SWT.TRAVERSE_TAB_NEXT)
            nextFigure = getFocusTraverseManager().getNextFocusableFigure(
                m_rootFigure, m_focusOwner);
        else if (i_e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
            nextFigure = getFocusTraverseManager().getPreviousFocusableFigure(
                m_rootFigure, m_focusOwner);

        if (nextFigure != null) {
            i_e.doit = false;
            setFocus(nextFigure);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseDoubleClicked(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseDoubleClicked(
            org.eclipse.swt.events.MouseEvent i_me) {

        receive(i_me);
        if (m_mouseTarget != null)
            m_mouseTarget.handleMouseDoubleClicked(m_currentEvent);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseEntered(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseEntered(org.eclipse.swt.events.MouseEvent i_me) {

        receive(i_me);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseExited(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseExited(org.eclipse.swt.events.MouseEvent i_me) {

        Vector3f w = Math3D.getVector3f();
        try {
            setHoverSource(null, (Control) i_me.getSource(), i_me.x, i_me.y);

            if (m_mouseTarget != null) {
                getPicker().updateCurrentSurface(i_me.x, i_me.y);
                float depth = getPicker().getDepth(i_me.x, i_me.y);

                ICamera camera = m_scene.getCamera();
                camera.unProject(i_me.x, i_me.y, depth, null, w);

                ISurface surface = getPicker().getCurrentSurface();
                Point s = surface.getSurfaceLocation2D(w, null);

                m_currentEvent = new MouseEvent(s.x, s.y, this, m_mouseTarget,
                    i_me.button, i_me.stateMask);

                m_mouseTarget.handleMouseExited(m_currentEvent);

                releaseCapture();
                m_mouseTarget = null;
            }
        } finally {
            Math3D.returnVector3f(w);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseHover(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseHover(org.eclipse.swt.events.MouseEvent i_me) {

        receive(i_me);
        if (m_mouseTarget != null)
            m_mouseTarget.handleMouseHover(m_currentEvent);
        /*
         * Check Tooltip source. Get Tooltip source's Figure. Set that tooltip
         * as the lws contents on the helper.
         */
        if (m_hoverSource != null) {
            TMP_P.x = i_me.x;
            TMP_P.y = i_me.y;

            Control control = (Control) i_me.getSource();
            org.eclipse.swt.graphics.Point absolute = control.toDisplay(TMP_P);

            getToolTipHelper().displayToolTipNear(m_hoverSource,
                m_hoverSource.getToolTip(), absolute.x, absolute.y);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseMoved(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseMoved(org.eclipse.swt.events.MouseEvent i_me) {

        receive(i_me);
        if (m_mouseTarget != null) {
            if ((i_me.stateMask & SWT.BUTTON_MASK) != 0)
                m_mouseTarget.handleMouseDragged(m_currentEvent);
            else
                m_mouseTarget.handleMouseMoved(m_currentEvent);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMousePressed(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMousePressed(org.eclipse.swt.events.MouseEvent i_me) {

        receive(i_me);
        if (m_mouseTarget != null) {
            m_mouseTarget.handleMousePressed(m_currentEvent);
            if (m_currentEvent.isConsumed())
                setCapture(m_mouseTarget);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseReleased(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseReleased(org.eclipse.swt.events.MouseEvent i_me) {

        receive(i_me);
        if (m_mouseTarget != null)
            m_mouseTarget.handleMouseReleased(m_currentEvent);

        releaseCapture();
        receive(i_me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#getAccessibilityDispatcher()
     */
    @Override
    protected AccessibilityDispatcher getAccessibilityDispatcher() {

        return null;
    }

    /**
     * Returns the current mouse event.
     * 
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @return the current mouse event; can be <code>null</code>
     */
    protected MouseEvent getCurrentEvent() {

        return m_currentEvent;
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @return the current tool tip
     * 
     * @see SWTEventDispatcher
     */
    protected IFigure getCurrentToolTip() {

        if (m_hoverSource != null)
            return m_hoverSource.getToolTip();
        else
            return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#getFocusOwner()
     */
    @Override
    IFigure getFocusOwner() {

        return m_focusOwner;
    }

    /**
     * Returns the focus traverse manager, which may be lazily created.
     * 
     * @return the focus traverse manager
     */
    protected FocusTraverseManager getFocusTraverseManager() {

        if (m_focusManager == null)
            m_focusManager = new FocusTraverseManager();

        return m_focusManager;
    }

    /**
     * Returns the picker.
     * 
     * @return the picker
     */
    protected Picker getPicker() {

        UpdateManager updateManager = m_scene.getUpdateManager();
        if (!(updateManager instanceof PickingUpdateManager3D))
            throw new AssertionError("wrong update manager: "
                    + updateManager.getClass().getName());

        return ((PickingUpdateManager3D) updateManager).getPicker();
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @return the tool tip helper
     * 
     * @see SWTEventDispatcher#getToolTipHelper()
     */
    protected ToolTipHelper getToolTipHelper() {

        if (m_toolTipHelper == null)
            m_toolTipHelper = new ToolTipHelper(m_control);

        return m_toolTipHelper;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#isCaptured()
     */
    @Override
    public boolean isCaptured() {

        return m_captured;
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @param i_me
     *            the last mouse event
     * 
     * @see SWTEventDispatcher
     */
    protected void receive(org.eclipse.swt.events.MouseEvent i_me) {

        Vector3f w = Math3D.getVector3f();
        try {
            getPicker().updateCurrentSurface(i_me.x, i_me.y);
            float depth = getPicker().getDepth(i_me.x, i_me.y);

            ICamera camera = m_scene.getCamera();
            camera.unProject(i_me.x, i_me.y, depth, null, w);

            ISurface surface = getPicker().getCurrentSurface();
            Point s = surface.getSurfaceLocation2D(w, null);

            Control control = (Control) i_me.getSource();
            updateFigureUnderCursor(s.x, s.y, control, i_me.x, i_me.y);

            m_currentEvent = null;
            if (m_captured) {
                if (m_mouseTarget != null)
                    m_currentEvent = new MouseEvent(s.x, s.y, this,
                        m_mouseTarget, i_me.button, i_me.stateMask);
            } else {
                IFigure figure = m_rootFigure.findMouseEventTargetAt(i_me.x,
                    i_me.y);

                if (figure == m_mouseTarget) {
                    if (m_mouseTarget != null)
                        m_currentEvent = new MouseEvent(s.x, s.y, this,
                            m_mouseTarget, i_me.button, i_me.stateMask);
                    return;
                }

                if (m_mouseTarget != null) {
                    m_currentEvent = new MouseEvent(s.x, s.y, this,
                        m_mouseTarget, i_me.button, i_me.stateMask);
                    m_mouseTarget.handleMouseExited(m_currentEvent);
                }

                setMouseTarget(figure);
                if (m_mouseTarget != null) {
                    m_currentEvent = new MouseEvent(s.x, s.y, this,
                        m_mouseTarget, i_me.button, i_me.stateMask);
                    m_mouseTarget.handleMouseEntered(m_currentEvent);
                }
            }
        } finally {
            Math3D.returnVector3f(w);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#releaseCapture()
     */
    @Override
    protected void releaseCapture() {

        m_captured = false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#requestFocus(org.eclipse.draw2d.IFigure)
     */
    @Override
    public void requestFocus(IFigure i_figure) {

        setFocus(i_figure);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#requestRemoveFocus(org.eclipse.draw2d.IFigure)
     */
    @Override
    public void requestRemoveFocus(IFigure i_figure) {

        if (getFocusOwner() == i_figure)
            setFocus(null);

        if (m_mouseTarget == i_figure)
            m_mouseTarget = null;

        if (m_cursorTarget == i_figure)
            m_cursorTarget = null;

        if (m_hoverSource == i_figure)
            m_hoverSource = null;

        getFocusTraverseManager().setCurrentFocusOwner(null);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#setCapture(org.eclipse.draw2d.IFigure)
     */
    @Override
    protected void setCapture(IFigure i_figure) {

        m_captured = true;
        m_mouseTarget = i_figure;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @see org.eclipse.draw2d.EventDispatcher#setControl(org.eclipse.swt.widgets.Control)
     */
    @Override
    public void setControl(Control i_control) {

        if (i_control == m_control)
            return;

        if (m_control != null && !m_control.isDisposed())
            throw new RuntimeException(
                "Can not set control again once it has been set");

        if (i_control != null)
            i_control.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {

                public void widgetDisposed(DisposeEvent e) {

                    if (m_toolTipHelper != null)
                        m_toolTipHelper.dispose();
                }
            });

        m_control = i_control;
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @param i_cursor
     *            the new cursor
     * 
     * @see SWTEventDispatcher#setCursor(Cursor)
     */
    protected void setCursor(Cursor i_cursor) {

        if ((i_cursor == null && m_cursor == null)
                || (i_cursor != null && i_cursor.equals(m_cursor)))
            return;

        m_cursor = i_cursor;
        if (m_control != null && !m_control.isDisposed())
            m_control.setCursor(m_cursor);
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @param i_figure
     *            the figure to set
     * 
     * @see SWTEventDispatcher#setFigureUnderCursor(IFigure)
     */
    protected void setFigureUnderCursor(IFigure i_figure) {

        if (m_cursorTarget == i_figure)
            return;

        m_cursorTarget = i_figure;
        updateCursor();
    }

    /**
     * Copied from SWTEventDispatcher.
     * 
     * @param i_newOwner
     *            the focus figure
     * 
     * @see SWTEventDispatcher#setFocus(IFigure)
     * @author hudsonr
     * @author Kristian Duske
     */
    private void setFocus(IFigure i_newOwner) {

        IFigure oldOwner = m_focusOwner;
        if (i_newOwner == oldOwner)
            return;

        FocusEvent fe = new FocusEvent(oldOwner, i_newOwner);
        m_focusOwner = i_newOwner;

        if (oldOwner != null)
            oldOwner.handleFocusLost(fe);

        if (i_newOwner != null)
            getFocusTraverseManager().setCurrentFocusOwner(i_newOwner);

        if (m_focusOwner != null)
            m_focusOwner.handleFocusGained(fe);
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @param i_hoverSource
     *            the new hover source
     * @param i_control
     *            the event source
     * @param i_mx
     *            the mouse X coordinate
     * @param i_my
     *            the mouse Y coordinate
     * 
     * @see SWTEventDispatcher#setHoverSource(Figure, MouseEvent)
     */
    protected void setHoverSource(Figure i_hoverSource, Control i_control,
            int i_mx, int i_my) {

        m_hoverSource = i_hoverSource;

        if (i_hoverSource != null) {
            TMP_P.x = i_mx;
            TMP_P.y = i_my;

            org.eclipse.swt.graphics.Point absolute = i_control.toDisplay(TMP_P);

            getToolTipHelper().updateToolTip(m_hoverSource,
                getCurrentToolTip(), absolute.x, absolute.y);
        } else if (m_toolTipHelper != null) {
            // Update with null to clear hoverSource in ToolTipHelper
            m_toolTipHelper.updateToolTip(m_hoverSource, getCurrentToolTip(),
                i_mx, i_my);
        }
    }

    /**
     * Sets the current mouse target.
     * 
     * @param i_mouseTarget
     *            the current mouse target
     */
    protected void setMouseTarget(IFigure i_mouseTarget) {

        m_mouseTarget = i_mouseTarget;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#setRoot(org.eclipse.draw2d.IFigure)
     */
    @Override
    public void setRoot(IFigure i_rootFigure) {

        m_rootFigure = i_rootFigure;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#updateCursor()
     */
    @Override
    protected void updateCursor() {

        Cursor newCursor = null;
        if (m_cursorTarget != null)
            newCursor = m_cursorTarget.getCursor();
        setCursor(newCursor);
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @param i_sx
     *            the surface X coordinate
     * @param i_sy
     *            the surface Y coordinate
     * @param i_control
     *            the event source
     * @param i_mx
     *            the mouse X coordinate
     * @param i_my
     *            the mouse Y coordinate
     * 
     * @see SWTEventDispatcher#updateFigureUnderCursor(MouseEvent)
     */
    protected void updateFigureUnderCursor(int i_sx, int i_sy,
            Control i_control, int i_mx, int i_my) {

        if (!m_captured) {
            IFigure figure = getPicker().getCurrentSurface().getOwner();
            figure = figure.findFigureAt(i_sx, i_sy);

            setFigureUnderCursor(figure);

            if (m_cursorTarget != m_hoverSource)
                updateHoverSource(i_control, i_mx, i_my);
        }
    }

    /**
     * <p>
     * Copied from SWTEventDispatcher.
     * </p>
     * 
     * @param i_control
     *            the event source
     * @param i_mx
     *            the mouse X coordinate
     * @param i_my
     *            the mouse Y coordinate
     * 
     * @param me
     */
    protected void updateHoverSource(Control i_control, int i_mx, int i_my) {

        /*
         * Derive source from figure under cursor. Set the source in
         * setHoverSource(); If figure.getToolTip() is null, get parent's
         * toolTip Continue parent traversal until a toolTip is found or root is
         * reached.
         */
        if (m_cursorTarget != null) {
            boolean sourceFound = false;
            Figure source = (Figure) m_cursorTarget;
            while (!sourceFound && source.getParent() != null) {
                if (source.getToolTip() != null)
                    sourceFound = true;
                else
                    source = (Figure) source.getParent();
            }
            setHoverSource(source, i_control, i_mx, i_my);
        } else {
            setHoverSource(null, i_control, i_mx, i_my);
        }
    }

}
