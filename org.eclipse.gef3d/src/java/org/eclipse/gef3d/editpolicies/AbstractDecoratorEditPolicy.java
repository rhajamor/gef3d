/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.editpolicies;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

/**
 * A base class for edit policies that decorate their children with satellite
 * edit policies.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 07.09.2009
 */
public abstract class AbstractDecoratorEditPolicy extends AbstractEditPolicy {

	private EditPartListener m_listener;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#activate()
	 */
	@Override
	public void activate() {

		setListener(createChildListener());
		decorateChildren();
		super.activate();
	}

	/**
	 * Creates the edit policies to decorate the given child edit part.
	 * 
	 * @param i_child the child edit part to decorate
	 * @return the edit policies to decorate the given child
	 */
	protected abstract EditPolicy[] createChildEditPolicies(EditPart i_child);

	/**
	 * Creates an edit part listener that decorates all newly added children.
	 * 
	 * @return the listener
	 */
	protected EditPartListener createChildListener() {

		return new EditPartListener.Stub() {
			@Override
			public void childAdded(EditPart i_child, int i_index) {
				decorateChild(i_child);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	@Override
	public void deactivate() {

		setListener(null);
		super.deactivate();
	}

	/**
	 * Decorates the given child with a new instance of this edit policy.
	 * 
	 * @param i_child
	 */
	protected void decorateChild(EditPart i_child) {

		EditPolicy[] policies = createChildEditPolicies(i_child);
		for (EditPolicy policy : policies) {
			Object role = getRole(policy);
			i_child.installEditPolicy(role, policy);
		}
	}

	/**
	 * Decorates the children of this edit part. This method is called upon
	 * activation.
	 */
	@SuppressWarnings("unchecked")
	protected void decorateChildren() {

		List children = getHost().getChildren();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			EditPart child = (EditPart) iterator.next();
			decorateChild(child);
		}
	}

	/**
	 * Returns the role for the given decoration edit policy.
	 * 
	 * @param i_editPolicy the decoration edit policy
	 * @return the role for the given decoration edit policy
	 */
	protected abstract Object getRole(EditPolicy i_editPolicy);

	/**
	 * Sets the edit part listener. The given listener is responsible for
	 * decorating any newly created children.
	 * 
	 * @param i_listener the listener to set
	 */
	protected void setListener(EditPartListener i_listener) {

		if (m_listener != null)
			getHost().removeEditPartListener(m_listener);

		m_listener = i_listener;
		if (m_listener != null)
			getHost().addEditPartListener(m_listener);
	}
}
