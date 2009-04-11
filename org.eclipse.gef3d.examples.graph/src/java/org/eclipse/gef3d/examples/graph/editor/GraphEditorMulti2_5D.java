/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - multi editor editor
 ******************************************************************************/
package org.eclipse.gef3d.examples.graph.editor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef3d.examples.graph.editor.actions.ActionBuilder;
import org.eclipse.gef3d.examples.graph.editor.editparts.GraphEditPartFactory;
import org.eclipse.gef3d.examples.graph.model.Edge;
import org.eclipse.gef3d.examples.graph.model.Graph;
import org.eclipse.gef3d.examples.graph.model.IntermodelContainer;
import org.eclipse.gef3d.examples.graph.model.Vertex;
import org.eclipse.gef3d.ext.multieditor.MultiEditorModelContainer;
import org.eclipse.gef3d.ext.multieditor.MultiEditorPartFactory;


/**
 * GraphEditorMulti2_5D displays two graphs in Dia3D mode with intermodel
 * connections
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 20.12.2007
 */
public class GraphEditorMulti2_5D extends GraphEditor2_5D {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(GraphEditorMulti2_5D.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.examples.graph.editor.GraphEditor3D#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {

		super.configureGraphicalViewer();

		MultiEditorPartFactory multiFactory = new MultiEditorPartFactory();
		getGraphicalViewer().setEditPartFactory(multiFactory);
	}
	
	/**
	 * Creates actions using the {@link ActionBuilder}.
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions()
	 */
	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		ActionBuilder.buildActions(registry, this);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {

		int planes = 10; // max 50
		int nodesPerPlane = 20; // max: 200;
		Graph[] graphs = new Graph[planes];

		MultiEditorModelContainer container = new MultiEditorModelContainer();
		GraphicalViewer viewer = getGraphicalViewer();
		MultiEditorPartFactory multiFactory = (MultiEditorPartFactory) viewer
				.getEditPartFactory();
		GraphEditPartFactory graphFactory = new GraphEditPartFactory();

		IntermodelContainer intermodel = new IntermodelContainer();
		IntermodelEditPartFactory intermodelFactory = new IntermodelEditPartFactory();
		multiFactory.prepare(intermodel, intermodelFactory);

		for (int p = 0; p < planes; p++) {
			Graph g = Graph.getSample(nodesPerPlane, 0, 0, 65, 30, 5);
			container.add(g);
			multiFactory.prepare(g, graphFactory);
			multiFactory.prepare(g, intermodelFactory,
					MultiEditorPartFactory.HIGHEST_PRIORITY);

			graphs[p] = g;
		}
		
		
		
//		for (int p = 1; p < planes; p++) {
//			for (int c = 0; c < nodesPerPlane / 2; c++) {
//				int source = (int) (Math.random() * nodesPerPlane);
//				int target = (int) (Math.random() * nodesPerPlane);
//
//				intermodel.add(new Edge(graphs[p - 1].getVerteces().get(source),
//						graphs[p].getVerteces().get(target)));
//			}
//		}
		
		for (int p = 1; p < planes; p++) {
			for (int c = 0; c < nodesPerPlane; c+=20 ) {
				int source = (int) (c);
				int target = (int) (c);

				intermodel.add(new Edge(graphs[p - 1].getVerteces().get(source),
						graphs[p].getVerteces().get(target)));
			}
		}
		

		int vPerGraph = graphs[0].getVerteces().size();
		int ePerGraph = 0; // with 3D edges
		for (Vertex v: graphs[0].getVerteces()) {
			ePerGraph += v.getSources().size();
		}
		
		
		int connections3D = intermodel.getConnections().size();

		if (log.isLoggable(Level.INFO)) {
			
			log.info("3D Nodes, " +
					"3D Edges, " +
					"2D Nodes per Plane, " +
					"2D Nodes, " + 
					"Edges"
					);
			log.info( planes
					+ ", " + connections3D
					+ ", " + vPerGraph
					+ ", " + planes * vPerGraph
					+ ", " + planes * ePerGraph 
					); //$NON-NLS-1$
		}
		
		
		
		viewer.setContents(container);
	}

	
}
