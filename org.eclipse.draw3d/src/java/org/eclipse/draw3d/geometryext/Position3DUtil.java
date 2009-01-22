/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.geometryext;

import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Transformable;

/**
 * Utility class implementing certain calculation methods for
 * {@link IPosition3D}.
 * 
 * @todo create test case
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Jan 21, 2009
 */
public class Position3DUtil {
	
	
	/**
	 * Creates a relative position, i.e. position is part of a glyph hierarchy
	 * and uses the parents position to calculate its absolute position. 
	 * Note that this creates a new host, which has the given host as
	 * parent. If you have a "real" host, such as a figure, simply use
	 * the constructor in {@link Position3DImpl#Position3DImpl(IHost3D)}.
	 * @param parent must not be null
	 * @return relative position
	 * @see Host3D
	 */
	public static Position3D createRelativePosition(IHost3D parent) {
		if (parent == null) // parameter precondition
			throw new NullPointerException("parent must not be null");
		
		return new Host3D(parent).getPosition3D();
	}
	
	/**
	 * Creates an absolute position, i.e. the position has no parent and
	 * its location, size and rotation are not relative to another position.
	 * @return
	 * @see Position3DImpl#Position3DImpl()
	 */
	public static Position3D createAbsolutePosition() {
		return new Position3DImpl();
	}
	

	/**
	 * Transforms the given transformable from this figure's parent's
	 * coordinates to this figure's coordinates. If the transformable's
	 * coordinates were relative to the parent's coordinates, they will be
	 * relative to this figure's coordinates afterwards.
	 * 
	 * @param position3D the position3D which is used as base
	 * @param i_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public static void transformFromParent(IPosition3D position3D,
			Transformable i_transformable) {
		IVector3f location3D = position3D.getLocation3D();
		float dX = -location3D.getX();
		float dY = -location3D.getY();
		float dZ = -location3D.getZ();

		i_transformable.translate(dX, dY, dZ);
	}

	/**
	 * Transforms the given transformable from this figure's parent's
	 * coordinates to this figure's coordinates. If the transformable's
	 * coordinates were relative to the parent's coordinates, they will be
	 * relative to this figure's coordinates afterwards.
	 * 
	 * @param position3D the position3D which is used as base
	 * @param i_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public static void transformToAbsolute(IPosition3D position3D,
			Transformable io_transformable) {
		IMatrix4f parentLocationMatrix = getParentLocationMatrix(position3D);
		io_transformable.transform(parentLocationMatrix);
	}

	/**
	 * Transforms the given transformable from this figure's parent's
	 * coordinates to this figure's coordinates. If the transformable's
	 * coordinates were relative to the parent's coordinates, they will be
	 * relative to this figure's coordinates afterwards.
	 * 
	 * @param position3D the position3D which is used as base
	 * @param i_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public static void transformToParent(IPosition3D position3D,
			Transformable io_transformable) {

		IVector3f location3D = position3D.getLocation3D();
		float dX = location3D.getX();
		float dY = location3D.getY();
		float dZ = location3D.getZ();

		io_transformable.translate(dX, dY, dZ);
	}

	/**
	 * Transforms the given transformable from this figure's parent's
	 * coordinates to this figure's coordinates. If the transformable's
	 * coordinates were relative to the parent's coordinates, they will be
	 * relative to this figure's coordinates afterwards.
	 * 
	 * @param position3D the position3D which is used as base
	 * @param i_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public static void transformToRelative(IPosition3D position3D,
			Transformable io_transformable) {

		IMatrix4f parentLocationMatrix = getParentLocationMatrix(position3D);
		Matrix4f inverted = Math3D.invert(parentLocationMatrix, null);

		if (inverted == null)
			throw new IllegalStateException("loation matrix cannot be inverted");

		io_transformable.transform(inverted);
	}

	/**
	 * Returns the matrix of given positions parent. If the position does not
	 * have a parent, the identity matrix is returned.
	 * 
	 * @param position3D
	 * @return
	 */
	public static IMatrix4f getParentLocationMatrix(IPosition3D position3D) {
		IMatrix4f parentLocationMatrix = null;
		if ( // position3D.getHost() != null && // must be null, this is only a precaution
				position3D.getHost().getParentHost3D() != null
				&& position3D.getHost().getParentHost3D().getPosition3D() != null) {
			parentLocationMatrix = position3D.getHost().getParentHost3D()
					.getPosition3D().getLocationMatrix();
		}
		if (parentLocationMatrix == null)
			parentLocationMatrix = IMatrix4f.IDENTITY;
		return parentLocationMatrix;

	}

}
