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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
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

	class DepthComparator implements Comparator<TransparentObject> {

		RenderContext renderContext;

		/**
		 * @param i_renderContext
		 */
		public DepthComparator(RenderContext i_renderContext) {
			super();
			renderContext = i_renderContext;
		}

		public int compare(TransparentObject i_o1, TransparentObject i_o2) {

			float depth1 = i_o1.getTransparencyDepth(renderContext);
			float depth2 = i_o2.getTransparencyDepth(renderContext);
			// return -1 * Float.compare(depth1, depth2);
			return Float.compare(depth2, depth1);
		}
	}

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(RenderContext.class.getName());

	private final Comparator<TransparentObject> depthComparator =
		new DepthComparator(this);

	private GLCanvas m_Canvas;

	private final Map<Graphics3D, DisplayListManager> m_displayListManagers;

	private Graphics3D m_g3d = null;

	private IScene m_scene;

	private final SortedSet<TransparentObject> m_superimposedObjects;

	private final SortedSet<TransparentObject> m_transparentObjects;

	/**
	 * Creates a new render context. The context is created by the
	 * {@link LightweightSystem3D}.
	 */
	public RenderContext() {

		m_transparentObjects = new TreeSet<TransparentObject>(depthComparator);
		m_superimposedObjects = new TreeSet<TransparentObject>(depthComparator);

		m_displayListManagers = new HashMap<Graphics3D, DisplayListManager>();
	}

	/**
	 * 
	 */
	public void activate() {
		m_Canvas.setCurrent();
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
	 * Clears the transparent objects.
	 */
	public void clear() {

		m_transparentObjects.clear();
		m_superimposedObjects.clear();
		// m_displayListManagers.clear();
		m_scene = null;
	}

	/**
	 * Clears the display manager for the current g3d instance.
	 */
	public void clearDisplayManager() {
		if (getGraphics3D() == null) {
			throw new IllegalStateException("no graphics3D intance set yet");
		}
		m_displayListManagers.remove(getGraphics3D());
	}

	/**
	 * Disposes all {@link DisplayListManager}, clears display manager map, and
	 * disposes the current {@link Graphics3D} instance. This method is called
	 * by the {@link LightweightSystem3D} when the widget is disposed.
	 */
	public synchronized void dispose() {

		for (DisplayListManager displayListManager : m_displayListManagers
			.values()) {
			try {
				displayListManager.dispose();
			} catch (Exception ex) {
				log.warning("Error disposing dipslay list manager: " + ex);
			}
		}
		try {
			m_displayListManagers.clear();
		} catch (Exception ex) {
			log.warning("Error clearing dispy list manager map: " + ex);
		}
		try {
			m_g3d.dispose();
		} catch (Exception ex) {
			log.warning("Error disposing current graphics 3D instance: " + ex);
		}
	}

	/**
	 * Returns the display list manager of this render context.
	 * 
	 * @return the display list manager
	 * @throws IllegalStateException if no display list manager is set
	 */
	public DisplayListManager getDisplayListManager() {

		if (getGraphics3D() == null) {
			throw new IllegalStateException("no graphcis 3D instance set yet");
		}

		DisplayListManager manager = m_displayListManagers.get(getGraphics3D());
		// if (manager == null)
		// throw new IllegalStateException(
		// "display list manager was not set for this graphics3D instane");
		if (manager == null) {
			manager = new DisplayListManager(getGraphics3D());
			m_displayListManagers.put(getGraphics3D(), manager);
		}

		return manager;
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
	 * Returns the scene
	 * 
	 * @return the scene
	 */
	public IScene getScene() {
		return m_scene;
	}

	/**
	 * Renders all transparent objects.
	 */
	public void renderTransparency() {

		for (TransparentObject transparentObject : m_transparentObjects) {
			transparentObject.renderTransparent(this);
		}

		Graphics3D g3d = getGraphics3D();
		// disable depth test
		g3d.glDisable(Graphics3DDraw.GL_DEPTH_TEST);
		// maybe disable face culling
		// GL11.glDisable(GL11.GL_CULL_FACE);

		try {
			for (TransparentObject transparentObject : m_superimposedObjects) {
				transparentObject.renderTransparent(this);
			}
		} finally {

			g3d.glEnable(Graphics3DDraw.GL_DEPTH_TEST);
		}

	}

	/**
	 * @param i_canvas
	 */
	public void setCanvas(GLCanvas i_canvas) {
		m_Canvas = i_canvas;

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
	 * Sets the scene
	 * 
	 * @param i_scene the scene
	 */
	public void setScene(IScene i_scene) {

		m_scene = i_scene;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("RenderContext[transparent objects: ");
		builder.append(m_transparentObjects.size());
		builder.append("]");

		return builder.toString();
	}
}
