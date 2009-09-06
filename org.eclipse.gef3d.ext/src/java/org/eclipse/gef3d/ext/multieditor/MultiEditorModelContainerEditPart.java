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

package org.eclipse.gef3d.ext.multieditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.StackLayout3D;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * MultiEditorModelContainerEditPart There should really be more documentation
 * here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 14.01.2008
 */
public class MultiEditorModelContainerEditPart extends
		AbstractGraphicalEditPart implements PropertyChangeListener {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(MultiEditorModelContainerEditPart.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {

		Figure fig = new MultiEditorModelContainerFigure();
		fig.setLayoutManager(new StackLayout3D());

		return fig;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {

		// TODO implement method
		// MultiEditorModelContainerEditPart.createEditPolicies
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {

		MultiEditorModelContainer base = (MultiEditorModelContainer) getModel();
		return base.getModelContainers();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.fernuni.gef3d.EditPart#activate()
	 */
	@Override
	public void activate() {
		if (!isActive()) {
			((MultiEditorModelContainer) getModel())
				.addPropertyChangeListener(this);
		}
		super.activate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		((MultiEditorModelContainer) getModel())
			.removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent i_Event) {
		refreshChildren();
		refreshVisuals(); // this does nothing?!
	}
}
