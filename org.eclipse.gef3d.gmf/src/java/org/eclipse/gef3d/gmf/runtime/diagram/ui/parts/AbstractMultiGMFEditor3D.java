/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.gmf.runtime.diagram.ui.parts;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef3d.ext.multieditor.AbstractMultiEditor3D;
import org.eclipse.gef3d.ext.multieditor.INestableEditor;
import org.eclipse.gef3d.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart3D;
import org.eclipse.gef3d.ui.parts.GraphicalViewer3D;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeTypes;
import org.eclipse.gmf.runtime.notation.Diagram;

/**
 * Abstract multi editor for EMF/GMF based diagrams (i.e., nested GMF editors),
 * provides an editing domain. Actually, this editor is feature complete and can
 * directly be used as an editor. However, it is defined abstract, as it is not
 * registered via extension points to any file type. You will have to extend
 * this class and register your own editor to the extension point, e.g.,
 * <code><pre>
 * &lt;extension point="org.eclipse.ui.editors">
 *       &lt;editor
 *          class="org.eclipse.gef3d.examples.uml2.multi.part.MultiGraphicalEditor3D"
 *          default="false"
 *          extensions="umlclass,umlusc,umlact"
 *          id="org.eclipse.gef3d.examples.uml2.multi.part.MultiGraphicalEditor3D"
 *          name="Multi Editor 3D">
 *       &lt;/editor>
 * &lt;/extension>
 * </pre></code> This snippet is taken from the UML3D example, the class defined
 * there simply extends this class and is empty.
 * <p>
 * You may override the {@link #acceptsInput(org.eclipse.ui.IEditorInput)} method, which
 * by default accepts all input for which a nestable editor can be retrieved, see
 * {@link AbstractMultiEditor3D#acceptsInput(org.eclipse.ui.IEditorInput)}.
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 5, 2010
 */
public abstract class AbstractMultiGMFEditor3D extends AbstractMultiEditor3D {

	/**
	 * 
	 */
	public AbstractMultiGMFEditor3D() {
		// GMF specific:
		MapModeTypes.DEFAULT_MM = MapModeTypes.IDENTITY_MM;
	}

	/**
	 * {@inheritDoc}
	 * 
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
	 * {@inheritDoc} Supports the following types:
	 * <ul>
	 * <li>{@link IEditingDomainProvider} -- returns domain provider of this
	 * editor</li>
	 * </ul>
	 * 
	 * @todo return MultiPropertySheetPage, which nests the property sheet pages
	 *       of nested editor
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class type) {

		if (type == IEditingDomainProvider.class) {
			return domainProvider;
		}

		return super.getAdapter(type);
	}

	private IEditingDomainProvider domainProvider =
		new IEditingDomainProvider() {
			public EditingDomain getEditingDomain() {
				return AbstractMultiGMFEditor3D.this.getEditingDomain();
			}
		};

	public TransactionalEditingDomain getEditingDomain() {
		for (INestableEditor nestedEditor : nestedEditors.values()) {
			if (nestedEditor instanceof DiagramEditor) {
				Diagram diagram = ((DiagramEditor) nestedEditor).getDiagram();
				if (diagram != null) {
					TransactionalEditingDomain domain =
						TransactionUtil.getEditingDomain(diagram);
					if (domain != null)
						return domain;
				}
			}
		}
		return null;
	}
}
