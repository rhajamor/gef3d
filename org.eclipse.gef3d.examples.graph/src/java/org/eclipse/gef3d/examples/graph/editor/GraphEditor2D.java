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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef3d.examples.graph.editor.actions.ActionBuilder;
import org.eclipse.gef3d.examples.graph.editor.editparts.GraphEditPartFactory;
import org.eclipse.gef3d.examples.graph.editor.figures.GraphFigureFactory;
import org.eclipse.gef3d.examples.graph.model.Graph;
import org.eclipse.gef3d.examples.graph.model.Vertex;
import org.eclipse.gef3d.factories.DisplayMode;
import org.eclipse.gef3d.ui.parts.ScrollingGraphicalViewerEx;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;


/**
 * GraphEditor3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 16.11.2007
 */
public class GraphEditor2D extends GraphicalEditorWithPalette {

	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(GraphEditor2D.class
			.getName());

	/**
	 * 
	 */
	public GraphEditor2D() {
		setEditDomain(new DefaultEditDomain(this));
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
		controls.add(new CreationToolEntry("Vertex", "Create Vertex",
				new SimpleFactory(Vertex.class), null, null));
		return root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createGraphicalViewer(Composite parent) {
		ScrollingGraphicalViewerEx viewer = new ScrollingGraphicalViewerEx();
		viewer.createControl(parent);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		if (log.isLoggable(Level.INFO)) {
			log.info("2D editor"); //$NON-NLS-1$
		}

		Graph g = Graph.getSample(10, 0, 0, 80, 60, 20);
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
		((ScrollingGraphicalViewerEx) getGraphicalViewer())
				.setFigureFactory(new GraphFigureFactory(
						DisplayMode.TwoDimensional));

		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		getGraphicalViewer().setRootEditPart(root);
		initZoom();
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

	private void initZoom() {
		List listZoomLevels = new ArrayList(3);

		listZoomLevels.add(ZoomManager.FIT_WIDTH);
		listZoomLevels.add(ZoomManager.FIT_HEIGHT);
		listZoomLevels.add(ZoomManager.FIT_ALL);

		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getGraphicalViewer()
				.getRootEditPart();
		root.getZoomManager().setZoomLevelContributions(listZoomLevels);

		IAction actionZoomIn = new ZoomInAction(root.getZoomManager());
		IAction actionZoomOut = new ZoomOutAction(root.getZoomManager());
		getActionRegistry().registerAction(actionZoomIn);
		getActionRegistry().registerAction(actionZoomOut);
		getSite().getKeyBindingService().registerAction(actionZoomIn);
		getSite().getKeyBindingService().registerAction(actionZoomOut);
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getAdapter(java.lang.Class)
	 */

	@Override
	public Object getAdapter(Class i_Type) {
		if (i_Type == ZoomManager.class) {
			return getGraphicalViewer().getProperty(
					ZoomManager.class.toString());
		} else {
			return super.getAdapter(i_Type);
		}

	}

}
