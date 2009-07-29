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

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector2fImpl;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * A triangle set graphic primitive.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.06.2009
 */
public class X3DTriangleSet extends X3DShape {

	/**
	 * The coordinates node.
	 */
	protected X3DNode m_coordNode;

	/**
	 * The texture coordinates, if any.
	 */
	protected List<IVector2f> m_texCoords;

	/**
	 * The texture coordinates node.
	 */
	protected X3DNode m_textureCoordNode;

	/**
	 * The list of vertices in this triangle set.
	 */
	protected List<IVector3f> m_vertices;

	/**
	 * Creates a new triangle set.
	 */
	public X3DTriangleSet() {

		super();
		m_vertices = new ArrayList<IVector3f>();
		m_texCoords = new ArrayList<IVector2f>();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DShape#complete()
	 */
	@Override
	protected void complete() {

		super.complete();

		if (!m_vertices.isEmpty())
			m_coordNode = X3DNode.createCoordinateNode(m_vertices);

		if (!m_texCoords.isEmpty())
			m_textureCoordNode = X3DNode
					.createTextureCoordinateNode(m_texCoords);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DShape#draw(org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawCommand)
	 */
	@Override
	public boolean draw(X3DDrawCommand i_command) {

		super.draw(i_command);
		if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_VERTEX3F)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 3);
			float x = 0, y = 0, z = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];
			z = (Float) parameter[2];

			m_vertices.add(new Vector3fImpl(x, y, z));
		} else if (i_command.getName()
				.equals(X3DDrawCommand.CMD_NAME_TEXCOORDS)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 2);
			float x = 0, y = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];

			m_texCoords.add(new Vector2fImpl(x, y));
		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_END)) {
			complete();
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawTarget#getNodeIterator()
	 */
	public ListIterator<X3DNode> getNodeIterator() {

		X3DNode transformationNode = getTransformationNode();
		X3DNode shapeNode = transformationNode.getNodeByName("Shape");

		if (shapeNode == null)
			throw new Graphics3DException("Shape node not found.");

		X3DNode triangleSet = new X3DNode("TriangleSet");

		if (m_coordNode == null)
			throw new Graphics3DException("Triangle set has no coordinates");

		triangleSet.addNode(m_coordNode);

		if (m_textureCoordNode != null)
			triangleSet.addNode(m_textureCoordNode);

		shapeNode.addNode(triangleSet);

		List<X3DNode> list = new ArrayList<X3DNode>();
		list.add(transformationNode);
		return list.listIterator();
	}

}
