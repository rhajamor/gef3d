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
import java.util.HashSet;

/**
 * CombinedPointQueue There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 19.11.2009
 */
public class CombinedPointQueue implements PointQueue {

	private Collection<PointQueue> m_queues = new HashSet<PointQueue>();

	private PointQueue m_insertQueue;

	private static final int DEF_INSERT_SIZE = 10;

	public CombinedPointQueue() {

		m_insertQueue =
			new PointQueue(new int[2 * DEF_INSERT_SIZE],
				new int[DEF_INSERT_SIZE], 0);
		m_queues.add(insertQueue);
	}

	public void addQueue(int[] i_points, int[] i_sorted, int i_size) {

		m_queues.add(new PointQueue(i_points, i_sorted, i_size));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.intersection.PointQueue#isEmpty()
	 */
	public boolean isEmpty() {

		for (PointQueue queue : m_queues)
			if (!queue.isEmpty())
				return false;

		return m_insertQueue.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.intersection.PointQueue#next(int[])
	 */
	public boolean next(int[] i_result) {

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.intersection.PointQueue#peek(int[])
	 */
	public void peek(int[] i_result) {
		// TODO implement method CombinedPointQueue.peek

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.intersection.PointQueue#pop(int[])
	 */
	public void pop(int[] i_result) {
		// TODO implement method CombinedPointQueue.pop

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.intersection.PointQueue#previous(int[])
	 */
	public boolean previous(int[] i_result) {
		// TODO implement method CombinedPointQueue.previous
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.intersection.PointQueue#push(int, int)
	 */
	public int push(int i_x, int i_y) {
		// TODO implement method CombinedPointQueue.push
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.geometry.intersection.PointQueue#size()
	 */
	public int size() {
		// TODO implement method CombinedPointQueue.size
		return 0;
	}

}
