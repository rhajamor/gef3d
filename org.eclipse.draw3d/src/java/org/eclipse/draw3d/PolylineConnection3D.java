/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others,
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation of 2D version
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.AnchorListener;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.DelegatingLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.RoutingListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.ArrowLocator3D.Alignment;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Math3DCache;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.geometry.Math3D.Side;
import org.eclipse.draw3d.geometryext.Plane;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.DebugPrimitives;

/**
 * PolylineConnection3D is the 3D version of {@link PolylineConnection} with
 * exactly the same features. Instead of a 2D connection router, a 3D router is
 * used, and, of course, it extends Polyline3D instead of Polyline.
 * <p>
 * Internal note: This class is a copy of PolylineConnection, modifications are
 * deocumented.
 * 
 * @author IBM Corporation (original 2D version)
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 26.11.2007
 * @see org.eclipse.draw2d.PolylineConnection
 */
public class PolylineConnection3D extends Polyline3D implements Connection3D,
		AnchorListener {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(PolylineConnection3D.class.getName());

	/** Start anchor, just as in PolylineConnection */
	private ConnectionAnchor startAnchor;

	/** End anchor, just as in PolylineConnection */
	private ConnectionAnchor endAnchor;

	/**
	 * Connection router, just as in PolylineConnection, except it is a 3D
	 * router here.
	 */
	private ConnectionRouter connectionRouter = ConnectionRouter3D.NULL;

	/** Start decoration, just as in PolylineConnection */
	private RotatableDecoration startArrow;

	/** End decoration, just as in PolylineConnection */
	private RotatableDecoration endArrow;

	/* no more attributes, just as in PolylineConnection */

	/* Init method, just as in PolylineConnection */
	{
		setLayoutManager(new DelegatingLayout());
		addPoint(new Vector3fImpl(0, 0, 0));
		addPoint(new Vector3fImpl(1, 1, 1));
	}

	/**
	 * Hooks the source and target anchors.
	 * 
	 * @see Figure#addNotify()
	 * @see PolylineConnection#addNotify()
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		hookSourceAnchor();
		hookTargetAnchor();
	}

	/**
	 * Appends the given routing listener to the list of listeners.
	 * 
	 * @param listener the routing listener
	 * @see PolylineConnection#addRoutingListener(RoutingListener)
	 */
	public void addRoutingListener(RoutingListener listener) {
		if (connectionRouter instanceof RoutingNotifier) {
			RoutingNotifier notifier = (RoutingNotifier) connectionRouter;
			notifier.listeners.add(listener);
		} else {
			connectionRouter = new RoutingNotifier(connectionRouter, listener);
		}
	}

	/**
	 * Called by the anchors of this connection when they have moved,
	 * revalidating this polyline connection.
	 * 
	 * @param anchor the anchor that moved
	 * @see PolylineConnection#anchorMoved(ConnectionAnchor)
	 */
	public void anchorMoved(ConnectionAnchor anchor) {
		revalidate();
	}

	/**
	 * Returns the bounds which holds all the points in this polyline
	 * connection. Returns any previously existing bounds, else calculates by
	 * unioning all the children's dimensions.
	 * 
	 * @return the bounds
	 * @see PolylineConnection#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		if (bounds == null) {
			super.getBounds();
			for (int i = 0; i < getChildren().size(); i++) {
				IFigure child = (IFigure) getChildren().get(i);
				bounds.union(child.getBounds());
			}
		}
		return bounds;
	}

	// TODO getBounds3D() ?

	/**
	 * Returns the <code>ConnectionRouter</code> used to layout this connection.
	 * Will not return <code>null</code>.
	 * 
	 * @return this connection's router
	 * @see PolylineConnection#getConnectionRouter()
	 */
	public ConnectionRouter getConnectionRouter() {
		if (connectionRouter instanceof RoutingNotifier) {
			return ((RoutingNotifier) connectionRouter).realRouter;
		}
		return connectionRouter;
	}

	/**
	 * Returns this connection's routing constraint from its connection router.
	 * May return <code>null</code>.
	 * 
	 * @return the connection's routing constraint
	 * @see PolylineConnection#getRoutingConstraint()
	 */
	public Object getRoutingConstraint() {
		if (getConnectionRouter() != null) {
			return getConnectionRouter().getConstraint(this);
		} else {
			return null;
		}
	}

	/**
	 * Returns start anchor.
	 * 
	 * @return the anchor at the start of this polyline connection (may be null)
	 * @see PolylineConnection#getSourceAnchor()
	 */
	public ConnectionAnchor getSourceAnchor() {
		return startAnchor;
	}

	/**
	 * Returns the start decoration
	 * 
	 * @return the source decoration (may be null)
	 * @see PolylineConnection#getSourceDecoration()
	 */
	protected RotatableDecoration getSourceDecoration() {
		return startArrow;
	}

	/**
	 * Returns the end anchor.
	 * 
	 * @return the anchor at the end of this polyline connection (may be null)
	 * @see PolylineConnection#getTargetAnchor()
	 */
	public ConnectionAnchor getTargetAnchor() {
		return endAnchor;
	}

	/**
	 * Returns the end decoration.
	 * 
	 * @return the target decoration (may be null)
	 * @see PolylineConnection#getTargetDecoration()
	 */
	protected RotatableDecoration getTargetDecoration() {
		return endArrow;
	}

	/**
	 * Registers this figure as listener at its source anchor.
	 * 
	 * @see PolylineConnection#hookSourceAnchor()
	 */
	private void hookSourceAnchor() {
		if (getSourceAnchor() != null) {
			getSourceAnchor().addAnchorListener(this);
		}
	}

	/**
	 * Registers this figure as listener at its target anchor.
	 * 
	 * @see PolylineConnection#hookTargetAnchor()
	 */
	private void hookTargetAnchor() {
		if (getTargetAnchor() != null) {
			getTargetAnchor().addAnchorListener(this);
		}
	}

	/**
	 * Layouts this polyline. If the start and end anchors are present, the
	 * connection router is used to route this, after which it is laid out. It
	 * also fires a moved method. This method is currently disabled, since a 3D
	 * line has to be layouted differently. Internal note: Copied and slightly
	 * modified
	 * 
	 * @todo implement this method
	 * @see PolylineConnection#layout()
	 */
	@Override
	public void layout() {
		if ( // from PolylineConnection:
		(getSourceAnchor() != null && getTargetAnchor() != null)
		// added -- and removed: XYZAnchor has no owner!:
		// && (getSourceAnchor().getOwner() != null && getTargetAnchor()
		// .getOwner() != null)
		) {
			connectionRouter.route(this); // from PolylineConnection
		}

		Rectangle oldBounds = bounds; // from PolylineConnection
		super.layout(); // from PolylineConnection

		// TODO uups? This was copied from original, why is it set to null?
		// is there a local field in original version?
		// bounds = null;

		// from PolylineConnection
		if (!getBounds().contains(oldBounds)) {
			getParent().translateToParent(oldBounds);
			getUpdateManager().addDirtyRegion(getParent(), oldBounds);
		}

		repaint(); // from PolylineConnection
		// invalidate(); not in original
		fireFigureMoved(); // from PolylineConnection
	}

	/**
	 * Called just before the receiver is being removed from its parent. Results
	 * in removing itself from the connection router.
	 * 
	 * @see PolylineConnection#removeNotify()
	 */
	@Override
	public void removeNotify() {
		unhookSourceAnchor();
		unhookTargetAnchor();
		connectionRouter.remove(this);
		super.removeNotify();
	}

	/**
	 * Removes the first occurence of the given listener.
	 * 
	 * @param listener the listener being removed
	 * @see PolylineConnection#removeRoutingListener(RoutingListener)
	 */
	public void removeRoutingListener(RoutingListener listener) {
		if (connectionRouter instanceof RoutingNotifier) {
			RoutingNotifier notifier = (RoutingNotifier) connectionRouter;
			notifier.listeners.remove(listener);
			if (notifier.listeners.isEmpty()) {
				connectionRouter = notifier.realRouter;
			}
		}
	}

	/**
	 * @see PolylineConnection#revalidate()
	 * @see PolylineConnection#revalidate()
	 */
	@Override
	public void revalidate() {
		// if (log.isLoggable(Level.INFO)) {
		//			log.info("revalidate "); //$NON-NLS-1$
		// }

		super.revalidate();

		// invalidate(); // added
		// if (getParent() != null) getParent().revalidate(); // added

		// added null check (kristian)
		if (connectionRouter != null)
			connectionRouter.invalidate(this);
	}

	/**
	 * Sets the connection router which handles the layout of this polyline.
	 * Generally set by the parent handling the polyline connection.
	 * 
	 * @param cr the connection router
	 * @see PolylineConnection#setConnectionRouter(ConnectionRouter)
	 */
	public void setConnectionRouter(ConnectionRouter cr) {
		if (cr == null) {
			cr = ConnectionRouter3D.NULL;
		}
		ConnectionRouter oldRouter = getConnectionRouter();
		if (oldRouter != cr) {
			connectionRouter.remove(this);
			if (connectionRouter instanceof RoutingNotifier) {
				((RoutingNotifier) connectionRouter).realRouter = cr;
			} else {
				connectionRouter = cr;
			}
			firePropertyChange(Connection.PROPERTY_CONNECTION_ROUTER,
				oldRouter, cr);
			revalidate();
		}
	}

	/**
	 * Sets the routing constraint for this connection.
	 * 
	 * @param cons the constraint
	 * @see PolylineConnection#setRoutingConstraint(Object)
	 */
	public void setRoutingConstraint(Object cons) {
		if (connectionRouter != null) {
			connectionRouter.setConstraint(this, cons);
		}
		revalidate();
	}

	/**
	 * Sets the anchor to be used at the start of this polyline connection.
	 * 
	 * @param anchor the new source anchor
	 * @see PolylineConnection#setSourceAnchor(ConnectionAnchor)
	 */
	public void setSourceAnchor(ConnectionAnchor anchor) {
		if (anchor == startAnchor) {
			return;
		}
		unhookSourceAnchor();
		// No longer needed, revalidate does this. (original comment)
		// getConnectionRouter().invalidate(this); (originally uncommented)
		startAnchor = anchor;
		if (getParent() != null) {
			hookSourceAnchor();
		}
		revalidate();
	}

	/**
	 * Sets the decoration to be used at the start of the {@link Connection}.
	 * 
	 * @param dec the new source decoration
	 * @see PolylineConnection#setSourceDecoration(RotatableDecoration)
	 */
	public void setSourceDecoration(RotatableDecoration dec) {
		if (startArrow == dec) {
			return;
		}
		if (startArrow != null) {
			remove(startArrow);
		}
		startArrow = dec;
		if (startArrow != null) {
			add(startArrow, new ArrowLocator3D(this, Alignment.SOURCE));
		}
	}

	/**
	 * Sets the anchor to be used at the end of the polyline connection. Removes
	 * this listener from the old anchor and adds it to the new anchor.
	 * 
	 * @param anchor the new target anchor
	 * @see PolylineConnection#setTargetAnchor(ConnectionAnchor)
	 */
	public void setTargetAnchor(ConnectionAnchor anchor) {
		if (anchor == endAnchor) {
			return;
		}
		unhookTargetAnchor();
		endAnchor = anchor;
		if (getParent() != null) {
			hookTargetAnchor();
		}
		revalidate();
	}

	/**
	 * Sets the decoration to be used at the end of the {@link Connection}.
	 * 
	 * @param dec the new target decoration
	 * @see PolylineConnection#setTargetDecoration(RotatableDecoration)
	 */
	public void setTargetDecoration(RotatableDecoration dec) {
		if (endArrow == dec) {
			return;
		}
		if (endArrow != null) {
			remove(endArrow);
		}
		endArrow = dec;
		if (endArrow != null) {
			add(endArrow, new ArrowLocator3D(this, Alignment.TARGET));
		}
	}

	/**
	 * Removes this figure from start anchors listeners.
	 * 
	 * @see PolylineConnection#unhookSourceAnchor()
	 */
	private void unhookSourceAnchor() {
		if (getSourceAnchor() != null) {
			getSourceAnchor().removeAnchorListener(this);
		}
	}

	/**
	 * Removes this figure from end anchors listeners.
	 * 
	 * @see PolylineConnection#unhookTargetAnchor()
	 */
	private void unhookTargetAnchor() {
		if (getTargetAnchor() != null) {
			getTargetAnchor().removeAnchorListener(this);
		}
	}

	/**
	 * RoutingNotifier There should really be more documentation here.
	 * 
	 * @see PolylineConnection.RoutingNotifier
	 */
	final class RoutingNotifier implements ConnectionRouter {

		ConnectionRouter realRouter;

		List<RoutingListener> listeners = new ArrayList<RoutingListener>(1);

		RoutingNotifier(ConnectionRouter router, RoutingListener listener) {
			realRouter = router;
			listeners.add(listener);
		}

		public Object getConstraint(Connection connection) {
			return realRouter.getConstraint(connection);
		}

		public void invalidate(Connection connection) {
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).invalidate(connection);
			}

			realRouter.invalidate(connection);
		}

		public void route(Connection connection) {
			boolean consumed = false;
			for (int i = 0; i < listeners.size(); i++) {
				consumed |= listeners.get(i).route(connection);
			}

			if (!consumed) {
				realRouter.route(connection);
			}

			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).postRoute(connection);
			}
		}

		public void remove(Connection connection) {
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).remove(connection);
			}
			realRouter.remove(connection);
		}

		public void setConstraint(Connection connection, Object constraint) {
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).setConstraint(connection, constraint);
			}
			realRouter.setConstraint(connection, constraint);
		}

	}

	/**
	 * {@inheritDoc} Added for debugging purposes only, may be removed.
	 * 
	 * @see org.eclipse.draw2d.Figure#setValid(boolean)
	 */
	@Override
	public void setValid(boolean i_value) {
		// if (! i_value) {
		// if (log.isLoggable(Level.INFO)) {
		//				log.info("boolean - figure is set invalid"); //$NON-NLS-1$
		// }
		// }
		super.setValid(i_value);
	}

	private static final String KEY_VERTICAL_BORDER = "vertical border";

	private static final String KEY_HORIZONTAL_BORDER = "horizontal border";

	private static final String KEY_VISIBLE_BORDER = "visible border";

	private Plane getHorizontalBorder(Query i_query) {

		Plane horizontalBorder = (Plane) i_query.get(KEY_HORIZONTAL_BORDER);
		if (horizontalBorder == null) {
			Vector3f normal = Math3DCache.getVector3f();
			try {
				Math3D.cross(i_query.getRayStart(), i_query.getRayDirection(),
					normal);
				Math3D.normalise(normal, normal);

				horizontalBorder = new Plane();
				horizontalBorder.set(i_query.getRayStart(), normal);

				i_query.set(KEY_HORIZONTAL_BORDER, horizontalBorder);
			} finally {
				Math3DCache.returnVector3f(normal);
			}
		}

		return horizontalBorder;
	}

	private void addDebugPrimitives(Object i_key, Plane i_plane, Query i_query) {

		Vector3f planeNormal = Math3DCache.getVector3f();
		Vector3f tmpDir = Math3DCache.getVector3f();
		Vector3f tmpPos = Math3DCache.getVector3f();
		Vector3f p1 = Math3DCache.getVector3f();
		Vector3f p2 = Math3DCache.getVector3f();
		try {
			IVector3f rayStart = i_query.getRayStart();
			IVector3f rayDirection = i_query.getRayDirection();
			i_plane.getNormal(planeNormal);

			Math3D.cross(planeNormal, rayDirection, tmpDir);
			tmpPos.set(rayDirection);
			tmpPos.scale(1000);
			Math3D.add(rayStart, tmpPos, tmpPos);

			Math3D.add(tmpPos, tmpDir, p1);

			tmpDir.scale(-1);
			Math3D.add(tmpPos, tmpDir, p2);

			DebugPrimitives.getInstance().addLine(i_key, p1, p2);
		} finally {
			Math3DCache.returnVector3f(planeNormal);
			Math3DCache.returnVector3f(tmpDir);
			Math3DCache.returnVector3f(tmpPos);
			Math3DCache.returnVector3f(p1);
			Math3DCache.returnVector3f(p2);
		}
	}

	private Plane getVerticalBorder(Query i_query) {

		Plane verticalBorder = (Plane) i_query.get(KEY_VERTICAL_BORDER);
		if (verticalBorder == null) {
			Vector3f hNormal = Math3DCache.getVector3f();
			Vector3f vNormal = Math3DCache.getVector3f();
			try {
				Plane horizontalBorder = getHorizontalBorder(i_query);
				horizontalBorder.getNormal(hNormal);
				Math3D.cross(i_query.getRayDirection(), hNormal, vNormal);

				verticalBorder = new Plane();
				verticalBorder.set(i_query.getRayStart(), vNormal);

				i_query.set(KEY_VERTICAL_BORDER, verticalBorder);
			} finally {
				Math3DCache.returnVector3f(hNormal);
				Math3DCache.returnVector3f(vNormal);
			}
		}

		return verticalBorder;
	}

	private Plane getVisibleBorder(Query i_query) {

		Plane visibleBorder = (Plane) i_query.get(KEY_VISIBLE_BORDER);
		if (visibleBorder == null) {
			visibleBorder = new Plane();
			visibleBorder.set(i_query.getRayStart(), i_query.getRayDirection());

			i_query.set(KEY_VISIBLE_BORDER, visibleBorder);
		}

		return visibleBorder;
	}

	private static final float ACCURACY = 10f;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getDistance(org.eclipse.draw3d.picking.Query)
	 */
	public float getDistance(Query i_query) {

		if (points.size() < 2)
			return Float.NaN;

		Plane visBorder = getVisibleBorder(i_query);
		Plane hBorder = getHorizontalBorder(i_query);
		Plane vBorder = getVerticalBorder(i_query);

		IVector3f p1 = points.get(0);
		IVector3f p2;

		Side visSide1 = visBorder.getSide(p1);
		Side hSide1 = null, vSide1 = null, visSide2, hSide2, vSide2;

		for (int i = 1; i < points.size(); i++) {
			p2 = points.get(i);
			visSide2 = visBorder.getSide(p2);

			// is at least one point in front of the camera?
			if (visSide1 != visSide2 || visSide1 == Side.FRONT) {

				if (hSide1 == null)
					hSide1 = hBorder.getSide(p1);
				hSide2 = hBorder.getSide(p2);

				// are the points in different quadrants?
				if (hSide1 != hSide2) {

					if (vSide1 == null)
						vSide1 = vBorder.getSide(p1);
					vSide2 = vBorder.getSide(p2);

					if (vSide1 != vSide2) {
						Vector3f intersection = Math3DCache.getVector3f();
						Vector3f tmp = Math3DCache.getVector3f();
						try {
							// the two points are in different
							// quadrants, so we try and hit the line
							// between them
							hBorder.intersectionWithSegment(p1, p2,
								intersection);

							// intersection only if tmp is on the
							// picking ray
							IVector3f rayStart = i_query.getRayStart();
							IVector3f rayDirection = i_query.getRayDirection();

							Math3D.sub(intersection, rayStart, tmp);
							float fx = tmp.getX() / rayDirection.getX();
							float fy = tmp.getY() / rayDirection.getY();
							float fz = tmp.getZ() / rayDirection.getZ();

							/*
							 * log.info("fx: " + fx + " fy: " + fy + " fz: " +
							 * fz + " fx / fy: " + fx / fy + " fx / fz: " + fx /
							 * fz + " fy / fz: " + fy / fz);
							 */

							if (!Math3D.equals(fx, fy, ACCURACY))
								return Float.NaN;

							if (!Math3D.equals(fx, fz, ACCURACY))
								return Float.NaN;

							if (!Math3D.equals(fy, fz, ACCURACY))
								return Float.NaN;

							return (fx + fy + fz) / 3f;
						} finally {
							Math3DCache.returnVector3f(intersection);
							Math3DCache.returnVector3f(tmp);
						}
					}

					vSide1 = vSide2;
				} else {
					vSide1 = null;
				}

				hSide1 = hSide2;
			} else {
				hSide1 = null;
				vSide1 = null;
			}

			visSide1 = visSide2;
			p1 = p2;
		}

		return Float.NaN;
	}
}
