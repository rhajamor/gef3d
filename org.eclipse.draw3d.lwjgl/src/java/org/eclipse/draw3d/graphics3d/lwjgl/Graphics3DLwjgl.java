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
package org.eclipse.draw3d.graphics3d.lwjgl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.graphics.optimizer.OptimizingGraphics;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass;
import org.eclipse.draw3d.graphics3d.AbstractGraphics3DDraw;
import org.eclipse.draw3d.graphics3d.CompoundExecutableGraphics2D;
import org.eclipse.draw3d.graphics3d.DisplayListManager;
import org.eclipse.draw3d.graphics3d.ExecutableGraphics2D;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDescriptor;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBuffers;
import org.eclipse.draw3d.graphics3d.lwjgl.font.LwjglFontManager;
import org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableGradientQuads;
import org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableImages;
import org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableLines;
import org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutablePolygons;
import org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutablePolylines;
import org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableQuads;
import org.eclipse.draw3d.graphics3d.lwjgl.offscreen.LwjglOffscreenBackBuffers;
import org.eclipse.draw3d.graphics3d.lwjgl.offscreen.LwjglOffscreenBufferConfig;
import org.eclipse.draw3d.graphics3d.lwjgl.offscreen.LwjglOffscreenBuffersFbo;
import org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglTextureFbo;
import org.eclipse.draw3d.graphics3d.lwjgl.texture.LwjglTextureManager;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.draw3d.util.LogGraphics;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

/**
 * Graphics3DLwjgl is the implementor of the Graphics3D interface for the LWJGL
 * renderer. <h3>OpenGL Notes</h3> glMultMatrix(FloatBuffer) is replaced with
 * setPosition(Object).
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since 06.12.2008
 */
