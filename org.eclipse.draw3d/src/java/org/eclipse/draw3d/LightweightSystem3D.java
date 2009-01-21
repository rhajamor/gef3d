/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GraphicsSource;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.camera.FirstPersonCamera;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.camera.ICameraListener;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Transformable;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.geometryext.IHost3D;
import org.eclipse.draw3d.geometryext.IPosition3D;
import org.eclipse.draw3d.geometryext.Plane;
import org.eclipse.draw3d.geometryext.Position3D;
import org.eclipse.draw3d.geometryext.Position3DImpl;
import org.eclipse.draw3d.geometryext.IPosition3D.MatrixState;
import org.eclipse.draw3d.geometryext.IPosition3D.PositionHint;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.draw3d.util.CoordinateConverter;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Canvas;

/**
 * Lightweight system for 3D scene. Its root figure renders the camera, which is
 * created and set by GraphicalViewer ({@link GraphicalViewer3DImpl}. Creates
 * RootFigure (this is not the figure of the root edit part!. GLCanvas is
 * initialized in {@link de.feu.gef3d.ui.parts.GraphicalViewer3DImpl}. The
 * default resolution defined here is 190.5. With a screen resolution of 75 dpi
 * (i.e. 190,5 dots per cm), the 3D objects can be defined in "real" metrics.
 * 
 * @todo LWD3DRootFigure
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 08.11.2007
 */
