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

import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;

/**
 * LwjglVectorFont There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public class LwjglVectorFont extends AWTBasedFont {

	private VectorChar[] m_chars;

	private boolean m_disposed = false;

	private float m_precision;

	public LwjglVectorFont(String i_name, int i_size, float i_precision,
			Flag... i_flags) {
		super(i_name, i_size, i_flags);

		if (i_precision < 0 || i_precision > 1)
			throw new IllegalArgumentException(
				"precision must be between 0 and 1, inclusive");

		m_precision = i_precision;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DFont#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		m_disposed = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.AWTBasedFont#doCreateGlyphVector(java.lang.String)
	 */
	@Override
	protected IDraw3DGlyphVector doCreateGlyphVector(String i_string) {
		// TODO implement method LwjglVectorFont.doCreateGlyphVector
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.AWTBasedFont#doUpdateFontData(GlyphVector,
	 *      char, char, char, char)
	 */
	@Override
	protected void doUpdateFontData(GlyphVector i_glyphs, char i_nf, char i_nl,
		char i_of, char i_ol) {
		GLUtessellator tesselator = GLU.gluNewTess();
		try {
			LwjglAWTGlyphCallback callback = new LwjglAWTGlyphCallback();

			// bug in LWJGL, must set edge flag callback to null before setting
			// begin callback
			tesselator.gluTessCallback(GLU.GLU_TESS_EDGE_FLAG, null);
			tesselator.gluTessCallback(GLU.GLU_TESS_EDGE_FLAG_DATA, null);
			tesselator.gluTessCallback(GLU.GLU_TESS_BEGIN, callback);
			tesselator.gluTessCallback(GLU.GLU_TESS_BEGIN_DATA, null);
			tesselator.gluTessCallback(GLU.GLU_TESS_VERTEX, callback);
			tesselator.gluTessCallback(GLU.GLU_TESS_VERTEX_DATA, null);
			tesselator.gluTessCallback(GLU.GLU_TESS_COMBINE, callback);
			tesselator.gluTessCallback(GLU.GLU_TESS_COMBINE_DATA, null);
			tesselator.gluTessCallback(GLU.GLU_TESS_END, callback);
			tesselator.gluTessCallback(GLU.GLU_TESS_END_DATA, null);
			tesselator.gluTessCallback(GLU.GLU_TESS_ERROR, callback);
			tesselator.gluTessCallback(GLU.GLU_TESS_ERROR_DATA, null);

			tesselator.gluTessProperty(GLU.GLU_TESS_TOLERANCE, 0);
			tesselator.gluTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, 0);

			tesselator.gluTessNormal(0, 0, -1);

			AffineTransform at = new AffineTransform();

			VectorChar[] temp = m_chars;
			m_chars = new VectorChar[i_glyphs.getNumGlyphs()];
			if (temp != null)
				System.arraycopy(temp, 0, m_chars, i_of - i_nf, temp.length);

			double[] coords = new double[3];
			float[] vertex = new float[2];
			double flatness = 9.9d * (1 - m_precision) + 0.1d;

			for (int i = 0; i < i_glyphs.getNumGlyphs(); i++) {
				if (m_chars[i] != null) {
					GlyphMetrics metrics = i_glyphs.getGlyphMetrics(i);
					Shape outline = i_glyphs.getGlyphOutline(i);
					PathIterator path = outline.getPathIterator(at, flatness);

					float advanceX = metrics.getAdvanceX();
					float advanceY = metrics.getAdvanceY();
					m_chars[i] = new VectorChar(advanceX, advanceY);

					if (!path.isDone()) {
						if (path.getWindingRule() == PathIterator.WIND_EVEN_ODD)
							tesselator.gluTessProperty(
								GLU.GLU_TESS_WINDING_RULE,
								GLU.GLU_TESS_WINDING_ODD);
						else if (path.getWindingRule() == PathIterator.WIND_NON_ZERO)
							tesselator.gluTessProperty(
								GLU.GLU_TESS_WINDING_RULE,
								GLU.GLU_TESS_WINDING_NONZERO);

						tesselator.gluTessBeginPolygon(null);
						while (!path.isDone()) {
							int segmentType = path.currentSegment(coords);

							switch (segmentType) {
							case PathIterator.SEG_MOVETO:
								tesselator.gluTessBeginContour();
								vertex[0] = (float) coords[0];
								vertex[1] = (float) coords[1];
								tesselator.gluTessVertex(coords, 0, vertex);
								break;
							case PathIterator.SEG_CLOSE:
								tesselator.gluTessEndContour();
								break;
							case PathIterator.SEG_LINETO:
								vertex[0] = (float) coords[0];
								vertex[1] = (float) coords[1];
								tesselator.gluTessVertex(coords, 0, vertex);
								break;
							}
							path.next();
						}
						tesselator.gluTessEndPolygon();

						callback.setData(m_chars[i]);
						callback.reset();
					}
					at.translate(-advanceX, -advanceY);
				}
			}
		} finally {
			tesselator.gluDeleteTess();
		}
	}
}
