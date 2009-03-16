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
import java.util.List;
import java.util.Map;
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
	private static final Logger log = Logger.getLogger(DisplayListManager.class
			.getName());
	
	
	private static final int RANGE = 10;

	private List<Integer> m_baseIds = new ArrayList<Integer>();

	HashMap<Graphics3D, Map<String, Integer>> displayLists;
	
	
	private Map<String, Integer> m_displayLists = new HashMap<String, Integer>();

	private final boolean m_disposed = false;

	private int m_index = RANGE;
	
		
	/**
	 * Clears all displays lists in this manager.
	 * 
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public void clear() {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		Graphics3D g3d = RenderContext.getContext().getGraphics3D();
		for (int baseId : m_baseIds)
			g3d.glDeleteLists(baseId, RANGE);

		m_index = RANGE;
		m_baseIds.clear();
		m_displayLists.clear();
	}

	/**
	 * Creates a new display lists with the given name. The display lists will
	 * contain the GL commands that are executed by the given runnable. If there
	 * already is a display list with the given name, it will be overwritten.
	 * 
	 * @param i_name the name of the new display list
	 * @param i_runnable the code that generates the GL commands for the display
	 *            list
	 * @throws NullPointerException if either of the given arguments is
	 *             <code>null</code>
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public void createDisplayList(String i_name, Runnable i_runnable) {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		if (i_name == null)
			throw new NullPointerException("i_name must not be null");

		if (i_runnable == null)
			throw new NullPointerException("i_runnable must not be null");

		Integer id = m_displayLists.get(i_name);
		if (id == null)
			id = getNewId();

		Graphics3D g3d = RenderContext.getContext().getGraphics3D();
		g3d.glNewList(id, Graphics3DDraw.GL_COMPILE);
		i_runnable.run();
		g3d.glEndList();

		m_displayLists.put(i_name, id);
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
	 * Executes the display list with the given name.
	 * 
	 * @param i_name the name of the display list to execute
	 * @throws NullPointerException if the given name is <code>null</code>
	 * @throws IllegalArgumentException if there is no display list with the
	 *             given name
	 * @throws IllegalStateException if the display list with the given name was
	 *             created before, but has since been discarded
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public void executeDisplayList(String i_name) {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		if (i_name == null)
			throw new NullPointerException("i_name must not be null");

		Integer id = m_displayLists.get(i_name);
		if (id == null)
			throw new IllegalArgumentException("unknown display list: "
					+ i_name);

		Graphics3D g3d = RenderContext.getContext().getGraphics3D();
		g3d.glCallList(id);
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

		if (m_index == RANGE) {
			Graphics3D g3d = RenderContext.getContext().getGraphics3D();
			int baseId = g3d.glGenLists(RANGE);
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
	 * @param i_names the names of the display lists to check for
	 * @return <code>true</code> if all display lists with the given names are
	 *         ready to use or <code>false</code> otherwise
	 * @throws IllegalStateException if this display list manager is disposed
	 */
	public boolean isDisplayList(String... i_names) {

		if (m_disposed)
			throw new IllegalStateException("display list manager is disposed");

		if (i_names != null && i_names.length > 0)
			for (String name : i_names)
				if (!m_displayLists.containsKey(name))
					return false;

		return true;
	}

	
	
}
