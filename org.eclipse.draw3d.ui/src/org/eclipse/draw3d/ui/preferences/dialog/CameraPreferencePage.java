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
package org.eclipse.draw3d.ui.preferences.dialog;

import static org.eclipse.draw3d.ui.preferences.PrefNames.*;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw3d.camera.FirstPersonCamera;
import org.eclipse.draw3d.camera.RestrictedFirstPersonCamera;
import org.eclipse.draw3d.ui.Draw3DUIPlugin;
import org.eclipse.draw3d.ui.preferences.PrefNames;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Provides the implementation of the camera preferences.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 4.3.2008
 */
public class CameraPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Creates a new instance.
	 */
	public CameraPreferencePage() {
		super(GRID);
		setPreferenceStore(Draw3DUIPlugin.getDefault().getPreferenceStore());
		setDescription("GEF 3D camera preferences");
	}

	private void addKeyEditor(String i_prefName, String i_label,
			int i_keyStrokeLimit) {

		Composite parent = getFieldEditorParent();

		KeyBindingFieldEditor keyEditor = new KeyBindingFieldEditor(i_prefName,
				i_label, parent);

		keyEditor.setKeyStrokeLimit(i_keyStrokeLimit);
		addField(keyEditor);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		Composite parent = getFieldEditorParent();

		String[][] cameraTypes = new String[][] {
				{ "Default first person camera",
						FirstPersonCamera.class.getName() },
				{ "Restricted first person camera",
						RestrictedFirstPersonCamera.class.getName() } };

		addField(new RadioGroupFieldEditor(PrefNames.LWS_CAMERA_TYPE,
				"Camera type:", 1, cameraTypes, parent));

		addField(new ColorFieldEditor(PrefNames.LWS_BACKGROUND,
				"Background color", parent));

		addField(new BooleanFieldEditor(PrefNames.LWS_DRAW_AXES, "Draw axes",
				parent));

		addField(new ScaleFieldEditor(PrefNames.MOUSE_SENSITIVITY,
				"Mouse sensitivity:", parent, 1, 100, 1, 20));

		addKeyEditor(KEY_FORWARD, "Move forward:", 1);
		addKeyEditor(KEY_BACKWARD, "Move backward:", 1);

		addKeyEditor(KEY_LEFT, "Move left:", 1);
		addKeyEditor(KEY_RIGHT, "Move right:", 1);

		addKeyEditor(KEY_UP, "Move up:", 1);
		addKeyEditor(KEY_DOWN, "Move down:", 1);

		addKeyEditor(KEY_ROLL_CCW, "Roll left:", 1);
		addKeyEditor(KEY_ROLL_CW, "Roll right:", 1);

		addKeyEditor(KEY_CENTER, "Center view", 1);

		String[] modOrbitLabels;
		int[] modOrbitValues;
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			modOrbitLabels = new String[] { "Shift", "Ctrl", "Alt", "Cmd" };
			modOrbitValues = new int[] { SWT.SHIFT, SWT.CONTROL, SWT.ALT,
					SWT.MOD1 };
		} else {
			modOrbitLabels = new String[] { "Shift", "Ctrl", "Alt" };
			modOrbitValues = new int[] { SWT.SHIFT, SWT.CONTROL, SWT.ALT };
		}

		addField(new BitFieldEditor(PrefNames.MOD_ORBIT, "Orbit key:",
				modOrbitLabels.length, modOrbitLabels, modOrbitValues, parent));
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

}