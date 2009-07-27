/*******************************************************************************
 * Copyright (c) 2008 Kristian Duske and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.ecore.diagram.providers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecoretools.diagram.edit.parts.EPackageContentsEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.CreateEditPoliciesOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.IEditPolicyProvider;

/**
 * EcoreEditPolicyProvider3D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 06.01.2009
 */
public class EcoreEditPolicyProvider3D extends AbstractProvider implements
		IEditPolicyProvider {

	private static final Logger log = Logger
			.getLogger(EcoreEditPolicyProvider3D.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.IEditPolicyProvider#createEditPolicies(org.eclipse.gef.EditPart)
	 */
	public void createEditPolicies(EditPart i_editPart) {

		if (log.isLoggable(Level.INFO))
			log.fine("modifying edit policies of "
					+ i_editPart.getClass().getName());

		i_editPart.installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
				new Handles3DEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.common.core.service.IProvider#provides(org.eclipse.gmf.runtime.common.core.service.IOperation)
	 */
	public boolean provides(IOperation i_operation) {

		if (!(i_operation instanceof CreateEditPoliciesOperation))
			return false;

		CreateEditPoliciesOperation epOp = (CreateEditPoliciesOperation) i_operation;
		EditPart editPart = epOp.getEditPart();

		if (editPart instanceof DiagramEditPart)
			return true;

		if (editPart instanceof EPackageContentsEditPart)
			return true;

		return false;

	}

}
