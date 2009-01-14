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
package org.eclipse.gef3d.examples.graph.editor.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef3d.examples.graph.model.Graph;
import org.eclipse.gef3d.examples.graph.model.Vertex;


/**
 * CreateVertexCommand creates a new vertex.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jul 9, 2008
 */
public class CreateVertexCommand extends Command {

	float x; float y;

	Graph m_graph;

	Vertex m_newVertex;

	/**
	 * @param i_g
	 * @param i_location
	 */
	public CreateVertexCommand(Graph i_g, float x, float y) {
		this.x = x;
		this.y = y;
		m_graph = i_g;
		m_newVertex = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (m_newVertex == null) {
			m_newVertex = new Vertex();
			m_newVertex.setX(x);
			m_newVertex.setY(y);
			m_newVertex.setWidth(80);
			m_newVertex.setHeight(30);
		}
		m_graph.addVertex(m_newVertex);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (m_newVertex != null) {
			m_graph.removeVertex(m_newVertex);
		}
	}
}