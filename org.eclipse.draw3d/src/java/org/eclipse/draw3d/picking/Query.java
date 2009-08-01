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
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.geometry.IParaxialBoundingBox;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * Executes a search query and returns a hit.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 01.08.2009
 */
public class Query {

	private Map<Object, Object> m_objects;

	private IVector3f m_rayDirection;

	private IVector3f m_rayStart;

	private IFigure m_rootFigure;

	private TreeSearch m_search;

	/**
	 * Constructs a new picking query with the given parameters. All figures
	 * which are not accepted or pruned by the given search are ignored.
	 * 
	 * @param i_rayStart the starting point of the picking ray
	 * @param i_rayDirection the direction of the picking ray, must be
	 *            normalised
	 * @param i_rootFigure the root figure
	 * @param i_search the search instance, may be <code>null</code>
	 * @throws NullPointerException if the given ray starting point, ray
	 *             direction or root figure is <code>null</code>
	 */
	public Query(IVector3f i_rayStart, IVector3f i_rayDirection,
			IFigure i_rootFigure, TreeSearch i_search) {

		if (i_rayStart == null)
			throw new NullPointerException("i_rayStart must not be null");

		if (i_rayDirection == null)
			throw new NullPointerException("i_rayDirection must not be null");

		if (i_rootFigure == null)
			throw new NullPointerException("i_rootFigure must not be null");

		m_rayStart = i_rayStart;
		m_rayDirection = i_rayDirection;
		m_rootFigure = i_rootFigure;
		m_search = i_search;
	}

	private boolean consider(IFigure i_figure) {

		if (!i_figure.isVisible())
			return false;

		if (m_search == null)
			return true;

		if (m_search.prune(i_figure))
			return false;

		return m_search.accept(i_figure);
	}

	/**
	 * Executes this query on the given figure and its subtree.
	 * 
	 * @param i_figure the figure to search
	 * @param i_tmpVector a temporary vector that will hold the world location
	 *            of the current hit. This is passed in here to avoid the
	 *            creation of lots of temporary objects due to the recursive
	 *            nature of this method.
	 * @return a hit or <code>null</code> if no figure was hit
	 */
	@SuppressWarnings("unchecked")
	public HitImpl execute(IFigure3D i_figure) {

		HitImpl hit = null;
		if (consider(i_figure)) {

			float d = getDistance(i_figure);
			if (!Float.isNaN(d)) {

				List<IFigure3D> descendants3D = i_figure.getDescendants3D();
				for (IFigure3D descendant3D : descendants3D) {
					if (consider(descendant3D)) {
						float cd = getDistance(descendant3D);
						if (!Float.isNaN(cd)) {
							HitImpl childHit = execute(descendant3D);
							if (childHit != null)
								hit = childHit.getBestCandidate(hit);
						}
					}
				}

				if (hit == null && !i_figure.equals(m_rootFigure))
					hit = new HitImpl(i_figure, d, m_rayStart, m_rayDirection);
			}
		}

		return hit;
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

	private float getDistance(IFigure3D i_figure) {

		if (i_figure instanceof Pickable) {
			Pickable pickable = (Pickable) i_figure;
			return pickable.getDistance(this);
		}

		IParaxialBoundingBox pBounds = i_figure.getParaxialBoundingBox();
		return pBounds.intersectRay(m_rayStart, m_rayDirection);

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
	 * Returns the starting point of the picking ray.
	 * 
	 * @return the starting point
	 */
	public IVector3f getRayStart() {

		return m_rayStart;
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
}