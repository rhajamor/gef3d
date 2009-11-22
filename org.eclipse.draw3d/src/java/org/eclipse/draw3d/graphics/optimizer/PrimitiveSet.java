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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * PrimitiveSet There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public class PrimitiveSet {

	private PrimitiveSet m_parent;

	private Collection<Primitive> m_primitives;

	private PrimitiveType m_type;

	public boolean add(Primitive i_primitive) {

		if (i_primitive == null)
			throw new NullPointerException("i_primitive must not be null");

		if (m_type != i_primitive.getType())
			if (m_parent != null && !overlaps(i_primitive))
				return m_parent.add(i_primitive);
			else
				return false;

		m_primitives.add(i_primitive);
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

	protected PrimitiveType getType() {

		return m_type;
	}

	protected boolean overlaps(Primitive i_candidate) {

		if (i_candidate == null)
			throw new NullPointerException("i_candidate must not be null");

		for (Primitive primitive : m_primitives)
			if (primitive.intersects(i_candidate))
				return true;

		return false;
	}
}
