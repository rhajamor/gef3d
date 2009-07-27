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
package org.eclipse.draw3d.ui.camera;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.draw3d.util.CoordinateConverter;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;

/**
 * Handles mouse and user input for a camera and modifies that camera
 * accordingly.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 01.06.2009
 */
public class CameraInputHandler {

	/**
	 * Sensitivity factor for pointing the camera with the mouse.
	 */
	private final static float FACTOR_LOOK = 0.0002f;

	/**
	 * Sensitivity factor for moving the camera with the mouse.
	 */
	private final static float FACTOR_MOVE = 0.12f;

	/**
	 * Sensitivity factor for orbiting the camera with the mouse.
	 */
	private final static float FACTOR_ORBIT = 0.0009f;

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(CameraInputHandler.class.getName());

	private static final int MODIFIER_MASK = SWT.ALT | SWT.SHIFT | SWT.CONTROL
			| SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | SWT.MOD4;

	/**
	 * The key sequence to center the camera.
	 */
	private KeySequence m_centerKey;

	/**
	 * The key that is currently pressed.
	 */
	private int m_currentKey;

	private int m_currentModifiers = 0;

	/**
	 * The modifier key for fast camera movement.
	 */
	private int m_fastKey = SWT.ALT;

	/**
	 * The speed at which to move the camera when it is moved by the keyboard.
	 */
	private float m_keySpeed = 50;

	/**
	 * The last mouse location.
	 */
	private Point m_lastMouseLocation = new Point();

	/**
	 * The mouse button that the user clicks to move point the camera.
	 */
	private int m_lookButton = 1;

	/**
	 * Indicates whether the user is currently using the mouse to point the
	 * camera.
	 */
	private boolean m_mouseLook = false;

	/**
	 * Indicates whether the user is currently using the mouse to move the
	 * camera.
	 */
	private boolean m_mouseMove = false;

	/**
	 * The key sequence to move the camera backward.
	 */
	private KeySequence m_moveBackwardKey;

	/**
	 * The mouse button that the user clicks to move around.
	 */
	private int m_moveButton = 3;
	/**
	 * The key sequence to move the camera down.
	 */
	private KeySequence m_moveDownKey;

	/**
	 * The key sequence to move the camera forward.
	 */
	private KeySequence m_moveForwardKey;

	/**
	 * The key sequence to move the camera to the left.
	 */
	private KeySequence m_moveLeftKey;

	/**
	 * The key sequence to move the camera to the right.
	 */
	private KeySequence m_moveRightKey;

	/**
	 * The key sequence to move the camera up.
	 */
	private KeySequence m_moveUpKey;

	/**
	 * The current orbit center.
	 */
	private Vector3f m_orbitCenter;

	/**
	 * The modifier keys that trigger a camera orbit.
	 */
	private int m_orbitModifiers;

	/**
	 * The key sequence to roll the camera counter clockwise.
	 */
	private KeySequence m_rollCCWKey;

	/**
	 * The key sequence to roll the camera clockwise.
	 */
	private KeySequence m_rollCWKey;

	private IScene m_scene;

	/**
	 * The mouse sensitivity.
	 */
	private float m_sensitivity;

	/**
	 * The modifier key for slow camera movement.
	 */
	private int m_slowKey = SWT.SHIFT;

	private float m_wheelSpeed = 20;

	/**
	 * Handles a mouse button down event.
	 * 
	 * @param i_event
	 *            the event
	 */
	public void buttonDown(MouseEvent i_event) {

		int modifiers = getModifiers(i_event.stateMask);
		doButtonDown(i_event.button, modifiers, i_event.x, i_event.y);
	}

	/**
	 * Handles a mouse button up event.
	 * 
	 * @param i_event
	 *            the event
	 */
	public void buttonUp(MouseEvent i_event) {

		int modifiers = getModifiers(i_event.stateMask);
		doButtonUp(i_event.button, modifiers, i_event.x, i_event.y);
	}

	private void doButtonDown(int i_button, int i_modifiers, int i_x, int i_y) {

		m_lastMouseLocation.setLocation(i_x, i_y);

		m_orbitCenter = null;
		if (i_modifiers != 0 && i_modifiers == m_orbitModifiers) {
			UpdateManager updateManager = getScene().getUpdateManager();
			if (updateManager == null
					|| !(updateManager instanceof PickingUpdateManager3D))
				return;

			PickingUpdateManager3D pickingManager = (PickingUpdateManager3D) updateManager;
			ColorPicker picker = pickingManager.getPicker();
			Graphics3D g3d = picker.getGraphics3D();

			float depth = picker.getDepth(i_x, i_y);

			if (depth < 0.999f) {
				m_orbitCenter = CoordinateConverter.screenToWorld(i_x, i_y,
						depth, g3d, null);
			}
		} else {
			m_mouseLook = i_button == m_lookButton;
			m_mouseMove = i_button == m_moveButton;
		}
	}

