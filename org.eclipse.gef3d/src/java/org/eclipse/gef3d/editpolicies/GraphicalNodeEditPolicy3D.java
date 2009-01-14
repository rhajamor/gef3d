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
package org.eclipse.gef3d.editpolicies;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw3d.PolylineConnection3D;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;


/**
 * GraphicalNodeEditPolicy3D, creates a 3D feedback connection line instead of
 * a simple 2D line.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	May 5, 2008
 */
public abstract class GraphicalNodeEditPolicy3D extends GraphicalNodeEditPolicy {

	
	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#createDummyConnection(org.eclipse.gef.Request)
	 */
	@Override
	protected Connection createDummyConnection(Request req) {
		return new PolylineConnection3D();
	}

	
}
