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

package org.eclipse.draw3d.graphics3d.x3d.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * An object of this class represents a node in the X3D structure. A node may
 * contain attributes and children nodes itself.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DNode {

	/**
	 * Each node has an ID, which is also an attribute. This is the key of the
	 * ID attribute.
	 */
	private static String ATTRIBUTE_ID = "ID";

	/**
	 * In order to maintain unique node IDs during an export run, the next free
	 * one is stored in here.
	 */
	private static int nextID = 1;

	/**
	 * Creates a new coordinate node with the given coordinates.
	 * 
	 * @param i_coords
	 *            the coordinate list
	 * @return the node
	 */
	public static X3DNode createCoordinateNode(List<IVector3f> i_coords) {

		X3DNode node = new X3DNode("Coordinate");
		node.addAttribute(new X3DAttribute("points", i_coords));

		return node;
	}

	/**
	 * Creates a new texture coordinate node with the given texture coordinates.
	 * 
	 * @param i_coords
	 *            the coordinate list
	 * @return the node
	 */
	public static X3DNode createTextureCoordinateNode(List<IVector2f> i_coords) {

		X3DNode node = new X3DNode("TextureCoordinate");
		node.addAttribute(new X3DAttribute("points", i_coords));

		return node;
	}

	/**
	 * Gets the next free ID to use for a new node.
	 */
	private static Integer getNextID() {
		return nextID++;
	}

	/**
	 * A ist with the attributes of this nodes.
	 */
	private final List<X3DAttribute> m_attributes;

	/**
	 * A list with the children of this node.
	 */
	private final List<X3DNode> m_children;

	/**
	 * THe name of this node.
	 */
	private final String m_strName;

	/**
	 * Constructs a new node with the given name.
	 * 
	 * @param i_strName
	 *            The node's name.
	 */
	public X3DNode(String i_strName) {
		m_strName = i_strName;
		m_children = new ArrayList<X3DNode>();
		m_attributes = new ArrayList<X3DAttribute>();

		// Every node has to have an ID attribute with unique value to be able
		// to identify a node.
		m_attributes
				.add(new X3DAttribute(ATTRIBUTE_ID, getNextID().toString()));
	}

	/**
	 * Adds a new attribute to the node.
	 * 
	 * @param i_attribute
	 *            The attribute to add.
	 */
	public void addAttribute(X3DAttribute i_attribute) {
		m_attributes.add(i_attribute);
	}

	/**
	 * Adds a new node as a child to this node.
	 * 
	 * @param i_node
	 *            The node to add as a child.
	 */
	public void addNode(X3DNode i_node) {
		m_children.add(i_node);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {

		X3DNode other = new X3DNode(this.m_strName);

		for (X3DAttribute attr : m_attributes) {
			if (attr.getKey() != ATTRIBUTE_ID) {
				other.m_attributes.add((X3DAttribute) attr.clone());
			}
		}

		for (X3DNode node : m_children) {
			other.m_children.add((X3DNode) node.clone());
		}

		return other;
	}

	/**
	 * Finds and returns the ID attribute of this node.
	 * 
	 * @return This node's ID attribute.
	 */
	public X3DAttribute getIDAttribute() {
		for (X3DAttribute attr : m_attributes) {
			if (attr.getKey().equals(ATTRIBUTE_ID)) {
				return attr;
			}
		}

		throw new IllegalStateException("X3DNode has no ID.");
	}

	/**
	 * Gets a node by it's ID. Can be this node or any direct or indirect child
	 * of this node.
	 * 
	 * @param i_id
	 *            The ID to search for.
	 * @return The found node or null, if none is found.
	 */
	public X3DNode getNodeByID(int i_id) {
		if (Integer.parseInt(getIDAttribute().getValue()) == i_id) {
			return this;
		} else {
			for (X3DNode child : m_children) {
				X3DNode match = child.getNodeByID(i_id);
				if (match != null) {
					return match;
				}
			}

			return null;
		}
	}

	/**
	 * Gets a node by it's name. Can be this node or any direct or indirect
	 * child of this node.
	 * 
	 * @param i_name
	 *            The name of the node to search for.
	 * @return The found node or null, if none is found.
	 */

	public X3DNode getNodeByName(String i_name) {
		if (this.m_strName.equalsIgnoreCase(i_name)) {
			return this;
		} else {
			for (X3DNode child : m_children) {
				X3DNode match = child.getNodeByName(i_name);
				if (match != null) {
					return match;
				}
			}

			return null;
		}
	}

	/**
	 * Returns an iterator over the list of children nodes.
	 * 
	 * @return The iterator.
	 */
	public ListIterator<X3DNode> getNodeIterator() {
		return m_children.listIterator();
	}

	/**
	 * This returns a String representation which may be used in the X3D export
	 * file.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Open brackets, write name and all attributes.
		sb.append("<" + m_strName);

		for (X3DAttribute attr : m_attributes) {
			if (attr != getIDAttribute()) {
				sb.append(" " + attr.getKey() + "='" + attr.getValue() + "'");
			}
		}

		// If the node has children: close brackets, append all children, write
		// closing tag.
		// Else just close the brackets with termination sign.
		if (m_children.size() > 0) {
			sb.append(">" + System.getProperty("line.separator"));

			for (X3DNode child : m_children) {
				sb.append(child.toString());
			}

			sb.append("</" + m_strName + ">"
					+ System.getProperty("line.separator"));
		} else {
			sb.append("/>" + System.getProperty("line.separator"));
		}

		return sb.toString();
	}
}
