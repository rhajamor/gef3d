/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.shapes;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.picking.Query;

/**
 * A shape that contains other shapes and does no rendering itself. Instead,
 * rendering is delegated to the child shapes in the order they were added.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 31.03.2008
 */
public class CompositeShape implements Shape {

	private List<Shape> m_shapes = new LinkedList<Shape>();

	/**
	 * Adds the given shape to this composite. If the given shape was already
	 * added to this composite, it will be added again.
	 * 
	 * @param i_shape the shape to add
	 * @throws NullPointerException if the given shape is <code>null</code>
	 */
	public void add(Shape i_shape) {

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		m_shapes.add(i_shape);
	}

	/**
	 * Removes the given shape from this composite. If the given shape is not
	 * part of this composite, it is ignored. If the given shaped was added to
	 * this composite more than once, the first occurence of the given shape
	 * within this composite is removed, much like the {@link List} interface
	 * describes.
	 * 
	 * @param i_shape the shape to remove
	 * @throws NullPointerException if the given shape is <code>null</code>
	 */
	public void remove(Shape i_shape) {

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		m_shapes.remove(i_shape);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#render()
	 */
	public void render(RenderContext renderContext) {

		for (Shape shape : m_shapes)
			shape.render(renderContext);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#getDistance(org.eclipse.draw3d.picking.Query,
	 *      org.eclipse.draw3d.geometry.Position3D)
	 */
	public float getDistance(Query i_query, Position3D i_position) {

		float distance = Float.NaN;
		for (Shape shape : m_shapes)
			distance =
				Math3D.minDistance(distance, shape.getDistance(i_query,
					i_position));

		return distance;
	}
}
