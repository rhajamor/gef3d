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

package org.eclipse.gef3d.ext.assimilator;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

/**
 * Calls the assimilator to modify the edit parts (hosts) policies the first
 * time this policy is activated or otherwise used by the edit part.
 * This policy is installed by a {@link BorgEditPartFactory}.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Apr 8, 2008
 */
public class AssimulatorPolicy extends AbstractEditPolicy {
	
	public final static String ASSIMILATOR_POLICY_ROLE =
		"Assimilator Role";
	
	IEditPartAssimilator assimilator;
	
	boolean bWaitForActivation;
	

	
	/**
	 * @param i_io_editPart
	 * @param i_assimilator
	 */
	public AssimulatorPolicy(EditPart io_editPart,
			IEditPartAssimilator i_assimilator) {
		if (io_editPart == null) // parameter precondition
			throw new NullPointerException("io_editPart must not be null");
		if (i_assimilator == null) // parameter precondition
			throw new NullPointerException("i_assimilator must not be null");
		
		assimilator = i_assimilator;
		bWaitForActivation = false;
		setHost(io_editPart);
	}





	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#activate()
	 */
	@Override
	public void activate() {
		modifyPolicies();
	}


	


	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	@Override
	public Command getCommand(Request i_request) {
		modifyPolicies();
		return null;
	}





	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#showSourceFeedback(org.eclipse.gef.Request)
	 */
	@Override
	public void showSourceFeedback(Request i_request) {
		modifyPolicies();
	}





	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#showTargetFeedback(org.eclipse.gef.Request)
	 */
	@Override
	public void showTargetFeedback(Request i_request) {
		modifyPolicies();
	}





	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#understandsRequest(org.eclipse.gef.Request)
	 */
	@Override
	public boolean understandsRequest(Request i_req) {
		modifyPolicies();
		return false;
	}





	/**
	 * 
	 */
	private void modifyPolicies() {
		if (bWaitForActivation) {
			assimilator.modifyPolicies(getHost());
			getHost().removeEditPolicy(ASSIMILATOR_POLICY_ROLE);
		}
	}

	/**
	 * Let this {@link AssimulatorPolicy} wait to be activated.
	 */
	public void waitForActivation() {
		bWaitForActivation = true;
	}
	
	

	
}
