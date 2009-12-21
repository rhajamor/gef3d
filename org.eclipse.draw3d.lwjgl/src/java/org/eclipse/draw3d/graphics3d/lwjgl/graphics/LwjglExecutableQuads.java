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
package org.eclipse.draw3d.graphics3d.lwjgl.graphics;

import org.eclipse.draw3d.graphics.optimizer.SolidAttributes;
import org.eclipse.draw3d.graphics.optimizer.OutlineAttributes;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveType;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.ColorConverter;
import org.lwjgl.opengl.GL11;

/**
 * LwjglExecutableQuads There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.12.2009
 */
public class LwjglExecutableQuads extends LwjglExecutableVBO {

	private boolean m_filled;

	private float[] m_color = new float[4];

	private int m_numQuads;

	public LwjglExecutableQuads(PrimitiveSet i_primitives) {

		super(i_primitives.getVertexBuffer());

		PrimitiveType type = i_primitives.getType();
		if (!type.isQuad())
			throw new IllegalArgumentException(i_primitives
				+ " does not contain quads");

		m_numQuads = i_primitives.getSize();
		m_filled = type.isFilled();
		if (m_filled) {
			SolidAttributes fa = (SolidAttributes) i_primitives.getAttributes();
			ColorConverter.toFloatArray(fa.getColor(), fa.getAlpha(), m_color);
		} else {
			OutlineAttributes oa =
				(OutlineAttributes) i_primitives.getAttributes();
			ColorConverter.toFloatArray(oa.getColor(), oa.getAlpha(), m_color);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#doExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void doExecute(Graphics3D i_g3d) {

		i_g3d.glColor4f(m_color);

		if (m_filled)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		else
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4 * m_numQuads);
	}
}
