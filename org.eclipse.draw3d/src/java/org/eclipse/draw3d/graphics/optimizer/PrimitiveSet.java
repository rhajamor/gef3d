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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.VertexPrimitive;

/**
 * PrimitiveSet There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class PrimitiveSet {

	private PrimitiveSet m_parent;

	private PrimitiveClass m_primitiveClass;

	private List<Primitive> m_primitives = new LinkedList<Primitive>();

	private int m_vertexCount;

	public PrimitiveSet(PrimitiveClass i_primitiveClass) {

		if (i_primitiveClass == null)
			throw new NullPointerException("i_primitiveClass must not be null");

		m_primitiveClass = i_primitiveClass;
	}

	protected PrimitiveSet(PrimitiveSet i_parent,
			PrimitiveClass i_primitiveClass) {

		this(i_primitiveClass);

		if (i_parent == null)
			throw new NullPointerException("i_parent must not be null");

		m_parent = i_parent;
	}

	public boolean add(Primitive i_primitive) {

		if (i_primitive == null)
			throw new NullPointerException("i_primitive must not be null");

		if (!m_primitiveClass.contains(i_primitive))
			if (m_parent != null && !overlaps(i_primitive))
				return m_parent.add(i_primitive);
			else
				return false;

		m_primitives.add(i_primitive);

		if (i_primitive instanceof VertexPrimitive) {
			VertexPrimitive vertexPrimitive = (VertexPrimitive) i_primitive;
			m_vertexCount += vertexPrimitive.getVertexCount();
		}

		return true;
	}

	public int getNumVertices() {

		return m_vertexCount;
	}

	public PrimitiveClass getPrimitiveClass() {

		return m_primitiveClass;
	}

	public List<Primitive> getPrimitives() {

		return Collections.unmodifiableList(m_primitives);
	}

	public List<PrimitiveSet> getSets(List<PrimitiveSet> io_result) {

		List<PrimitiveSet> result = io_result;
		if (result == null)
			result = new LinkedList<PrimitiveSet>();

		if (m_parent != null)
			result = m_parent.getSets(result);

		result.add(this);
		return result;
	}

	public int getSize() {

		return m_primitives.size();
	}

	protected boolean overlaps(Primitive i_candidate) {

		if (i_candidate == null)
			throw new NullPointerException("i_candidate must not be null");

		for (Primitive primitive : m_primitives)
			if (primitive.intersects(i_candidate))
				return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "PrimitiveSet [class=" + m_primitiveClass + ", primitives="
			+ m_primitives.size() + "]";
	}
}
