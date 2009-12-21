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

import org.eclipse.swt.graphics.Image;

/**
 * ImagePrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 16.12.2009
 */
public class ImagePrimitive extends QuadPrimitive {

	private Image m_sImg;

	private int m_sX;

	private int m_sY;

	private int m_sW;

	private int m_sH;

	public Image getImage() {

		return m_sImg;
	}

	public int getSourceX() {

		return m_sX;
	}

	public int getSourceY() {

		return m_sY;
	}

	public int getSourceWidth() {

		return m_sW;
	}

	public int getSourceHeight() {

		return m_sH;
	}

	/**
	 * @param i_points
	 * @param i_filled
	 */
	public ImagePrimitive(float[] i_points, Image i_sImg, int i_sX, int i_sY,
			int i_sW, int i_sH) {

		super(i_points, PrimitiveType.IMAGE);

		m_sImg = i_sImg;
		m_sX = i_sX;
		m_sY = i_sY;
		m_sW = i_sW;
		m_sH = i_sH;
	}

}
