/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - refactoring and optimizations
 ******************************************************************************/
package org.eclipse.gef3d.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.PickingUpdateManager3D;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.draw3d.util.CoordinateConverter;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.gef3d.preferences.PrefNames;
import org.eclipse.gef3d.preferences.PreferenceProvider;
import org.eclipse.gef3d.preferences.RuntimePreferenceProvider;
import org.eclipse.gef3d.ui.parts.IScene;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * CameraTool moves and rotates a camera of a 3D lighweight system. This tool
 * moves the camera like a first person camera:
 * <ul>
 * <li>Mouse drag -- rorates camera (look)</li>
 * <li>Arrow key -- move camera left/right (strafe) and backward/forward (slide)
 * </li>
 * <li>+/- keys -- moves camera up and down
 * <li>
 * <li>Mouse wheel -- move camera backward/forward
 * <li>ESC -- rest camera to initial position</li>
 * </ul>
 * <p>
 * There are several parameters for sensivity, keyspeed, and wheelspeed. All
 * these parameters may be made configurable later.
 * <p>
 * The implementation is a little bit dirty, but it is running quite well, at
 * least under Mac OS X. There's a bug under OS X (see {@link https
 * ://bugs.eclipse.org/bugs/show_bug.cgi?id=207298}) which causes all key events
 * to stop when mouse button is pressed. This implementation here doesn't
 * workaround this bug directly, but at least a pressed key is still working
 * after the mouse button is released.
 * 
 * @author Jens von Pilgrim, Kristian Duske
 * @version $Revision$
 * @since 19.11.2007
 */
public class CameraTool extends AbstractTool {

	private final static float FACTOR_MOVE = 1.2f;

	private final static float FACTOR_ORBIT = 0.009f;

	private final static float FACTOR_ROTATE = 0.002f;

