/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.ui.parts;

import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.gef.GraphicalViewer;

/**
 * IGraphicalViewer3D
 * There should really be more documentation here.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Apr 15, 2009
 */
public interface GraphicalViewer3D extends GraphicalViewer{
	/**
	 * Returns the 3D light weight system
	 * @return
	 */
	public LightweightSystem3D getLightweightSystem3D();
}
