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
package org.eclipse.draw3d.geometry.intersection;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Math3DCache;
import org.eclipse.draw3d.geometry.Vector2f;

/**
 * PolylineIntersectsPolylineAlgorithm There should really be more documentation
 * here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 19.11.2009
 */
public class PolylineIntersectsPolylineAlgorithm {

	private static class Event {

		/**
		 * Segments of which this is an inner point.
		 */
		private Set<TreeSegment> m_inner;

		/**
		 * Segments of which this is the lower point
		 */
		private Set<TreeSegment> m_lower;

		private IVector2f m_point;

		/**
		 * Segments of which this is the upper point.
		 */
		private Set<TreeSegment> m_upper;

		public Event(IVector2f i_point) {

			m_point = i_point;
		}

		public void addInner(TreeSegment i_segment) {

			if (m_inner == null)
				m_inner = new HashSet<TreeSegment>();

			m_inner.add(i_segment);
		}

		public void addLower(TreeSegment i_segment) {

			if (m_lower == null)
				m_lower = new HashSet<TreeSegment>();

			m_lower.add(i_segment);
		}

		public void addUpper(TreeSegment i_segment) {

			if (m_upper == null)
				m_upper = new HashSet<TreeSegment>();

			m_upper.add(i_segment);
		}

		public Set<TreeSegment> getInner() {

			if (m_inner == null)
				return Collections.emptySet();

			return m_inner;
		}

		public Set<TreeSegment> getLower() {

			if (m_lower == null)
				return Collections.emptySet();

			return m_lower;
		}

		public IVector2f getPoint() {

			return m_point;
		}

		public Set<TreeSegment> getUpper() {

			if (m_upper == null)
				return Collections.emptySet();

			return m_upper;
		}

		public boolean isEmpty() {

			return (m_upper == null || m_upper.isEmpty())
				&& (m_lower == null || m_lower.isEmpty())
				&& (m_inner == null || m_inner.isEmpty());
		}

		public void removeInner(TreeSegment i_segment) {

			m_inner.remove(i_segment);
		}
	}

	private static class TreeSegment {

		private IVector2f m_intersection;

		private Segment m_segment;

		private Polyline m_line;

		private Set<TreeSegment> m_overlaps;

		public void addOverlap(TreeSegment i_segment) {

			if (m_overlaps == null)
				m_overlaps = new HashSet<TreeSegment>();

			m_overlaps.add(i_segment);

			if (!i_segment.getOverlaps().contains(this))
				i_segment.addOverlap(this);
		}

		public Set<TreeSegment> getOverlaps() {

			if (m_overlaps == null)
				return Collections.emptySet();

			return m_overlaps;
		}

		public void removeOverlap(TreeSegment i_segment) {

			m_overlaps.remove(i_segment);

			if (i_segment.getOverlaps().contains(this))
				i_segment.removeOverlap(this);
		}

		public Polyline getPolyline() {

			return m_line;
		}

		public TreeSegment(Polyline i_line, Segment i_segment) {

			m_line = i_line;
			m_segment = i_segment;
		}

		public Segment getSegment() {

			return m_segment;
		}

		public boolean overlaps(TreeSegment i_segment, Vector2f i_start,
			Vector2f i_end) {

			float mg = getSegment().getG();
			float tg = i_segment.getSegment().getG();

			float mc = getSegment().getC();
			float tc = i_segment.getSegment().getC();

			if (mg == tg && mc == tc) {
				// segments are parallel
				IVector2f mu = getUpper();
				IVector2f ml = getLower();
				IVector2f tu = i_segment.getUpper();
				IVector2f tl = i_segment.getLower();

				if (Math3D.in(mu.getY(), ml.getY(), tu.getY())) {
					i_start.set(tu);
					if (Math3D.in(mu.getY(), ml.getY(), tl.getY()))
						i_end.set(tl);
					else
						i_end.set(ml);

					return true;
				} else if (Math3D.in(tu.getY(), tl.getY(), mu.getY())) {
					i_start.set(mu);
					if (Math3D.in(tu.getY(), tl.getY(), ml.getY()))
						i_end.set(ml);
					else
						i_end.set(tl);

					return true;
				}
			}

			return false;
		}

