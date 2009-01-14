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
package org.eclipse.gef3d.preferences;

import static org.eclipse.gef3d.preferences.PrefNames.*;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.draw3d.camera.FirstPersonCamera;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.gef3d.ui.parts.IScene;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * Listens to preference changes and updates a scene accordingly.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 15.04.2008
 */
public class ScenePreferenceListener implements IPropertyChangeListener {

	/**
	 * The default camera type.
	 */
	public static final String DEFAULT_CAMERA_TYPE = FirstPersonCamera.class
			.getName();

	private static final Logger log = Logger
			.getLogger(ScenePreferenceListener.class.getName());

	private PreferenceProvider m_preferenceProvider = new RuntimePreferenceProvider();

	private IScene m_scene;

	/**
	 * Creates a new listener for the given scene.
	 * 
	 * @param i_scene the scene to update on preference changes
	 * @throws NullPointerException if the given scene is <code>null</code>
	 */
	public ScenePreferenceListener(IScene i_scene) {

		if (i_scene == null)
			throw new NullPointerException("i_scene must not be null");

		m_scene = i_scene;
	}

	/**
	 * Returns the camera of the given type or the default camera type if the
	 * given type is unavailable.
	 * 
	 * @param type the type name of the camera to return
	 * @return the camera of the given type
	 * @throws NullPointerException if the given type is <code>null</code>
	 * @throws IllegalArgumentException if the given type is the default camera
	 *             type, but it cannot be loaded
	 */
	private ICamera getCamera(String type) {

		if (type == null)
			throw new NullPointerException("type must not be null");

		try {
			Class<?> clazz = Class.forName(type);
			return (ICamera) clazz.newInstance();
		} catch (Exception ex) {
			if (DEFAULT_CAMERA_TYPE.equals(type))
				throw new IllegalArgumentException("Default camera type "
						+ DEFAULT_CAMERA_TYPE + " is unavailable", ex);

			log.severe("Unable to load camera type " + type);
			return getCamera(DEFAULT_CAMERA_TYPE);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent i_event) {

		Preferences preferences = m_preferenceProvider.getPreferences();

		String property = i_event.getProperty();
		if (LWS_CAMERA_TYPE.equals(property)) {
			if (!i_event.getNewValue().equals(i_event.getOldValue())) {
				String type = preferences.getString(LWS_CAMERA_TYPE);
				updateCameraType(type);
			}
			m_scene.render();
		} else if (LWS_BACKGROUND.equals(property)) {
			String colorStr = preferences.getString(LWS_BACKGROUND);
			updateBackgroundColor(colorStr);
			m_scene.render();
		} else if (LWS_DRAW_AXES.equals(property)) {
			boolean drawAxes = preferences.getBoolean(LWS_DRAW_AXES);
			m_scene.setDrawAxes(drawAxes);
			m_scene.render();
		}
	}

	/**
	 * Starts listening to preference events and sets all scene properties
	 * according to the current preferences.
	 */
	public void start() {

		Preferences preferences = m_preferenceProvider.getPreferences();
		preferences.addPropertyChangeListener(this);

		String cameraType = preferences.getString(LWS_CAMERA_TYPE);
		updateCameraType(cameraType);

		String colorStr = preferences.getString(LWS_BACKGROUND);
		updateBackgroundColor(colorStr);

		boolean drawAxes = preferences.getBoolean(LWS_DRAW_AXES);
		m_scene.setDrawAxes(drawAxes);

		m_scene.render();
	}

	/**
	 * Stops listening to preference events.
	 */
	public void stop() {

		Preferences preferences = m_preferenceProvider.getPreferences();
		preferences.removePropertyChangeListener(this);
	}

	/**
	 * @param colorStr
	 */
	private void updateBackgroundColor(String colorStr) {
		RGB rgb = StringConverter.asRGB(colorStr);

		// TODO: this needs to be disposed!
		Color color = new Color(Display.getCurrent(), rgb);
		m_scene.setBackgroundColor(color);
	}

	/**
	 * @param type
	 */
	private void updateCameraType(String type) {
		ICamera newCamera = getCamera(type);
		ICamera currentCamera = m_scene.getCamera();

		if (!newCamera.getClass().equals(currentCamera.getClass()))
			m_scene.setCamera(newCamera);
	}
}
