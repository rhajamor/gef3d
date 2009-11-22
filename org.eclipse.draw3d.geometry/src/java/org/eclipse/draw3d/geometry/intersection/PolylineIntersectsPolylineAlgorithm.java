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

/**
 * PolylineIntersectsPolylineAlgorithm There should really be more documentation
 * here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 19.11.2009
 */
public class PolylineIntersectsPolylineAlgorithm {

	private static enum Structure {
		POLYLINE1, POLYLINE2, INTERSECTION, DONE;

		public boolean isSegment() {

			return this == POLYLINE1 || this == POLYLINE2;
		}
	}

	private int[] isx;

	private int[] isy;

	private int is;

	private int i1;

	private int i2;

	private int nis;

	private int[] pl1;

	private int[] pl2;

	private int[] s1;

	private int[] s2;

	private int handleSegment(int[] pl, int si1, int si2) {

		int x1 = pl[2 * si1];
		int x2 = pl[2 * si2];

		if (x1 == x2)
			return testVerticalSegment(si1, si2);

		if (x1 < x2)
			return insertSegment(si1, si2);

		deleteSegment(si1, si2);
		return false;
	}

	public int intersects(int i_max) {

		Structure a;
		int num = 0;
		while ((a = next()) != Structure.DONE) {

			if (a.isSegment()) {
				int si;
				int n;
				int[] pl;

				if (a == Structure.POLYLINE1) {
					si = s1[i1];
					pl = pl1;
					n = s1.length;
				} else {
					si = s2[i2];
					pl = pl2;
					n = s2.length;
				}

				if (si == 0)
					num += handleSegment(pl, 0, 1);
				else if (si == n - 1)
					num  += handleSegment(pl, n - 1, n - 2);
				else {
					num += handleSegment(pl, si, si - 1);
					if (num < i_max)
						num += handleSegment(pl, si, si - 2);
				}

				if (num >= i_max)
					return num;
			} else {
				handleIntersection();
			}
		}

		return num;
	}

	private Structure next() {

		if (i1 == s1.length || i2 == s2.length)
			return Structure.DONE;

		int x1, x2, xis;
		if (i1 < s1.length)
			x1 = pl1[2 * s1[i1]];
		else
			x1 = Integer.MAX_VALUE;

		if (i2 < s2.length)
			x2 = pl2[2 * s2[i2]];
		else
			x2 = Integer.MAX_VALUE;

		if (is < nis)
			xis = isx[is];
		else
			xis = Integer.MAX_VALUE;

		if (x1 < x2 && x1 < xis) {
			i1++;
			return Structure.POLYLINE1;
		}

		if (x2 < x1 && x2 < xis) {
			i2++;
			return Structure.POLYLINE2;
		}

		is++;
		return Structure.INTERSECTION;
	}
}
