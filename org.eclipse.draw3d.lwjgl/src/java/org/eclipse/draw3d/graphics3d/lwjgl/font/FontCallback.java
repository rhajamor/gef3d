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
package org.eclipse.draw3d.graphics3d.lwjgl.font;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.Vector2fImpl;
import org.eclipse.draw3d.graphics3d.lwjgl.font.LwjglVectorFont.VectorCharData;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

class FontCallback extends GLUtessellatorCallbackAdapter {

	private VectorCharData m_charData = new VectorCharData();

	private int m_curType;

	private List<IVector2f> m_curVerts = new LinkedList<IVector2f>();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#begin(int)
	 */
	@Override
	public void begin(int i_type) {
		m_curType = i_type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#combine(double[],
	 *      java.lang.Object[], float[], java.lang.Object[])
	 */
	@Override
	public void combine(double[] i_coords, Object[] i_data,
		float[] i_weight, Object[] i_outData) {

		i_outData[0] =
			new Vector2fImpl((float) i_coords[0], (float) i_coords[1]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#end()
	 */
	@Override
	public void end() {
		m_charData.addPrimitive(m_curType, m_curVerts);
		m_curVerts.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#error(int)
	 */
	@Override
	public void error(int i_errnum) {
		throw new RuntimeException(
			"caught error during polygon tesselation: " + i_errnum);
	}

	public VectorCharData getCharData() {
		return m_charData;
	}

	public void reset() {
		m_curVerts.clear();
		m_charData = new VectorCharData();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lwjgl.util.glu.GLUtessellatorCallbackAdapter#vertex(java.lang.Object)
	 */
	@Override
	public void vertex(Object i_vertexData) {
		m_curVerts.add((IVector2f) i_vertexData);
	}
}