	/**
	 * Logger for this class.
	 */
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CameraTool.class
			.getName());

	private KeySequence backwardKey;

	private boolean bKeyPressed = false;

	private boolean bLook = false;

	private boolean bMove;

	private KeySequence centerLook;

	private int currentKey;

	private KeySequence downKey;

	private int fast = SWT.ALT;

	private KeySequence forwardKey; // + ;

	private float keyspeed = 5;

	private Point lastme = new Point();

	private KeySequence leftKey;

	private int lookButton = 1;

	private float mouseSensitivity = 1;

	private int moveButton = 3;

	private Vector3f orbitCenter;

	/**
	 * Updates the configuration of this tool whenever the preferences are
	 * modified.
	 */
	private IPropertyChangeListener preferenceListener = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent i_event) {
			updatePreferences();
		}
	};

	private PreferenceProvider preferenceProvider = new RuntimePreferenceProvider();

	private Cursor pushedCursor = null;

	private KeySequence rightKey;

	private KeySequence rollLeftKey;

	private KeySequence rollRightKey;

	private int slow = SWT.SHIFT;

	private KeySequence upKey;

	private float wheelspeed = 20;

	private int orbitModifiers;

	/**
	 * Initializes the default key bindings.
	 */
	public CameraTool() {

		forwardKey = getKeySequence('+');
		backwardKey = getKeySequence('-');

		leftKey = getKeySequence(SWT.ARROW_LEFT);
		rightKey = getKeySequence(SWT.ARROW_RIGHT);

		upKey = getKeySequence(SWT.ARROW_UP);
		downKey = getKeySequence(SWT.ARROW_DOWN);

		rollLeftKey = getKeySequence('Y');
		rollRightKey = getKeySequence('X');

		centerLook = getKeySequence(SWT.ESC);
		orbitModifiers = SWT.ALT;
	}

	@Override
	public void activate() {
		super.activate();

		Preferences preferences = preferenceProvider.getPreferences();
		preferences.addPropertyChangeListener(preferenceListener);

		updatePreferences();
	}

	@Override
	public void deactivate() {
		super.deactivate();

		Preferences preferences = preferenceProvider.getPreferences();
		preferences.removePropertyChangeListener(preferenceListener);
	}

	private ICamera getCamera() {

		EditPartViewer viewer = getCurrentViewer();
		if (viewer instanceof IScene) {
			IScene scene = (IScene) viewer;
			return scene.getCamera();
		}

		return null;
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

	private KeySequence getKeySequence(int i_keyCode) {

		KeyStroke keyStroke = KeyStroke.getInstance(i_keyCode);
		return KeySequence.getInstance(keyStroke);
	}

	/**
	 * Return the key strokes represented by the given SWT key event.
	 * 
	 * @param keyEvent the key event
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

	private KeySequence getPrefSequence(String i_prefName) {

		try {
			Preferences preferences = preferenceProvider.getPreferences();
			String prefKeyString = preferences.getString(i_prefName);

			return KeySequence.getInstance(prefKeyString);
		} catch (ParseException ex) {
			throw new RuntimeException("Error while updating from preferences",
					ex);
		}
	}

	private UpdateManager getUpdateManager() {

		EditPartViewer viewer = getCurrentViewer();
		if (viewer == null || !(viewer instanceof IScene))
			return null;

		IScene scene = (IScene) viewer;
		return scene.getUpdateManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleButtonDown(int)
	 */
	@Override
	protected boolean handleButtonDown(int i_button) {

		if (i_button != 0) {
			Point me = getCurrentInput().getMouseLocation();
			lastme.setLocation(me);

			orbitCenter = null;

			int modifiers = getModifiers(getCurrentInput());
			if (modifiers != 0 && modifiers == orbitModifiers) {
				UpdateManager updateManager = getUpdateManager();
				if (updateManager == null
						|| !(updateManager instanceof PickingUpdateManager3D))
					return false;

				PickingUpdateManager3D pickingManager = (PickingUpdateManager3D) updateManager;
				ColorPicker picker = pickingManager.getPicker();
				Graphics3D g3d = picker.getGraphics3D();

				float depth = picker.getDepth(me.x, me.y);

				if (depth < 0.999f) {
					orbitCenter = CoordinateConverter.screenToWorld(me.x, me.y,
							depth, g3d, null);
				}
			} else {
				bLook = i_button == lookButton;
				bMove = i_button == moveButton;
			}
			setCursor(SWT.CURSOR_CROSS);
			return true;
		} else {
			return super.handleButtonDown(i_button);
		}
	}

	private int getModifiers(Input i_currentInput) {

		int modifiers = 0;
		if (i_currentInput.isShiftKeyDown())
			modifiers |= SWT.SHIFT;

		if (i_currentInput.isControlKeyDown())
			modifiers |= SWT.CONTROL;

		if (i_currentInput.isAltKeyDown())
			modifiers |= SWT.ALT;

		if (Platform.OS_MACOSX.equals(Platform.getOS())
				&& i_currentInput.isModKeyDown(SWT.MOD1))
			modifiers |= SWT.MOD1;

		return modifiers;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleButtonUp(int)
	 */
	@Override
	protected boolean handleButtonUp(int i_button) {

		if (i_button != 0) {
			bLook = false;
			bMove = false;

			popCursor();
			return true;
		} else {
			return super.handleButtonUp(i_button);
		}
	}

	/**
	 * Handles rotation, i.e. mouse moves while key is pressed.
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleDragInProgress()
	 */
	@Override
	protected boolean handleDragInProgress() {

		ICamera camera = getCamera();
		if (camera == null)
			return super.handleDragInProgress();

		Point me = getCurrentInput().getMouseLocation();
		float dX = me.x - lastme.x;
		float dY = me.y - lastme.y;
		lastme.setLocation(me);

		int modifiers = getModifiers(getCurrentInput());
		if (modifiers != 0 && modifiers == orbitModifiers
				&& orbitCenter != null) {
			float hAngle = (float) Math.asin(orbitSpeed(dX));
			float vAngle = (float) Math.asin(orbitSpeed(dY));
			camera.orbit(orbitCenter, -hAngle, vAngle);
			return true;
		} else if (bLook) {
			float yaw = (float) Math.asin(rotateSpeed(dX));
			float pitch = (float) Math.asin(rotateSpeed(dY));
			camera.rotate(0, pitch, yaw);
			return true;
		} else if (bMove) {
			camera.moveBy(0, moveSpeed(-dX), moveSpeed(dY));
			return true;
		} else {
			return super.handleDragInProgress();
		}

	}

	private boolean isKeySequence(KeySequence sequence, KeyStroke[] keyStrokes) {

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
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#keyDown(org.eclipse.swt.events.KeyEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void keyDown(KeyEvent e, EditPartViewer i_viewer) {

		KeyStroke[] keyStrokes = getKeyStrokes(e);

		ICamera camera = getCamera();
		if (camera != null && !(bKeyPressed && currentKey == e.keyCode)) {

			float speed = this.keyspeed;
			if ((e.stateMask & slow) == 0)
				speed *= 10;

			if ((e.stateMask & fast) != 0)
				speed *= 10;

			if (isKeySequence(leftKey, keyStrokes)) {
				camera.moveBy(0, -speed, 0);
			} else if (isKeySequence(rightKey, keyStrokes)) {
				camera.moveBy(0, speed, 0);
			} else if (isKeySequence(upKey, keyStrokes)) {
				camera.moveBy(0, 0, speed);
			} else if (isKeySequence(downKey, keyStrokes)) {
				camera.moveBy(0, 0, -speed);
			} else if (isKeySequence(forwardKey, keyStrokes)) {
				camera.moveBy(speed, 0, 0);
			} else if (isKeySequence(backwardKey, keyStrokes)) {
				camera.moveBy(-speed, 0, 0);
			} else if (isKeySequence(centerLook, keyStrokes)) {
				camera.reset();
			} else if (isKeySequence(rollLeftKey, keyStrokes)) {
				float roll = (float) Math.toRadians(5);
				camera.rotate(roll, 0, 0);
			} else if (isKeySequence(rollRightKey, keyStrokes)) {
				float roll = (float) Math.toRadians(5);
				camera.rotate(-roll, 0, 0);

			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#mouseWheelScrolled(org.eclipse.swt.widgets.Event,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseWheelScrolled(Event i_event, EditPartViewer i_viewer) {

		ICamera camera = getCamera();
		if (camera != null) {
			float speed = i_event.count * wheelspeed;
			camera.moveBy(speed, 0, 0);
		}
	}

	private float moveSpeed(float i_speed) {

		return i_speed * mouseSensitivity * FACTOR_MOVE;
	}

	private float orbitSpeed(float i_speed) {

		return i_speed * mouseSensitivity * FACTOR_ORBIT;
	}

	private void popCursor() {
		if (pushedCursor != null) {
			Control ctrl = getCurrentViewer().getControl();
			ctrl.setCursor(pushedCursor);
			pushedCursor = null;
		}
	}

	private void pushCursor() {
		Control ctrl = getCurrentViewer().getControl();
		while (ctrl != null) {
			if (ctrl.getCursor() != null) {
				pushedCursor = ctrl.getCursor();
			}
			ctrl = ctrl.getParent();
		}
		pushedCursor = getCurrentViewer().getControl().getDisplay()
				.getSystemCursor(SWT.CURSOR_ARROW);
	}

	private float rotateSpeed(float i_speed) {

		return i_speed * mouseSensitivity * FACTOR_ROTATE;
	}

	private void setCursor(int cursorid) {
		popCursor();
		pushCursor();
		Control ctrl = getCurrentViewer().getControl();
		ctrl.setCursor(ctrl.getDisplay().getSystemCursor(cursorid));
	}

	/**
	 * Sets the preference provider to be used by the camera tool
	 * 
	 * @param i_preferenceProvider the preference provider to use
	 * @throws NullPointerException if the given preference provider is
	 *             <code>null</code>
	 */
	public void setPreferenceProvider(PreferenceProvider i_preferenceProvider) {

		preferenceProvider = i_preferenceProvider;
	}

	private void updatePreferences() {

		Preferences store = preferenceProvider.getPreferences();

		float prefMouseSens = store.getInt(PrefNames.MOUSE_SENSITIVITY);
		mouseSensitivity = prefMouseSens / 10f;

		forwardKey = getPrefSequence(PrefNames.KEY_FORWARD);
		backwardKey = getPrefSequence(PrefNames.KEY_BACKWARD);

		leftKey = getPrefSequence(PrefNames.KEY_LEFT);
		rightKey = getPrefSequence(PrefNames.KEY_RIGHT);

		upKey = getPrefSequence(PrefNames.KEY_UP);
		downKey = getPrefSequence(PrefNames.KEY_DOWN);

		rollLeftKey = getPrefSequence(PrefNames.KEY_ROLL_LEFT);
		rollRightKey = getPrefSequence(PrefNames.KEY_ROLL_RIGHT);

		centerLook = getPrefSequence(PrefNames.KEY_CENTER);
		orbitModifiers = store.getInt(PrefNames.MOD_ORBIT);
	}

}
