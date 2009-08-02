/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthias Thiele - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.graphics3d.x3d;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * Stores a set of rendering properties and gives common access to their value.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DPropertyContainer {

	/**
	 * Contains all the rendering properties.
	 */
	private final Properties m_properties;

	/*
	 * For each property to be stored inside the container, a identifying key
	 * and the default value is defined here.
	 */

	public static final String PRP_BG_COLOR = "PRP_BG_COLOR";

	public static final Color DEF_BG_COLOR = new Color(0.6f, 0.6f, 0.6f, 1);

	public static final String PRP_CURRENT_COLOR = "PRP_CURRENT_COLOR";

	public static final Color DEF_CURRENT_COLOR = DEF_BG_COLOR;

	public static final String PRP_DEF_DEPTH = "PRP_DEF_DEPTH";

	public static final double DEF_DEF_DEPTH = 1.0;

	public static final String PRP_TEX_ENVI_REPLACE = "PRP_TEX_ENVI_REPLACE";

	public static final Boolean DEF_TEX_ENVI_REPLACE = true;

	public static final String PRP_POLYGON_MODE_DO_FILL = "PRP_POLYGON_MODE_DO_FILL";

	public static final Boolean DEF_POLYGON_MODE_DO_FILL = true;

	public static final String PRP_VERSION = "PRP_VERSION";

	public static final String DEF_VERSION = "1.0";

	public static final String PRP_VIEWPORT = "PRP_VIEWPORT";

	public static final int[] DEF_VIEWPORT = { 0, 0, 400, 400 };

	public static final String PRP_VIEWPOINT_CENTER = "PRP_VIEWPOINT_CENTER";

	public static final float[] DEF_VIEWPOINT_CENTER = { 0.0f, 0.0f, 0.0f };

	public static final String PRP_VIEWPOINT_POSITION = "PRP_VIEWPOINT_POSITION";

	public static final float[] DEF_VIEWPOINT_POSITION = { 0.0f, 0.0f, 10.0f };

	public static final String PRP_LINE_WIDTH = "PRP_LINE_WIDTH";

	public static final float DEF_LINE_WIDTH = 1.0f;

	public static final String PRP_LINE_DASHED = "PRP_LINE_DASHED";

	public static final Boolean DEF_LINE_DASHED = false;

	public static final String PRP_NORMAL = "PRP_NORMAL";

	public static final Vector3f DEF_NORMAL = new Vector3fImpl(0, 0, 0);

	/**
	 * Default constructor, constructs a new property container.
	 */
	public X3DPropertyContainer() {
		m_properties = new Properties();

		setupDefaults();
	}

	/**
	 * Puts all properties into the map, using the defined defaults.
	 */
	private void setupDefaults() {

		m_properties.put(PRP_BG_COLOR, DEF_BG_COLOR);
		m_properties.put(PRP_CURRENT_COLOR, DEF_CURRENT_COLOR);
		m_properties.put(PRP_DEF_DEPTH, DEF_DEF_DEPTH);
		m_properties.put(PRP_TEX_ENVI_REPLACE, DEF_TEX_ENVI_REPLACE);
		m_properties.put(PRP_POLYGON_MODE_DO_FILL, DEF_POLYGON_MODE_DO_FILL);
		m_properties.put(PRP_VERSION, DEF_VERSION);
		m_properties.put(PRP_VIEWPORT, DEF_VIEWPORT);
		m_properties.put(PRP_VIEWPOINT_CENTER, DEF_VIEWPOINT_CENTER);
		m_properties.put(PRP_VIEWPOINT_POSITION, DEF_VIEWPOINT_POSITION);
		m_properties.put(PRP_LINE_WIDTH, DEF_LINE_WIDTH);
		m_properties.put(PRP_LINE_DASHED, DEF_LINE_DASHED);
		m_properties.put(PRP_NORMAL, DEF_NORMAL);

	}

	/**
	 * Gets the property map.
	 * 
	 * @return The property Map.
	 */
	public Properties getProperties() {
		return m_properties;
	}

	/**
	 * Creates an exact copy of this container. The copy is deep, there will be
	 * no references between this instance and the copy.
	 * 
	 * @return The copy
	 */
	public X3DPropertyContainer getCopy() {

		X3DPropertyContainer copy = new X3DPropertyContainer();

		for (Entry<Object, Object> entry : this.m_properties.entrySet()) {
			copy.m_properties.put(new String((String) entry.getKey()),
					copyProperty(entry.getValue()));
		}

		return copy;
	}

	/**
	 * Copies an object via serialization.
	 * 
	 * @param i_Value The object to copy
	 * @return The copy
	 */
	private Object copyProperty(Object i_Value) {

		Object newValue = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(i_Value);
			os.flush();

			ByteArrayInputStream in = new ByteArrayInputStream(out
					.toByteArray());
			ObjectInputStream is = new ObjectInputStream(in);
			newValue = is.readObject();

			is.close();
			os.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return newValue;

	}
}
