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
package org.eclipse.draw3d.graphics3d.x3d.draw;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DAttribute;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * A triangle strip set graphic primitive.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.06.2009
 */
public class X3DTriangleStripSet extends X3DTriangleSet {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DTriangleSet#getNodeIterator()
	 */
	@Override
	public ListIterator<X3DNode> getNodeIterator() {

		X3DNode transformationNode = getTransformationNode();
		X3DNode shapeNode = transformationNode.getNodeByName("Shape");

		if (shapeNode == null)
			throw new Graphics3DException("Shape node not found.");

		X3DNode triangleFanSet = new X3DNode("TriangleStripSet");

		if (m_vertices.size() < 3)
			throw new Graphics3DException(
					"Triangle strip set must have at least 3 coordinates");

		triangleFanSet.addNode(m_coordNode);

		if (m_textureCoordNode != null)
			triangleFanSet.addNode(m_textureCoordNode);

		triangleFanSet.addAttribute(new X3DAttribute("stripCount", m_vertices
				.size()));

		shapeNode.addNode(triangleFanSet);

		List<X3DNode> list = new ArrayList<X3DNode>();
		list.add(transformationNode);

		return list.listIterator();
	}
}
