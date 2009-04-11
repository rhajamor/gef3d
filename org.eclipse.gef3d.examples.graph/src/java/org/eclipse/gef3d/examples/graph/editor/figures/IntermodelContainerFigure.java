/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 *    Kristian Duske - multi editor editor
 ******************************************************************************/
package org.eclipse.gef3d.examples.graph.editor.figures;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw3d.ConnectionLayerFactory;
import org.eclipse.draw3d.DispatchingConnectionLayer;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.gef3d.ext.intermodel.IInterModelDiagram;


/**
 * This figure is only used for creating the tree structure and is not visible
 * itself.
 * 
 * @author Kristian Duske, Jens von Pilgrim
 * @version $Revision$
 * @since 22.01.2008
 */
public class IntermodelContainerFigure extends Figure3D implements
		IInterModelDiagram {

	/**
	 * 
	 */
	public IntermodelContainerFigure() {
		connectionLayer = new DispatchingConnectionLayer();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure2DHost3D#getConnectionLayer(org.eclipse.draw3d.ConnectionLayerFactory)
	 */
	@Override
	public ConnectionLayer getConnectionLayer(ConnectionLayerFactory i_clfactory) {

		if (connectionLayer == null && i_clfactory != null)
			connectionLayer = i_clfactory.createConnectionLayer(this);

		return connectionLayer;
	}

	

}
