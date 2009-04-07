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
package org.eclipse.gef3d.examples.uml2.usecase.providers;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef3d.examples.uml2.usecase.edit.parts.UMLEditPartFactory3D;
import org.eclipse.gef3d.examples.uml2.usecase.part.UMLDiagramEditor3D;
import org.eclipse.gef3d.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart3D;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.IEditPartOperation;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.diagram.usecase.providers.UMLEditPartProvider;

/**
 * UMLEditPartProvider3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 7, 2009
 */
public class UMLEditPartProvider3D extends UMLEditPartProvider {

	/**
	 * 
	 */
	public UMLEditPartProvider3D() {
		super(); // sets 2D factory

		setFactory(new UMLEditPartFactory3D());
		setAllowCaching(false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.uml2.diagram.clazz.providers.UMLEditPartProvider#provides(org.eclipse.gmf.runtime.common.core.service.IOperation)
	 */
	@Override
	public synchronized boolean provides(IOperation i_operation) {
		if (!is3D())
			return false;
		return super.provides(i_operation);
	}

	public boolean is3D() {
		Exception ex = new Exception();
		String thisClass = this.getClass().getName();
		String name;
		String myEditor = UMLDiagramEditor3D.class.getName();
		for (StackTraceElement element : ex.getStackTrace()) {
			name = element.getClassName(); 
			if (name.equals(myEditor)) 
				return true;
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
