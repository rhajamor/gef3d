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

package org.eclipse.draw3d.graphics3d.x3d.graphics2d;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * Manages all the Graphics2D instances created when drawing in X3D.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DGraphics2DManager {

	/**
	 * This map stores the Graphics2D instances.
	 */
	private final Map<Object, X3DGraphics> m_g2dMap;

	/**
	 * This is the Graphics2D object which is currently active (being drawn
	 * into).
	 */
	private X3DGraphics m_activeG2D;

	/**
	 * Constructs a new Graphics2DManager.
	 */
	public X3DGraphics2DManager() {

		m_g2dMap = new HashMap<Object, X3DGraphics>();
		m_activeG2D = null;

	}

	/**
	 * Activates a new Graphics2D object. If one exists for the given key, that
	 * one is removed first. The created Graphics2D object is saved into the map
	 * and set as being active.
	 * 
	 * @param i_key Create a Graphics2D instance for this key object.
	 * @param i_width Desired width for the Graphics2D object.
	 * @param i_height Desired height for the Graphics2D object.
	 * @param i_alpha Desired alpha value for the Graphics2D object.
	 * @param i_color Desired backgound color for the Graphics2D object.
	 * @return The created and activated Graphics2D object.
	 */
	public Graphics activateGraphics2D(Object i_key, int i_width, int i_height,
			int i_alpha, Color i_color) {

		if (i_key == null)
			throw new NullPointerException("The key must not be null.");
		if (i_color == null)
			throw new NullPointerException("Color must not be null.");

		if (m_g2dMap.containsKey(i_key))
			m_g2dMap.remove(i_key);

		X3DGraphics newG2D = new X3DGraphics(i_width, i_height, i_alpha,
				i_color);

		m_g2dMap.put(i_key, newG2D);
		m_activeG2D = newG2D;

		return m_activeG2D.getGraphics();
	}

	/**
	 * Deactivates the currently active Graphics2D object.
	 */
	public void deactivateGraphics2D() {
		m_activeG2D = null;
	}

	/**
	 * Gets the ID of the Graphics2D object for a given key object. Please note,
	 * it is illegal to call this method, if no Graphics2D object exists for the
	 * given key.
	 * 
	 * @param i_key The key object.
	 * @return The ID of the associated Graphics2D object.
	 */
	public int getGraphics2DId(Object i_key) {

		if (i_key == null)
			throw new NullPointerException("The key must not be null.");

		if (!m_g2dMap.containsKey(i_key)) {
			throw new IllegalArgumentException("No Graphics2D for object: "
					+ i_key.toString());
		} else {
			return i_key.hashCode();
		}
	}

	/**
	 * Determines whether there is a Graphics2D object for the given key object.
	 * 
	 * @param i_key The key object
	 * @return True, if an associated Graphics2D object was found, False
	 *         otherwise.
	 */
	public boolean hasGraphics2D(Object i_key) {

		if (i_key == null)
			throw new NullPointerException("The key must not be null.");

		return m_g2dMap.containsKey(i_key);
	}

	/**
	 * Writes the Graphics2D object with the given ID to the given path. The
	 * name of the file will be the ID of the object.
	 * 
	 * @param g2dID The ID of the Graphics2D object to write.
	 * @param i_strExportPath The path to the directory to write into.
	 * @return The complete path to the written file.
	 */
	public String writeImage(int g2dID, String i_strExportPath) {
		for (Object key : m_g2dMap.keySet()) {
			if (key.hashCode() == g2dID) {
				return writeImage(key, i_strExportPath);
			}
		}

		throw new Graphics3DException("Graphics2D ID not found in map.");
	}

	/**
	 * Writes the Graphics2D object associated with the given key object to the
	 * given path. The name of the file will be the ID of the object.
	 * 
	 * @param i_key The key object whose associated Graphics2D object shall be
	 *            written.
	 * @param i_strExportPath The path to the directory to write into.
	 * @return The complete path to the written file.
	 */
	public String writeImage(Object i_key, String i_strExportPath) {

		if (i_key == null)
			throw new NullPointerException("The key must not be null.");

		if (i_strExportPath == null)
			throw new NullPointerException("The export path must not be null.");

		if (!new File(i_strExportPath).exists())
			throw new IllegalArgumentException("The export path "
					+ i_strExportPath + " does not exist.");

		if (!m_g2dMap.containsKey(i_key)) {
			throw new IllegalArgumentException("No Graphics2D for object: "
					+ i_key.toString());
		}

		// Get the Graphics object.
		X3DGraphics g2write = m_g2dMap.get(i_key);

		// Build the path to the export file
		String strExportFile = i_strExportPath + "/" + getGraphics2DId(i_key)
				+ ".png";

		// Get the Graphics2D's image data and write the image to disk.
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { g2write.getImageData() };
		loader.save(strExportFile, SWT.IMAGE_PNG);

		return strExportFile;
	}
}