public class Graphics3DLwjgl extends AbstractGraphics3DDraw implements
		Graphics3D {
	/**
	 * Indicates which attribute groups must be saved prior to using this.
	 */
	private static final int ATTRIB_MASK =
		GL11.GL_LIGHTING_BIT | GL11.GL_CURRENT_BIT | GL11.GL_TRANSFORM_BIT
			| GL11.GL_LINE_BIT | GL11.GL_POLYGON_BIT | GL11.GL_TEXTURE_BIT
			| GL11.GL_VIEWPORT_BIT | GL11.GL_DEPTH_BUFFER_BIT
			| GL11.GL_COLOR_BUFFER_BIT | GL13.GL_MULTISAMPLE_BIT
			| GL11.GL_ENABLE_BIT;

	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(Graphics3DLwjgl.class.getName());

	/**
	 * Descriptor of this instance.
	 */
	protected Graphics3DDescriptor descriptor;

	/** Cashed hash code */
	final int hashCode;

	/**
	 * The GL context of this instance.
	 */
	public GLCanvas m_context = null;

	private LwjglFontManager m_fontManager;

	private boolean m_log2D;

	/**
	 * The texture manager handles OpenGL texture as GL's mechanism to render 2D
	 * content. It is lazily created in
	 * {@link #activateGraphics2D(Object, int, int, int, Color)}.
	 */
	private LwjglTextureManager m_textureManager = null;

	private DisplayListManager m_displayListManager = null;

	Properties properties = new Properties();

	/**
	 * Standard constructor.
	 */
	public Graphics3DLwjgl() {
		super();

		hashCode = super.hashCode();

		if (log.isLoggable(Level.INFO)) {
			log.info("Graphics3DLwjgl constructor called"); //$NON-NLS-1$
		}
	}

	private Graphics m_activeGraphics;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#activateGraphics2D(Object,
	 *      int, int)
	 */
	public Graphics activateGraphics2D(Object i_key, IPosition3D i_position,
		int i_width, int i_height) {

		log.info("activating 2D graphics");

		m_activeGraphics = new OptimizingGraphics();
		// graphics.disableClipping();
		//
		// String fontAntialias = getProperty(PROP_FONT_AA);
		// if (fontAntialias != null)
		// graphics.setOverrideTextAntialias(Boolean.valueOf(fontAntialias));
		// else
		// graphics.setOverrideTextAntialias(null);
		//
		// // save all state variables that may be changed by the graphics
		// object
		// GL11.glPushAttrib(ATTRIB_MASK);
		//
		// GL11.glMatrixMode(GL11.GL_MODELVIEW);
		// GL11.glPushMatrix();
		//
		// Matrix4f m = Draw3DCache.getMatrix4f();
		// Vector3f t = Draw3DCache.getVector3f();
		// try {
		// t.set(0, 0, 0.001f);
		// m.set(i_position.getRotationLocationMatrix());
		// Math3D.translate(m, t, m);
		// setMatrix(m);
		// } finally {
		// Draw3DCache.returnVector3f(t);
		// Draw3DCache.returnMatrix4f(m);
		// }
		//
		// GL11.glShadeModel(GL11.GL_FLAT);
		// GL11.glDisable(GL11.GL_DEPTH_TEST);
		// // GL11.glDepthFunc(GL11.GL_LEQUAL);
		// // GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		// GL11.glDisable(GL11.GL_CULL_FACE);
		// GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		// GL11.glEnable(GL11.GL_LINE_SMOOTH);

		if (m_log2D)
			return new LogGraphics(m_activeGraphics);

		return m_activeGraphics;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#deactivateGraphics2D()
	 */
	public ExecutableGraphics2D deactivateGraphics2D() {

		log.info("deactivating 2D graphics");

		if (m_activeGraphics instanceof OptimizingGraphics) {
			OptimizingGraphics og = (OptimizingGraphics) m_activeGraphics;
			List<PrimitiveSet> primiveSets = og.getPrimiveSets();
			List<ExecutableGraphics2D> executables =
				new LinkedList<ExecutableGraphics2D>();

			for (PrimitiveSet set : primiveSets) {
				PrimitiveClass clazz = set.getPrimitiveClass();
				if (clazz.isPolygon()) {
					executables.add(new LwjglExecutablePolygons(set));
				} else if (clazz.isQuad()) {
					if (clazz.isGradient())
						executables.add(new LwjglExecutableGradientQuads(set));
					else if (clazz.isImage())
						executables.add(new LwjglExecutableImages(set));
					else
						executables.add(new LwjglExecutableQuads(set));
				} else if (clazz.isPolyline()) {
					executables.add(new LwjglExecutablePolylines(set));
				} else if (clazz.isLine()) {
					executables.add(new LwjglExecutableLines(set));
				} else if (clazz.isText()) {

				} else {
					throw new AssertionError("unknown primitive class: "
						+ clazz);
				}
			}

			return new CompoundExecutableGraphics2D(executables) {
				/**
				 * {@inheritDoc}
				 * 
				 * @see org.eclipse.draw3d.graphics3d.CompoundExecutableGraphics2D#execute(org.eclipse.draw3d.graphics3d.Graphics3D)
				 */
				@Override
				public void execute(Graphics3D i_g3d) {
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					super.execute(i_g3d);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
				}
			};
		}

		return null;

		// GL11.glPopMatrix();
		// GL11.glPopAttrib();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#dispose()
	 */
	public void dispose() {
		try {
			if (m_textureManager != null)
				m_textureManager.dispose();
			if (m_fontManager != null)
				m_fontManager.dispose();
			if (m_displayListManager != null)
				m_displayListManager.dispose();
		} catch (Exception ex) {
			log.warning("Error disposing texture manager: " + ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getDescriptor()
	 */
	public Graphics3DDescriptor getDescriptor() {
		return descriptor;
	}

	private LwjglFontManager getFontManager() {

		if (m_fontManager == null)
			m_fontManager = new LwjglFontManager(getDisplayListManager());

		return m_fontManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getGraphics3DId()
	 */
	public int getGraphics2DId(Object i_key) {

		LwjglTextureManager textureManager = getTextureManager();
		return textureManager.getTextureId(i_key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getGraphics3DOffscreenBuffer(int,
	 *      int, org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig)
	 */
	public Graphics3DOffscreenBuffers getGraphics3DOffscreenBuffer(
		int i_height, int i_width,
		Graphics3DOffscreenBufferConfig i_bufferConfig) {
		if (LwjglTextureFbo.isSuppported()) {
			return new LwjglOffscreenBuffersFbo(i_height, i_width,
				i_bufferConfig);
		} else {
			return new LwjglOffscreenBackBuffers(i_height, i_width,
				i_bufferConfig);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getGraphics3DOffscreenBufferConfig(int,
	 *      int[])
	 */
	public Graphics3DOffscreenBufferConfig getGraphics3DOffscreenBufferConfig(
		int i_buffers, int... i_args) {
		return new LwjglOffscreenBufferConfig(this, i_buffers, i_args);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getID()
	 */
	public String getID() {
		return Graphics3DLwjgl.class.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#getPlatform()
	 */
	public int getPlatform() {
		return org.lwjgl.LWJGLUtil.getPlatform();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getProperty(java.lang.String)
	 */
	public String getProperty(String i_key) {
		return properties.getProperty(i_key);
	}

	private LwjglTextureManager getTextureManager() {

		if (m_textureManager == null)
			m_textureManager =
				new LwjglTextureManager(m_context, getDisplayListManager(),
					getFontManager());
		else if (m_textureManager.isDisposed())
			throw new IllegalStateException("TextureManager is disposed");

		return m_textureManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getDisplayListManager()
	 */
	public DisplayListManager getDisplayListManager() {

		if (m_displayListManager == null)
			m_displayListManager = new DisplayListManager(this);

		return m_displayListManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glBegin(int)
	 */
	public void glBegin(int mode) {
		org.lwjgl.opengl.GL11.glBegin(mode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glBindTexture(int, int)
	 */
	public void glBindTexture(int target, int texture) {
		org.lwjgl.opengl.GL11.glBindTexture(target, texture);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glBlendFunc(int, int)
	 */
	public void glBlendFunc(int sfactor, int dfactor) {
		org.lwjgl.opengl.GL11.glBlendFunc(sfactor, dfactor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glCallList(int)
	 */
	public void glCallList(int list) {
		org.lwjgl.opengl.GL11.glCallList(list);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glClear(int)
	 */
	public void glClear(int mask) {
		org.lwjgl.opengl.GL11.glClear(mask);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glClearColor(float,
	 *      float, float, float)
	 */
	public void glClearColor(float red, float green, float blue, float alpha) {
		org.lwjgl.opengl.GL11.glClearColor(red, green, blue, alpha);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glClearDepth(double)
	 */
	public void glClearDepth(double depth) {
		org.lwjgl.opengl.GL11.glClearDepth(depth);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glColor3f(float, float,
	 *      float)
	 */
	public void glColor3f(float red, float green, float blue) {
		org.lwjgl.opengl.GL11.glColor3f(red, green, blue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glColor4f(float, float,
	 *      float, float)
	 */
	public void glColor4f(float red, float green, float blue, float alpha) {
		org.lwjgl.opengl.GL11.glColor4f(red, green, blue, alpha);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glDeleteLists(int, int)
	 */
	public void glDeleteLists(int list, int range) {
		org.lwjgl.opengl.GL11.glDeleteLists(list, range);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glDisable(int)
	 */
	public void glDisable(int cap) {
		org.lwjgl.opengl.GL11.glDisable(cap);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glEnable(int)
	 */
	public void glEnable(int cap) {
		org.lwjgl.opengl.GL11.glEnable(cap);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glEnd()
	 */
	public void glEnd() {
		org.lwjgl.opengl.GL11.glEnd();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glEndList()
	 */
	public void glEndList() {
		org.lwjgl.opengl.GL11.glEndList();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glFinish()
	 */
	public void glFinish() {
		org.lwjgl.opengl.GL11.glFinish();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glFlush()
	 */
	public void glFlush() {
		org.lwjgl.opengl.GL11.glFlush();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGenLists(int)
	 */
	public int glGenLists(int range) {
		return org.lwjgl.opengl.GL11.glGenLists(range);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGetFloat(int,
	 *      java.nio.FloatBuffer)
	 */
	public void glGetFloat(int pname, FloatBuffer params) {
		org.lwjgl.opengl.GL11.glGetFloat(pname, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGetInteger(int,
	 *      java.nio.IntBuffer)
	 */
	public void glGetInteger(int pname, IntBuffer params) {
		org.lwjgl.opengl.GL11.glGetInteger(pname, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGetString(int)
	 */
	public String glGetString(int name) {
		return org.lwjgl.opengl.GL11.glGetString(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glHint(int, int)
	 */
	public void glHint(int target, int mode) {
		org.lwjgl.opengl.GL11.glHint(target, mode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glIsEnabled(int)
	 */
	public boolean glIsEnabled(int cap) {
		return org.lwjgl.opengl.GL11.glIsEnabled(cap);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glLineStipple(int,
	 *      short)
	 */
	public void glLineStipple(int factor, short pattern) {
		org.lwjgl.opengl.GL11.glLineStipple(factor, pattern);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glLineWidth(float)
	 */
	public void glLineWidth(float width) {
		org.lwjgl.opengl.GL11.glLineWidth(width);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glLoadIdentity()
	 */
	public void glLoadIdentity() {
		org.lwjgl.opengl.GL11.glLoadIdentity();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glMatrixMode(int)
	 */
	public void glMatrixMode(int mode) {
		org.lwjgl.opengl.GL11.glMatrixMode(mode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glNewList(int, int)
	 */
	public void glNewList(int list, int mode) {
		org.lwjgl.opengl.GL11.glNewList(list, mode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glNormal3f(float,
	 *      float, float)
	 */
	public void glNormal3f(float nx, float ny, float nz) {
		org.lwjgl.opengl.GL11.glNormal3f(nx, ny, nz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glNormal3f(int, int,
	 *      int)
	 */
	public void glNormal3f(int nx, int ny, int nz) {
		org.lwjgl.opengl.GL11.glNormal3f(nx, ny, nz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPixelStorei(int, int)
	 */
	public void glPixelStorei(int pname, int param) {
		org.lwjgl.opengl.GL11.glPixelStorei(pname, param);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPointSize(float)
	 */
	public void glPointSize(float size) {
		org.lwjgl.opengl.GL11.glPointSize(size);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPolygonMode(int, int)
	 */
	public void glPolygonMode(int face, int mode) {
		org.lwjgl.opengl.GL11.glPolygonMode(face, mode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPopMatrix()
	 */
	public void glPopMatrix() {
		org.lwjgl.opengl.GL11.glPopMatrix();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPushMatrix()
	 */
	public void glPushMatrix() {
		org.lwjgl.opengl.GL11.glPushMatrix();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glShadeModel(int)
	 */
	public void glShadeModel(int mode) {
		org.lwjgl.opengl.GL11.glShadeModel(mode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glTexCoord2f(float,
	 *      float)
	 */
	public void glTexCoord2f(float s, float t) {
		org.lwjgl.opengl.GL11.glTexCoord2f(s, t);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glTexEnvi(int, int,
	 *      int)
	 */
	public void glTexEnvi(int target, int pname, int param) {
		org.lwjgl.opengl.GL11.glTexEnvi(target, pname, param);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glTranslatef(float,
	 *      float, infloatt)
	 */
	public void glTranslatef(float x, float y, float z) {
		org.lwjgl.opengl.GL11.glTranslatef(x, y, z);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluLookAt(float, float,
	 *      float, float, float, float, float, float, float)
	 */
	public void gluLookAt(float eyex, float eyey, float eyez, float centerx,
		float centery, float centerz, float upx, float upy, float upz) {
		org.lwjgl.util.glu.GLU.gluLookAt(eyex, eyey, eyez, centerx, centery,
			centerz, upx, upy, upz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluOrtho2D(int, int,
	 *      int, int)
	 */
	public void gluOrtho2D(int left, int right, int bottom, int top) {
		org.lwjgl.util.glu.GLU.gluOrtho2D(left, right, bottom, top);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluPerspective(int,
	 *      float, int, int)
	 */
	public void gluPerspective(int fovy, float aspect, int zNear, int zFar) {
		org.lwjgl.util.glu.GLU.gluPerspective(fovy, aspect, zNear, zFar);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluUnProject(int, int,
	 *      float, java.nio.FloatBuffer, java.nio.FloatBuffer,
	 *      java.nio.IntBuffer, java.nio.FloatBuffer)
	 */
	public void gluUnProject(int winx, int winy, float winz,
		FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport,
		FloatBuffer obj_pos) {
		org.lwjgl.util.glu.GLU.gluUnProject(winx, winy, winz, modelMatrix,
			projMatrix, viewport, obj_pos);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glVertex2f(float,
	 *      float)
	 */
	public void glVertex2f(float x, float y) {
		org.lwjgl.opengl.GL11.glVertex2f(x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glVertex3f(float,
	 *      float, float)
	 */
	public void glVertex3f(float x, float y, float z) {
		org.lwjgl.opengl.GL11.glVertex3f(x, y, z);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glViewport(int, int,
	 *      int, int)
	 */
	public void glViewport(int x, int y, int width, int height) {
		org.lwjgl.opengl.GL11.glViewport(x, y, width, height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#hasGraphics2D(java.lang.Object)
	 */
	public boolean hasGraphics2D(Object i_key) {

		LwjglTextureManager textureManager = getTextureManager();
		return textureManager.contains(i_key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setDescriptor(org.eclipse.draw3d.graphics3d.Graphics3DDescriptor)
	 */
	public void setDescriptor(Graphics3DDescriptor i_graphics3DDescriptor) {
		descriptor = i_graphics3DDescriptor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setGLCanvas(org.eclipse.swt.opengl.GLCanvas)
	 */
	public void setGLCanvas(GLCanvas i_canvas) {

		if (m_textureManager != null) {
			throw new IllegalStateException(
				"Texture manager already initialized, cannot set new canvas");
		}

		m_context = i_canvas;

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setLog2D(boolean)
	 */
	public void setLog2D(boolean i_log2D) {

		m_log2D = i_log2D;
	}

	private void setMatrix(IMatrix4f i_matrix) {

		FloatBuffer buffer = Draw3DCache.getFloatBuffer(16);
		try {
			buffer.rewind();
			i_matrix.toBufferRowMajor(buffer);
			buffer.rewind();
			org.lwjgl.opengl.GL11.glMultMatrix(buffer);
		} finally {
			Draw3DCache.returnFloatBuffer(buffer);
		}
	}

	/**
	 * Sets the position.
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#setPosition(IPosition3D)
	 */
	public void setPosition(IPosition3D i_position) {

		setMatrix(i_position.getTransformationMatrix());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setProperty(String key, String value) {

		if (value != null)
			properties.setProperty(key, value);
		else
			properties.remove(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#useContext(Object)
	 */
	public void useContext(Object context) throws Graphics3DException {
		try {
			GLContext.useContext(context);
		} catch (LWJGLException ex) {
			throw new Graphics3DException(ex);
		}
	}
}
