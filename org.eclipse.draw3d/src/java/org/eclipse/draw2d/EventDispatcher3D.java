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

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.MouseEvent3D;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * Dispatches events to a delegate event dispatcher. Events which contain the
 * current mouse position are replaced with instances of {@link MouseEvent3D},
 * which contain the surface coordinates of the surface that the mouse is
 * currently hovering over in their x and y properties as well as the original
 * screen coordinates and depth.
 * 
 * <p>
 * This class resides in the <code>org.eclipse.draw2d</code> package because its
 * super class {@link EventDispatcher} has abstract package private methods.
 * </p>
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.06.2009
 */
public class EventDispatcher3D extends EventDispatcher {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(EventDispatcher3D.class.getName());

    private EventDispatcher m_dispatcher;

    private Method m_getAccessibilityDispatcherMethod;

    private Method m_releaseCaptureMethod;

    private IScene m_scene;

    private Method m_setCaptureMethod;

    private Method m_updateCursorMethod;

    /**
     * Creates a new event dispatcher that delegates to the given event
     * dispatcher.
     * 
     * @param i_dispatcher
     *            the dispatcher to delegate to
     * @param i_scene
     *            the scene
     * 
     * @throws NullPointerException
     *             if the given dispatcher is <code>null</code>
     */
    public EventDispatcher3D(EventDispatcher i_dispatcher, IScene i_scene) {

        if (i_dispatcher == null)
            throw new NullPointerException("i_dispatcher must not be null");

        if (i_scene == null)
            throw new NullPointerException("i_scene must not be null");

        m_dispatcher = i_dispatcher;
        m_scene = i_scene;
    }

