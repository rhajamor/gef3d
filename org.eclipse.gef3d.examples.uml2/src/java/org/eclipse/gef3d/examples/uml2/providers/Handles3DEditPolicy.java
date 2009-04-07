/*
 * Type:    de.feu.gef3d.topcased.modeler.uml.classdiagram.Handles3DEditPolicy
 * File:  	Handles3DEditPolicy.java
 * Project:	de.feu.gef3d.topcased
 * Date: 	May 5, 2008
 * Author: 	Jens von Pilgrim
 * Version:	$Revision$
 * Changed: $Date$ by $Author$ 
 * URL:     $HeadURL$
 *
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.gef3d.examples.uml2.providers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef3d.editpolicies.ResizableEditPolicy3D;

/**
 * Handles3DEditPolicy There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 5, 2008
 * @see $HeadURL:
 *      https://gorgo.fernuni-hagen.de/OpenglGEF/trunk/de.feu.gef3d.topcased
 *      /src/java/de/feu/gef3d/topcased/modeler/uml/classdiagram/policies/
 *      Handles3DEditPolicy.java $
 */
public class Handles3DEditPolicy extends AbstractEditPolicy {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(Handles3DEditPolicy.class.getName());

	private EditPartListener listener;

	/**
	 * Extends activate() to allow proper decoration of children.
	 * 
	 * @see org.eclipse.gef.EditPolicy#activate()
	 */
	public void activate() {
		setListener(createListener());
		decorateChildren();
		super.activate();
	}

	/**
	 * Returns the "satellite" EditPolicy used to decorate the child.
	 * 
	 * @param child the child EditPart
	 * @return an EditPolicy to be installed as the
	 *         {@link EditPolicy#PRIMARY_DRAG_ROLE}
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		return new ResizableEditPolicy3D();
	}

	/**
	 * creates the EditPartListener for observing when children are added to the
	 * host.
	 * 
	 * @return EditPartListener
	 */
	protected EditPartListener createListener() {
		return new EditPartListener.Stub() {
			public void childAdded(EditPart child, int index) {
				decorateChild(child);
			}
		};
	}

	/**
	 * Overrides deactivate to remove the EditPartListener.
	 * 
	 * @see org.eclipse.gef.EditPolicy#deactivate()
	 */
	public void deactivate() {
		// if (sizeOnDropFeedback != null) {
		// removeFeedback(sizeOnDropFeedback);
		// sizeOnDropFeedback = null;
		// }
		setListener(null);
		super.deactivate();
	}

	/**
	 * Decorates the child with a {@link EditPolicy#PRIMARY_DRAG_ROLE} such as
	 * {@link ResizableEditPolicy}.
	 * 
	 * @param child the child EditPart being decorated
	 */
	protected void decorateChild(EditPart child) {

		EditPolicy policy = createChildEditPolicy(child);

		child.installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, policy);

		if (log.isLoggable(Level.FINE))
			log.fine("Decorated child " + child.getClass().getName()
					+ " with :" + policy);
	}

	/**
	 * Decorates all existing children. This method is called on activation.
	 */
	protected void decorateChildren() {
		List children = getHost().getChildren();
		for (int i = 0; i < children.size(); i++)
			decorateChild((EditPart) children.get(i));
	}

	protected void setListener(EditPartListener listener) {
		if (this.listener != null)
			getHost().removeEditPartListener(this.listener);
		this.listener = listener;
		if (this.listener != null)
			getHost().addEditPartListener(this.listener);
	}

}