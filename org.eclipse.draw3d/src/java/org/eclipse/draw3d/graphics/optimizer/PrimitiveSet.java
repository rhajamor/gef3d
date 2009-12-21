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

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw3d.util.BufferUtils;

/**
 * PrimitiveSet There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class PrimitiveSet {

	private Attributes m_attributes;

	private int m_numVertices = 0;

	private PrimitiveSet m_parent;

	private List<Primitive> m_primitives = new LinkedList<Primitive>();

	private PrimitiveType m_type;

	public List<Primitive> getPrimitives() {

		return Collections.unmodifiableList(m_primitives);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "PrimitiveSet [type=" + m_type + ", attributes=" + m_attributes
			+ ", primitives=" + m_primitives.size() + ", vertices: "
			+ m_numVertices + "]";
	}

	public PrimitiveType getType() {

		return m_type;
	}

	public Attributes getAttributes() {

		return m_attributes;
	}

	public int[] getNumVertices() {

		int[] result = new int[m_primitives.size()];
		int i = 0;

		for (Primitive primitive : m_primitives)
			result[i++] = primitive.getNumVertices();

		return result;
	}

	public PrimitiveSet(PrimitiveSet i_parent, PrimitiveType i_type,
			Attributes i_attributes) {

		this(i_type, i_attributes);

		if (i_parent == null)
			throw new NullPointerException("i_parent must not be null");

		m_parent = i_parent;
	}

	public PrimitiveSet(PrimitiveType i_type, Attributes i_attributes) {

		if (i_type == null)
			throw new NullPointerException("i_type must not be null");

		if (i_attributes == null)
			throw new NullPointerException("i_attributes must not be null");

		m_type = i_type;
		m_attributes = i_attributes;
	}

	public boolean add(Primitive i_primitive, Attributes i_attributes) {

		if (i_primitive == null)
			throw new NullPointerException("i_primitive must not be null");

		if (i_attributes == null)
			throw new NullPointerException("i_attributes must not be null");

		if (!m_attributes.equals(i_attributes)
			|| !(m_type.equals(i_primitive.getType())))
			if (m_parent != null && !overlaps(i_primitive))
				return m_parent.add(i_primitive, i_attributes);
			else
				return false;

		m_primitives.add(i_primitive);
		m_numVertices += i_primitive.getNumVertices();

		return true;
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

	public FloatBuffer getVertexBuffer() {

		if (m_primitives.isEmpty())
			return null;

		FloatBuffer buffer = BufferUtils.createFloatBuffer(2 * m_numVertices);
		for (Primitive primitive : m_primitives)
			primitive.getVertices(buffer);

		return buffer;
	}

	protected boolean overlaps(Primitive i_candidate) {

		if (i_candidate == null)
			throw new NullPointerException("i_candidate must not be null");

		for (Primitive primitive : m_primitives)
			if (primitive.intersects(i_candidate))
				return true;

		return false;
	}

	public int getSize() {

		return m_primitives.size();
	}
}
