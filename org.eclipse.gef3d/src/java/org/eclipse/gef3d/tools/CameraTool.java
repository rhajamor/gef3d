/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.tools;

import java.util.logging.Logger;

import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.ui.camera.CameraInputHandler;
import org.eclipse.draw3d.ui.preferences.CameraPreferenceDistributor;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * This tool controls the camera. The actual camera control happens in an
 * instance of {@link CameraInputHandler} and this class only delegates SWT
 * events to that class.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 02.06.2009
 */
public class CameraTool extends AbstractTool {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CameraTool.class
			.getName());

	private Point m_cursorLocation;

	/**
	 * The camera input handler.
	 */
	protected CameraInputHandler m_handler;

	// TODO needs to be disposed somehow
	private Cursor m_invisibleCursor;

	/**
	 * The camera preference distributor.
	 */
	protected CameraPreferenceDistributor m_prefDistributor;

	private Cursor m_pushedCursor;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#activate()
	 */
	@Override
	public void activate() {

		super.activate();

		if (m_handler == null)
			m_handler = new CameraInputHandler();

		if (m_prefDistributor == null)
			m_prefDistributor = new CameraPreferenceDistributor(m_handler);

		m_prefDistributor.start();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#deactivate()
	 */
	@Override
	public void deactivate() {

		super.deactivate();
		m_prefDistributor.stop();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#getCommandName()
	 */
	@Override
	protected String getCommandName() {

		return "Camera Tool";
	}

	/**
	 * Returns the scene which contains the camera.
	 * 
	 * @return the scene or <code>null</code> if no scene is available
	 */
	protected IScene getScene() {

		EditPartViewer viewer = getCurrentViewer();
		if (viewer != null && viewer instanceof IScene)
			return (IScene) viewer;

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleKeyDown(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	protected boolean handleKeyDown(KeyEvent i_e) {

		// need to override this to allow ESC as center view key
		return true;
	}

	private void hideCursor() {

		// save current cursor
		Control ctrl = getCurrentViewer().getControl();
		while (ctrl != null) {
			if (ctrl.getCursor() != null) {
				m_pushedCursor = ctrl.getCursor();
				break;
			}

			ctrl = ctrl.getParent();
		}

		m_pushedCursor = getCurrentViewer().getControl().getDisplay()
				.getSystemCursor(SWT.CURSOR_ARROW);

		// hide cursor and save position
		ctrl = getCurrentViewer().getControl();
		Display display = ctrl.getDisplay();

		if (m_invisibleCursor == null) {
			Color white = display.getSystemColor(SWT.COLOR_WHITE);
			Color black = display.getSystemColor(SWT.COLOR_BLACK);

			PaletteData palette = new PaletteData(new RGB[] { white.getRGB(),
					black.getRGB() });
			ImageData sourceData = new ImageData(16, 16, 1, palette);
			sourceData.transparentPixel = 0;

			m_invisibleCursor = new Cursor(display, sourceData, 0, 0);
		}

		ctrl.setCursor(m_invisibleCursor);
		m_cursorLocation = display.getCursorLocation();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#keyDown(org.eclipse.swt.events.KeyEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void keyDown(KeyEvent i_evt, EditPartViewer i_viewer) {

		super.keyDown(i_evt, i_viewer);

		m_handler.setScene(getScene());
		m_handler.keyDown(i_evt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#keyUp(org.eclipse.swt.events.KeyEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void keyUp(KeyEvent i_evt, EditPartViewer i_viewer) {

		super.keyUp(i_evt, i_viewer);

		m_handler.setScene(getScene());
		m_handler.keyUp(i_evt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#mouseDown(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseDown(MouseEvent i_me, EditPartViewer i_viewer) {

		super.mouseDown(i_me, i_viewer);

		hideCursor();
		m_handler.setScene(getScene());
		m_handler.buttonDown(i_me);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#mouseDrag(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseDrag(MouseEvent i_me, EditPartViewer i_viewer) {

		super.mouseDrag(i_me, i_viewer);

		m_handler.setScene(getScene());
		m_handler.mouseMove(i_me);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#mouseMove(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseMove(MouseEvent i_me, EditPartViewer i_viewer) {

		super.mouseMove(i_me, i_viewer);

		m_handler.setScene(getScene());
		m_handler.mouseMove(i_me);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#mouseUp(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseUp(MouseEvent i_me, EditPartViewer i_viewer) {

		restoreCursor();
		super.mouseUp(i_me, i_viewer);

		m_handler.setScene(getScene());
		m_handler.buttonUp(i_me);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#mouseWheelScrolled(org.eclipse.swt.widgets.Event,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseWheelScrolled(Event i_event, EditPartViewer i_viewer) {

		super.mouseWheelScrolled(i_event, i_viewer);

		m_handler.setScene(getScene());
		m_handler.mouseWheelScrolled(i_event);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#nativeDragFinished(org.eclipse.swt.dnd.DragSourceEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void nativeDragFinished(DragSourceEvent i_event,
			EditPartViewer i_viewer) {

		restoreCursor();
		super.nativeDragFinished(i_event, i_viewer);

		m_handler.setScene(getScene());
		m_handler.nativeDragFinished(i_event);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#nativeDragStarted(org.eclipse.swt.dnd.DragSourceEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void nativeDragStarted(DragSourceEvent i_event,
			EditPartViewer i_viewer) {

		super.nativeDragStarted(i_event, i_viewer);

		hideCursor();
		m_handler.setScene(getScene());
		m_handler.nativeDragStarted(i_event);
	}

	private void restoreCursor() {

		Control ctrl = getCurrentViewer().getControl();
		if (m_cursorLocation != null) {
			Display display = ctrl.getDisplay();
			display.setCursorLocation(m_cursorLocation);
			m_cursorLocation = null;
		}

		if (m_pushedCursor != null) {
			ctrl.setCursor(m_pushedCursor);
			m_pushedCursor = null;
		}
	}
}