	private void doButtonUp(int i_button, int i_stateMask, int i_x, int i_y) {

		m_lastMouseLocation.setLocation(i_x, i_y);

		m_mouseLook = false;
		m_mouseMove = false;
		m_orbitCenter = null;
	}

	/**
	 * Returns the camera for the scene.
	 * 
	 * @return the camera
	 */
	protected ICamera getCamera() {

		ICamera camera = getScene().getCamera();
		if (camera == null)
			throw new AssertionError("camera is null");

		return camera;
	}

	/**
	 * Return the key strokes represented by the given SWT key event.
	 * 
	 * @param keyEvent
	 *            the key event
	 * @return the key strokes
	 */
	private KeyStroke[] getKeyStrokes(KeyEvent keyEvent) {

		Event event = new Event();
		event.character = keyEvent.character;
		event.data = keyEvent.data;
		event.display = keyEvent.display;
		event.doit = keyEvent.doit;
		event.keyCode = keyEvent.keyCode;
		event.stateMask = keyEvent.stateMask;
		event.time = keyEvent.time;
		event.widget = keyEvent.widget;

		final List<KeyStroke> keyStrokes = new ArrayList<KeyStroke>(3);

		/*
		 * If this is not a keyboard event, then there are no key strokes. This
		 * can happen if we are listening to focus traversal events.
		 */
		if ((event.stateMask == 0) && (event.keyCode == 0)
				&& (event.character == 0)) {
			return keyStrokes.toArray(new KeyStroke[keyStrokes.size()]);
		}

		// Add each unique key stroke to the list for consideration.
		final int firstAccelerator = SWTKeySupport
				.convertEventToUnmodifiedAccelerator(event);
		keyStrokes.add(SWTKeySupport
				.convertAcceleratorToKeyStroke(firstAccelerator));

		// We shouldn't allow delete to undergo shift resolution.
		if (event.character == SWT.DEL) {
			return keyStrokes.toArray(new KeyStroke[keyStrokes.size()]);
		}

		final int secondAccelerator = SWTKeySupport
				.convertEventToUnshiftedModifiedAccelerator(event);
		if (secondAccelerator != 0 && secondAccelerator != firstAccelerator) {
			keyStrokes.add(SWTKeySupport
					.convertAcceleratorToKeyStroke(secondAccelerator));
		}

		final int thirdAccelerator = SWTKeySupport
				.convertEventToModifiedAccelerator(event);
		if (thirdAccelerator != 0 && thirdAccelerator != secondAccelerator
				&& thirdAccelerator != firstAccelerator) {
			keyStrokes.add(SWTKeySupport
					.convertAcceleratorToKeyStroke(thirdAccelerator));
		}

		return keyStrokes.toArray(new KeyStroke[keyStrokes.size()]);
	}

	private int getModifiers(int i_stateMask) {

		return i_stateMask & MODIFIER_MASK;
	}

	/**
	 * Returns the current scene.
	 * 
	 * @return the scene
	 */
	protected IScene getScene() {

		if (m_scene == null)
			throw new AssertionError("m_scene is null");

		return m_scene;
	}

	private boolean isKeySequence(KeySequence sequence, KeyStroke[] keyStrokes) {

		if (sequence == null)
			return false;

		KeyStroke[] seqStrokes = sequence.getKeyStrokes();

		if (seqStrokes.length != keyStrokes.length)
			return false;

		for (int i = 0; i < seqStrokes.length; i++) {
			KeyStroke seqKeyStroke = seqStrokes[i];
			KeyStroke keyStroke = keyStrokes[i];

			if (!seqKeyStroke.equals(keyStroke))
				return false;
		}

		return true;
	}

