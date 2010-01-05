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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.graphics.GraphicsState;
import org.eclipse.swt.graphics.Image;

/**
 * ImagePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class ImagePrimitive extends QuadPrimitive {

	private Image m_image;

	private Rectangle m_source;

	private Rectangle m_target;

	public ImagePrimitive(GraphicsState i_state, Image i_image,
			Rectangle i_source, Rectangle i_target) {

		super(i_state.getTransformation(), new ImageRenderRule(i_state),
			getVertices(i_target.x, i_target.y, i_target.width,
				i_target.height, false));

		m_image = i_image;
		m_source = i_source;
		m_target = i_target;
	}

	public Image getImage() {

		return m_image;
	}

	public Rectangle getSource() {

		return m_source;
	}

	public Rectangle getTarget() {

		return m_target;
	}
}
