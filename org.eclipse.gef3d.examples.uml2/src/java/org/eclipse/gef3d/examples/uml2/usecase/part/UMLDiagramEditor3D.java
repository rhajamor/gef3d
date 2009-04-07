/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.uml2.usecase.part;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef3d.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer3D;
import org.eclipse.gef3d.preferences.ScenePreferenceListener;
import org.eclipse.gef3d.tools.CameraTool;
import org.eclipse.gef3d.ui.parts.FpsStatusLineItem;
import org.eclipse.gmf.runtime.diagram.ui.actions.ActionIds;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IDiagramPreferenceSupport;
import org.eclipse.gmf.runtime.diagram.ui.internal.parts.DiagramGraphicalViewerKeyHandler;
import org.eclipse.gmf.runtime.diagram.ui.internal.parts.DirectEditKeyHandler;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.providers.DiagramContextMenuProvider;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.EditPartService;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeTypes;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.uml2.diagram.usecase.part.DiagramEditorContextMenuProvider;
import org.eclipse.uml2.diagram.usecase.part.UMLDiagramEditor;

/**
 * UMLDiagramEditor3D
 * There should really be more documentation here.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Apr 7, 2009
 */
public class UMLDiagramEditor3D extends UMLDiagramEditor {

	
	private ScenePreferenceListener sceneListener;

	/**
	 * A reference to the 3D diagram graphical viewer.
	 */
	protected DiagramGraphicalViewer3D viewer3D;
	
	
	/**
	 * 
	 */
	public UMLDiagramEditor3D() {
		// this is a hack:
		MapModeTypes.DEFAULT_MM = MapModeTypes.IDENTITY_MM;
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
	 * @see org.eclipse.uml2.diagram.clazz.part.UMLDiagramEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		
		{ // GraphicalEditor
			getGraphicalViewer().getControl().setBackground(ColorConstants.listBackground);
		}
		{ // DiagramEditor
			IDiagramGraphicalViewer viewer = getDiagramGraphicalViewer();

	        RootEditPart rootEP = EditPartService.getInstance().createRootEditPart(
	            getDiagram());
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
	        ContextMenuProvider provider = new DiagramContextMenuProvider(this,
	            viewer);
	        viewer.setContextMenu(provider);
	        getSite().registerContextMenu(ActionIds.DIAGRAM_EDITOR_CONTEXT_MENU,
	            provider, viewer);
	        KeyHandler viewerKeyHandler = new DiagramGraphicalViewerKeyHandler(viewer)
	            .setParent(getKeyHandler());
	        viewer.setKeyHandler(new DirectEditKeyHandler(viewer)
	            .setParent(viewerKeyHandler));
//	        ((FigureCanvas) viewer.getControl())
//            .setScrollBarVisibility(FigureCanvas.ALWAYS);
		}
		
		
		{ // org.eclipse.uml2.diagram.clazz.part.UMLDiagramEditor#configureGraphicalViewer()
			DiagramEditorContextMenuProvider provider = new DiagramEditorContextMenuProvider(this, getDiagramGraphicalViewer());
			getDiagramGraphicalViewer().setContextMenu(provider);
			getSite().registerContextMenu(ActionIds.DIAGRAM_EDITOR_CONTEXT_MENU, provider, getDiagramGraphicalViewer());
		}
	}
	
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.GraphicalEditor3DWithPalette#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createGraphicalViewer(Composite i_parent) {

		viewer3D = new DiagramGraphicalViewer3D();

		// 1:1 from GraphicalEditor.createGraphicalViewer(Composite)
		Control control = viewer3D.createControl3D(i_parent);
		setGraphicalViewer(viewer3D);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();

		IEditorSite editorSite = getEditorSite();
		IActionBars actionBars = editorSite.getActionBars();
		IStatusLineManager statusLine = actionBars.getStatusLineManager();

		FpsStatusLineItem fpsCounter = new FpsStatusLineItem();
		LightweightSystem3D lightweightSystem3D = viewer3D
				.getLightweightSystem3D();
		lightweightSystem3D.addRendererListener(fpsCounter);

		statusLine.add(fpsCounter);

		sceneListener = new ScenePreferenceListener(viewer3D);
		sceneListener.start();

		control.addDisposeListener(lightweightSystem3D);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.feu.gef3d.ecoretools.diagram.part.EcoreDiagramEditor#createPaletteRoot(org.eclipse.gef.palette.PaletteRoot)
	 */
	@Override
	protected PaletteRoot createPaletteRoot(PaletteRoot i_existingPaletteRoot) {

		PaletteRoot root = super.createPaletteRoot(i_existingPaletteRoot);

		PaletteDrawer drawer = new PaletteDrawer("GEF3D");
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

		if (sceneListener != null)
			sceneListener.stop();

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
	
	

}
