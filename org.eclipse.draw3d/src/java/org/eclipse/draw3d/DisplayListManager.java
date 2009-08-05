/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;

/**
 * Manages display lists during a render operation in the current GL context.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 24.05.2008
 */
public class DisplayListManager {

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(DisplayListManager.class.getName());

	private static final int RANGE = 10;

	private List<Integer> m_baseIds = new ArrayList<Integer>();

	private Map<Object, Integer> m_displayLists =
		new HashMap<Object, Integer>();

	private final boolean m_disposed = false;

	private Queue<Integer> m_freeIds = new LinkedList<Integer>();

	private Graphics3D m_graphics3D;

	private int m_index = RANGE;

	/**
	 * Creates a new display list manager for the given graphics3D object.
	 * 
	 * @param i_graphics3D the graphics3D object that contains this manager
	 */
	public DisplayListManager(Graphics3D i_graphics3D) {

		if (i_graphics3D == null)
			throw new NullPointerException("i_graphics3D must not be null");

		m_graphics3D = i_graphics3D;
	}

	/**
	 * Clears all displays lists in this manager.
	 * 
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public void clear() {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		for (int baseId : m_baseIds)
			m_graphics3D.glDeleteLists(baseId, RANGE);

		m_index = RANGE;
		m_baseIds.clear();
		m_displayLists.clear();
		m_freeIds.clear();
	}

	/**
	 * Creates a new display lists with the given key. The display lists will
	 * contain the GL commands that are executed by the given runnable. If there
	 * already is a display list with the given key, it will be overwritten.
	 * 
	 * @param i_key the key of the new display list
	 * @param i_runnable the code that generates the GL commands for the display
	 *            list
	 * @throws NullPointerException if either of the given arguments is
	 *             <code>null</code>
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public void createDisplayList(Object i_key, Runnable i_runnable) {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		if (i_key == null)
			throw new NullPointerException("i_key must not be null");

		if (i_runnable == null)
			throw new NullPointerException("i_runnable must not be null");

		Integer id = m_displayLists.get(i_key);
		if (id == null)
			id = getNewId();

		m_graphics3D.glNewList(id, Graphics3DDraw.GL_COMPILE);
		i_runnable.run();
		m_graphics3D.glEndList();

		m_displayLists.put(i_key, id);
	}

	/**
	 * Deletes the display lists with the given keys. If any of the given keys
	 * is not the key of a display list that was created with this manager, it
	 * is ignored.
	 * 
	 * @param i_keys the keys of the display lists to delete
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public void deleteDisplayLists(Object... i_keys) {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		for (Object key : i_keys) {
			int id = m_displayLists.get(key);
			m_graphics3D.glDeleteLists(id, 1);
			m_displayLists.remove(key);
			m_freeIds.offer(id);
		}
	}

	/**
	 * Disposes all ressources associated with this display list manager.
	 */
	public void dispose() {

		if (m_disposed)
			return;

		clear();
		m_baseIds = null;
		m_displayLists = null;
	}

	/**
	 * Executes the display list with the given key.
	 * 
	 * @param i_key the key of the display list to execute
	 * @throws NullPointerException if the given name is <code>null</code>
	 * @throws IllegalArgumentException if there is no display list with the
	 *             given name
	 * @throws IllegalStateException if the display list with the given name was
	 *             created before, but has since been discarded
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public void executeDisplayList(Object i_key) {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		if (i_key == null)
			throw new NullPointerException("i_key must not be null");

		Integer id = m_displayLists.get(i_key);
		if (id == null)
			throw new IllegalArgumentException("unknown display list: " + i_key);

		m_graphics3D.glCallList(id);
	}

	/**
	 * Returns an unused display list ID.
	 * 
	 * @return an unused display list ID
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	private int getNewId() {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		if (!m_freeIds.isEmpty())
			return m_freeIds.poll();

		if (m_index == RANGE) {
			int baseId = m_graphics3D.glGenLists(RANGE);
			m_baseIds.add(baseId);
			m_index = 0;
		}

		int baseId = m_baseIds.get(m_baseIds.size() - 1);
		return baseId + m_index++;
	}

	/**
	 * Indicates whether a number of display lists have been registered with
	 * this manager.
	 * 
	 * @param i_keys the keys of the display lists to check for
	 * @return <code>true</code> if all display lists with the given keys are
	 *         ready to use or <code>false</code> otherwise
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public boolean isDisplayList(Object... i_keys) {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		if (i_keys != null && i_keys.length > 0)
			for (Object key : i_keys)
				if (!m_displayLists.containsKey(key))
					return false;

		return true;
	}

}
