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
package org.eclipse.draw3d.graphics.optimizer;

import junit.framework.TestCase;

/**
 * PrimitiveSetTest
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	26.11.2009
 */
public class PrimitiveSetTest extends TestCase {

	public void testAddSameKey() {

		int[] p1 = new int[] { 0, 0, 20, 0, 20, 20, 0, 20 };
		int[] p2 = new int[] { 100, 100, 120, 100, 120, 120, 100, 120 };
		int[] p3 = new int[] { 10, 10, 30, 10, 30, 30, 10, 30 };

		Primitive r1 = new RectanglePrimitive(p1, false);
		Primitive r2 = new RectanglePrimitive(p2, false);
		Primitive r3 = new RectanglePrimitive(p3, false);

		Object key = new Object();

		PrimitiveSet set = new PrimitiveSet(key);
		assertTrue(set.add(r1, key));
		assertTrue(set.add(r2, key));
		assertTrue(set.add(r3, key));
	}

	public void testAddDifferentKey() {

		int[] p1 = new int[] { 0, 0, 20, 0, 20, 20, 0, 20 };
		int[] p2 = new int[] { 100, 100, 120, 100, 120, 120, 100, 120 };
		int[] p3 = new int[] { 10, 10, 30, 10, 30, 30, 10, 30 };

		Primitive r1 = new RectanglePrimitive(p1, false);
		Primitive r2 = new RectanglePrimitive(p2, false);
		Primitive r3 = new RectanglePrimitive(p3, false);

		Object key1 = new Object();
		Object key2 = new Object();

		PrimitiveSet set = new PrimitiveSet(key1);
		assertTrue(set.add(r1, key1));
		assertTrue(set.add(r2, key1));
		assertFalse(set.add(r3, key2));
	}

	public void testAddDifferentKeyWithParent() {

		int[] p1 = new int[] { 0, 0, 20, 0, 20, 20, 0, 20 };
		int[] p2 = new int[] { 100, 100, 120, 100, 120, 120, 100, 120 };
		int[] p3 = new int[] { 10, 10, 30, 10, 30, 30, 10, 30 };

		Primitive r1 = new RectanglePrimitive(p1, false);
		Primitive r2 = new RectanglePrimitive(p2, false);
		Primitive r3 = new RectanglePrimitive(p3, false);

		Object key1 = new Object();
		Object key2 = new Object();

		PrimitiveSet parent = new PrimitiveSet(key2);
		PrimitiveSet set = new PrimitiveSet(key1, parent);
		assertTrue(set.add(r1, key1));
		assertTrue(set.add(r2, key2));
		assertFalse(set.add(r3, key2));
	}
}
