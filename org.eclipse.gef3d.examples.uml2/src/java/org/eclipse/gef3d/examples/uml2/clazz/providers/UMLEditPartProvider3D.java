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
package org.eclipse.gef3d.examples.uml2.clazz.providers;

import org.eclipse.gef.RootEditPart;
import org.eclipse.gef3d.examples.uml2.clazz.edit.parts.UMLEditPartFactory3D;
import org.eclipse.gef3d.examples.uml2.clazz.part.UMLDiagramEditor3D;
import org.eclipse.gef3d.examples.uml2.multi.part.MultiGraphicalEditor3D;
import org.eclipse.gef3d.ext.multieditor.MultiEditorPartFactory;
import org.eclipse.gef3d.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart3D;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.CreateGraphicEditPartOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.CreateRootEditPartOperation;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.diagram.clazz.providers.UMLEditPartProvider;

/**
 * UMLEditPartProvider3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 7, 2009
 */
public class UMLEditPartProvider3D extends UMLEditPartProvider {

	public static String[] SUPPORTED_EDITORS =
		{ UMLDiagramEditor3D.class.getName() };

	/**
	 * 
	 */
	public UMLEditPartProvider3D() {
		super(); // sets 2D factory

		setFactory(new UMLEditPartFactory3D());
		setAllowCaching(false);
	}

	/**
	 * Returns true if editor is supported (see {@link #SUPPORTED_EDITORS} and
	 * if the operation is an {@link CreateRootEditPartOperation}.
	 * 
	 * @see org.eclipse.uml2.diagram.clazz.providers.UMLEditPartProvider#provides(org.eclipse.gmf.runtime.common.core.service.IOperation)
	 */
	@Override
	public synchronized boolean provides(IOperation i_operation) {
		if (!isSupported())
			return false;
		if (i_operation instanceof CreateRootEditPartOperation // in case of UML
				// editor 3D example
				|| i_operation instanceof CreateGraphicEditPartOperation // in
		// case of multi editor 3D example
		)
			return true;
		return super.provides(i_operation);
	}

	/**
	 * Tests if the editor using this provider is supported. This method
	 * actually is a hack and we have to find a better solution.
	 * 
	 * @return
	 */
	public boolean isSupported() {
		Exception ex = new Exception();
		// ex.printStackTrace();
		String name;
		for (StackTraceElement element : ex.getStackTrace()) {
			name = element.getClassName();
			if (name.startsWith(" org.eclipse.ui"))
				break;
			for (int i = 0; i < SUPPORTED_EDITORS.length; i++) {
				if (name.equals(SUPPORTED_EDITORS[i]))
					return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider#getDiagramEditPartClass(org.eclipse.gmf.runtime.notation.View)
	 */
	@Override
	protected Class getDiagramEditPartClass(View i_view) {
		// TODO implement method UMLEditPartProvider3D.getDiagramEditPartClass
		return super.getDiagramEditPartClass(i_view);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider#createRootEditPart(org.eclipse.gmf.runtime.notation.Diagram)
	 */
	@Override
	public RootEditPart createRootEditPart(Diagram i_diagram) {
		return new DiagramRootEditPart3D();
	}

}
