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
package org.eclipse.gef3d.examples.graph.editor.editpolicies;

import static org.eclipse.draw3d.util.CoordinateConverter.screenToSurface;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef3d.editpolicies.XY3DLayoutPolicy;
import org.eclipse.gef3d.examples.graph.editor.commands.CreateVertexCommand;
import org.eclipse.gef3d.examples.graph.model.Graph;


/**
 * Graph3DLayoutPolicy There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 28, 2008
 * 
 */
public class Graph3DLayoutPolicy extends XY3DLayoutPolicy {

	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(Graph3DLayoutPolicy.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart,
	 *      java.lang.Object)
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart i_child,
			Object i_constraint) {
		// TODO implement method
		// Graph3DLayoutPolicy.createChangeConstraintCommand
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest i_request) {

		Graph g = (Graph) this.getHost().getModel();
		Point screen = i_request.getLocation();

		IFigure3D f3D = Figure3DHelper.getAncestor3D(getHostFigure());
		if (f3D != null) {
			Point surface = Point.SINGLETON;
			screenToSurface(screen.x, screen.y, f3D, surface);

			if (log.isLoggable(Level.INFO)) {
				log.info("CreateRequest:" + i_request + " at " + surface); //$NON-NLS-1$
			}

			return new CreateVertexCommand(g, surface.x, surface.y);
		} else {
			// 2D only:
			return new CreateVertexCommand(g, screen.x, screen.y);
		}

	}

}
