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
package org.eclipse.gef3d.ext.multieditor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef3d.editparts.ScalableFreeformRootEditPart3D;
import org.eclipse.gef3d.ext.assimilator.BorgEditPartFactory;
import org.eclipse.gef3d.ext.multieditor.dnd.EditorInputDropPolicy;
import org.eclipse.gef3d.ext.multieditor.dnd.EditorInputTransferDropTargetListener;
import org.eclipse.gef3d.tools.CameraTool;
import org.eclipse.gef3d.ui.parts.GraphicalEditor3DWithFlyoutPalette;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.AbstractMultiEditor;
import org.osgi.framework.Bundle;

/**
 * This is an abstract base class for multi editors with GEF3D. All nested
 * editors are to be instances of {@link INestableEditor}, in case of
 * {@link INestableEMFEditor} a {@link ResourceSet} is used for all nested
 * editors. A {@link MultiEditorPartFactory} is used to combine all nested
 * factories, subclasses can combine this factory with other patterns, such as
 * the {@link BorgEditPartFactory}. Subclasses have to implement
 * {@link IMultiEditor#acceptsInput(IEditorInput)}, this method is called during
 * droping a file onto the 3D scene. Nested editors are retrieved by searching
 * the Eclipse extension registry, if other strategies are to be used,
 * {@link #createNestedEditor(IEditorInput)} has to be overridden.
 * <p>
 * In contrast to {@link AbstractMultiEditor}, editors in an 3D editor share the
 * same view.
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Sep 22, 2009
 */
