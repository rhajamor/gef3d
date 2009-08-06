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
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
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
	private static final Logger log = Logger
			.getLogger(MultiEditorModelContainerEditPart.class.getName());

	/**
	 * 
	 */
	public MultiEditorModelContainerEditPart() {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		// TODO create MultiEditorFigure with layout?

		Figure fig = new MultiEditorModelContainerFigure();

		
		return fig;
	}

	public void movePlanes(float delta) {
		IFigure3D fig3D = ((IFigure3D) getFigure());
		IBoundingBox box = fig3D.getBounds3D();
		Vector3f vec = new Vector3fImpl();
		box.getPosition(vec);
		vec.setZ(vec.getZ() + delta);
		fig3D.getPosition3D().setLocation3D(vec);
		refresh();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
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
