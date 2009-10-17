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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef3d.handles.ConnectionEndPointHandle3D;

/**
 * 3D version of {@link ConnectionEndpointEditPolicy}.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 17.10.2009
 */
public class ConnectionEndpointEditPolicy3D extends
		ConnectionEndpointEditPolicy {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy#createSelectionHandles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List createSelectionHandles() {

		List<Handle> result = new LinkedList<Handle>();

		ConnectionEditPart connEditPart = (ConnectionEditPart) getHost();

		result.add(new ConnectionEndPointHandle3D(connEditPart,
			ConnectionLocator.SOURCE));
		result.add(new ConnectionEndPointHandle3D(connEditPart,
			ConnectionLocator.TARGET));

		return result;
	}
}
