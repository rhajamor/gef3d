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
package org.eclipse.draw3d.graphics3d.lwjgl.font;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.Vector2fImpl;
import org.eclipse.draw3d.util.BufferUtils;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.swt.SWT;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

/**
 * VectorFont There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 22.01.2010
 */
public class LwjglVectorFont {

	private static class FontCallback extends GLUtessellatorCallbackAdapter {

		private VectorCharData m_charData = new VectorCharData();

		private int m_curType;

		private List<IVector2f> m_curVerts = new LinkedList<IVector2f>();

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#begin(int)
		 */
		@Override
		public void begin(int i_type) {
			m_curType = i_type;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#combine(double[],
		 *      java.lang.Object[], float[], java.lang.Object[])
		 */
		@Override
		public void combine(double[] i_coords, Object[] i_data,
			float[] i_weight, Object[] i_outData) {

			i_outData[0] =
				new Vector2fImpl((float) i_coords[0], (float) i_coords[1]);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#end()
		 */
		@Override
		public void end() {
			m_charData.addPrimitive(m_curType, m_curVerts);
			m_curVerts.clear();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#error(int)
		 */
		@Override
		public void error(int i_errnum) {
			throw new RuntimeException(
				"caught error during polygon tesselation: " + i_errnum);
		}

		public VectorCharData getCharData() {
			return m_charData;
		}

		public void reset() {
			m_curVerts.clear();
			m_charData = new VectorCharData();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#vertex(java.lang.Object)
		 */
		@Override
		public void vertex(Object i_vertexData) {
			m_curVerts.add((IVector2f) i_vertexData);
		}
	}

	private static class PrimitiveData {
		private float[] m_vertices;

		public PrimitiveData(List<IVector2f> i_vertices) {
			m_vertices = new float[i_vertices.size() * 2];

			int i = 0;
			for (IVector2f vertex : i_vertices)
				vertex.toArray(m_vertices, 2 * i++);
		}

		public int getVertexCount() {
			return m_vertices.length / 2;
		}

		public float[] getVertices() {
			return m_vertices;
		}
	}

	private static class RangeCharacterIterator implements CharacterIterator {

		private int m_current = 0;

		private int m_numChars;

		public RangeCharacterIterator(int i_numChars) {
			m_numChars = i_numChars;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#current()
		 */
		public char current() {
			if (m_current < getBeginIndex() || m_current >= getEndIndex())
				return DONE;

			return (char) m_current;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#first()
		 */
		public char first() {
			m_current = getBeginIndex();
			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#getBeginIndex()
		 */
		public int getBeginIndex() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#getEndIndex()
		 */
		public int getEndIndex() {
			return m_numChars;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#getIndex()
		 */
		public int getIndex() {
			return m_current;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#last()
		 */
		public char last() {
			m_current = getEndIndex() - 1;
			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#next()
		 */
		public char next() {
			m_current++;
			if (m_current >= getEndIndex())
				return DONE;

			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#previous()
		 */
		public char previous() {
			m_current--;
			if (m_current < getBeginIndex())
				return DONE;

			return current();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.text.CharacterIterator#setIndex(int)
		 */
		public char setIndex(int i_index) {
			if (i_index < getBeginIndex() || i_index > getEndIndex())
				throw new IllegalArgumentException("index is out of bounds: "
					+ i_index);

			m_current = i_index;
			return current();
		}

	}

	private static class VectorChar {
		private float m_advanceX;

		private float m_advanceY;

		private List<PrimitiveData> m_lineLoops;

		private IntBuffer m_loopIndices;

		private IntBuffer m_loopLengths;

		private List<PrimitiveData> m_triangleFans;

		private List<PrimitiveData> m_triangles;

		private List<PrimitiveData> m_triangleStrips;

		private IntBuffer m_triFanIndices;

		private IntBuffer m_triFanLengths;

		private IntBuffer m_trisIndices;

		private IntBuffer m_trisLengths;

		private IntBuffer m_triStripIndices;

		private IntBuffer m_triStripLengths;

		public VectorChar(float i_advanceX, float i_advanceY) {
			m_advanceX = i_advanceX;
			m_advanceY = i_advanceY;
		}

		public int compile(FloatBuffer i_buffer, int i_startIndex) {
			int index = i_startIndex;

			index = compileTriangleFans(i_buffer, index);
			index = compileTriangleStrips(i_buffer, index);
			index = compileTriangles(i_buffer, index);
			index = compileLineLoops(i_buffer, index);

			return index;
		}

		public int compileLineLoops(FloatBuffer i_buffer, int i_startIndex) {
			if (m_lineLoops == null || m_lineLoops.isEmpty())
				return i_startIndex;

			m_loopIndices = BufferUtils.createIntBuffer(m_lineLoops.size());
			m_loopLengths = BufferUtils.createIntBuffer(m_lineLoops.size());

			int index = i_startIndex;
			for (PrimitiveData primitive : m_lineLoops) {
				i_buffer.put(primitive.getVertices());
				m_loopIndices.put(index);
				m_loopLengths.put(primitive.getVertexCount());
				index += primitive.getVertexCount();
			}

			m_lineLoops = null;
			return index;
		}

		private int compileTriangleFans(FloatBuffer i_buffer, int i_startIndex) {
			if (m_triangleFans == null || m_triangleFans.isEmpty())
				return i_startIndex;

			m_triFanIndices =
				BufferUtils.createIntBuffer(m_triangleFans.size());
			m_triFanLengths =
				BufferUtils.createIntBuffer(m_triangleFans.size());

			int index = i_startIndex;
			for (PrimitiveData primitive : m_triangleFans) {
				i_buffer.put(primitive.getVertices());
				m_triFanIndices.put(index);
				m_triFanLengths.put(primitive.getVertexCount());
				index += primitive.getVertexCount();
			}

			m_triangleFans = null;
			return index;
		}

		public int compileTriangles(FloatBuffer i_buffer, int i_startIndex) {
			if (m_triangles == null || m_triangles.isEmpty())
				return i_startIndex;

			m_trisIndices = BufferUtils.createIntBuffer(m_triangles.size());
			m_trisLengths = BufferUtils.createIntBuffer(m_triangles.size());

			int index = i_startIndex;
			for (PrimitiveData primitive : m_triangles) {
				i_buffer.put(primitive.getVertices());
				m_trisIndices.put(index);
				m_trisLengths.put(primitive.getVertexCount());
				index += primitive.getVertexCount();
			}

			m_triangles = null;
			return index;
		}

		public int compileTriangleStrips(FloatBuffer i_buffer, int i_startIndex) {
			if (m_triangleStrips == null || m_triangleStrips.isEmpty())
				return i_startIndex;

			m_triStripIndices =
				BufferUtils.createIntBuffer(m_triangleStrips.size());
			m_triStripLengths =
				BufferUtils.createIntBuffer(m_triangleStrips.size());

			int index = i_startIndex;
			for (PrimitiveData primitive : m_triangleStrips) {
				i_buffer.put(primitive.getVertices());
				m_triStripIndices.put(index);
				m_triStripLengths.put(primitive.getVertexCount());
				index += primitive.getVertexCount();
			}

			m_triangleStrips = null;
			return index;
		}

		public void render() {
			if (m_triFanIndices != null) {
				m_triFanIndices.rewind();
				m_triFanLengths.rewind();

				GL14.glMultiDrawArrays(GL11.GL_TRIANGLE_FAN, m_triFanIndices,
					m_triFanLengths);
			}

			if (m_triStripIndices != null) {
				m_triStripIndices.rewind();
				m_triStripLengths.rewind();

				GL14.glMultiDrawArrays(GL11.GL_TRIANGLE_STRIP,
					m_triStripIndices, m_triStripLengths);
			}

			if (m_trisIndices != null) {
				m_trisIndices.rewind();
				m_trisLengths.rewind();
				GL14.glMultiDrawArrays(GL11.GL_TRIANGLES, m_trisIndices,
					m_trisLengths);
			}

			if (m_loopIndices != null) {
				m_loopIndices.rewind();
				m_loopLengths.rewind();
				GL14.glMultiDrawArrays(GL11.GL_LINE_LOOP, m_loopIndices,
					m_loopLengths);
			}

			GL11.glTranslatef(m_advanceX, m_advanceY, 0);
		}

		public void setLineLoops(List<PrimitiveData> i_lineLoops) {
			m_lineLoops = i_lineLoops;
		}

		public void setTriangleFans(List<PrimitiveData> i_triangleFans) {
			m_triangleFans = i_triangleFans;
		}

		public void setTriangles(List<PrimitiveData> i_triangles) {
			m_triangles = i_triangles;
		}

		public void setTriangleStrips(List<PrimitiveData> i_triangleStrips) {
			m_triangleStrips = i_triangleStrips;
		}
	}

	private static class VectorCharData {

		private Map<Integer, List<PrimitiveData>> m_primitives =
			new HashMap<Integer, List<PrimitiveData>>();

		private int m_vertexCount = 0;

		public void addPrimitive(int i_type, List<IVector2f> i_primitive) {
			if (i_primitive.size() > 0) {
				List<PrimitiveData> primitivesOfType = m_primitives.get(i_type);
				if (primitivesOfType == null) {
					primitivesOfType = new LinkedList<PrimitiveData>();
					m_primitives.put(i_type, primitivesOfType);
				}

				primitivesOfType.add(new PrimitiveData(i_primitive));
				m_vertexCount += i_primitive.size();
			}
		}

		public int getVertexCount() {
			return m_vertexCount;
		}

		public void setPrimitives(VectorChar i_vectorChar) {
			i_vectorChar.setTriangleFans(m_primitives.get(GL11.GL_TRIANGLE_FAN));
			i_vectorChar.setTriangleStrips(m_primitives.get(GL11.GL_TRIANGLE_STRIP));
			i_vectorChar.setTriangles(m_primitives.get(GL11.GL_TRIANGLES));
			i_vectorChar.setLineLoops(m_primitives.get(GL11.GL_LINE_LOOP));
		}
	}

	private static int getAwtStyle(boolean i_bold, boolean i_italic) {

		int awtStyle = 0;
		if (i_bold)
			awtStyle |= java.awt.Font.BOLD;
		if (i_italic)
			awtStyle |= java.awt.Font.ITALIC;

		return awtStyle;
	}

	private boolean m_antialias;

	private Font m_awtFont;

	private int m_numChars;

	private int m_textureId = 0;

	private int m_textureListBaseId = -1;

	private int m_vectorListBaseId = -1;

	private int m_vertexBufferId = 0;

	public LwjglVectorFont(org.eclipse.swt.graphics.Font i_swtFont,
			int i_numChars, boolean i_antialias) {

		this(i_swtFont.getFontData()[0].getName(),
			i_swtFont.getFontData()[0].getHeight(),
			(i_swtFont.getFontData()[0].getStyle() & SWT.BOLD) != 0,
			(i_swtFont.getFontData()[0].getStyle() & SWT.ITALIC) != 0,
			i_numChars, i_antialias);
	}

	public LwjglVectorFont(String i_name, int i_height, boolean i_bold,
			boolean i_italic, int i_numChars, boolean i_antialias) {

		this(i_name, i_height, getAwtStyle(i_bold, i_italic), i_numChars,
			i_antialias);
	}

	public LwjglVectorFont(String i_name, int i_height, int i_awtStyle,
			int i_numChars, boolean i_antialias) {

		if (i_name == null)
			throw new NullPointerException("i_name must not be null");

		m_awtFont = new Font(i_name, i_awtStyle, i_height);
		m_numChars = i_numChars;
		m_antialias = i_antialias;
	}

	private int createDisplayLists(VectorChar[] i_chars) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_vertexBufferId);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		try {
			GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
			int listBaseId = GL11.glGenLists(i_chars.length);
			for (int i = 0; i < i_chars.length; i++) {
				int listId = m_vectorListBaseId + i;
				GL11.glNewList(listId, GL11.GL_COMPILE);
				i_chars[i].render();
				GL11.glEndList();
			}

			return listBaseId;
		} finally {
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		}
	}

	private void createTextureData(GlyphVector i_glyphs) {

	}

	private int createVectorData(GlyphVector i_glyphs,
		VectorChar[] i_vectorChars) {
		GLUtessellator tesselator = GLU.gluNewTess();
		try {
			FontCallback callback = new FontCallback();

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

			// The tesselator creates several primitives of different types per
			// character, so we have to store all of that data in different
			// arrays.

			int totalVertexCount = 0;
			double[] coords = new double[] { 0, 0, 0 };

			AffineTransform af = new AffineTransform();
			af.translate(0, m_awtFont.getSize());

			for (int i = 0; i < i_glyphs.getNumGlyphs(); i++) {
				Shape outline = i_glyphs.getGlyphOutline(i);
				PathIterator path = outline.getPathIterator(af, 0.01d);

				float advanceX = i_glyphs.getGlyphMetrics(i).getAdvanceX();
				float advanceY = i_glyphs.getGlyphMetrics(i).getAdvanceY();
				i_vectorChars[i] = new VectorChar(advanceX, advanceY);

				if (!path.isDone()) {
					if (path.getWindingRule() == PathIterator.WIND_EVEN_ODD)
						tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE,
							GLU.GLU_TESS_WINDING_ODD);
					else if (path.getWindingRule() == PathIterator.WIND_NON_ZERO)
						tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE,
							GLU.GLU_TESS_WINDING_NONZERO);

					tesselator.gluTessBeginPolygon(null);
					while (!path.isDone()) {
						int segmentType = path.currentSegment(coords);

						switch (segmentType) {
						case PathIterator.SEG_MOVETO:
							tesselator.gluTessBeginContour();
							tesselator.gluTessVertex(coords, 0,
								new Vector2fImpl((float) coords[0],
									(float) coords[1]));
							break;
						case PathIterator.SEG_CLOSE:
							tesselator.gluTessEndContour();
							break;
						case PathIterator.SEG_LINETO:
							tesselator.gluTessVertex(coords, 0,
								new Vector2fImpl((float) coords[0],
									(float) coords[1]));
							break;
						}
						path.next();
					}
					tesselator.gluTessEndPolygon();

					VectorCharData charData = callback.getCharData();

					charData.setPrimitives(i_vectorChars[i]);
					totalVertexCount += charData.getVertexCount();

					callback.reset();
				}

				af.translate(-advanceX, -advanceY);
			}

			return totalVertexCount;
		} finally {
			tesselator.gluDeleteTess();
		}
	}

	private int createVertexBuffer(VectorChar[] i_chars, int i_totalVertexCount) {

		int idx = 0;
		FloatBuffer buf = BufferUtils.createFloatBuffer(2 * i_totalVertexCount);
		for (int i = 0; i < i_chars.length; i++)
			idx = i_chars[i].compile(buf, idx);

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.rewind();
			GL15.glGenBuffers(idBuffer);

			int bufferId = idBuffer.get(0);
			buf.rewind();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STREAM_READ);

			return bufferId;
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

	public void dispose() {

		if (m_vectorListBaseId != -1) {
			GL11.glDeleteLists(m_vectorListBaseId, m_numChars);
			m_vectorListBaseId = -1;
		}

		if (m_textureListBaseId != 1) {
			GL11.glDeleteLists(m_textureListBaseId, m_numChars);
			m_textureListBaseId = -1;
		}

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			if (m_vertexBufferId != 0) {
				idBuffer.rewind();
				idBuffer.put(m_vertexBufferId);

				idBuffer.rewind();
				GL15.glDeleteBuffers(idBuffer);

				m_vertexBufferId = 0;
			}

			if (m_textureId != 0) {
				idBuffer.rewind();
				idBuffer.put(m_textureId);

				idBuffer.rewind();
				GL11.glDeleteTextures(idBuffer);

				m_textureId = 0;
			}
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}

	}

	public void initialize() {
		// create a glyph vector for all chars
		FontRenderContext ctx = new FontRenderContext(null, m_antialias, true);
		RangeCharacterIterator charIter =
			new RangeCharacterIterator(m_numChars);
		GlyphVector glyphs = m_awtFont.createGlyphVector(ctx, charIter);

		// add the missing char to the glyph vector
		int[] codes = new int[m_numChars + 1];
		glyphs.getGlyphCodes(0, m_numChars, codes);
		codes[codes.length - 1] = m_awtFont.getMissingGlyphCode();
		glyphs = m_awtFont.createGlyphVector(ctx, codes);

		// create vector font
		VectorChar[] vectorChars = new VectorChar[glyphs.getNumGlyphs()];
		int totalVertexCount = createVectorData(glyphs, vectorChars);
		m_vertexBufferId = createVertexBuffer(vectorChars, totalVertexCount);
		m_vectorListBaseId = createDisplayLists(vectorChars);

		createTextureData(glyphs);
	}

	public void render(String i_string) {

		if (i_string == null)
			throw new NullPointerException("i_string must not be null");

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_vertexBufferId);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		try {
			GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);

			IntBuffer listIdBuffer =
				Draw3DCache.getIntBuffer(i_string.length());

			try {
				listIdBuffer.rewind();
				for (int i = 0; i < i_string.length(); i++) {
					char c = i_string.charAt(i);
					if (c >= 0 && c < m_numChars)
						listIdBuffer.put(m_vectorListBaseId + c - 2);
					else
						listIdBuffer.put(m_vectorListBaseId + m_numChars);
				}

				listIdBuffer.limit(listIdBuffer.position());
				listIdBuffer.rewind();

				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
				GL11.glCallLists(listIdBuffer);
			} finally {
				Draw3DCache.returnIntBuffer(listIdBuffer);
			}
		} finally {
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		}
	}

}
