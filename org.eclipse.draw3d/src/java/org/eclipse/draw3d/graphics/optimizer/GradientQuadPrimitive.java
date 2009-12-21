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
package org.eclipse.draw3d.graphics.optimizer;

import org.eclipse.swt.graphics.Color;

/**
 * GradientQuadPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.12.2009
 */
public class GradientQuadPrimitive extends QuadPrimitive {

	private Color m_fromColor;

	private Color m_toColor;

	public GradientQuadPrimitive(float[] i_points, Color i_fromColor,
			Color i_toColor) {

		super(i_points, PrimitiveType.GRADIENT_QUAD);

		m_fromColor = i_fromColor;
		m_toColor = i_toColor;
	}

	public Color getFromColor() {
		return m_fromColor;
	}

	public Color getToColor() {
		return m_toColor;
	}

}