	/**
	 * Handles a key down event.
	 * 
	 * @param i_event
	 *            the key down event
	 */
	public void keyDown(KeyEvent i_event) {

		m_currentModifiers = getModifiers(i_event.stateMask);

		KeyStroke[] keyStrokes = getKeyStrokes(i_event);
		if (keyStrokes == null)
			return;

		if (m_currentKey != i_event.keyCode) {

			float speed = m_keySpeed;
			int modifiers = getModifiers(i_event.stateMask);

			if ((modifiers & m_slowKey) != 0)
				speed /= 10;

			if ((modifiers & m_fastKey) != 0)
				speed *= 10;

			if (isKeySequence(m_moveLeftKey, keyStrokes)) {
				getCamera().moveBy(0, -speed, 0);
			} else if (isKeySequence(m_moveRightKey, keyStrokes)) {
				getCamera().moveBy(0, speed, 0);
			} else if (isKeySequence(m_moveUpKey, keyStrokes)) {
				getCamera().moveBy(0, 0, speed);
			} else if (isKeySequence(m_moveDownKey, keyStrokes)) {
				getCamera().moveBy(0, 0, -speed);
			} else if (isKeySequence(m_moveForwardKey, keyStrokes)) {
				getCamera().moveBy(speed, 0, 0);
			} else if (isKeySequence(m_moveBackwardKey, keyStrokes)) {
				getCamera().moveBy(-speed, 0, 0);
			} else if (isKeySequence(m_centerKey, keyStrokes)) {
				getCamera().reset();
			} else if (isKeySequence(m_rollCCWKey, keyStrokes)) {
				float roll = (float) Math.toRadians(speed);
				getCamera().rotate(roll, 0, 0);
			} else if (isKeySequence(m_rollCWKey, keyStrokes)) {
				float roll = (float) Math.toRadians(speed);
				getCamera().rotate(-roll, 0, 0);
			}
		}
	}

	/**
	 * Handles a key up event.
	 * 
	 * @param i_event
	 *            the event
	 */
	public void keyUp(KeyEvent i_event) {

		m_currentModifiers = 0;
	}

	private float lookSpeed(int i_speed) {

		return i_speed * m_sensitivity * FACTOR_LOOK;
	}

	/**
	 * Handles a mouse move event.
	 * 
	 * @param i_event
	 *            the event
	 */
	public void mouseMove(MouseEvent i_event) {

		int x = i_event.x;
		int y = i_event.y;

		int dx = x - m_lastMouseLocation.x;
		int dy = y - m_lastMouseLocation.y;

		m_lastMouseLocation.setLocation(x, y);

		int modifiers = getModifiers(i_event.stateMask);
		if (modifiers != 0 && modifiers == m_orbitModifiers
				&& m_orbitCenter != null) {

			float hAngle = (float) Math.asin(orbitSpeed(dx));
			float vAngle = (float) Math.asin(orbitSpeed(dy));
			getCamera().orbit(m_orbitCenter, -hAngle, vAngle);
		} else if (m_mouseLook) {

			float yaw = (float) Math.asin(lookSpeed(dx));
			float pitch = (float) Math.asin(lookSpeed(dy));
			getCamera().rotate(0, pitch, yaw);
		} else if (m_mouseMove) {
			getCamera().moveBy(0, moveSpeed(-dx), moveSpeed(dy));
		}
	}

	/**
	 * Handles mouse scroll events.
	 * 
	 * @param i_event
	 *            the event
	 */
	public void mouseWheelScrolled(Event i_event) {

		float speed = i_event.count * m_wheelSpeed;
		getCamera().moveBy(speed, 0, 0);
	}

	private float moveSpeed(int i_speed) {

		return i_speed * m_sensitivity * FACTOR_MOVE;
	}

	/**
	 * Handles a drag finished event. This is neccessary because in the case of
	 * native drags, the mouse up event gets lost.
	 * 
	 * @param i_event
	 *            the event
	 */
	public void nativeDragFinished(DragSourceEvent i_event) {

		doButtonUp(1, m_currentModifiers, i_event.x, i_event.y);
	}

	/**
	 * Handles a drag started event. This is neccessary because in the case of
	 * native drags, the mouse down event gets lost.
	 * 
	 * @param i_event
	 *            the event
	 */
	public void nativeDragStarted(DragSourceEvent i_event) {

		doButtonDown(1, m_currentModifiers, i_event.x, i_event.y);
	}

	private float orbitSpeed(int i_speed) {

		return i_speed * m_sensitivity * FACTOR_ORBIT;
	}

	/**
	 * Sets the key sequence that causes the camera to be centered.
	 * 
	 * @param i_centerKey
	 *            the key sequence
	 */
	public void setCenterKey(KeySequence i_centerKey) {
		m_centerKey = i_centerKey;
	}

	/**
	 * Sets the modifier key mask that causes the camera to move fast.
	 * 
	 * @param i_fastKey
	 *            the modifier key mask
	 */
	public void setFastKey(int i_fastKey) {
		m_fastKey = i_fastKey;
	}

