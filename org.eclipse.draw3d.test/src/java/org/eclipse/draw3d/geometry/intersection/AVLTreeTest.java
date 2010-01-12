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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import junit.framework.TestCase;

/**
 * AVLTreeTest There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 20.11.2009
 */
public class AVLTreeTest extends TestCase {

	private AVLTree<Integer> tree =
		new AVLTree<Integer>(new Comparator<Integer>() {
			public int compare(Integer i_o1, Integer i_o2) {
				return i_o1 - i_o2;
			}
		});

	private int[] toArray() {

		Object[] numbers = tree.toArray();
		int[] result = new int[numbers.length];

		for (int i = 0; i < numbers.length; i++)
			result[i] = (Integer) numbers[i];

		return result;
	}

	private int[] numbers(int n) {

		ArrayList<Integer> numbers = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++)
			numbers.add(i);

		Collections.shuffle(numbers);

		int[] result = new int[n];
		for (int i = 0; i < n; i++)
			result[i] = numbers.get(i);

		return result;
	}

	private static final int[] UNIQUE_NUMBERS_100 =
		new int[] { 35, 98, 7, 53, 92, 66, 58, 63, 74, 89, 16, 9, 5, 34, 73, 1,
			88, 81, 2, 83, 32, 99, 62, 33, 10, 19, 37, 65, 36, 40, 54, 48, 86,
			70, 28, 79, 51, 87, 82, 57, 77, 59, 85, 78, 94, 27, 23, 20, 24, 22,
			72, 17, 8, 64, 6, 29, 39, 76, 25, 12, 71, 95, 44, 0, 47, 60, 13,
			41, 3, 42, 14, 61, 75, 90, 96, 4, 56, 15, 43, 69, 11, 45, 93, 30,
			52, 49, 68, 38, 18, 84, 97, 80, 21, 26, 55, 67, 91, 46, 50, 31 };

	private static final int[] UNIQUE_NUMBERS_10 =
		new int[] { 35, 98, 7, 53, 92, 66, 58, 63, 74, 89 };

	private static final int[] DUPLICATE_NUMBERS =
		new int[] { 23, 0, 35, 61, 41, 55, 66, 90, 86, 55, 46, 37, 21, 29, 15,
			87, 25, 60, 71, 82, 36, 76, 32, 48, 18, 21, 42, 76, 82, 54, 33, 92,
			40, 46, 33, 34, 23, 41, 75, 27, 65, 55, 50, 62, 14, 14, 23, 50, 74,
			38, 5, 69, 28, 54, 44, 8, 96, 94, 77, 76, 23, 20, 95, 99, 42, 69,
			41, 76, 49, 28, 96, 85, 39, 35, 45, 95, 32, 6, 92, 91, 98, 52, 39,
			91, 17, 88, 3, 93, 38, 45, 80, 93, 31, 23, 12, 79, 2, 95, 26, 27 };

	public void testInsert() {

		int[] numbers = UNIQUE_NUMBERS_100.clone();

		for (int i = 0; i < numbers.length; i++)
			tree.insert(numbers[i]);

		Arrays.sort(numbers);
		assertTrue(Arrays.equals(numbers, toArray()));
	}

	private int[] remove(int[] numbers, int i) {

		if (numbers.length == 0)
			return numbers;

		tree.remove(numbers[i]);

		int[] result = new int[numbers.length - 1];
		System.arraycopy(numbers, 0, result, 0, i);
		System.arraycopy(numbers, i + 1, result, i, numbers.length - i - 1);

		return result;
	}

	public void testRemove() {

		Random r = new Random(System.currentTimeMillis());

		int[] numbers = new int[100];
		int i = 0;

		while (i < numbers.length) {
			int n = r.nextInt(numbers.length);
			if (tree.insert(n))
				numbers[i++] = n;
		}

		while (numbers.length > 0) {
			numbers = remove(numbers, 0);

			int[] tmp = Arrays.copyOf(numbers, numbers.length);
			Arrays.sort(tmp);

			System.out.println(Arrays.toString(tmp));
			System.out.println(Arrays.toString(toArray()));
			assertTrue(Arrays.equals(tmp, toArray()));
		}

		/*
		 * Random r = new Random(System.currentTimeMillis()); while
		 * (numbers.length > 0) { numbers = remove(numbers,
		 * r.nextInt(numbers.length)); int[] tmp = Arrays.copyOf(numbers,
		 * numbers.length); Arrays.sort(tmp);
		 * System.out.println(Arrays.toString(tmp));
		 * System.out.println(Arrays.toString(toArray()));
		 * assertTrue(Arrays.equals(tmp, toArray())); }
		 */
	}
}
