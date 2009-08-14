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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * The render state encapsulates information about the current render pass and
 * also collects transparent objects that need to be rendered last.
 * <p>
 * Note on transparent and superimposed object: An object becomes transparent by
 * firstly setting its color alpha to a value less 255 and by secondly adding it
 * to the list of transparent objects via
 * {@link #addTransparentObject(TransparentObject)}. The overall strategy is to
 * firstly render all opaque objects, correctly sorted and drawn by OpenGL.
 * Secondly, all transparent objects are drawn (ordered by their distance to the
 * camera) in order to enable real transparency which OpenGL does not support
 * directly. A problem may occure when objects are registered as transparent
 * objects while the transparent objects are already rendered here. This may
 * occur if an transparent object is composed of other transparent objects, and
 * recursively calls their render method (which only then adds the nested
 * objecdt to the list of transparent objects here). If this case the newly
 * added transparent (nested) object is rendered directly after the object was
 * rendered in which this object is nested. The problem is that the order may be
 * corrupt, that is the nested object should have been rendered before its
 * container. This case cannot be corrected here. To prevent this, try to make
 * rendering of objects as flat as possible. Especially shapes tend to be
 * nested. In this case, simply try to prevent calling a nested shapes render
 * method from within a container shapes doRender method.
 * </p>
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

	private final Comparator<TransparentObject> m_depthComparator =
		new DepthComparator(this);

	private GLCanvas m_Canvas;

	private final Map<Graphics3D, DisplayListManager> m_displayListManagers;

	private Graphics3D m_g3d = null;

	private IScene m_scene;

	private List<TransparentObject> m_superimposedObjects;

	private List<TransparentObject> m_transparentObjects;

	private boolean isRendering = false;

	/**
	 * Creates a new render context. The context is created by the
	 * {@link LightweightSystem3D}.
	 */
	public RenderContext() {

		m_transparentObjects = new ArrayList<TransparentObject>();
		m_superimposedObjects = new ArrayList<TransparentObject>();

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

		synchronized (m_superimposedObjects) {

			int index =
				Collections.binarySearch(m_superimposedObjects,
					i_transparentObject, m_depthComparator);

			if (index < 0)
				index = -index - 1;

			m_superimposedObjects.add(index, i_transparentObject);
		}
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

		synchronized (m_transparentObjects) {
			int index =
				Collections.binarySearch(m_transparentObjects,
					i_transparentObject, m_depthComparator);

			if (index < 0)
				index = -index - 1;

			m_transparentObjects.add(index, i_transparentObject);
		}
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
	public synchronized void renderTransparency() {
		isRendering = true;
		try {

			Graphics3D g3d = getGraphics3D();
			// disable depth test
			g3d.glDisable(Graphics3DDraw.GL_DEPTH_TEST);

			try {
				doRenderTransparentObjects();
				doRenderSuperImposedObjects();
				
			} finally {
				g3d.glEnable(Graphics3DDraw.GL_DEPTH_TEST);
			}
		} finally {
			isRendering = false;
		}

	}


	/**
	 * Renders transparent objects. These objects are rendered by
	 * iterating over a copy of the transparency list. The original
	 * list is cleared. When a transparent object is rendered, it may add new
	 * transparent objects, that is the children of a transparent container may
	 * be transparent themselves. Adding transparent nested objects causes the
	 * original list, which was cleared here at the beginning, to be non empty
	 * again. If this happens, the new non empty list is rendered before we 
	 * continue with the nest sibling.
	 * 
	 */
	private void doRenderTransparentObjects() {
		List<TransparentObject> renderList = m_transparentObjects;
		m_transparentObjects = new ArrayList<TransparentObject>();

		for (TransparentObject transparentObject : renderList) {
			transparentObject.renderTransparent(this);
			if (!m_transparentObjects.isEmpty()) {
				doRenderTransparentObjects();
			}
		}
	}
	
	/**
	 * Renders superimposed objects.
	 * @see #doRenderTransparentObjects() 
	 */
	private void doRenderSuperImposedObjects() {
		List<TransparentObject> renderList = m_superimposedObjects;
		m_superimposedObjects = new ArrayList<TransparentObject>();

		for (TransparentObject transparentObject : renderList) {
			transparentObject.renderTransparent(this);
			if (!m_superimposedObjects.isEmpty()) {
				doRenderTransparentObjects();
			}
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
