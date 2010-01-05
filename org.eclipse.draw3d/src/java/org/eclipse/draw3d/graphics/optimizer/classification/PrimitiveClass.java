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
package org.eclipse.draw3d.graphics.optimizer.classification;

import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.RenderRule;

/**
 * PrimitiveClass
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	27.12.2009
 */
public interface PrimitiveClass {

	public boolean contains(Primitive i_primitive);

	public RenderRule getRenderRule();

	public boolean isPolygon();

	public boolean isPolyline();

	public boolean isSolid();

	public boolean isGradient();

	public boolean isOutline();

	public boolean isQuad();

	public boolean isLine();

	public boolean isText();

	public boolean isImage();
}