public class LightweightSystem3D extends LightweightSystem implements
		ICameraListener, DisposeListener {

	/**
	 * The 3D root figure class does not extend Figure3D for design reasons.
	 * 
	 * @author Jens von Pilgrim
	 * @version $Revision$
	 * @since 08.11.2007
	 * @see $HeadURL:
	 *      https://gorgo.fernuni-hagen.de/OpenglGEF/trunk/org.eclipse.draw3d
	 *      /src/java/de/feu/draw3d/LightweightSystem3D.java $
	 */
	protected class RootFigure3D extends LightweightSystem.RootFigure implements
			IFigure3D {

		private static final String DL_AXES = "coordinate_axes";

		private final Figure3DHelper helper;

		// the position of the root is the universe.. the root is the universe
		private Position3DImpl universe;

		/**
		 * Creates and initializes a 3D new root figure.
		 */
		RootFigure3D() {
			universe = new Position3DImpl(this) {

				/** 
				 * {@inheritDoc}
				 * @see org.eclipse.draw3d.geometryext.AbstractPosition3D#invalidateMatrices()
				 */
				@Override
				public void invalidateMatrices() {
					// this is not possible!
				}

				/** 
				 * {@inheritDoc}
				 * @see org.eclipse.draw3d.geometryext.Position3DImpl#setLocation3D(org.eclipse.draw3d.geometry.IVector3f)
				 */
				@Override
				public void setLocation3D(IVector3f i_point) {
					

				}

				/** 
				 * {@inheritDoc}
				 * @see org.eclipse.draw3d.geometryext.Position3DImpl#setSize3D(org.eclipse.draw3d.geometry.IVector3f)
				 */
				@Override
				public void setSize3D(IVector3f i_size) {
				}

				/** 
				 * {@inheritDoc}
				 * @see org.eclipse.draw3d.geometryext.AbstractPosition3D#setRotation3D(org.eclipse.draw3d.geometry.IVector3f)
				 */
				@Override
				public void setRotation3D(IVector3f i_rotation) {
				}
				
				
				
				
			};
			universe.setSize3D(new Vector3fImpl(Float.MAX_VALUE,
					Float.MAX_VALUE, Float.MAX_VALUE));

			helper = new Figure3DHelper(new Figure3DFriend(this) {

				@Override
				public Font getLocalFont() {
					return RootFigure3D.this.getLocalFont();
				}

				@Override
				public boolean is2DContentDirty() {
					return false;
				}
			});
		}

		private void drawCoordinateAxes() {

			RenderContext renderContext = RenderContext.getContext();
			DisplayListManager displayListManager = renderContext
					.getDisplayListManager();

			if (!displayListManager.isDisplayList(DL_AXES))
				displayListManager.createDisplayList(DL_AXES, new Runnable() {
					public void run() {

						int length = 2000;
						int x = 0;
						int y = 0;
						int z = 0;

						Graphics3D g3d = RenderContext.getContext()
								.getGraphics3D();
						// TODO: optimize this, reduce the amount of
						g3d.glLineWidth(1f);

						// X axis (red)
						g3d.glColor3f(1, 0, 0);
						g3d.glBegin(Graphics3DDraw.GL_LINES);
						g3d.glVertex3f(x, y, z);
						g3d.glVertex3f(x + length, y, z);
						g3d.glEnd();

						// Y axis (green)
						g3d.glColor3f(0, 1, 0);
						g3d.glBegin(Graphics3DDraw.GL_LINES);
						g3d.glVertex3f(x, y, z);
						g3d.glVertex3f(x, y + length, z);
						g3d.glEnd();

						// Z axis (blue)
						g3d.glColor3f(0, 0, 1);
						g3d.glBegin(Graphics3DDraw.GL_LINES);
						g3d.glVertex3f(x, y, z);
						g3d.glVertex3f(x, y, z + length);
						g3d.glEnd();

						g3d.glLineStipple(1, (short) (1 + 4 + 16 + 64));
						g3d.glEnable(Graphics3DDraw.GL_LINE_STIPPLE);
						g3d.glColor3f(1, 0, 0);
						g3d.glBegin(Graphics3DDraw.GL_LINES);
						g3d.glVertex3f(x, y, z);
						g3d.glVertex3f(x - length, y, z);
						g3d.glEnd();
						g3d.glColor3f(0, 1, 0);
						g3d.glBegin(Graphics3DDraw.GL_LINES);
						g3d.glVertex3f(x, y, z);
						g3d.glVertex3f(x, y - length, z);
						g3d.glEnd();
						g3d.glColor3f(0, 0, 1);
						g3d.glBegin(Graphics3DDraw.GL_LINES);
						g3d.glVertex3f(x, y, z);
						g3d.glVertex3f(x, y, z - length);
						g3d.glEnd();
						g3d.glDisable(Graphics3DDraw.GL_LINE_STIPPLE);
					}
				});

			displayListManager.executeDisplayList(DL_AXES);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw2d.Figure#findFigureAt(int, int,
		 *      org.eclipse.draw2d.TreeSearch)
		 * @see Figure3DHelper#findFigureAt(int, int, TreeSearch)
		 */
		@Override
		public IFigure findFigureAt(int i_x, int i_y, TreeSearch i_search) {
			IFigure fig = helper.findFigureAt(i_x, i_y, i_search);

			if (fig == null) {
				return this;
			}
			return fig;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getAlpha()
		 */
		public int getAlpha() {
			return (byte) 255;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getAncestor3D()
		 */
		public IFigure3D getAncestor3D() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getBounds3D()
		 */
		public IBoundingBox getBounds3D() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure2DHost3D#getChildren2D()
		 */
		public List<IFigure> getChildren2D() {

			return helper.getChildren2D();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getChildren3D()
		 */
		public List<IFigure3D> getChildren3D() {

			return helper.getChildren3D();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure2DHost3D#getConnectionLayer(org.eclipse.draw3d.ConnectionLayerFactory)
		 */
		public ConnectionLayer getConnectionLayer(
				ConnectionLayerFactory i_clfactory) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getDescendants3D()
		 */
		public List<IFigure3D> getDescendants3D() {
			return helper.getDescendants3D();
		}

		/**
		 * Lightweight system's root figure as no location, this method returns
		 * null.
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getLocation3D()
		 */
		public IVector3f getLocation3D() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure2DHost3D#getLocation3D(org.eclipse.draw2d.geometry.Point)
		 */
		public IVector3f getLocation3D(Point i_point2D) {

			return CoordinateConverter.surfaceToWorld(i_point2D.x, i_point2D.y,
					this, null);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getLocationMatrix()
		 */
		public IMatrix4f getLocationMatrix() {
			return IMatrix4f.IDENTITY;
		}

		/**
		 * Returns always true. {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getMatrixState()
		 */
		public MatrixState getMatrixState() {
			return MatrixState.VALID;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getModelMatrix()
		 */
		public IMatrix4f getModelMatrix() {
			return IMatrix4f.IDENTITY;
		}

		/**
		 * Returns null. {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getPreferredSize3D()
		 */
		public IVector3f getPreferredSize3D() {
			return null;
		}

		/**
		 * Lightweight system's root figure as no rotation, this method returns
		 * null.
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getRotation3D()
		 */
		public IVector3f getRotation3D() {
			return null;
		}

		/**
		 * Lightweight system's root figure as no size, this method returns
		 * null.
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getSize3D()
		 */
		public IVector3f getSize3D() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#getSurfacePlane(float,
		 *      org.eclipse.draw3d.geometryext.Plane)
		 */
		public Plane getSurfacePlane(float i_z, Plane i_io_result) {

			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw2d.Figure#paint(org.eclipse.draw2d.Graphics)
		 */
		@Override
		public void paint(Graphics i_graphics) {

			RenderContext renderContext = RenderContext.getContext();
			renderContext.setDisplayListManager(m_displayListManager);
			renderContext.setCamera(m_camera);

			// ignore the incoming graphics object if it is not a dummy
			Graphics graphics = i_graphics;
			if (!(graphics instanceof DummyGraphics))
				graphics = new DummyGraphics();

			try {
				for (RenderListener listener : m_renderListeners)
					listener.renderPassStarted();

				// the root figure needs to paint itself first
				render();
				paintBorder(graphics);
				paintClientArea(graphics);
				postrender();
			} finally {
				for (RenderListener listener : m_renderListeners)
					listener.renderPassFinished();

				renderContext.clear();
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw2d.Figure#paintBorder(org.eclipse.draw2d.Graphics)
		 */
		@Override
		protected final void paintBorder(Graphics i_graphics) {

			helper.paintBorder(i_graphics);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw2d.Figure#paintChildren(org.eclipse.draw2d.Graphics)
		 */
		@Override
		protected final void paintChildren(Graphics i_graphics) {

			helper.paintChildren(i_graphics);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
		 */
		@Override
		protected void paintFigure(Graphics i_graphics) {

			helper.paintFigure(i_graphics);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#postrender()
		 */
		public void postrender() {

			RenderContext renderContext = RenderContext.getContext();
			renderContext.renderTransparency();
			Graphics3D g3d = RenderContext.getContext().getGraphics3D();

			g3d.glFlush();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.Figure3D#render()
		 */
		public void render() {

			// if (log.isLoggable(Level.INFO)) {
			// log.info("render "+ this); //$NON-NLS-1$
			// }

			m_canvas.setCurrent();
			RenderContext renderContext = RenderContext.getContext();
			Graphics3D g3d = RenderContext.getContext().getGraphics3D();

			if (renderContext.getMode().isPaint())
				g3d.glClearColor(m_clearColor[0], m_clearColor[1],
						m_clearColor[2], m_clearColor[3]);

			g3d.glClear(Graphics3DDraw.GL_COLOR_BUFFER_BIT
					| Graphics3DDraw.GL_DEPTH_BUFFER_BIT);

			m_camera.render();

			if (renderContext.getMode().isPaint() && m_drawAxes)
				drawCoordinateAxes();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw2d.Figure#revalidate()
		 */
		@Override
		public void revalidate() {
			super.revalidate();
			helper.revalidate();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#setAlpha(byte)
		 */
		public void setAlpha(int i_alpha) {
			// nothing to do
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw2d.Figure#setBounds(org.eclipse.draw2d.geometry.Rectangle)
		 */
		@Override
		public void setBounds(org.eclipse.draw2d.geometry.Rectangle i_rect) {

			super.setBounds(new org.eclipse.draw2d.geometry.Rectangle(0, 0,
					Integer.MAX_VALUE, Integer.MAX_VALUE));
		}

		/**
		 * Location of lightweight system's root figure cannot be set, this
		 * method does nothing.
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#setLocation3D(org.eclipse.draw3d.geometry.IVector3f)
		 */
		public void setLocation3D(IVector3f i_point) {
			// nothing to do
		}

		/**
		 * Ignored. {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#setPreferredSize3D(org.eclipse.draw3d.geometry.IVector3f)
		 */
		public void setPreferredSize3D(IVector3f i_preferredSize3D) {
			// nothing to do
		}

		/**
		 * Rotation of lightweight system's root figure cannot be set, this
		 * method does nothing.
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#setRotation3D(org.eclipse.draw3d.geometry.IVector3f)
		 */
		public void setRotation3D(IVector3f i_rotation) {
			// nothing to do
		}

		/**
		 * Size of lightweight system's root figure cannot be set, this method
		 * does nothing.
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#setSize3D(org.eclipse.draw3d.geometry.IVector3f)
		 */
		public void setSize3D(IVector3f i_size) {
			// nothing to do
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#transformFromParent(org.eclipse.draw3d.geometry.Transformable)
		 */
		public void transformFromParent(Transformable i_transformable) {
			// nothing to do
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#transformToAbsolute(org.eclipse.draw3d.geometry.Transformable)
		 */
		public void transformToAbsolute(Transformable i_transformable) {
			// nothing to do
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#transformToParent(org.eclipse.draw3d.geometry.Transformable)
		 */
		public void transformToParent(Transformable i_transformable) {
			// nothing to do
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.IFigure3D#transformToRelative(org.eclipse.draw3d.geometry.Transformable)
		 */
		public void transformToRelative(Transformable i_transformable) {
			// nothing to do
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.geometryext.IHost3D#getParentHost3D()
		 */
		public IHost3D getParentHost3D() {
			return getAncestor3D();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.geometryext.IHost3D#getPosition3D()
		 */
		public Position3D getPosition3D() {
			return universe;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.draw3d.geometryext.IHost3D#positionChanged(java.util.EnumSet,
		 *      org.eclipse.draw3d.geometry.IVector3f)
		 */
		public void positionChanged(EnumSet<PositionHint> i_hint,
				IVector3f i_delta) {
			if (log.isLoggable(Level.WARNING)) {
				log
						.warning("positionChanged on root figure, this must not happen"); //$NON-NLS-1$
			}

		}

	} // end of inner class RootFigure3D

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(LightweightSystem3D.class.getName());

	private ICamera m_camera;

	/**
	 * cached field (since super field is private)
	 */
	protected GLCanvas m_canvas;

	private final float[] m_clearColor = new float[] { 0.6f, 0.6f, 0.6f, 1 };

	private DisplayListManager m_displayListManager = new DisplayListManager();

	private boolean m_drawAxes;

	private final List<RenderListener> m_renderListeners = new LinkedList<RenderListener>();

	/**
	 * Adds the given render listener to this lightweight system.
	 * 
	 * @param i_listener the listener to add
	 * @throws NullPointerException if the given listener is <code>null</code>
	 */
	public void addRendererListener(RenderListener i_listener) {

		if (i_listener == null)
			throw new NullPointerException("i_listener must not be null");

		m_renderListeners.add(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.camera.ICameraListener#cameraChanged()
	 */
	public void cameraChanged() {
		// validation not necessary, only camera was moved
		// TODO change this in future versions for enabling distance aware
		// figures

		// if (log.isLoggable(Level.INFO)) {
		// log.info("Update after camera update"); //$NON-NLS-1$
		// }

		UpdateManager updateManager = getUpdateManager();

		updateManager.addDirtyRegion(getRootFigure(), 0, 0, 1000, 10000);
		updateManager.performUpdate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.LightweightSystem#controlResized()
	 */
	@Override
	protected void controlResized() {

		super.controlResized();
		updateViewport();
	}

	/**
	 * {@inheritDoc} Here, a 3D root figure is created.
	 * 
	 * @see org.eclipse.draw2d.LightweightSystem#createRootFigure()
	 */
	@Override
	protected RootFigure createRootFigure() {
		// TODO configure root figure 3D
		RootFigure f = new RootFigure3D();
		f.addNotify();
		f.setOpaque(true);
		f.setLayoutManager(new StackLayout());
		return f;
	}

	/**
	 * @return the camera
	 */
	public ICamera getCamera() {
		return m_camera;
	}

	/**
	 * {@inheritDoc} Creates a 3D update manager here, in super class, a default
	 * update manager is created on initialization.
	 * 
	 * @see org.eclipse.draw2d.LightweightSystem#init()
	 */
	@Override
	protected void init() {
		setUpdateManager(new PickingUpdateManager3D());

		// TODO: this should be aware of the configuration
		setCamera(new FirstPersonCamera());
		super.init();
	}

	/**
	 * Removes the given render listener from this lightweight system. If the
	 * given listener was not registered, nothing happens. If it was register
	 * more than once, the most recently registered listener is removed.
	 * 
	 * @param i_listener the listener to remove
	 * @throws NullPointerException if the given listener is <code>null</code>
	 */
	public void removeRendererListener(RenderListener i_listener) {

		if (i_listener == null)
			throw new NullPointerException("i_listener must not be null");

		synchronized (m_renderListeners) {
			int index = m_renderListeners.lastIndexOf(i_listener);
			if (index != -1)
				m_renderListeners.remove(index);
		}
	}

	/**
	 * Sets the background color of the lightweight system.
	 * 
	 * @param i_backgroundColor the background color
	 */
	public void setBackgroundColor(Color i_backgroundColor) {

		ColorConverter.toFloatArray(i_backgroundColor, 1, m_clearColor);
	}

	/**
	 * @param i_camera the camera to set
	 */
	public void setCamera(ICamera i_camera) {

		if (i_camera != m_camera) {
			if (m_camera != null)
				m_camera.removeCameraListener(this);

			m_camera = i_camera;
			m_camera.addCameraListener(this);

			UpdateManager updateManager = getUpdateManager();
			if (updateManager instanceof PickingUpdateManager3D) {
				PickingUpdateManager3D pickingManager = (PickingUpdateManager3D) updateManager;
				pickingManager.setCamera(m_camera);
			}

			updateViewport();
		}
	}

	/**
	 * {@inheritDoc} Keep a copy of the control here since super class defines
	 * the member private
	 * 
	 * @see org.eclipse.draw2d.LightweightSystem#setControl(org.eclipse.swt.widgets.Canvas)
	 */
	@Override
	public void setControl(Canvas i_canvas) {

		if (!(i_canvas instanceof GLCanvas))
			throw new IllegalArgumentException(
					"Control of LWS3D must be a GLCanvas, was " + i_canvas);

		GLCanvas glCanvas = (GLCanvas) i_canvas;

		DeferredUpdateManager3D updateManager = (DeferredUpdateManager3D) getUpdateManager();
		updateManager.setCanvas(glCanvas);

		m_canvas = glCanvas;

		super.setControl(i_canvas);

		updateManager.setGraphicsSource(new GraphicsSource() {

			Graphics graphics = new DummyGraphics();

			public void flushGraphics(
					org.eclipse.draw2d.geometry.Rectangle i_region) {
				// nothing to do
			}

			public Graphics getGraphics(
					org.eclipse.draw2d.geometry.Rectangle i_region) {
				return graphics;
			}
		});
	}

	/**
	 * Specifies whether coordinate axes should be drawn.
	 * 
	 * @param i_drawAxes <code>true</code> if coordinate axes should be drawn or
	 *            <code>false</code> otherwise
	 */
	public void setDrawAxes(boolean i_drawAxes) {

		m_drawAxes = i_drawAxes;
	}

	private void updateViewport() {

		if (m_canvas == null || m_camera == null)
			return;

		Rectangle bounds = m_canvas.getBounds();
		int width = bounds.width;
		int height = bounds.height;

		m_camera.setViewport(width, height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	public void widgetDisposed(DisposeEvent i_e) {

		if (m_displayListManager != null) {
			m_displayListManager.dispose();
			m_displayListManager = null;
		}

		Graphics3D g3d = RenderContext.getContext().getGraphics3D();
		g3d.dispose();
	}
}
