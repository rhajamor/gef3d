/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.font;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * LwjglTextureFont There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 04.08.2010
 */
public class LwjglTextureFont extends AWTBasedFont {

	private boolean m_disposed = false;

	public LwjglTextureFont(String i_name, int i_size, Flag[] i_flags) {
		super(i_name, i_size, i_flags);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.AWTBasedFont#doCreateGlyphVector(java.awt.font.GlyphVector)
	 */
	@Override
	protected IDraw3DGlyphVector doCreateGlyphVector(GlyphVector i_glyphs) {
		FontRenderContext ctx = new FontRenderContext(null, true, true);
		Rectangle bounds = i_glyphs.getPixelBounds(ctx, 0, 0);
		BufferedImage img =
			new BufferedImage(bounds.width, bounds.height,
				BufferedImage.TYPE_BYTE_GRAY);

		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
			RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, bounds.width, bounds.height);

		g.translate(0, bounds.height);
		g.setColor(Color.BLACK);
		for (int i = 0; i < i_glyphs.getNumGlyphs(); i++) {
			Shape outline = i_glyphs.getOutline();
			g.fill(outline);
		}

		String path =
			"/Users/kristian/Temp/font_" + getAwtFont() + "_"
				+ System.currentTimeMillis() + ".png";
		File f = new File(path);
		try {
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DFont#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is already disposed");

		m_disposed = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DFont#initialize()
	 */
	public void initialize() {
		// nothing to initialize
	}

}
