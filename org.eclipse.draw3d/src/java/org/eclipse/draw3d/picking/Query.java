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

	private TreeSearch m_figureSearch;

	private Map<Object, Object> m_objects;

	private IVector3f m_rayDirection;

	private IVector3f m_rayStart;

	private IFigure3D m_rootFigure;

	private TreeSearch m_surfaceSearch;

	/**
	 * Constructs a new picking query with the given parameters. All figures
	 * which are not accepted or pruned by the given figure search are ignored.
	 * 
	 * @param i_rayStart the starting point of the picking ray
	 * @param i_rayDirection the direction of the picking ray, must be
	 *            normalised
	 * @param i_rootFigure the root figure
	 * @param i_figureSearch the search instance for the figure search, may be
	 *            <code>null</code>
	 * @param i_surfaceSearch the search instance for the surface search, may be
	 *            <code>null</code>
	 * @throws NullPointerException if the given ray starting point, ray
	 *             direction or root figure is <code>null</code>
	 */
	public Query(IVector3f i_rayStart, IVector3f i_rayDirection,
			IFigure3D i_rootFigure, TreeSearch i_figureSearch,
			TreeSearch i_surfaceSearch) {

		if (i_rayStart == null)
			throw new NullPointerException("i_rayStart must not be null");

		if (i_rayDirection == null)
			throw new NullPointerException("i_rayDirection must not be null");

		if (i_rootFigure == null)
			throw new NullPointerException("i_rootFigure must not be null");

		m_rayStart = i_rayStart;
		m_rayDirection = i_rayDirection;
		m_rootFigure = i_rootFigure;
		m_figureSearch = i_figureSearch;
		m_surfaceSearch = i_surfaceSearch;
	}

	private boolean accept(IFigure i_figure, TreeSearch i_search) {

		if (!i_figure.isVisible())
			return false;

		if (i_search == null)
			return true;

		return i_search.accept(i_figure);
	}

	private HitCombo combineParentChildHits(IFigure i_parentFigure,
		float i_parentDistance, boolean i_parentFigurePruned,
		boolean i_parentSurfacePruned, HitCombo i_childHits) {

		if (i_childHits != null) {
			if (i_parentFigurePruned)
				i_childHits.setFigureHit(null);
			if (i_parentSurfacePruned)
				i_childHits.setSurfaceHit(null);
		}

		if (!(i_parentFigure instanceof IFigure3D))
			return i_childHits;

		IFigure3D parentFigure3D = (IFigure3D) i_parentFigure;
		if (parentFigure3D.equals(m_rootFigure))
			return i_childHits;

		HitImpl figureHit = null, surfaceHit = null;

		if (i_childHits != null) {
			figureHit = i_childHits.getFigureHit();
			surfaceHit = i_childHits.getSurfaceHit();
		}

		if (figureHit == null && !i_parentFigurePruned
			&& accept(parentFigure3D, m_figureSearch))
			figureHit =
				new HitImpl(parentFigure3D, i_parentDistance, m_rayStart,
					m_rayDirection);

		if (surfaceHit == null && !i_parentSurfacePruned
			&& accept(parentFigure3D, m_surfaceSearch))
			surfaceHit =
				new HitImpl(parentFigure3D, i_parentDistance, m_rayStart,
					m_rayDirection);

		HitCombo result = i_childHits;
		if (result == null)
			result = new HitCombo(figureHit, surfaceHit);
		else {
			result.setFigureHit(figureHit);
			result.setSurfaceHit(surfaceHit);
		}

		return result;
	}

	private HitCombo combineSiblingHits(HitCombo i_hits1, HitCombo i_hits2) {

		if (i_hits1 == null)
			return i_hits2;

		if (i_hits2 == null)
			return i_hits1;

		HitImpl surfaceHit1 = i_hits1.getSurfaceHit();
		HitImpl figureHit1 = i_hits1.getFigureHit();

		HitImpl surfaceHit2 = i_hits2.getSurfaceHit();
		HitImpl figureHit2 = i_hits2.getFigureHit();

		if (surfaceHit1 != null)
			i_hits1.setSurfaceHit(surfaceHit1.getBestHit(surfaceHit2));
		else
			i_hits1.setSurfaceHit(surfaceHit2);

		if (figureHit1 != null)
			i_hits1.setFigureHit(figureHit1.getBestHit(figureHit2));
		else
			i_hits1.setFigureHit(figureHit2);

		return i_hits1;
	}

	@SuppressWarnings("unchecked")
	private HitCombo doExecute(IFigure i_figure, float i_distance) {

		if (Float.isNaN(i_distance))
			return null;

		boolean figurePruned = prune(i_figure, m_figureSearch);
		boolean surfacePruned = prune(i_figure, m_surfaceSearch);

		if (figurePruned && surfacePruned)
			return null;

		HitCombo hits = null;
		List children = i_figure.getChildren();

		for (Iterator iter = children.iterator(); iter.hasNext();) {
			IFigure child = (IFigure) iter.next();

			float descDistance;
			if (child instanceof IFigure3D)
				descDistance = getDistance((IFigure3D) child);
			else
				descDistance = i_distance;

			hits = combineSiblingHits(doExecute(child, descDistance), hits);
		}

		return combineParentChildHits(i_figure, i_distance, figurePruned,
			surfacePruned, hits);
	}

	/**
	 * Executes this query on the given figure and its subtree.
	 * 
	 * @param i_figure the figure to search
	 * @param i_tmpVector a temporary vector that will hold the world location
	 *            of the current hit. This is passed in here to avoid the
	 *            creation of lots of temporary objects due to the recursive
	 *            nature of this method.
	 * @return a combination of a surface and a figure hit or <code>null</code>
	 *         if neither a surface nor a figure was hit
	 */
	@SuppressWarnings("unchecked")
	public HitCombo execute() {

		return doExecute(m_rootFigure, getDistance(m_rootFigure));
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

		float distance = Float.NaN;
		if (i_figure instanceof Pickable) {
			Pickable pickable = (Pickable) i_figure;
			distance = pickable.getDistance(this);
		}

		if (Float.isNaN(distance)) {
			IParaxialBoundingBox pBounds = i_figure.getParaxialBoundingBox();
			distance = pBounds.intersectRay(m_rayStart, m_rayDirection);
		}

		return distance;
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

	private boolean prune(IFigure i_figure, TreeSearch i_search) {

		if (!i_figure.isVisible())
			return false;

		if (i_search == null)
			return true;

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
}