		public boolean intersects(TreeSegment i_segment, Vector2f i_point) {

			float mg = getSegment().getG();
			float tg = i_segment.getSegment().getG();

			float mc = getSegment().getC();
			float tc = i_segment.getSegment().getC();

			if (mg != tg) {
				float x = (mc - tc) / (mg - tg);
				float y = mg * x - mc;

				i_point.set(x, y);
				return true;
			}

			return false;
		}

		public IVector2f getLower() {

			IVector2f s = m_segment.getStart();
			IVector2f e = m_segment.getEnd();

			return m_pointComparator.compare(s, e) < 0 ? s : e;
		}

		public IVector2f getUpper() {

			if (m_intersection != null)
				return m_intersection;

			IVector2f s = m_segment.getStart();
			IVector2f e = m_segment.getEnd();

			return m_pointComparator.compare(s, e) > 0 ? s : e;
		}

		public void setIntersection(IVector2f i_intersection) {

			m_intersection = i_intersection;
		}

		public void removeOverlaps() {

		}
	}

	private static final Comparator<Event> m_eventComparator =
		new Comparator<Event>() {
			public int compare(Event i_e0, Event i_e1) {

				return m_pointComparator.compare(i_e0.getPoint(),
					i_e1.getPoint());
			}
		};

	private static final Comparator<IVector2f> m_pointComparator =
		new Comparator<IVector2f>() {
			public int compare(IVector2f i_p0, IVector2f i_p1) {

				if (i_p0.getY() < i_p1.getY())
					return -1;
				else if (i_p0.getY() > i_p1.getY())
					return 1;
				else if (i_p0.getX() < i_p1.getX())
					return -1;
				else if (i_p0.getX() > i_p1.getX())
					return 1;

				return 0;
			}
		};

	private static final Comparator<Object> m_queryComparator =
		new Comparator<Object>() {

			public int compare(Object i_o0, Object i_o1) {

				if (i_o0 instanceof TreeSegment && i_o1 instanceof TreeSegment)
					return m_segmentComparator.compare((TreeSegment) i_o0,
						(TreeSegment) i_o1);

				if (i_o0 instanceof IVector2f && i_o1 instanceof IVector2f)
					return m_pointComparator.compare((IVector2f) i_o0,
						(IVector2f) i_o1);

				if (i_o0 instanceof TreeSegment && i_o1 instanceof IVector2f) {

					TreeSegment s = (TreeSegment) i_o0;
					IVector2f v = (IVector2f) i_o1;

					IVector2f u = s.getUpper();
					IVector2f l = s.getLower();

					int c = m_pointComparator.compare(u, v);
					if (c == 0)
						c = m_pointComparator.compare(l, v);

					return c;
				}

				if (i_o0 instanceof IVector2f && i_o1 instanceof TreeSegment)
					return -1 * compare(i_o1, i_o0);

				throw new AssertionError(
					"can only compare segments and vectors");
			}

		};

	private static final Comparator<TreeSegment> m_segmentComparator =
		new Comparator<TreeSegment>() {

			public int compare(TreeSegment i_o0, TreeSegment i_o1) {

				IVector2f u0 = i_o0.getUpper();
				IVector2f l0 = i_o0.getLower();
				IVector2f u1 = i_o1.getUpper();
				IVector2f l1 = i_o1.getLower();

				if (u0.getX() < u0.getX())
					return -1;
				else if (u0.getX() > u0.getX())
					return 1;
				else if (u0.getY() < u1.getY())
					return -1;
				else if (u0.getY() > u1.getY())
					return 1;
				else if (l0.getX() < l0.getX())
					return -1;
				else if (l0.getX() > l0.getX())
					return 1;
				else if (l0.getY() < l1.getY())
					return -1;
				else if (l0.getY() > l1.getY())
					return 1;

				return 0;
			}
		};

	private AVLTree<Event> m_events;

	private AVLTree<TreeSegment> m_segments;

