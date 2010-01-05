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
package org.eclipse.draw3d.graphics.optimizer.primitive;

import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveBounds;

/**
 * Primitive
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	22.12.2009
 */
public interface Primitive {

	public IMatrix4f getTransformation();

	public PrimitiveBounds getBounds();

	public boolean intersects(Primitive i_primitive);

	public RenderRule getRenderRule();
}
