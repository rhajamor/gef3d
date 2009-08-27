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
public class MultiGraphicalEditor3D extends GraphicalEditor3DWithFlyoutPalette
		implements IMultiEditor {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(MultiGraphicalEditor3D.class.getName());

	private MultiEditorPartFactory m_multiFactory;

	private MultiEditorModelContainer m_container;

	private Map<String, PaletteDrawer> m_nestedPaletteDrawers;

	/**
	 * 
	 */
	public MultiGraphicalEditor3D() {
		m_nestedPaletteDrawers = new HashMap<String, PaletteDrawer>();

		// GMF specific:
		MapModeTypes.DEFAULT_MM = MapModeTypes.IDENTITY_MM;
		setEditDomain(new DefaultEditDomain(this));

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		MultiPaletteRoot root = new MultiPaletteRoot();
		PaletteDrawer drawer = new PaletteDrawer("GEF3D");
		drawer.setDescription("GEF3D tools");
		drawer.add(new ToolEntry("Camera", "Camera Tool", null, null,
			CameraTool.class) {
			// nothing to implement
		});
		drawer.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
		root.add(0, drawer);

		m_nestedPaletteDrawers.put(drawer.getLabel(), drawer);

		return root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor i_monitor) {
		// TODO implement method MultiGraphicalEditor3D.doSave

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.examples.graph.editor.GraphEditor3D#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {

		super.configureGraphicalViewer();

		// we need a special 3D root edit part for connections and feedback
		RootEditPart root = new DiagramRootEditPart3D();
		getGraphicalViewer().setRootEditPart(root);

		m_multiFactory = new MultiEditorPartFactory();
		getGraphicalViewer().setEditPartFactory(m_multiFactory);

		getGraphicalViewer().addDropTargetListener(
			new EditorInputTransferDropTargetListener(this,
				getGraphicalViewer()));

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {

		m_container = new MultiEditorModelContainer();
		GraphicalViewer viewer = getGraphicalViewer();
		// MultiEditorPartFactory multiFactory = (MultiEditorPartFactory) viewer
		// .getEditPartFactory();
		viewer.setContents(m_container);

		viewer.getContents().installEditPolicy(
			EditorInputDropPolicy.EDITOR_INPUT_ROLE,
			new EditorInputDropPolicy());

		addEditor(getEditorInput());
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
	 * 
	 * @see org.eclipse.gef3d.ext.multieditor.IMultiEditor#addEditor(org.eclipse.ui.IEditorInput)
	 */
	public void addEditor(IEditorInput i_editorInput) {
		// find appropriate editor
		INestableEditor nestedEditor = findEditor(i_editorInput);
		if (nestedEditor == null) {
			if (log.isLoggable(Level.INFO)) {
				log
					.info("IEditorInput - No nestable editor found - i_editorInput=" + i_editorInput); //$NON-NLS-1$
			}

			return;
		}

		try {
			nestedEditor.init(getEditorSite(), i_editorInput);

			nestedEditor.initializeAsNested(getGraphicalViewer(),
				m_multiFactory, m_container);

			addNestedPalette(nestedEditor.createPaletteDrawer());

		} catch (PartInitException ex) {
			log.warning("IEditorInput - exception: " + ex); //$NON-NLS-1$

		}
		getGraphicalViewer().getRootEditPart().refresh();

	}

	/**
	 * @param i_createPaletteDrawer
	 */
	private void addNestedPalette(PaletteDrawer drawer) {
		PaletteRoot root = getEditDomain().getPaletteViewer().getPaletteRoot();

		if (m_nestedPaletteDrawers.containsKey(drawer.getLabel()))
			return;
		root.add(drawer);
		m_nestedPaletteDrawers.put(drawer.getLabel(), drawer);
		getEditDomain().getPaletteViewer();

	}

	/**
	 * Searches matching editor for given input (in extension registry) and
	 * returns an instance of that editor.
	 * @param i_editorInput
	 * @return the nestable editor, or null if no matching editor was found
	 */
	private INestableEditor findEditor(IEditorInput i_editorInput) {
		String strName = i_editorInput.getName();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point =
			registry.getExtensionPoint("org.eclipse.ui.editors");
		if (point == null)
			return null;
		IExtension[] extensions = point.getExtensions();

		for (IExtension extension : extensions) {
			String strContributorName = extension.getContributor().getName();
			// if (log.isLoggable(Level.INFO)) {
			// log.info("Extension found: " + extension
			//					+ ", Contributor: " + strContributorName); //$NON-NLS-1$
			// }

			IConfigurationElement[] ices = extension.getConfigurationElements();
			String ext, token;
			StringTokenizer strt;
			for (IConfigurationElement element : ices) {
				if (element.getName().equals("editor")) {
					ext = element.getAttribute("extensions");
					if (ext != null) {
						strt = new StringTokenizer(ext, ",");
						while (strt.hasMoreTokens()) {
							token = strt.nextToken();
							if (strName.endsWith(token)) {
								Bundle bundle =
									Platform.getBundle(strContributorName);
								String strClassname =
									element.getAttribute("class");
								try {
									Class clazz =
										bundle.loadClass(strClassname);
									if (INestableEditor.class
										.isAssignableFrom(clazz)) {
										Object obj = clazz.newInstance();
										return (INestableEditor) obj;
									}
								} catch (ClassNotFoundException ex) {
									log.warning("Cannot create nested editor " //$NON-NLS-1$
										+ strClassname + ", ex=" + ex);  //$NON-NLS-1$
								} catch (InstantiationException ex) {
									log.warning("Cannot create nested editor " //$NON-NLS-1$
										+ strClassname + ", ex=" + ex);  //$NON-NLS-1$
								} catch (IllegalAccessException ex) {
									log.warning("Cannot create nested editor " //$NON-NLS-1$
										+ strClassname + ", ex=" + ex);  //$NON-NLS-1$
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
}