	/**
	 * Sets the speed by which the camera moves when navigated by keyboard.
	 * 
	 * @param i_keySpeed
	 *            the keyboard speed
	 */
	public void setKeySpeed(float i_keySpeed) {
		m_keySpeed = i_keySpeed;
	}

	/**
	 * Sets the mouse button that the user holds to point the camera.
	 * 
	 * @param i_lookButton
	 *            the mouse button
	 */
	public void setLookButton(int i_lookButton) {
		m_lookButton = i_lookButton;
	}

	/**
	 * Sets the key sequence that causes the camera to move backward.
	 * 
	 * @param i_moveBackwardKey
	 *            the key sequence
	 */
	public void setMoveBackwardKey(KeySequence i_moveBackwardKey) {
		m_moveBackwardKey = i_moveBackwardKey;
	}

	/**
	 * Sets the mouse button that the user holds to move the camera.
	 * 
	 * @param i_moveButton
	 *            the mouse button
	 */
	public void setMoveButton(int i_moveButton) {
		m_moveButton = i_moveButton;
	}

	/**
	 * Sets the key sequence that causes the camera to move down.
	 * 
	 * @param i_moveDownKey
	 *            the key sequence
	 */
	public void setMoveDownKey(KeySequence i_moveDownKey) {
		m_moveDownKey = i_moveDownKey;
	}

	/**
	 * Sets the key sequence that causes the camera to move forward.
	 * 
	 * @param i_moveForwardKey
	 *            the key sequence.
	 */
	public void setMoveForwardKey(KeySequence i_moveForwardKey) {
		m_moveForwardKey = i_moveForwardKey;
	}

	/**
	 * Sets the key sequence that causes the camera to move left.
	 * 
	 * @param i_moveLeftKey
	 *            the key sequence
	 */
	public void setMoveLeftKey(KeySequence i_moveLeftKey) {
		m_moveLeftKey = i_moveLeftKey;
	}

	/**
	 * Sets the key sequence that causes the camera to move right.
	 * 
	 * @param i_moveRightKey
	 *            the key sequence
	 */
	public void setMoveRightKey(KeySequence i_moveRightKey) {
		m_moveRightKey = i_moveRightKey;
	}

	/**
	 * Sets the key sequence that causes the camera to move up.
	 * 
	 * @param i_moveUpKey
	 *            the key sequence
	 */
	public void setMoveUpKey(KeySequence i_moveUpKey) {

		m_moveUpKey = i_moveUpKey;
	}

	/**
	 * Sets the modifier key mask that activates orbit mode.
	 * 
	 * @param i_orbitModifiers
	 *            the modifier key mask
	 */
	public void setOrbitModifiers(int i_orbitModifiers) {
		m_orbitModifiers = i_orbitModifiers;
	}

	/**
	 * Sets the key sequence that causes the camera to roll counter-clockwise.
	 * 
	 * @param i_rollCCWKey
	 *            the key sequence
	 */
	public void setRollCCWKey(KeySequence i_rollCCWKey) {
		m_rollCCWKey = i_rollCCWKey;
	}

	/**
	 * Sets the key sequence that causes the camera to roll clockwise.
	 * 
	 * @param i_rollCWKey
	 *            the key sequence
	 */
	public void setRollCWKey(KeySequence i_rollCWKey) {
		m_rollCWKey = i_rollCWKey;
	}

	/**
	 * Sets the scene that contains the camera.
	 * 
	 * @param i_scene
	 *            the scene
	 */
	public void setScene(IScene i_scene) {

		m_scene = i_scene;
	}

	/**
	 * Sets the mouse sensitivity.
	 * 
	 * @param i_sensitivity
	 *            the mouse sensitivity
	 */
	public void setSensitivity(float i_sensitivity) {
		m_sensitivity = i_sensitivity;
	}

	/**
	 * Sets the modifier key mask that causes the camera to move slowly.
	 * 
	 * @param i_slowKey
	 *            the modifier key mask
	 */
	public void setSlowKey(int i_slowKey) {
		m_slowKey = i_slowKey;
	}

	/**
	 * Sets the speed by which the camera moves when the mouse wheel is
	 * scrolled.
	 * 
	 * @param i_wheelSpeed
	 *            the mouse wheel speed
	 */
	public void setWheelSpeed(float i_wheelSpeed) {
		m_wheelSpeed = i_wheelSpeed;
	}
}