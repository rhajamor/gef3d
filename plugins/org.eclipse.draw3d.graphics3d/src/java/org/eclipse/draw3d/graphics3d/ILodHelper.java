/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * LodContext There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.01.2010
 */
public interface ILodHelper {

	public float getNormalizedArea(IVector3f i_position, IVector2f i_size,
		IVector3f i_normal);

	public float getNormalizedDistance(IVector3f i_position, IVector2f i_size,
		IVector3f i_normal);
}
