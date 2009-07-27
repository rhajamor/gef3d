/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthias Thiele - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.graphics3d.x3d.draw;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.x3d.X3DPropertyContainer;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DAttribute;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * A quad graphic primitive.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DQuad extends X3DShape {

	/**
	 * The list of vertices (typically 4) which define the Quads corners.
	 */
	protected List<Vector3f> m_vertices;

	/**
	 * The coordinates of the texture if there is one. m_texCoords[n]
	 * corresponds to m_vertices[n].
	 */
	protected List<Point> m_texCoords;

	/**
	 * The normal vector of the quad.
	 */
	protected Vector3f m_normalVector;

	/**
	 * The X3D node which is constructed from the vertices on complete.
	 */
	protected X3DNode m_coordNode;

	/**
	 * The X3D node which is constructed from the texture coordinates on
	 * complete.
	 */
	protected X3DNode m_textureCoordNode;

	/**
	 * Constructs a Quad.
	 */
	public X3DQuad() {
		super();
		m_vertices = new ArrayList<Vector3f>();
		m_texCoords = new ArrayList<Point>();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DShape#draw(org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawCommand)
	 */
	@Override
	public boolean draw(X3DDrawCommand i_command) {

		super.draw(i_command);
		if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_BEGIN)) {
			// Set normals on begin
			Vector3f n = (Vector3f) i_command.getRenderingProperties()
					.getProperties().get(X3DPropertyContainer.PRP_NORMAL);

			if (n != null) {
				m_normalVector = new Vector3fImpl(n);
			}

		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_VERTEX2F)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 2);
			float x = 0, y = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];

			addVertex(x, y, 0);

		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_VERTEX3F)) {

			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 3);
			float x = 0, y = 0, z = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];
			z = (Float) parameter[2];

			addVertex(x, y, z);
		} else if (i_command.getName()
				.equals(X3DDrawCommand.CMD_NAME_TEXCOORDS)) {
			Object[] parameter = getParameters(
					i_command.getParameterIterator(), 2);
			float x = 0, y = 0;

			x = (Float) parameter[0];
			y = (Float) parameter[1];

			addTexCoord(x, y);
		} else if (i_command.getName().equals(X3DDrawCommand.CMD_NAME_END)) {
			complete();
		}

		if (m_vertices.size() == 4) {
			// Completed on 4 vertices, return true to get exchanged against a
			// new Quad
			return true;
		}

		return false;
	}

	/**
	 * Adds a new vertex to the list.
	 * 
	 * @param i_x X coordinate of the new vertex.
	 * @param i_y Y coordinate of the new vertex.
	 * @param i_z Z coordinate of the new vertex.
	 */
	private void addVertex(float i_x, float i_y, float i_z) {
		m_vertices.add(new Vector3fImpl(i_x, i_y, i_z));
	}

	/**
	 * Adds a new texture coordinate to the list.
	 * 
	 * @param i_x X coordinate of the new texture coordinate.
	 * @param i_y Y coordinate of the new texture coordinate.
	 */
	private void addTexCoord(float i_x, float i_y) {
		m_texCoords.add(new Point(i_x, i_y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DShape#complete()
	 */
	@Override
	protected void complete() {
		super.complete();

		// Create the coordinates node
		m_coordNode = new X3DNode("Coordinate");

		StringBuilder points = new StringBuilder();
		int i = 0;

		for (Vector3f point : m_vertices) {
			points.append(point.getX() + " ");
			points.append(point.getY() + " ");
			points.append(point.getZ());

			if (++i < m_vertices.size()) {
				points.append(", ");
			}
		}
		m_coordNode.addAttribute(new X3DAttribute("point", points.toString()));

		// Create the texture coordinate node
		m_textureCoordNode = new X3DNode("TextureCoordinate");
		points = new StringBuilder();
		i = 0;
		for (Point point : m_texCoords) {
			points.append(point.x + " ");
			points.append(point.y);

			if (++i < m_texCoords.size()) {
				points.append(", ");
			}
		}

		m_textureCoordNode.addAttribute(new X3DAttribute("point", points
				.toString()));
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

		X3DNode indexedFaceSet = new X3DNode("IndexedFaceSet");
		indexedFaceSet.addAttribute(new X3DAttribute("coordIndex", "0 1 2 3"));

		X3DNode normalNode = new X3DNode("Normal");
		normalNode.addAttribute(new X3DAttribute("vector", m_normalVector
				.getX()
				+ " " + m_normalVector.getY() + " " + m_normalVector.getZ()));

		indexedFaceSet.addNode(m_textureCoordNode);
		indexedFaceSet.addNode(m_coordNode);
		// It is recommended to let the X3D browser calculate the normals itself
		// indexedFaceSet.addNode(normalNode);
		shapeNode.addNode(indexedFaceSet);

		List<X3DNode> list = new ArrayList<X3DNode>();
		list.add(transformationNode);
		return list.listIterator();
	}
}
