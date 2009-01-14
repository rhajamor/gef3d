/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.graphics3d.Graphics3DRegistry;
import org.eclipse.draw3d.picking.ColorProvider;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * The render state encapsulates information about the current render pass and
 * also collects transparent objects that need to be rendered last.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 23.01.2008
 */
public class RenderContext {

	/**
	 * 
	 */
	private static final Comparator<TransparentObject> DEPTH_COMPARATOR = new Comparator<TransparentObject>() {

		public int compare(TransparentObject i_o1, TransparentObject i_o2) {

			float depth1 = i_o1.getTransparencyDepth();
			float depth2 = i_o2.getTransparencyDepth();
			// return -1 * Float.compare(depth1, depth2);
			return Float.compare(depth2, depth1);
		}
	};

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(RenderContext.class
			.getName());

	private static ThreadLocal<RenderContext> renderContext = new ThreadLocal<RenderContext>() {
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		@Override
		protected RenderContext initialValue() {

			return new RenderContext();
		}
	};

	/**
	 * Returns the render context specific to the current thread.
	 * 
	 * @return the current render context, which is never <code>null</code>
	 */
	public static RenderContext getContext() {

		return renderContext.get();
	}

	private ICamera m_camera;

	private ColorProvider m_colorProvider;

	private DisplayListManager m_displayListManager = null;

	private RenderMode m_mode;

	private final SortedSet<TransparentObject> m_transparentObjects;

	private final SortedSet<TransparentObject> m_superimposedObjects;

	private Graphics3D m_g3d = null;

	/**
	 * Creates a new render context.
	 */
	private RenderContext() {

		m_mode = RenderMode.PAINT;

		m_transparentObjects = new TreeSet<TransparentObject>(DEPTH_COMPARATOR);
		m_superimposedObjects = new TreeSet<TransparentObject>(DEPTH_COMPARATOR);

	}

	/**
	 * Adds the given transparent object to be rendered later.
	 * 
	 * @param i_transparentObject the transparent object to add
	 * @throws NullPointerException if the given object is <code>null</code>
	 */
	public void addTransparentObject(TransparentObject i_transparentObject) {

		if (i_transparentObject == null)
			throw new NullPointerException(
					"i_transparentObject must not be null");

		m_transparentObjects.add(i_transparentObject);
	}

	/**
	 * Adds the given transparent object to be superimposed. These objects are
	 * rendered <b>after</b> all other objects have been rendered and
	 * <b>after</b> the depth buffer has been cleared.
	 * 
	 * @param i_transparentObject the transparent object to add
	 * @throws NullPointerException if the given object is <code>null</code>
	 */
	public void addSuperimposedObject(TransparentObject i_transparentObject) {

		if (i_transparentObject == null)
			throw new NullPointerException(
					"i_transparentObject must not be null");

		m_superimposedObjects.add(i_transparentObject);
	}

	/**
	 * Sets the render mode to <code>null</code> and clears the transparent
	 * objects.
	 */
	public void clear() {

		m_mode = RenderMode.PAINT;
		m_transparentObjects.clear();
		m_superimposedObjects.clear();
		m_displayListManager = null;
		m_camera = null;
	}

	/**
	 * Returns the current camera.
	 * 
	 * @return the current camera
	 * @throws IllegalStateException if the camera is not set
	 */
	public ICamera getCamera() {

		if (m_camera == null)
			throw new IllegalStateException("camera is not set");

		return m_camera;
	}

	/**
	 * Returns the color for the given figure.
	 * 
	 * @param i_figure the figure
	 * @return the color for the given figure
	 * @throws IllegalStateException if no color provider is set or if the
	 *             current render mode is not {@link RenderMode#COLOR}
	 * @see ColorProvider#getColor(IFigure3D)
	 */
	public int getColor(IFigure3D i_figure) {

		if (m_colorProvider == null)
			throw new IllegalStateException("no color provider set");

		if (!getMode().isColor())
			throw new IllegalStateException(
					"can't provide color when not in color mode");

		return m_colorProvider.getColor(i_figure);
	}

