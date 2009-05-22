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
package org.eclipse.gef3d.examples.graph.editor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef3d.examples.graph.editor.actions.ActionBuilder;
import org.eclipse.gef3d.examples.graph.editor.editparts.GraphEditPartFactory;
import org.eclipse.gef3d.examples.graph.editor.editparts.ScalableFreeformRootEditPart3D;
import org.eclipse.gef3d.examples.graph.editor.figures.GraphFigureFactory;
import org.eclipse.gef3d.examples.graph.model.Graph;
import org.eclipse.gef3d.examples.graph.model.Vertex;
import org.eclipse.gef3d.factories.DisplayMode;
import org.eclipse.gef3d.tools.CameraTool;
import org.eclipse.gef3d.ui.parts.GraphicalEditor3DWithPalette;
import org.eclipse.gef3d.ui.parts.GraphicalViewer3DImpl;


/**
 * GraphEditor3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 16.11.2007
 */
public class GraphEditor3D extends GraphicalEditor3DWithPalette {
	
	protected DisplayMode mode;
	
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(GraphEditor3D.class
			.getName());

	/**
	 * 
	 */
	public GraphEditor3D() {
		initMode();
		setEditDomain(new DefaultEditDomain(this));
	}

	protected void initMode() {
		mode = DisplayMode.ThreeDimensional;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		PaletteGroup controls = new PaletteGroup("Controls");
		root.add(controls);
		controls.add(new SelectionToolEntry());
		controls.add(new ToolEntry("Camera", "Camera Tool", null, null,
				CameraTool.class) {
		});
		controls.add(new CreationToolEntry("Vertex", "Create Vertex", new SimpleFactory(Vertex.class),
				null, null));
		return root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		if (log.isLoggable(Level.INFO)) {
			log.info("3D editor"); //$NON-NLS-1$
		}

		Graph g = Graph.getSample( 10 /* max 50*40*/, 0, 0, 80, 30, 10);
		// Graph g = Graph.getSample();

		if (log.isLoggable(Level.INFO)) {
			log
					.info("Created graph with " + g.getVerteces().size() + " nodes."); //$NON-NLS-1$
		}

		getGraphicalViewer().setContents(g);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor i_monitor) {
		// TODO implement method GraphEditor3D.doSave

	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		getGraphicalViewer().setEditPartFactory(new GraphEditPartFactory());
		((GraphicalViewer3DImpl) getGraphicalViewer()).setFigureFactory(new GraphFigureFactory(mode));

		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart3D();
		getGraphicalViewer().setRootEditPart(root);
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

	

	
}
