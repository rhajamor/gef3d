/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/package org.eclipse.gef3d.examples.graph.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Graph, container for vertices.  Observer pattern is implemented using
 * {@link PropertyChangeSupport}.
 * 
 * @author jpilgrim
 * @version $Revision$
 * @since 26.09.2004
 */
public class Graph {

	ArrayList<Vertex> m_listVertices;

	// Observer-Pattern (Subject)
	PropertyChangeSupport m_Listeners;

	public final static String PROPERTY_VERTECES = "verteces";

	/**
	 * 
	 */
	public Graph() {
		m_Listeners = new PropertyChangeSupport(this);
		m_listVertices = new ArrayList<Vertex>();
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

	public void addVertex(Vertex i_Vertex) {
		m_listVertices.add(i_Vertex);
		m_Listeners.firePropertyChange(PROPERTY_VERTECES, null, i_Vertex);
	}

	public void removeVertex(Object i_Vertex) {
		m_listVertices.remove(i_Vertex);
		m_Listeners.firePropertyChange(PROPERTY_VERTECES, i_Vertex, null);
	}

	public List<Vertex> getVerteces() {
		return m_listVertices;
	}

	public static Graph getSample() {
		Graph G = new Graph();

		Vertex v = new Vertex();
		v.setX(400);
		v.setY(50);
		v.setWidth(50);
		v.setHeight(100);
		v.setZ(0);
		G.addVertex(v);

		Vertex v1 = new Vertex();
		v1.setX(0f);
		v1.setY(100f);
		v1.setWidth(100);
		v1.setHeight(100f);
		v1.setZ(0);
		G.addVertex(v1);

		new Edge(v, v1);

		return G;
	}

	/**
	 * Creates a graph based on the given parameters. All vertices are ordered
	 * in a grid starting at x0/y0, each vertex has the given width and height.
	 * All vertex are connected to their neighbors in the grid.
	 * 
	 * @param iNumberOfVertices Total number of vertices
	 * @param x0 Left offset
	 * @param y0 Lower offset
	 * @param width Width of each vertex
	 * @param height Height of each vertex
	 * @param spacing Spacing between two vertices
	 * @return
	 */
	public static Graph getSample(int iNumberOfVertices, float x0, float y0,
			float width, float height, float spacing) {
		int cols = (int) Math.sqrt(iNumberOfVertices);
		float x = x0, y = y0 - height - spacing;

		Graph G = new Graph();
		Vertex v = null;
		Vertex prev;

		for (int i = 0; i < iNumberOfVertices; i++) {
			if (i % cols == 0) {
				y += height + spacing;
				x = x0;
			} else {
				x += width + spacing;
			}

			prev = v;

			v = new Vertex();
			v.setX(x);
			v.setY(y);
			v.setZ(0);
			v.setWidth(width);
			v.setHeight(height);

			G.addVertex(v);

			if (x != x0) {
				new Edge(prev, v);
			}
			if (i >= cols) {
				Vertex vupper = G.getVerteces().get(i - cols);
				new Edge(vupper, v);
			}

		}

		return G;

	}
}
