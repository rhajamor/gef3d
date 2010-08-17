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
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;

/**
 * A font that uses vector font data to render text directly.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2010
 */
public class LwjglVectorFont extends AwtBasedFont {

	private LwjglAWTGlyphCallback m_callback;

	/**
	 * Buffer for a single vertex during character tesselation.
	 */
	private double[] m_cBuf = new double[3];

	private boolean m_disposed = false;

	private float m_precision;

	private GLUtessellator m_tesselator;

	/**
	 * Vertex buffer for GLU tesselator.
	 */
	private float[][] m_vBuf = new float[8][2];

	/**
	 * Creates a new instance. The given precision factor must be between 0 and
	 * 1 (inclusive) and indicates how precisely the font data should be
	 * tesselated. A value of 1 means high precision and produces more vertices
	 * than a value of 0, which means lowest precision.
	 * 
	 * @param i_name the font name
	 * @param i_size the font size
	 * @param i_precision the precision factor
	 * @param i_flags the flags
	 * @see AwtBasedFont#AwtBasedFont(String, int,
	 *      org.eclipse.draw3d.font.IDraw3DFont.Flag...)
	 */
	public LwjglVectorFont(String i_name, int i_size, float i_precision,
			Flag... i_flags) {
		super(i_name, i_size, i_flags);

		if (i_precision < 0 || i_precision > 1)
			throw new IllegalArgumentException(
				"precision must be between 0 and 1, inclusive");

		m_precision = i_precision;

		m_tesselator = GLU.gluNewTess();
		m_callback = new LwjglAWTGlyphCallback();

		// bug in LWJGL, must set edge flag callback to null before setting
		// begin callback
		m_tesselator.gluTessCallback(GLU.GLU_TESS_EDGE_FLAG, null);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_EDGE_FLAG_DATA, null);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_BEGIN, m_callback);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_BEGIN_DATA, null);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_VERTEX, m_callback);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_VERTEX_DATA, null);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_COMBINE, m_callback);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_COMBINE_DATA, null);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_END, m_callback);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_END_DATA, null);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_ERROR, m_callback);
		m_tesselator.gluTessCallback(GLU.GLU_TESS_ERROR_DATA, null);

		m_tesselator.gluTessProperty(GLU.GLU_TESS_TOLERANCE, 0);
		m_tesselator.gluTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, 0);

		m_tesselator.gluTessNormal(0, 0, -1);
	}

	private VectorChar createVectorChar(GlyphVector i_glyphs, int i_index,
		AffineTransform i_at, double i_flatness) {
		GlyphMetrics metrics = i_glyphs.getGlyphMetrics(i_index);
		Shape outline = i_glyphs.getGlyphOutline(i_index);
		PathIterator path = outline.getPathIterator(i_at, i_flatness);

		float advX = metrics.getAdvanceX();
		float advY = metrics.getAdvanceY();

		if (!path.isDone()) {
			if (path.getWindingRule() == PathIterator.WIND_EVEN_ODD)
				m_tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE,
					GLU.GLU_TESS_WINDING_ODD);
			else if (path.getWindingRule() == PathIterator.WIND_NON_ZERO)
				m_tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE,
					GLU.GLU_TESS_WINDING_NONZERO);

			int vi = 0;
			m_tesselator.gluTessBeginPolygon(null);
			while (!path.isDone()) {
				int segmentType = path.currentSegment(m_cBuf);

				switch (segmentType) {
				case PathIterator.SEG_MOVETO:
					m_tesselator.gluTessBeginContour();

					if (vi == m_vBuf.length)
						m_vBuf = resizeVertexBuffer(m_vBuf);
					m_vBuf[vi][0] = (float) m_cBuf[0];
					m_vBuf[vi][1] = (float) m_cBuf[1];
					m_tesselator.gluTessVertex(m_cBuf, 0, m_vBuf[vi++]);
					break;
				case PathIterator.SEG_CLOSE:
					m_tesselator.gluTessEndContour();
					break;
				case PathIterator.SEG_LINETO:
					if (vi == m_vBuf.length)
						m_vBuf = resizeVertexBuffer(m_vBuf);
					m_vBuf[vi][0] = (float) m_cBuf[0];
					m_vBuf[vi][1] = (float) m_cBuf[1];
					m_tesselator.gluTessVertex(m_cBuf, 0, m_vBuf[vi++]);
					break;
				}
				path.next();
			}
			m_tesselator.gluTessEndPolygon();
		}
		VectorChar vectorChar = m_callback.createVectorChar(advX, advY);

		m_callback.reset();
		i_at.translate(-advX, -advY);

		return vectorChar;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.IDraw3DFont#dispose()
	 */
	public void dispose() {
		if (m_disposed)
			throw new IllegalStateException(this + " is disposed");

		m_callback = null;
		m_tesselator.gluDeleteTess();
		m_tesselator = null;

		m_cBuf = null;
		m_vBuf = null;

		m_disposed = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.font.AwtBasedFont#doCreateText(String)
	 */
	@Override
	protected IDraw3DText doCreateText(String i_string) {
		FontRenderContext ctx = new FontRenderContext(null, true, false);
		LineMetrics lineMetrics = getAwtFont().getLineMetrics(i_string, ctx);
		GlyphVector glyphs = getAwtFont().createGlyphVector(ctx, i_string);

		AffineTransform at = new AffineTransform();
		at.translate(0, lineMetrics.getAscent() - 1);

		double flatness = 1.9d * m_precision + 0.1d;
		VectorChar[] stringChars = new VectorChar[glyphs.getNumGlyphs()];

		for (int i = 0; i < glyphs.getNumGlyphs(); i++)
			stringChars[i] = createVectorChar(glyphs, i, at, flatness);

		return new LwjglVectorText(stringChars);
	}

	private float[][] resizeVertexBuffer(float[][] i_v) {
		float[][] r = new float[2 * i_v.length][];
		System.arraycopy(i_v, 0, r, 0, i_v.length);
		for (int i = i_v.length; i < r.length; i++)
			r[i] = new float[2];
		return r;
	}
}
