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

/**
 * RenderRule There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public interface RenderRule {

	public boolean isOutline();

	public boolean isSolid();

	public boolean isText();

	public boolean isGradient();

	public boolean isImage();

	public OutlineRenderRule asOutline();

	public SolidRenderRule asSolid();

	public TextRenderRule asText();

	public ImageRenderRule asImage();

	public GradientRenderRule asGradient();
}
