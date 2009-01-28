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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Helper class for {@link DispatchingConnectionLayer} (and other connection
 * layers used in GEF3D) dispatching the layers. The functionality was moved to
 * this helper class which acts as an delegator since several connection layer
 * classes use it and different class hierarchies must be used.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 2, 2008
 */
public class DispatchingConnectionLayerHelper {

	public static class ConnectionConstraints {
		public Object constaint;

		public int index;

		/**
		 * @param i_constaint
		 * @param i_index
		 */
		public ConnectionConstraints(Object i_constaint, int i_index) {
			constaint = i_constaint;
			index = i_index;
		}

	}

	public final static String CONSTRAINT_LAYER = "CONSTRAINT_LAYER";

	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(DispatchingConnectionLayerHelper.class.getName());

	/**
	 * layer cache, maps 3D figure to layer
	 */
	private final Map<IFigure, ConnectionLayer> distributedLayersMap = new HashMap<IFigure, ConnectionLayer>();

	// private final List<ConnectionLayer> distributedLayers = new
	// ArrayList<ConnectionLayer>(10000);

	private final ConnectionLayerFactory factory;

	private final ConnectionLayer host;

	private final Map<Connection, ConnectionConstraints> pendingConnections = new HashMap<Connection, ConnectionConstraints>();

	/**
	 * @param i_host
	 */
	public DispatchingConnectionLayerHelper(ConnectionLayer i_host,
			ConnectionLayerFactory i_factory) {
		host = i_host;
		factory = i_factory;
	}

