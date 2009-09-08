/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others,
 * Copyright (c) 2008 Kristian Duske and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation (original 2D version)
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.ecore.diagram.part;

import java.util.logging.Logger;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.draw3d.ui.preferences.ScenePreferenceDistributor;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef3d.ext.multieditor.INestableTransactionalEditor;
import org.eclipse.gef3d.ext.multieditor.MultiEditorModelContainer;
import org.eclipse.gef3d.ext.multieditor.MultiEditorPartFactory;
import org.eclipse.gef3d.gmf.runtime.core.service.ProviderAcceptor;
import org.eclipse.gef3d.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer3D;
import org.eclipse.gef3d.tools.CameraTool;
import org.eclipse.gef3d.ui.parts.FpsStatusLineItem;
import org.eclipse.gmf.runtime.common.core.service.IProvider;
import org.eclipse.gmf.runtime.diagram.ui.actions.ActionIds;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IDiagramPreferenceSupport;
import org.eclipse.gmf.runtime.diagram.ui.internal.parts.DiagramGraphicalViewerKeyHandler;
import org.eclipse.gmf.runtime.diagram.ui.internal.parts.DirectEditKeyHandler;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.providers.DiagramContextMenuProvider;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.EditPartService;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeTypes;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;

/**
 * 3D version of the ecore diagram editor that extends the appropriate GMF
 * classes.
 * 
 * @author Kristian Duske
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 01.12.2008
 */
