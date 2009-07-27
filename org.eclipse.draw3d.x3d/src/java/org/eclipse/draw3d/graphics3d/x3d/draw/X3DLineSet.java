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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.x3d.X3DPropertyContainer;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DAttribute;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * The abstract superclass for all graphic primitive that consist of a set of
 * lines.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public abstract class X3DLineSet extends X3DShape {

	/**
	 * The vertices of the line set.
	 */
	protected List<Vector3f> m_vertices;

	/**
	 * The coordination node is constructed from the vertices on complete.
	 */
	protected X3DNode m_coordNode;

	/**
	 * The standard constructor.
	 */
	public X3DLineSet() {
		super();

		m_vertices = new ArrayList<Vector3f>();
		m_coordNode = null;
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
			// Set line style on begin
			X3DNode linePropertiesNode = new X3DNode("LineProperties");
			boolean lineDashed = (Boolean) i_command.m_renderingProperties
					.getProperties().get(X3DPropertyContainer.PRP_LINE_DASHED);
			X3DAttribute lineType = new X3DAttribute("linetype",
					lineDashed ? "2" : "1");
			linePropertiesNode.addAttribute(lineType);
			m_appearanceNode.addNode(linePropertiesNode);

			// Set emissive color on begin
			X3DNode materialNode = m_appearanceNode.getNodeByName("Material");
			if (materialNode == null)
				throw new Graphics3DException("Material node not found.");
			Color color = (Color) i_command.getRenderingProperties()
					.getProperties()
					.get(X3DPropertyContainer.PRP_CURRENT_COLOR);
			StringBuilder sb = new StringBuilder();
			sb.append((color.getRed() / 255.0f) + " ");
			sb.append((color.getGreen() / 255.0f) + " ");
			sb.append((color.getBlue() / 255.0f) + " ");
			sb.append((color.getAlpha() / 255.0f));
			materialNode.addAttribute(new X3DAttribute("emissiveColor", sb
					.toString()));
		}

		return false;
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

		X3DNode lineSetNode = new X3DNode("LineSet");
		lineSetNode.addAttribute(new X3DAttribute("vertexCount", ""
				+ m_vertices.size()));
		lineSetNode.addNode((X3DNode) m_coordNode.clone());
		shapeNode.addNode(lineSetNode);

		List<X3DNode> list = new ArrayList<X3DNode>();
		list.add(transformationNode);
		return list.listIterator();
	}
}
