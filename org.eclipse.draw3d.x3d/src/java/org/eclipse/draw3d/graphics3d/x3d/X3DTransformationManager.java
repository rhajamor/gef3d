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

package org.eclipse.draw3d.graphics3d.x3d;

import java.util.Stack;

import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Position3DImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DAttribute;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * This class intends to handle all problems related to transformations. At all
 * times, it can therefore provide the current transformation node, which can
 * group different graphic primitives with the same translation.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DTransformationManager {

	/**
	 * This is the current position (the one on top of the stack).
	 */
	private IPosition3D m_currentPosition;

	/**
	 * A stack with positions, the one on top is the current position.
	 */
	private final Stack<IPosition3D> m_positionStack;

	/**
	 * The default constructor.
	 */
	public X3DTransformationManager() {
		m_positionStack = new Stack<IPosition3D>();
		pushPosition();
	}

	/**
	 * Translates the current position's location by the given x, y, z values.
	 * 
	 * @param i_x Translation in X
	 * @param i_y Translation in Y
	 * @param i_z Translation in Z
	 */
	public void translate(float i_x, float i_y, int i_z) {

		if (m_currentPosition == null) {
			throw new Graphics3DException(
					"Illegal translate before setPosition.");
		}

		Math3D.translate(m_currentPosition.getLocation3D(), i_x, i_y, i_z,
				(Vector3f) m_currentPosition.getLocation3D());
	}

	/**
	 * Pushed a new dummy position onto the stack.
	 */
	public void pushPosition() {
		m_positionStack.push(new Position3DImpl());
		m_currentPosition = m_positionStack.peek();
	}

	/**
	 * Pops the position on top off the stack.
	 */
	public void popPosition() {
		m_positionStack.pop();
		m_currentPosition = m_positionStack.peek();
	}

	/**
	 * Gets the current transformation Node.
	 * 
	 * @return The current transformation Node.
	 */
	public X3DNode getTransformationNode() {
		X3DNode node = new X3DNode("Transform");

		IVector3f translation = m_currentPosition.getLocation3D();
		StringBuilder sb = new StringBuilder();
		sb.append(translation.getX() + " ");
		sb.append(translation.getY() + " ");
		sb.append(translation.getZ());
		node.addAttribute(new X3DAttribute("translation", sb.toString()));

		IVector3f rotation = m_currentPosition.getRotation3D();
		sb = new StringBuilder();
		sb.append(rotation.getX() + " ");
		sb.append(rotation.getY() + " ");
		sb.append(rotation.getZ());

		IVector3f scale = m_currentPosition.getSize3D();
		sb = new StringBuilder();
		float x = scale.getX() > 0 ? scale.getX() : 1.0f;
		sb.append(x + " ");
		float y = scale.getY() > 0 ? scale.getY() : 1.0f;
		sb.append(y + " ");
		float z = scale.getZ() > 0 ? scale.getZ() : 1.0f;
		sb.append(z);
		node.addAttribute(new X3DAttribute("scale", sb.toString()));

		return node;
	}

	/**
	 * Exchanges the current position against the specified one.
	 * 
	 * @param i_position This position is set as the current one.
	 */
	public void setPosition(IPosition3D i_position) {
		m_positionStack.set(m_positionStack.size() - 1, i_position);
		m_currentPosition = m_positionStack.peek();
	}

	/**
	 * Gets the current position.
	 * 
	 * @return The current position.
	 */
	public IPosition3D getPosition() {
		return m_currentPosition;
	}

	/**
	 * Sets the current position to be the default position.
	 */
	public void setIdentity() {
		// Hande setIdentity like pushPosition
		// pushPosition();
	}
}