	/**
	 * Returns the display list manager of this render context.
	 * 
	 * @return the display list manager
	 * @throws IllegalStateException if no display list manager is set
	 */
	public DisplayListManager getDisplayListManager() {

		if (m_displayListManager == null)
			throw new IllegalStateException("display list manager was not set");

		return m_displayListManager;
	}

	/**
	 * Returns the render mode.
	 * 
	 * @return the render mode
	 * @throws IllegalStateException if the render mode has not been set
	 */
	public RenderMode getMode() {

		if (m_mode == null)
			throw new IllegalStateException("current render mode was not set");

		return m_mode;
	}

	/**
	 * Renders all transparent objects.
	 */
	public void renderTransparency() {

		for (TransparentObject transparentObject : m_transparentObjects) {
			transparentObject.renderTransparent();
		}

		Graphics3D g3d = RenderContext.getContext().getGraphics3D();
		// depth test abschalten
		g3d.glDisable(Graphics3DDraw.GL_DEPTH_TEST);
		// evtl. face culling ausschalten
		// GL11.glDisable(GL11.GL_CULL_FACE);

		try {
			for (TransparentObject transparentObject : m_superimposedObjects) {
				transparentObject.renderTransparent();
			}
		} finally {

			g3d.glEnable(Graphics3DDraw.GL_DEPTH_TEST);
		}

	}

	/**
	 * Sets the camera of this render context.
	 * 
	 * @param i_camera the camera
	 */
	public void setCamera(ICamera i_camera) {

		if (i_camera == null)
			throw new NullPointerException("i_camera must not be null");

		m_camera = i_camera;
	}

	/**
	 * Sets the color provider for color rendering mode.
	 * 
	 * @param i_colorProvider the color provider
	 */
	public void setColorProvider(ColorProvider i_colorProvider) {

		m_colorProvider = i_colorProvider;
	}

	/**
	 * Sets the display list manager.
	 * 
	 * @param i_displayListManager the display list manager
	 * @throws NullPointerException if the given display list manager is
	 *             <code>null</code>
	 */
	public void setDisplayListManager(DisplayListManager i_displayListManager) {

		if (i_displayListManager == null)
			throw new NullPointerException(
					"i_displayListManager must not be null");

		m_displayListManager = i_displayListManager;
	}

	/**
	 * Sets the render mode.
	 * 
	 * @param i_mode the render mode, which must not be
	 *            {@link NullPointerException}
	 * @throws NullPointerException if the given render mode is
	 *             <code>null</code>
	 */
	public void setMode(RenderMode i_mode) {

		if (i_mode == null)
			throw new NullPointerException("i_mode must not be null");

		m_mode = i_mode;
	}

	/**
	 * Returns the Graphics3D instance which shall be used for rendering in this
	 * context.
	 * 
	 * @return the graphics3D instance
	 */
	public Graphics3D getGraphics3D() {
		return this.m_g3d;
	}

	/**
	 * Sets the default rendering implementation. Should be used directly on
	 * startup to initialize the renderer. The renderer can be exchanged
	 * whenever it makes sense by creating (see Graphics3DRegistry) another
	 * instance and setting it to this context.
	 * 
	 * @param i_context The rendering context.
	 */
	public void setDefaultRenderer(GLCanvas i_context) {
		Graphics3D g3d = Graphics3DRegistry.createGraphics3D(
				Graphics3DRegistry.G3D_IMPL_DEFAULT, i_context);
		this.m_g3d = g3d;
	}

	/**
	 * Sets another Graphics3D instance for further rendering within this
	 * context. The previous GRaphics3D instance has to be saved externally if
	 * re-usage is planned, the render context will keep any reference to it.
	 * 
	 * @param i_g3d The new Graphics3D instance to set.
	 */
	public void setGraphics3D(Graphics3D i_g3d) {
		this.m_g3d = i_g3d;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("RenderState[mode: ");
		builder.append(m_mode);
		builder.append(", transparent objects: ");
		builder.append(m_transparentObjects.size());
		builder.append("]");

		return builder.toString();
	}
}
