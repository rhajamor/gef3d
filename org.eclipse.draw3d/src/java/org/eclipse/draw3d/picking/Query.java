/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.picking;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.geometry.IParaxialBoundingBox;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * Executes a search query and returns a hit.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 01.08.2009
 */
public class Query {

	/**
	 * This is a simple map for storing client specific data.
	 */
	private Map<Object, Object> m_objects;

	private IVector3f m_rayDirection;

	private IVector3f m_rayOrigin;

	private IFigure3D m_rootFigure;

	private TreeSearch m_search;

	/**
	 * Constructs a new picking query with the given parameters. All figures
	 * which are not accepted or pruned by the given figure search are ignored.
	 * The vectors of the given ray are stored by reference and are not copied.
	 * 
	 * @param i_rayOrigin the origin of the picking ray
	 * @param i_rayDirection the direction of the picking ray, must be
	 *            normalised
	 * @param i_rootFigure the root figure
	 * @param i_search the search instance, may be <code>null</code>
	 * @throws NullPointerException if the given ray starting point, ray
	 *             direction or root figure is <code>null</code>
	 */
	public Query(IVector3f i_rayOrigin, IVector3f i_rayDirection,
			IFigure3D i_rootFigure, TreeSearch i_search) {

		if (i_rootFigure == null)
			throw new NullPointerException("i_rootFigure must not be null");

		setRay(i_rayOrigin, i_rayDirection);
		m_rootFigure = i_rootFigure;
		m_search = i_search;
	}

	private boolean accept(IFigure i_figure, TreeSearch i_search) {

		if (!i_figure.isVisible())
			return false;

		if (i_search == null)
			return true;

		return i_search.accept(i_figure);
	}

	private HitImpl combineParentChildHits(IFigure i_parentFigure,
		HitImpl i_childHit) {

		if (!(i_parentFigure instanceof IFigure3D))
			return i_childHit;

		IFigure3D parentFigure3D = (IFigure3D) i_parentFigure;
		if (parentFigure3D.equals(m_rootFigure))
			return i_childHit;

		IFigure searchResult = null;
		ISurface parentSurface = parentFigure3D.getSurface();
		if (parentSurface != null) {
			Point sLocation = Draw3DCache.getPoint();
			try {
				parentSurface.getSurfaceLocation2D(m_rayOrigin, m_rayDirection,
					sLocation);
				searchResult =
					parentSurface.findFigureAt(sLocation.x, sLocation.y,
						m_search);
			} finally {
				Draw3DCache.returnPoint(sLocation);
			}
		}

		HitImpl hit = i_childHit;
		if (accept(parentFigure3D, m_search) || searchResult != null) {
			float realDistance = parentFigure3D.getDistance(this);
			if (!Float.isNaN(realDistance)
				&& (hit == null || realDistance < hit.getDistance())) {
				if (searchResult == null)
					searchResult = parentFigure3D;

				hit =
					new HitImpl(parentFigure3D, searchResult, realDistance,
						m_rayOrigin, m_rayDirection);
			}
		}

		return hit;
	}

	private HitImpl combineSiblingHits(HitImpl i_hit1, HitImpl i_hit2) {

		if (i_hit1 == null)
			return i_hit2;

		if (i_hit2 == null)
			return i_hit1;

		if (i_hit1.isCloserThan(i_hit2))
			return i_hit1;

		return i_hit2;
	}

	@SuppressWarnings("unchecked")
	private HitImpl doExecute(IFigure i_figure, float i_boundingBoxDistance) {

		if (Float.isNaN(i_boundingBoxDistance))
			return null;

		HitImpl hit = null;
		List children = i_figure.getChildren();

		for (Iterator iter = children.iterator(); iter.hasNext();) {
			IFigure child = (IFigure) iter.next();

			if (!prune(child, m_search)) {
				float childDistance;
				if (child instanceof IFigure3D)
					childDistance = getBoundingBoxDistance((IFigure3D) child);
				else
					childDistance = i_boundingBoxDistance;

				hit = combineSiblingHits(doExecute(child, childDistance), hit);
			}
		}

		return combineParentChildHits(i_figure, hit);
	}