	private void buildEventQueue(Polyline i_line) {

		if (m_events == null)
			m_events = new AVLTree<Event>(m_eventComparator);

		for (Segment s : i_line.getSegments()) {
			Event u = new Event(s.getStart());
			Event l = new Event(s.getEnd());

			int c = m_eventComparator.compare(u, l);
			if (c > 0) {
				Event temp = u;
				u = l;
				l = temp;
			} else if (c == 0)
				throw new AssertionError("empty segment");

			if (m_events.contains(u))
				u = m_events.get(u);
			else
				m_events.insert(u);

			if (m_events.contains(l))
				l = m_events.get(l);
			else
				m_events.insert(l);

			TreeSegment ts = new TreeSegment(i_line, s);

			u.addUpper(ts);
			l.addLower(ts);
		}
	}

	private void handleEvent(Event i_event) {

		IVector2f p = i_event.getPoint();
		Set<TreeSegment> upper = i_event.getUpper();
		Set<TreeSegment> inner = i_event.getInner();
		Set<TreeSegment> lower = i_event.getLower();

		TreeSegment ln = null;
		TreeSegment rn = null;

		for (TreeSegment ts : lower) {
			m_segments.remove(ts);
			ts.removeOverlaps();
		}

		for (TreeSegment ts : inner)
			m_segments.remove(ts);

		for (TreeSegment ts : inner) {
			ts.setIntersection(p);
			m_segments.insert(ts);

			if (ln == null || m_segmentComparator.compare(ts, ln) < 0)
				ln = ts;

			if (rn == null || m_segmentComparator.compare(ts, rn) > 0)
				rn = ts;
		}

		for (TreeSegment ts : upper) {
			m_segments.insert(ts);

			if (ln == null || m_segmentComparator.compare(ts, ln) < 0)
				ln = ts;

			if (rn == null || m_segmentComparator.compare(ts, rn) > 0)
				rn = ts;
		}

		if (upper.isEmpty() && inner.isEmpty()) {
			TreeSegment l = m_segments.queryPrevious(p, m_queryComparator);
			TreeSegment r = m_segments.queryNext(p, m_queryComparator);
			findNextEvent(l, r, i_event);
		} else {
			TreeSegment l = m_segments.getPrevious(ln);
			TreeSegment r = m_segments.getNext(rn);

			if (l != null)
				findNextEvent(l, ln, i_event);

			if (r != null)
				findNextEvent(rn, r, i_event);
		}
	}

	private Collection<Intersection> m_intersections =
		new HashSet<Intersection>();

	private void handleIntersection(TreeSegment i_left, TreeSegment i_right,
		IVector2f i_point) {

		Event e = new Event(i_point);
		if (m_events.contains(e))
			e = m_events.get(e);

		e.addInner(i_left);
		e.addInner(i_right);
	}

	private void handleOverlap(TreeSegment i_left, TreeSegment i_right,
		IVector2f i_start, IVector2f i_end) {

	}

	private void findNextEvent(TreeSegment i_left, TreeSegment i_right,
		Event i_event) {

		Vector2f point = Math3DCache.getVector2f();
		Vector2f start = Math3DCache.getVector2f();
		Vector2f end = Math3DCache.getVector2f();
		try {
			if (i_left.overlaps(i_right, start, end)
				&& (m_pointComparator.compare(start, i_event.getPoint()) > 0 || m_pointComparator.compare(
					end, i_event.getPoint()) > 0))
				handleOverlap(i_left, i_right, start, end);
			else if (i_left.intersects(i_right, point)
				&& m_pointComparator.compare(point, i_event.getPoint()) > 0)
				handleIntersection(i_left, i_right, point);
		} finally {
			Math3DCache.returnVector2f(point, start, end);
		}
	}

	public boolean intersects(Polyline i_line0, Polyline i_line1) {

		if (i_line0 == null)
			throw new NullPointerException("i_line0 must not be null");

		if (i_line1 == null)
			throw new NullPointerException("i_line1 must not be null");

		if (i_line0.getSegments().isEmpty() || i_line1.getSegments().isEmpty())
			return false;

		if (m_events != null)
			m_events.clear();

		buildEventQueue(i_line0);
		buildEventQueue(i_line1);

		while (!m_events.isEmpty()) {
			Event event = m_events.getFirst();
			m_events.remove(event);
			handleEvent(event);
		}

		return false;
	}
}
