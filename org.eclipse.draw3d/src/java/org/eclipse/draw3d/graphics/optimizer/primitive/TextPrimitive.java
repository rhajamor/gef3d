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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.graphics.GraphicsState;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveBounds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

/**
 * TextPrimitive There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.12.2009
 */
public class TextPrimitive extends AbstractPrimitive {

	private boolean m_expand;

	private Font m_font;

	private Point m_position;

	private String m_text;

	public TextPrimitive(GraphicsState i_state, String i_text,
			boolean i_expand, Point i_position) {

		super(i_state.getTransformation(), new TextRenderRule(i_state));

		m_font = i_state.getFont();
		m_text = i_text;
		m_expand = i_expand;
		m_position = i_position;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.primitive.AbstractPrimitive#calculateBounds()
	 */
	@Override
	protected PrimitiveBounds calculateBounds() {

		Device device = m_font.getDevice();
		Image image = new Image(device, 1, 1);
		GC gc = new GC(image);

		try {
			org.eclipse.swt.graphics.Point extent =
				gc.textExtent(m_text, m_expand ? SWT.DRAW_DELIMITER
					| SWT.DRAW_TAB : 0);
			int w = extent.x;
			int h = extent.y;

			return new PrimitiveBounds(getTransformedVertices(m_position.x,
				m_position.y, w, h));
		} finally {
			gc.dispose();
			image.dispose();
		}
	}

	public Point getPosition() {
		return m_position;
	}

	public String getText() {
		return m_text;
	}

	public boolean isExpand() {
		return m_expand;
	}

}
