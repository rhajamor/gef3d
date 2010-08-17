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

import static java.awt.RenderingHints.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

/**
 * Base class for fonts that use textures to render text. The textures are
 * created by rendering text to AWT images.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 04.08.2010
 */
public abstract class AwtBasedTextureFont extends AwtBasedFont {

	private boolean m_disposed = false;

	/**
	 * Creates a new instance.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_flags the font flags
	 * @see {@link Flag}
	 */
	public AwtBasedTextureFont(String i_name, int i_size, Flag[] i_flags) {
		super(i_name, i_size, i_flags);
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
	 * Creates a {@link IDraw3DText} instance that renders a texture with the
	 * given image.
	 * 
	 * @param i_image the image to render
	 * @return the {@link IDraw3DText} instance
	 */
	protected abstract IDraw3DText doCreateText(BufferedImage i_image);

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.AwtBasedFont#doCreateText(String,
	 *      FontRenderContext)
	 */
	@Override
	protected IDraw3DText doCreateText(String i_string,
		FontRenderContext i_context) {
		GlyphVector glyphs =
			getAwtFont().createGlyphVector(i_context, i_string);
		LineMetrics lineMetrics =
			getAwtFont().getLineMetrics(i_string, i_context);
		Rectangle bounds = glyphs.getPixelBounds(i_context, 0, 0);

		BufferedImage img =
			new BufferedImage(bounds.width, bounds.height,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = img.createGraphics();
		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setRenderingHint(KEY_ALPHA_INTERPOLATION,
			VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_GASP);
		g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);

		g.setFont(getAwtFont());
		g.setColor(Color.BLACK);
		g.setBackground(new Color(1, 1, 1, 0));

		g.clearRect(0, 0, bounds.width, bounds.height);
		g.translate(0, bounds.height - lineMetrics.getDescent() - 0.5f);

		for (int i = 0; i < glyphs.getNumGlyphs(); i++) {
			Shape outline = glyphs.getGlyphOutline(i);
			g.fill(outline);
		}

		return doCreateText(img);
	}
}
