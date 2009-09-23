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
package org.eclipse.gef3d.examples.uml2.multi.part;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef3d.ext.multieditor.AbstractMultiEditor3D;
import org.eclipse.gef3d.ext.multieditor.IMultiEditor;
import org.eclipse.gef3d.ext.multieditor.INestableEditor;
import org.eclipse.gef3d.ext.multieditor.MultiEditorModelContainer;
import org.eclipse.gef3d.ext.multieditor.MultiEditorPartFactory;
import org.eclipse.gef3d.ext.multieditor.MultiPaletteRoot;
import org.eclipse.gef3d.ext.multieditor.dnd.EditorInputDropPolicy;
import org.eclipse.gef3d.ext.multieditor.dnd.EditorInputTransferDropTargetListener;
import org.eclipse.gef3d.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart3D;
import org.eclipse.gef3d.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer3D;
import org.eclipse.gef3d.tools.CameraTool;
import org.eclipse.gef3d.ui.parts.GraphicalEditor3DWithFlyoutPalette;
import org.eclipse.gef3d.ui.parts.GraphicalViewer3D;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeTypes;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.AbstractMultiEditor;
import org.osgi.framework.Bundle;

/**
 * Multi 3D editor, content is displayed (and edited) using nested editors.
 * This editor actually is independent from UML2, as nested editors are searched
 * via the extension registry. That is, all content can be displayed with this
 * editor if an {@link INestableEditor} is registered for that type of content.
 * However, the multi editor itself has to be registered to some extensions,
 * and this editor here is registered to the uml-diagram extensions.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 14, 2009
 */
public class MultiGraphicalEditor3D extends AbstractMultiEditor3D
		implements IMultiEditor {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(MultiGraphicalEditor3D.class.getName());


	/**
	 * 
	 */
	public MultiGraphicalEditor3D() {
		// GMF specific:
		MapModeTypes.DEFAULT_MM = MapModeTypes.IDENTITY_MM;
	}

		
	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef3d.ext.multieditor.AbstractMultiEditor3D#createRootEditPart()
	 */
	@Override
	protected RootEditPart createRootEditPart() {
		return new DiagramRootEditPart3D();
	}

	

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.GraphicalEditor3DWithFlyoutPalette#doCreateGraphicalViewer()
	 */
	@Override
	protected GraphicalViewer3D doCreateGraphicalViewer() {
		return new DiagramGraphicalViewer3D();
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef3d.ext.multieditor.IMultiEditor#acceptsInput(org.eclipse.ui.IEditorInput)
	 */
	public boolean acceptsInput(IEditorInput i_editorInput) {
		return ! findNestableEditorClasses(i_editorInput).isEmpty();
	}

		

	
}
