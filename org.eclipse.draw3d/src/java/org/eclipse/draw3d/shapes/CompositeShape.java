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
package org.eclipse.draw3d.shapes;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.ParaxialBoundingBox;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * A positionable shape that is composed of other shapes.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.08.2009
 */
public class CompositeShape extends PositionableShape {

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(CompositeShape.class.getName());

	private Set<Shape> m_opaqueShapes;

	private Set<TransparentShape> m_superimposedShapes;

	private Set<TransparentShape> m_transparentShapes;

	/**
	 * Creates a new composite shape.
	 */
	public CompositeShape() {

		this(null);
	}

	/**
	 * Creates a new composite shape. The given position is applied to all
	 * children.
	 * 
	 * @param i_position3D the position of this shape
	 */
	public CompositeShape(IPosition3D i_position3D) {

		super(i_position3D);

		if (i_position3D != null)
			log
				.warning("attention, composite positionable shapes do not properly implement getDistance yet");
	}

	/**
	 * Adds the given shape to this composite. If the given shape was already
	 * added to this composite, even with a different render type, it will be
	 * ignored.
	 * 
	 * @param i_shape the shape to add
	 * @throws NullPointerException if the given shape is <code>null</code>
	 */
	public void addOpaque(Shape i_shape) {

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		if (!contains(i_shape)) {
			if (m_opaqueShapes == null)
				m_opaqueShapes = new HashSet<Shape>();
			m_opaqueShapes.add(i_shape);
		}
	}

	/**
	 * Adds the given shape to this composite. If the given shape was already
	 * added to this composite, even with a different render type, it will be
	 * ignored.
	 * 
	 * @param i_shape the shape to add
	 * @throws NullPointerException if the given shape is <code>null</code>
	 */
	public void addSuperimposed(TransparentShape i_shape) {

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		if (!contains(i_shape)) {
			if (m_superimposedShapes == null)
				m_superimposedShapes = new HashSet<TransparentShape>();
			m_superimposedShapes.add(i_shape);
		}
	}

	/**
	 * Adds the given shape to this composite. If the given shape was already
	 * added to this composite, even with a different render type, it will be
	 * ignored.
	 * 
	 * @param i_shape the shape to add
	 * @throws NullPointerException if the given shape is <code>null</code>
	 */
	public void addTransparent(TransparentShape i_shape) {

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		if (!contains(i_shape)) {
			if (m_transparentShapes == null)
				m_transparentShapes = new HashSet<TransparentShape>();
			m_transparentShapes.add(i_shape);
		}
	}

