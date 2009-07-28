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
package org.eclipse.draw3d;

import org.eclipse.draw2d.ConnectionLayer;

/**
 * A 3D figure that can act as a host for 2D children.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 25.07.2009
 */
public interface IHostFigure3D extends IFigure3D {

    /**
     * Returns this figure's connection layer. If no layer is set yet but
     * connection layers are supported, the factory is used for creating the
     * layer lazily. This method is used by {@link DispatchingConnectionLayer}.
     * If this figure doesn't support connections, null is returned. The
     * {@link DispatchingConnectionLayer} uses the first available connection
     * layer.
     * 
     * @param i_clfactory
     *            the factory used or <code>null</code>, if no layer should be
     *            created lazily
     * @return the connection layer or <code>null</code> if this figure does not
     *         support connections
     * @see ConnectionLayerFactory#singleton
     */
    public ConnectionLayer getConnectionLayer(ConnectionLayerFactory i_clfactory);

    /**
     * Indicates whether the 2D children of this figure need to be repainted.
     * 
     * @return <code>true</code> if the 2D children of this figure need to be
     *         repainted and <code>false</code> otherwise
     */
    public boolean isDirty();
}
