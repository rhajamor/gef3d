/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.graph.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Edge connecting two vertices. Observer pattern is implemented using
 * {@link PropertyChangeSupport}. 
 * 
 * @author jpilgrim
 * @version $Revision$
 * @since 27.09.2004
 */
public class Edge {

	PropertyChangeSupport m_Listeners;

	public final static String PROPERTY_SOURCE = "source";

	public final static String PROPERTY_DESTINATION = "destination";

	Vertex m_vertexSource;

	Vertex m_vertexDestination;

	public Edge(Vertex i_vertexSource, Vertex i_vertexDestination) {
		m_Listeners = new PropertyChangeSupport(this);
		m_vertexSource = i_vertexSource;
		m_vertexDestination = i_vertexDestination;
		m_vertexSource.addSource(this);
		m_vertexDestination.addDestination(this);
	}

	/**
	 * Simple getter, returns property <code>destination</code>.
	 * 
	 * @return Returns the <code>destinations</code>.
	 */
	public Vertex getDestination() {
		return m_vertexDestination;
	}

	/**
	 * Simple getter, returns property <code>source</code>.
	 * 
	 * @return Returns the <code>sources</code>.
	 */
	public Vertex getSource() {
		return m_vertexSource;
	}

	/**
	 * @param i_Listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener i_Listener) {
		m_Listeners.addPropertyChangeListener(i_Listener);
	}

	/**
	 * @param i_Listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener i_Listener) {
		m_Listeners.removePropertyChangeListener(i_Listener);
	}

}
