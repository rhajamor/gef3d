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

import java.util.Arrays;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

/**
 * This class provides Graphics(2D) instances. Each instance of it wraps one
 * Graphics object.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DGraphics {

	/**
	 * This is the wrapped Graphics object.
	 */
	private SWTGraphics m_graphics;

	/**
	 * The image used to construct the Graphics object.
	 */
	private Image m_image;

	/**
	 * Construct a X3DGraphics and, therewith, one Graphics object.
	 * 
	 * @param i_width The desired width of the Graphics object.
	 * @param i_height The desired height of the Graphics object.
	 * @param i_alpha The desired alpha value of the Graphics object.
	 * @param i_color The desired background color of the Graphics object.
	 */
	public X3DGraphics(int i_width, int i_height, int i_alpha, Color i_color) {

		int width = i_width;
		int height = i_height;
		int size = width * height;

		// Reserve a buffer for the image data.
		byte[] data = new byte[size * 3];

		// Create the image data.
		PaletteData pal = new PaletteData(0xFF0000, 0x00FF00, 0x0000FF);
		ImageData imageData = new ImageData(width, height, 24, pal, 1, data);
		imageData.alphaData = new byte[size];
		Arrays.fill(imageData.alphaData, (byte) i_alpha);

		// Create the Graphics, using the image as Graphic Context.
		m_image = null;
		m_graphics = null;
		try {
			m_image = new Image(Display.getCurrent(), imageData);
			GC gc = new GC(m_image);
			m_graphics = new SWTGraphics(gc);
			m_graphics.setAlpha(0xFF);
			m_graphics.setBackgroundColor(i_color);
			m_graphics.fillRectangle(0, 0, i_width, i_height);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the wrapped Graphics object.
	 * 
	 * @return The Graphics object.
	 */
	public Graphics getGraphics() {
		return m_graphics;
	}

	/**
	 * Gets the image data of the Graphics object.
	 * 
	 * @return The image data.
	 */
	public ImageData getImageData() {
		return m_image.getImageData();
	}

}