	/**
	 * Indicates whether this composite shape contains the given shape.
	 * 
	 * @param i_shape the shape to check
	 * @return if this composite shape contains the given shape
	 * @throws NullPointerException if the given shape is <code>null</code>
	 */
	public boolean contains(Shape i_shape) {

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		if (m_opaqueShapes != null && m_opaqueShapes.contains(i_shape))
			return true;

		if (m_transparentShapes != null
			&& m_transparentShapes.contains(i_shape))
			return true;

		if (m_superimposedShapes != null
			&& m_superimposedShapes.contains(i_shape))
			return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#doGetDistance(org.eclipse.draw3d.picking.Query)
	 */
	@Override
	protected float doGetDistance(Query i_query) {

		float d = Float.NaN;
		if (m_opaqueShapes != null)
			for (Shape shape : m_opaqueShapes)
				d = Math3D.minDistance(d, shape.getDistance(i_query));

		if (m_transparentShapes != null)
			for (Shape shape : m_transparentShapes)
				d = Math3D.minDistance(d, shape.getDistance(i_query));

		if (m_superimposedShapes != null)
			for (Shape shape : m_superimposedShapes)
				d = Math3D.minDistance(d, shape.getDistance(i_query));

		return d;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#doRender(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	protected void doRender(RenderContext i_renderContext) {

		if (m_opaqueShapes != null)
			for (Shape shape : m_opaqueShapes)
				shape.render(i_renderContext);

		if (m_transparentShapes != null)
			for (TransparentShape shape : m_transparentShapes)
				i_renderContext.addTransparentObject(shape);

		if (m_superimposedShapes != null)
			for (TransparentShape shape : m_superimposedShapes)
				i_renderContext.addSuperimposedObject(shape);
	}

	private void transformParaxialBoundingBox(ParaxialBoundingBox i_pBounds) {

		if (getPosition3D() == null)
			return;

		Vector3f start = Draw3DCache.getVector3f();
		Vector3f end = Draw3DCache.getVector3f();
		try {
			i_pBounds.getLocation(start);
			i_pBounds.getEnd(end);

			IMatrix4f matrix = getPosition3D().getTransformationMatrix();
			start.transform(matrix);
			end.transform(matrix);

			i_pBounds.setLocation(start);
			i_pBounds.setEnd(end);
		} finally {
			Draw3DCache.returnVector3f(start, end);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#getParaxialBoundingBox(org.eclipse.draw3d.geometry.ParaxialBoundingBox)
	 */
	@Override
	public ParaxialBoundingBox getParaxialBoundingBox(
		ParaxialBoundingBox o_result) {

		if (getPosition3D() != null)
			super.getParaxialBoundingBox(o_result);

		if ((m_opaqueShapes == null || m_opaqueShapes.isEmpty())
			&& (m_transparentShapes == null || m_transparentShapes.isEmpty())
			&& (m_superimposedShapes == null || m_superimposedShapes.isEmpty()))
			return null;

		ParaxialBoundingBox result = null;
		ParaxialBoundingBox tmp = Draw3DCache.getParaxialBoundingBox();
		try {
			if (m_opaqueShapes != null)
				for (Shape shape : m_opaqueShapes) {
					ParaxialBoundingBox shapeBounds =
						shape.getParaxialBoundingBox(tmp);

					if (shapeBounds != null) {
						transformParaxialBoundingBox(tmp);
						if (result == null) {
							result = Draw3DCache.getParaxialBoundingBox();
							result.set(tmp);
						} else
							result.union(tmp);
					}
				}

			if (m_transparentShapes != null)
				for (Shape shape : m_transparentShapes) {
					ParaxialBoundingBox shapeBounds =
						shape.getParaxialBoundingBox(tmp);

					if (shapeBounds != null) {
						transformParaxialBoundingBox(tmp);
						if (result == null) {
							result = Draw3DCache.getParaxialBoundingBox();
							result.set(tmp);
						} else
							result.union(tmp);
					}
				}

			if (m_superimposedShapes != null)
				for (Shape shape : m_superimposedShapes) {
					ParaxialBoundingBox shapeBounds =
						shape.getParaxialBoundingBox(tmp);

					if (shapeBounds != null) {
						transformParaxialBoundingBox(tmp);
						if (result == null) {
							result = Draw3DCache.getParaxialBoundingBox();
							result.set(tmp);
						} else
							result.union(tmp);
					}
				}
		} finally {
			Draw3DCache.returnParaxialBoundingBox(tmp);
		}

		if (result == null)
			return null;

		if (o_result == null)
			return result;

		o_result.set(result);
		Draw3DCache.returnParaxialBoundingBox(result);

		return o_result;
	}

	/**
	 * Removes the given shape from this composite. If the given shape is not
	 * part of this composite, it is ignored.
	 * 
	 * @param i_shape the shape to remove
	 * @throws NullPointerException if the given shape is <code>null</code>
	 */
	public void remove(Shape i_shape) {

		if (i_shape == null)
			throw new NullPointerException("i_shape must not be null");

		if (m_opaqueShapes == null || !m_opaqueShapes.remove(i_shape))
			if (m_transparentShapes == null
				|| !m_transparentShapes.remove(i_shape))
				if (m_superimposedShapes != null)
					m_superimposedShapes.remove(i_shape);
	}
}