public class EcoreDiagramEditor3D extends EcoreDiagramEditor implements
		INestableTransactionalEditor {

	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(EcoreDiagramEditor3D.class.getName());

	/**
	 * Flag indicating whether this editor is nested inside a multi-editor or is
	 * used stand-alone.
	 */
	boolean fNested;

	private ScenePreferenceDistributor scenePreferenceDistributor;

	/**
	 * A reference to the 3D diagram graphical viewer.
	 */
	protected DiagramGraphicalViewer3D viewer3D;

	/**
	 * Creates a new editor instance.
	 */
	public EcoreDiagramEditor3D() {

		super();

		// this is a hack:
		MapModeTypes.DEFAULT_MM = MapModeTypes.IDENTITY_MM;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Parts of this method were copied from {@link GraphicalEditor} and
	 * {@link DiagramEditor}.
	 * </p>
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 * @author hudsonnr (fragments from GraphicalEditor)
	 * @author melaasar (fragments from DiagramEditor)
	 */
	@Override
	protected void configureGraphicalViewer() {

		configureProviderAcceptor();

		// had to copy all this code here because the super implementations
		// assume that the graphical viewer is a figure canvas

		{ // GraphicalEditor
			// org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
			getGraphicalViewer().getControl().setBackground(
				ColorConstants.listBackground);
		}
		{ // DiagramEditor
			// org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor#configureGraphicalViewer()
			final IDiagramGraphicalViewer viewer = getDiagramGraphicalViewer();

			final RootEditPart rootEP =
				EditPartService.getInstance().createRootEditPart(getDiagram());
			if (rootEP instanceof IDiagramPreferenceSupport) {
				((IDiagramPreferenceSupport) rootEP)
					.setPreferencesHint(getPreferencesHint());
			}

			if (getDiagramGraphicalViewer() instanceof DiagramGraphicalViewer) {
				((DiagramGraphicalViewer) getDiagramGraphicalViewer())
					.hookWorkspacePreferenceStore(getWorkspaceViewerPreferenceStore());
			}

			viewer.setRootEditPart(rootEP);

			viewer.setEditPartFactory(EditPartService.getInstance());
			final ContextMenuProvider provider =
				new DiagramContextMenuProvider(this, viewer);
			viewer.setContextMenu(provider);
			getSite().registerContextMenu(
				ActionIds.DIAGRAM_EDITOR_CONTEXT_MENU, provider, viewer);
			final KeyHandler viewerKeyHandler =
				new DiagramGraphicalViewerKeyHandler(viewer)
					.setParent(getKeyHandler());
			viewer.setKeyHandler(new DirectEditKeyHandler(viewer)
				.setParent(viewerKeyHandler));
		}
	}

	/**
	 * Configures the provider acceptor used by {@link IProvider}s in order to
	 * determine whether they are active for this editor or not.
	 */
	protected void configureProviderAcceptor() {

		// set special provider acceptor
		ProviderAcceptor providerAcceptor = new ProviderAcceptor(true);
		providerAcceptor.setProperty(ProviderAcceptor.GRAPHICAL_EDITOR, this);
		getGraphicalViewer().setProperty(
			ProviderAcceptor.PROVIDER_ACCEPTOR_PROPERTY_KEY, providerAcceptor);
		getDiagram().eAdapters().add(providerAcceptor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.GraphicalEditor3DWithPalette#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createGraphicalViewer(final Composite i_parent) {

		viewer3D = new DiagramGraphicalViewer3D();

		// 1:1 from GraphicalEditor.createGraphicalViewer(Composite)
		final Control control = viewer3D.createControl3D(i_parent);
		setGraphicalViewer(viewer3D);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();

		final IEditorSite editorSite = getEditorSite();
		final IActionBars actionBars = editorSite.getActionBars();
		final IStatusLineManager statusLine = actionBars.getStatusLineManager();

		final FpsStatusLineItem fpsCounter = new FpsStatusLineItem();
		final LightweightSystem3D lightweightSystem3D =
			viewer3D.getLightweightSystem3D();
		lightweightSystem3D.addSceneListener(fpsCounter);

		statusLine.add(fpsCounter);

		scenePreferenceDistributor =
			new ScenePreferenceDistributor(lightweightSystem3D);
		scenePreferenceDistributor.start();

		control.addDisposeListener(lightweightSystem3D);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ext.multieditor.INestableEditor#createPaletteDrawer()
	 */
	public PaletteDrawer createPaletteDrawer() {

		PaletteRoot root = createPaletteRoot(null);
		if (root.getChildren().size() == 1
			&& root.getChildren().get(0) instanceof PaletteDrawer) {
			return (PaletteDrawer) root.getChildren().get(0);
		} else {
			PaletteDrawer drawer = new PaletteDrawer("Class Diagram");
			drawer.setChildren(root.getChildren());
			return drawer;

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.feu.gef3d.ecoretools.diagram.part.EcoreDiagramEditor#createPaletteRoot(org.eclipse.gef.palette.PaletteRoot)
	 */
	@Override
	protected PaletteRoot createPaletteRoot(
		final PaletteRoot i_existingPaletteRoot) {

		final PaletteRoot root = super.createPaletteRoot(i_existingPaletteRoot);

		final PaletteDrawer drawer = new PaletteDrawer("GEF3D");
		drawer.setDescription("GEF3D tools");
		drawer.add(new ToolEntry("Camera", "Camera Tool", null, null,
			CameraTool.class) {
			// nothing to implement
		});
		root.add(0, drawer);

		return root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#dispose()
	 */
	@Override
	public void dispose() {

		if (scenePreferenceDistributor != null) {
			scenePreferenceDistributor.stop();
		}

		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor#getGraphicalControl()
	 */
	@Override
	protected Control getGraphicalControl() {

		return getGraphicalViewer().getControl();
	}

	/*
	 * *************************************************************************
	 * Nested
	 * **********************************************************************
	 */

	public void initializeAsNested(GraphicalViewer viewer,
		MultiEditorPartFactory i_multiEditorPartFactory,
		MultiEditorModelContainer i_multiEditorModelContainer) {

		fNested = true;
		try {
			// initializeGraphicalViewerContents():
			Diagram diagram = getDiagram();

			// set provider acceptor in nested content diagram
			diagram.eAdapters().add(
				ProviderAcceptor.retrieveProviderSelector(viewer));

			EditPartFactory factory = EditPartService.getInstance();

			i_multiEditorPartFactory.prepare(diagram, factory);
			i_multiEditorModelContainer.add(diagram);

			// we need this only during initialization, views
			// are shared between multiple editor instances, even
			// between 3D and 2D instances!
			diagram.eAdapters().remove(
				ProviderAcceptor.retrieveProviderSelector(viewer));

		} catch (Exception ex) {
			log.warning("GraphicalViewer exception: " + ex); //$NON-NLS-1$ 
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditor#initializeGraphicalViewerContents()
	 */
	@Override
	protected void initializeGraphicalViewerContents() {

		// zoom needs to be 1
		super.initializeGraphicalViewerContents();
		getZoomManager().setZoom(1.0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ext.multieditor.INestableTransactionalEditor#joinTransactionalEditingDomain(org.eclipse.emf.transaction.TransactionalEditingDomain)
	 */
	public void joinTransactionalEditingDomain(
		TransactionalEditingDomain i_domain) {

		getDiagramDocument().setEditingDomain(i_domain);
	}
}
