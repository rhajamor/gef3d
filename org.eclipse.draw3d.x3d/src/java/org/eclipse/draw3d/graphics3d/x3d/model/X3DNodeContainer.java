/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthias Thiele - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.graphics3d.x3d.model;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawTarget;

/**
 * Container for a list of X3DNodes.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DNodeContainer {

	/**
	 * The list of X3DNodes.
	 */
	private final List<X3DNode> m_nodes;

	/**
	 * Constructs a new node container.
	 */
	public X3DNodeContainer() {
		m_nodes = new ArrayList<X3DNode>();
	}

	/**
	 * Adds a new node to the container.
	 * 
	 * @param i_node The node to add.
	 */
	public void addNode(X3DNode i_node) {
		m_nodes.add(i_node);
	}

	/**
	 * Adds a draw target to this container.
	 * 
	 * @param i_drawTarget The draw target to add.
	 */
	public void addDrawTarget(X3DDrawTarget i_drawTarget) {

		ListIterator<X3DNode> it = i_drawTarget.getNodeIterator();

		while (it.hasNext()) {
			m_nodes.add(it.next());
		}
	}

	/**
	 * @return An iterator over the nodes within this container.
	 */
	public ListIterator<X3DNode> getNodeIterator() {
		return m_nodes.listIterator();
	}

	/**
	 * Searches a node in this container by its ID. Can be a node directly into
	 * this container or a child of them.
	 * 
	 * @param i_id The ID to search for.
	 * @return The found node, or null if none is found.
	 */
	public X3DNode getNodeByID(int i_id) {
		for (X3DNode node : m_nodes) {
			X3DNode match = node.getNodeByID(i_id);
			if (match != null) {
				return match;
			}
		}

		return null;
	}

}