    private MouseEvent3D convert(MouseEvent i_e) {

        Vector3f s = Math3D.getVector3f();
        Vector3f d = Math3D.getVector3f();
        try {
            // update the picker
            ColorPicker picker = getColorPicker();

            picker.getFigure3D(i_e.x, i_e.y);
            float depth = picker.getDepth(i_e.x, i_e.y);

            ICamera camera = m_scene.getCamera();
            camera.getPosition(s);

            IVector3f worldLoc = camera.unProject(i_e.x, i_e.y, depth, null,
                null);

            Math3D.sub(worldLoc, s, d);
            Math3D.normalise(d, d);

            ISurface surface = picker.getCurrentSurface();
            Point surfaceLoc = surface.getSurfaceLocation2D(s, d, null);

            return new MouseEvent3D(i_e, surfaceLoc, worldLoc, depth);
        } finally {
            Math3D.returnVector3f(s);
            Math3D.returnVector3f(d);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchFocusGained(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void dispatchFocusGained(FocusEvent i_e) {

        m_dispatcher.dispatchFocusGained(i_e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchFocusLost(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void dispatchFocusLost(FocusEvent i_e) {

        m_dispatcher.dispatchFocusLost(i_e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchKeyPressed(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void dispatchKeyPressed(KeyEvent i_e) {

        m_dispatcher.dispatchKeyPressed(i_e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchKeyReleased(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public void dispatchKeyReleased(KeyEvent i_e) {

        m_dispatcher.dispatchKeyReleased(i_e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchKeyTraversed(org.eclipse.swt.events.TraverseEvent)
     */
    @Override
    public void dispatchKeyTraversed(TraverseEvent i_e) {

        m_dispatcher.dispatchKeyTraversed(i_e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseDoubleClicked(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseDoubleClicked(MouseEvent i_me) {

        MouseEvent3D me = convert(i_me);
        m_dispatcher.dispatchMouseDoubleClicked(me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseEntered(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseEntered(MouseEvent i_me) {

        MouseEvent3D me = convert(i_me);
        m_dispatcher.dispatchMouseEntered(me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseExited(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseExited(MouseEvent i_me) {

        MouseEvent3D me = convert(i_me);
        m_dispatcher.dispatchMouseExited(me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseHover(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseHover(MouseEvent i_me) {

        MouseEvent3D me = convert(i_me);
        m_dispatcher.dispatchMouseHover(me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseMoved(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseMoved(MouseEvent i_me) {

        MouseEvent3D me = convert(i_me);
        m_dispatcher.dispatchMouseMoved(me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMousePressed(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMousePressed(MouseEvent i_me) {

        MouseEvent3D me = convert(i_me);
        m_dispatcher.dispatchMousePressed(me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseReleased(org.eclipse.swt.events.MouseEvent)
     */
    @Override
    public void dispatchMouseReleased(MouseEvent i_me) {

        MouseEvent3D me = convert(i_me);
        m_dispatcher.dispatchMouseReleased(me);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#dispatchMouseWheelScrolled(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void dispatchMouseWheelScrolled(Event i_e) {

        m_dispatcher.dispatchMouseWheelScrolled(i_e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#getAccessibilityDispatcher()
     */
    @Override
    protected AccessibilityDispatcher getAccessibilityDispatcher() {

        // TODO see https://bugs.eclipse.org/bugs/show_bug.cgi?id=281008
        try {
            if (m_getAccessibilityDispatcherMethod == null) {
                m_getAccessibilityDispatcherMethod = m_dispatcher.getClass().getDeclaredMethod(
                    "getAccessibilityDispatcher");
                m_getAccessibilityDispatcherMethod.setAccessible(true);
            }

            return (AccessibilityDispatcher) m_getAccessibilityDispatcherMethod.invoke(m_dispatcher);
        } catch (Exception e) {
            // very unlikely to happen, so
            throw new RuntimeException("could not reflectively invoke method "
                    + m_dispatcher.getClass().getName()
                    + ".getAccessibilityDispatcher()", e);
        }
    }

    private ColorPicker getColorPicker() {

        UpdateManager updateManager = m_scene.getUpdateManager();
        if (!(updateManager instanceof PickingUpdateManager3D))
            throw new AssertionError("update manager must be an instance of "
                    + PickingUpdateManager3D.class.getName());

        return ((PickingUpdateManager3D) updateManager).getPicker();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#getFocusOwner()
     */
    @Override
    IFigure getFocusOwner() {

        return m_dispatcher.getFocusOwner();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#isCaptured()
     */
    @Override
    public boolean isCaptured() {

        return m_dispatcher.isCaptured();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#releaseCapture()
     */
    @Override
    protected void releaseCapture() {

        // TODO see https://bugs.eclipse.org/bugs/show_bug.cgi?id=281008
        try {
            if (m_releaseCaptureMethod == null) {
                m_releaseCaptureMethod = m_dispatcher.getClass().getDeclaredMethod(
                    "releaseCapture");
                m_releaseCaptureMethod.setAccessible(true);
            }

            m_releaseCaptureMethod.invoke(m_dispatcher);
        } catch (Exception e) {
            // very unlikely to happen, so
            throw new RuntimeException("could not reflectively invoke method "
                    + m_dispatcher.getClass().getName() + ".releaseCapture()",
                e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#requestFocus(org.eclipse.draw2d.IFigure)
     */
    @Override
    public void requestFocus(IFigure i_fig) {

        m_dispatcher.requestFocus(i_fig);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#requestRemoveFocus(org.eclipse.draw2d.IFigure)
     */
    @Override
    public void requestRemoveFocus(IFigure i_fig) {

        m_dispatcher.requestRemoveFocus(i_fig);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#setCapture(org.eclipse.draw2d.IFigure)
     */
    @Override
    protected void setCapture(IFigure i_figure) {

        // TODO see https://bugs.eclipse.org/bugs/show_bug.cgi?id=281008
        try {
            if (m_setCaptureMethod == null) {
                m_setCaptureMethod = m_dispatcher.getClass().getDeclaredMethod(
                    "setCapture", IFigure.class);
                m_setCaptureMethod.setAccessible(true);
            }

            m_setCaptureMethod.invoke(m_dispatcher, i_figure);
        } catch (Exception e) {
            // very unlikely to happen, so
            throw new RuntimeException("could not reflectively invoke method "
                    + m_dispatcher.getClass().getName() + ".setCapture()", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#setControl(org.eclipse.swt.widgets.Control)
     */
    @Override
    public void setControl(Control i_control) {

        m_dispatcher.setControl(i_control);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#setRoot(org.eclipse.draw2d.IFigure)
     */
    @Override
    public void setRoot(IFigure i_figure) {

        m_dispatcher.setRoot(i_figure);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.EventDispatcher#updateCursor()
     */
    @Override
    protected void updateCursor() {

        // TODO see https://bugs.eclipse.org/bugs/show_bug.cgi?id=281008
        try {
            if (m_updateCursorMethod == null) {
                m_updateCursorMethod = m_dispatcher.getClass().getDeclaredMethod(
                    "updateCursor");
                m_updateCursorMethod.setAccessible(true);
            }

            m_updateCursorMethod.invoke(m_dispatcher);
        } catch (Exception e) {
            // very unlikely to happen, so
            throw new RuntimeException("could not reflectively invoke method "
                    + m_dispatcher.getClass().getName() + ".updateCursor()", e);
        }
    }
}