	/**
	 * @return true, if helper could handle this kind of figure
	 * @see org.eclipse.draw2d.ConnectionLayer#add(org.eclipse.draw2d.IFigure,
	 *      java.lang.Object, int)
	 */
	public boolean add(IFigure i_figure, Object i_constraint, int i_index) {
		if (i_constraint != CONSTRAINT_LAYER && i_figure instanceof Connection
				&& !(i_figure instanceof IFigure3D)) {
			ConnectionLayer distributedLayer = findDistributedLayer((Connection) i_figure);
			if (distributedLayer != null) {
				// if (distributedLayer.getConnectionRouter()==null) {
				distributedLayer
						.setConnectionRouter(host.getConnectionRouter());
				// }
				distributedLayer.add(i_figure, i_constraint, i_index);
				// if it was added before:
				pendingConnections.remove(i_figure);
			} else {
				pendingConnections.put((Connection) i_figure,
						new ConnectionConstraints(i_constraint, i_index));

			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 */
	public void dispatchPendingConnections() {
		if (!pendingConnections.isEmpty()) {
			ArrayList<IFigure> dispatchedConnections = new ArrayList<IFigure>();

			// if (log.isLoggable(Level.INFO))
			//				log.info(StopWatch.start("Dispatching " //$NON-NLS-1$
			//						+ pendingConnections.size() + " pending connections")); //$NON-NLS-1$
			//
			// int i = 0;
			//
			// if (log.isLoggable(Level.INFO)) {
			//				log.info(StopWatch.start("Dispatching 0..20")); //$NON-NLS-1$
			// }

			for (Connection figure : pendingConnections.keySet()) {

				// i++;
				// if (i % 20 == 0) {
				//
				// if (log.isLoggable(Level.INFO)) {
				// log.info(StopWatch.stop());
				// log.info(StopWatch
				//								.start("Dispatching " + i + ".." + (i + 20))); //$NON-NLS-1$
				// }
				//
				// }

				ConnectionConstraints cc = pendingConnections.get(figure);

				// if (log.isLoggable(Level.INFO) && (i % 20 == 0)) {
				//					log.info(StopWatch.start("find layer")); //$NON-NLS-1$
				// }

				ConnectionLayer distributedLayer = findDistributedLayer(figure);

				// if (log.isLoggable(Level.INFO) && (i % 20 == 0)) {
				//					log.info(StopWatch.stop()); //$NON-NLS-1$
				// }

				if (distributedLayer != null) {
					// if (distributedLayer.getConnectionRouter()==null) {
					distributedLayer.setConnectionRouter(host
							.getConnectionRouter());
					// }
					dispatchedConnections.add(figure);
					distributedLayer.add(figure, cc.constaint, cc.index);

					rewire(figure);

					distributedLayer.validate();
				}
			}
			// if (log.isLoggable(Level.INFO)) {
			// log.info(StopWatch.stop());
			// }

			for (IFigure conn : dispatchedConnections)
				pendingConnections.remove(conn);

			// if (log.isLoggable(Level.INFO))
			// log.info(StopWatch.stop());

		}
	}

	/**
	 * @param i_connection
	 * @return
	 */
	protected ConnectionLayer findDistributedLayer(Connection i_connection) {
		ConnectionLayer layer = null;

		ConnectionAnchor anchor;
		anchor = i_connection.getSourceAnchor();
		IFigure source = (anchor != null) ? anchor.getOwner() : null;
		anchor = i_connection.getTargetAnchor();
		IFigure target = (anchor != null) ? anchor.getOwner() : null;

		IFigure3D sourceAncestor3D = Figure3DHelper.getAncestor3D(source);
		IFigure3D targetAncestor3D = Figure3DHelper.getAncestor3D(target);
		if (sourceAncestor3D == targetAncestor3D) {
			if (sourceAncestor3D != null) {

				layer = distributedLayersMap.get(sourceAncestor3D);
				if (layer == null) {
					// sourceAncestor3D.invalidate();
					layer = sourceAncestor3D.getConnectionLayer(factory);
					host.add(layer, CONSTRAINT_LAYER);
					distributedLayersMap.put(sourceAncestor3D, layer);
					// distributedLayers.add(layer);
				}
			} else {
				return null;
			}
		} else {
			log.severe("2D connections with different 3D ancestors");

			throw new IllegalArgumentException(
					"Connection's anchors have different 3D ancestors");
		}

		return layer;
	}

	/**
	 * Paints the children of the host connection layer. Since the distributed
	 * connection layers are already painted by their host 3D figures (see
	 * {@link Figure3DHelper#paintChildren(Graphics)}), they are not painted
	 * here for performance reasons.
	 * 
	 * @param i_graphics the graphics object to paint on
	 */
	public void paintChildren(Graphics i_graphics) {

		List children = host.getChildren();
		Rectangle clip = Rectangle.SINGLETON;
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			IFigure child = (IFigure) iter.next();
			if (!(child instanceof ConnectionLayer) && child.isVisible()
					&& child.intersects(i_graphics.getClip(clip))) {
				i_graphics.clipRect(child.getBounds());
				child.paint(i_graphics);
				i_graphics.restoreState();
			}
		}
	}

	/**
	 * @return true, if helper could handle this kind of figure
	 * @see org.eclipse.draw2d.ConnectionLayer#remove(org.eclipse.draw2d.IFigure)
	 */
	public boolean remove(IFigure i_figure) {
		if (i_figure instanceof Connection && !(i_figure instanceof IFigure3D)) {

			if (pendingConnections.remove(i_figure) == null) {
				ConnectionLayer distributedLayer = findDistributedLayer((Connection) i_figure);
				if (distributedLayer != null) {
					distributedLayer.remove(i_figure);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Rewire the connection so that it is correctly set up. Because no parent
	 * was set in add(), the setup of the connection has possibly failed before
	 * (parent was null). Look at PolylineConnection#setSourceAnchor() and
	 * PolylineConnection#setTargetAnchor() to understand this.
	 * 
	 * @param figure
	 */
	private void rewire(IFigure figure) {

		if (figure instanceof Connection) {
			Connection conn = (Connection) figure;

			ConnectionAnchor sourceAnchor = conn.getSourceAnchor();
			ConnectionAnchor targetAnchor = conn.getTargetAnchor();

			conn.setSourceAnchor(null);
			conn.setTargetAnchor(null);

			conn.setSourceAnchor(sourceAnchor);
			conn.setTargetAnchor(targetAnchor);
		}
	}

	/**
	 * @see org.eclipse.draw2d.ConnectionLayer#setConnectionRouter(org.eclipse.draw2d.ConnectionRouter)
	 */
	public void setConnectionRouter(ConnectionRouter i_router) {
		for (ConnectionLayer distributedLayer : distributedLayersMap.values()) {
			// for (ConnectionLayer distributedLayer : distributedLayers) {
			if (distributedLayer.getConnectionRouter() == null) {
				distributedLayer.setConnectionRouter(i_router);
			}
		}

	}

	/**
	 * @see org.eclipse.draw2d.Figure#validate()
	 */
	public void validate() {
		dispatchPendingConnections();

		for (ConnectionLayer layer : distributedLayersMap.values()) {
			// for (ConnectionLayer layer : distributedLayers) {
			layer.validate();
		}
	}

}