	/**
	 * Executes this query.
	 * 
	 * @param i_figure the figure to search
	 * @param i_tmpVector a temporary vector that will hold the world location
	 *            of the current hit. This is passed in here to avoid the
	 *            creation of lots of temporary objects due to the recursive
	 *            nature of this method.
	 * @return a hit or <code>null</code> if no acceptable figure was hit
	 */
	@SuppressWarnings("unchecked")
	public Hit execute() {

		if (prune(m_rootFigure, m_search))
			return null;

		return doExecute(m_rootFigure, getBoundingBoxDistance(m_rootFigure));
	}

	/**
	 * Returns a cached object. If no object with the given key was stored in
	 * this query, <code>null</code> is returned.
	 * 
	 * @param i_key the key of the object
	 * @return the cached object
	 * @throws NullPointerException if the given key is <code>null</code>
	 */
	public Object get(Object i_key) {

		if (i_key == null)
			throw new NullPointerException("i_key must not be null");

		if (m_objects == null)
			return null;

		return m_objects.get(i_key);
	}

	private float getBoundingBoxDistance(IFigure3D i_figure) {

		IParaxialBoundingBox pBounds = i_figure.getParaxialBoundingBox();
		return pBounds.intersectRay(m_rayOrigin, m_rayDirection);
	}

	/**
	 * Returns the direction vector of the picking ray.
	 * 
	 * @return the ray direction
	 */
	public IVector3f getRayDirection() {

		return m_rayDirection;
	}

	/**
	 * Returns the origin of the picking ray.
	 * 
	 * @return the origin
	 */
	public IVector3f getRayOrigin() {

		return m_rayOrigin;
	}

	private boolean prune(IFigure i_figure, TreeSearch i_search) {

		if (!i_figure.isVisible())
			return false;

		if (i_search == null)
			return false;

		return i_search.prune(i_figure);
	}

	/**
	 * Caches the given object in this query under the given key. This can be
	 * used by objects implementing {@link Pickable} to store information that
	 * is valid for the duration of a query, for example helper objects or
	 * derived mathematical variables.
	 * <p>
	 * If the given object is <code>null</code>, it will be removed from the
	 * cache.
	 * </p>
	 * 
	 * @param i_key the key to store the object under
	 * @param i_object the object to store
	 * @throws NullPointerException if the given key is <code>null</code>
	 */
	public void set(Object i_key, Object i_object) {

		if (i_key == null)
			throw new NullPointerException("i_key must not be null");

		if (i_object == null) {
			if (m_objects != null)
				m_objects.remove(i_key);
			return;
		}

		if (m_objects == null)
			m_objects = new HashMap<Object, Object>();

		m_objects.put(i_key, i_object);
	}

	/**
	 * Sets the picking ray of this query. The given vectors are stored by
	 * reference and are not copied.
	 * 
	 * @param i_rayOrigin the origin of the ray
	 * @param i_rayDirection the direction vector of the ray, must be normalised
	 * @throws NullPointerException if any of the given vectors is
	 *             <code>null</code>
	 */
	public void setRay(IVector3f i_rayOrigin, IVector3f i_rayDirection) {

		if (i_rayOrigin == null)
			throw new NullPointerException("i_rayOrigin must not be null");

		if (i_rayDirection == null)
			throw new NullPointerException("i_rayDirection must not be null");

		m_rayOrigin = i_rayOrigin;
		m_rayDirection = i_rayDirection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Query [rayDirection=" + m_rayDirection + ", rayOrigin="
			+ m_rayOrigin + ", rootFigure=" + m_rootFigure + ", search="
			+ m_search + "]";
	}

}