public abstract class AbstractMultiEditor3D extends
		GraphicalEditor3DWithFlyoutPalette implements IMultiEditor {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(AbstractMultiEditor3D.class.getName());

	protected MultiEditorModelContainer m_container;

	protected MultiEditorPartFactory m_multiFactory;

	protected ResourceSet resourceSet;

	protected List<INestableEditor> nestedEditors;

	/**
	 * 
	 */
	public AbstractMultiEditor3D() {

		setEditDomain(new DefaultEditDomain(this));
		resourceSet = new ResourceSetImpl();
		nestedEditors = new ArrayList<INestableEditor>(5);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.examples.graph.editor.GraphEditor3D#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		RootEditPart root = createRootEditPart();
		getGraphicalViewer().setRootEditPart(root);

		m_multiFactory = createMulitFactory();
		getGraphicalViewer().setEditPartFactory(m_multiFactory);
	}

	/**
	 * Default implementation returns a {@link ScalableFreeformRootEditPart3D},
	 * called from {@link #configureGraphicalViewer()}.
	 * 
	 * @return
	 */
	protected RootEditPart createRootEditPart() {
		// we need a special 3D root edit part for connections and feedback
		return new ScalableFreeformRootEditPart3D();
	}

	/**
	 * 
	 */
	protected MultiEditorPartFactory createMulitFactory() {
		return new MultiEditorPartFactory();
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
		viewer.setContents(m_container);

		installDragAndDrop();

		addEditor(getEditorInput());
	}

	/**
	 * 
	 */
	protected void installDragAndDrop() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.addDropTargetListener(new EditorInputTransferDropTargetListener(
			this, viewer));
		viewer.getContents().installEditPolicy(
			EditorInputDropPolicy.EDITOR_INPUT_ROLE,
			new EditorInputDropPolicy());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ext.multieditor.IMultiEditor#addEditor(org.eclipse.ui.IEditorInput)
	 */
	public void addEditor(IEditorInput i_editorInput) {

		// do not add content twice
		for (INestableEditor nestedEditor : nestedEditors) {
			if (nestedEditor.getEditorInput().equals(i_editorInput)
				|| i_editorInput.getName().equals(
					nestedEditor.getEditorInput().getName()))
				return;
		}

		// find appropriate editor
		INestableEditor nestedEditor = createNestedEditor(i_editorInput);

		if (nestedEditor == null) {
			if (log.isLoggable(Level.INFO)) {
				log.info("No nestable editor found for input " //$NON-NLS-1$
					+ i_editorInput);
			}
			return;
		}

		if (nestedEditor instanceof INestableEMFEditor) {
			((INestableEMFEditor) nestedEditor).setResourceSet(resourceSet);
		}

		try {
			nestedEditor.init(getEditorSite(), i_editorInput);
			nestedEditor.initializeAsNested(getGraphicalViewer(),
				m_multiFactory, m_container);
			addNestedPalette(nestedEditor.createPaletteDrawer());

			nestedEditors.add(nestedEditor);
		} catch (PartInitException ex) {
			log.warning("IEditorInput - exception: " + ex); //$NON-NLS-1$
		}
//		getGraphicalViewer().getRootEditPart().refresh();
	}

	/**
	 * Searches matching editor for given input (in extension registry) and
	 * returns an instance of that editor.
	 * 
	 * @param i_editorInput
	 * @return the nestable editor, or null if no matching editor was found
	 */
	protected INestableEditor createNestedEditor(IEditorInput i_editorInput) {
		List<Class> editorClasses = findNestableEditorClasses(i_editorInput);
		for (Class clazz : editorClasses) {
			try {
				Object obj = clazz.newInstance();
				return (INestableEditor) obj;
			} catch (InstantiationException ex) {
				log.warning("Cannot create nested editor " //$NON-NLS-1$
					+ clazz.toString() + ", ex=" + ex); //$NON-NLS-1$
			} catch (IllegalAccessException ex) {
				log.warning("Cannot create nested editor " //$NON-NLS-1$
					+ clazz.toString() + ", ex=" + ex); //$NON-NLS-1$
			}
		}
		return null;
	}

	/**
	 * Returns a list of classes implementing {@link INestableEditor} which are
	 * registered for this editor input.
	 * 
	 * @param i_editorInput
	 * @return
	 */
	protected List<Class> findNestableEditorClasses(IEditorInput i_editorInput) {
		String strName = i_editorInput.getName();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point =
			registry.getExtensionPoint("org.eclipse.ui.editors");
		if (point == null)
			return null;
		IExtension[] extensions = point.getExtensions();

		List<Class> editorClasses = new ArrayList<Class>(5);

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

								Class clazz;
								try {
									clazz = bundle.loadClass(strClassname);
									if (INestableEditor.class
										.isAssignableFrom(clazz)) {
										editorClasses.add(clazz);
									}
								} catch (ClassNotFoundException ex) {
									log.warning("Cannot create nested editor " //$NON-NLS-1$
										+ strClassname + ", ex=" + ex); //$NON-NLS-1$
									ex.printStackTrace();
								}

							}
						}
					}
				}
			}
		}
		return editorClasses;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = null;
		if (getEditDomain() != null
			&& getEditDomain().getPaletteViewer() != null) {
			root = getEditDomain().getPaletteViewer().getPaletteRoot();
		}
		if (root == null) {
			root = createPaletteRoot();
		}
		return root;
	}

	/**
	 * Called by {@link #getPaletteRoot()} to lazily create the palette.
	 * @return
	 */
	protected PaletteRoot createPaletteRoot() {
		PaletteRoot root;
		root = new MultiPaletteRoot();
		PaletteDrawer drawer = new PaletteDrawer("GEF3D");
		drawer.setDescription("General Multi Editor 3D Tools");

		drawer.add(new ToolEntry("Camera", "Camera Tool", null, null,
			CameraTool.class) {
			// nothing to implement
		});

		root.add(drawer);
		return root;
	}

	/**
	 * @param i_createPaletteDrawer
	 */
	protected void addNestedPalette(PaletteDrawer drawer) {
		getPaletteRoot().add(drawer);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		for (INestableEditor editor : nestedEditors) {
			editor.doSave(monitor);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#isDirty()
	 */
	@Override
	public boolean isDirty() {
		for (INestableEditor editor : nestedEditors) {
			if (editor.isDirty())
				return true;
		}
		return false;
	}

}