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
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

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
	 * @see org.eclipse.draw3d.font.AWTBasedFont#doCreateGlyphVector(String)
	 */
	@Override
	protected IDraw3DGlyphVector doCreateGlyphVector(String i_string) {
		FontRenderContext ctx = new FontRenderContext(null, true, true);
		LineMetrics lineMetrics = getAwtFont().getLineMetrics(i_string, ctx);
		GlyphVector glyphs = getAwtFont().createGlyphVector(ctx, i_string);

		Rectangle bounds = glyphs.getPixelBounds(ctx, 0, 0);
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

		g.translate(0, lineMetrics.getAscent() - 1);
		g.setColor(Color.BLACK);
		g.setFont(getAwtFont());

		for (int i = 0; i < glyphs.getNumGlyphs(); i++) {
			Shape outline = glyphs.getGlyphOutline(i);
			g.fill(outline);
		}

		Raster data = img.getData();
		DataBufferByte dataBuffer = (DataBufferByte) data.getDataBuffer();

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
