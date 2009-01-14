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
package org.eclipse.gef3d.examples.graph.editor.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef3d.editparts.AbstractGraphicalEditPartEx;
import org.eclipse.gef3d.examples.graph.editor.editpolicies.Graph3DLayoutPolicy;
import org.eclipse.gef3d.examples.graph.model.Graph;


/**
 * GraphPart for managing graphs. Graphs are the root elements, i.e. the
 * diagram plane. This part can be using in 2D, 2.5D, and 3D mode.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 21.11.2007
 */
public class GraphPart extends AbstractGraphicalEditPartEx implements
		PropertyChangeListener {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List getModelChildren() {
		return ((Graph) getModel()).getVerteces();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.fernuni.gef3d.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new Graph3DLayoutPolicy());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.fernuni.gef3d.EditPart#activate()
	 */
	@Override
	public void activate() {
		if (!isActive()) {
			((Graph) getModel()).addPropertyChangeListener(this);
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
		((Graph) getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent i_Event) {
		String strPropertyName = i_Event.getPropertyName();
		if (Graph.PROPERTY_VERTECES.equals(strPropertyName)) {
			refresh();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		

		IFigure fig = getFigure();

		int numberOfVertices = ((Graph) getModel()).getVerteces().size();

		if (numberOfVertices > 50) {
			if (numberOfVertices > 210)
				fig.setSize(new Dimension(1500, 1300));
			else
				fig.setSize(new Dimension(1000, 700));
		} else {
			fig.setSize(new Dimension(400, 300));
		}
		
		super.refreshVisuals();
	}

}
