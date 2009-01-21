/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Transformable;
import org.eclipse.draw3d.geometryext.Plane;
import org.eclipse.draw3d.geometryext.SyncHost3D;
import org.eclipse.draw3d.geometryext.IPosition3D.MatrixState;

/**
 * 3D extension of GEF's IFigure interface.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 24.10.2007
 */
public interface IFigure3D extends IFigure, IFigure2DHost3D, Renderable, SyncHost3D {

	

	/**
	 * Returns the alpha value of this figure.
	 * 
	 * @return the alpha value of this figure
	 * @author Kristian Duske
	 * @todo See if maybe there is a better place for this method.
	 */
	public int getAlpha();

	/**
	 * Returns first predecessor which is an IFigure3D. This will often be the
	 * parent.
	 * 
	 * @return the 3D ancestor of this figure
	 * @see Figure3DHelper#getAncestor3D()
	 */
	IFigure3D getAncestor3D();

	/**
	 * Returns the smallest box completely enclosing the IFigure. This method is
	 * the 3D equivalent of {@link IFigure#getBounds()}. While GEF's version
	 * returns a mutable class (Rectangle) and forbids to change the returned
	 * object, here an immutable class (interface) is returned avoiding these
	 * problems.
	 * 
	 * @return
	 */
	public IBoundingBox getBounds3D();

	/**
	 * Returns the 3D children of this figure or an empty list if this figure
	 * does not have any 3D children.
	 * 
	 * @return an unmodifiable list containing the 3D children of this figure
	 */
	public List<IFigure3D> getChildren3D();

	/**
	 * Returns the first found IFigure3D successors, this may be the children of
	 * this figure. The result may be cached by the figure, the cache becomes
	 * invalid if successors are modified.
	 * <p>
	 * If a child is an IFigure3D, it is added to the result list. Otherwise,
	 * the children of the (2D) child are checked and added, if they are
	 * instances of IFigure3D. If a child branch contains no IFigure3D
	 * instances, it is ignored.
	 * 
	 * @return
	 * @see Figure3DHelper#getDescendants3D()
	 */
	List<IFigure3D> getDescendants3D();

	/**
	 * @return
	 */
	public IVector3f getLocation3D();

	/**
	 * Returns the matrix that performs the absolute transformation to this
	 * figure's location. This is the matrix that transforms (0, 0, 0) to this
	 * figure's origin.
	 * 
	 * @return the location matrix of this figure
	 */
	public IMatrix4f getLocationMatrix();

	/**
	 * Returns matrix state of internal matrices
	 * 
	 * @return
	 */
	public MatrixState getMatrixState();

	/**
	 * Returns the matrix that performs the transformation of this figure's
	 * basic shape to it's intended shape.
	 * 
	 * @return the model matrix of this figure
	 */
	public IMatrix4f getModelMatrix();

	/**
	 * Returns preferred size of this figure. The preferred size is synchronized
	 * with the preferred 2D size.
	 * 
	 * @return
	 */
	public IVector3f getPreferredSize3D();

	/**
	 * @return
	 */
	public IVector3f getRotation3D();

	/**
	 * Sets width, height and depth of the figure. This is the 3D equivalent to
	 * {@link }
	 * 
	 * @return
	 */
	public IVector3f getSize3D();

	/**
	 * Returns a plane that is parallel to this figure's surface. The given Z
	 * value specifies the vertical offset of the plane where
	 * <ul>
	 * <li>Z &lt; 0: the plane is behind the figure's back face</li>
	 * <li>Z &eq; 0: the plane is coplanar with the figure's back face</li>
	 * <li>0 &lt Z &lt; 1: the plane intersects the figure's volume (e.g. it is
	 * between the figure's back and front face)</li>
	 * <li>Z &eq; 1: the plane is coplanar with the figure's front face</li>
	 * <li>1 &lt; Z: the plane is in front of the figure's front face</li>
	 * </ul>
	 * 
	 * @param i_z the vertical offset of the plane
	 * @param io_result the result plane, if <code>null</code>, a new one will
	 *            be created
	 * @return the plane
	 */
	public Plane getSurfacePlane(float i_z, Plane io_result);

	/**
	 * Sets the alpha value of this figure. This value controls the transparency
	 * of this figure, where 0 means translucent and 255 means opaque.
	 * 
	 * @param alpha the new alpha value (should be between 0 and 255, inclusive)
	 * @author Kristian Duske
	 * @todo See if maybe there is a better place for this method.
	 */
	public void setAlpha(int alpha);

	/**
	 * Sets the location of this IFigure.
	 * 
	 * @param point The new location, this is usually the lower left corner.
	 */
	public void setLocation3D(IVector3f point);

	/**
	 * Sets preferred size of this figure. The preferred size is synchronized
	 * with the preferred 2D size.
	 * 
	 * @param i_preferredSize3D
	 */
	public void setPreferredSize3D(IVector3f i_preferredSize3D);

	/**
	 * Sets rotation of figure, i.e. angles for X, Y and Z axis. Rotations are
	 * applied in the following order: Y first, then Z and finally X.
	 * 
	 * @param rotation
	 */
	public void setRotation3D(IVector3f rotation);

	/**
	 * @param size
	 */
	public void setSize3D(IVector3f size);

	/**
	 * Transforms the given transformable from this figure's parent's
	 * coordinates to this figure's coordinates. If the transformable's
	 * coordinates were relative to the parent's coordinates, they will be
	 * relative to this figure's coordinates afterwards.
	 * 
	 * @param i_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public void transformFromParent(Transformable i_transformable);

	/**
	 * Transforms a transformable whose coordinates are relative to this
	 * figure's parent to absolute coordinates. If the transformable's
	 * coordinates were relative to this figure's parent's coordinates, they
	 * will be absolute afterwards.
	 * 
	 * @param io_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public void transformToAbsolute(Transformable io_transformable);

	/**
	 * Transforms the given transformable from this figure's coordinates to this
	 * figure's parent's coordinates. If the given transformable was relative to
	 * this figure's coordinates, it will be relative to this figure's parent's
	 * coordinates afterwards.
	 * 
	 * @param io_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public void transformToParent(Transformable io_transformable);

	/**
	 * Transforms the given transformable from absolute coordinates to this
	 * figure's parent's local coordinates. If the figure was relative to the
	 * origin, it will be relative to this figure's parent's coordinates
	 * afterwards.
	 * 
	 * @param io_transformable the transformable
	 * @throws NullPointerException if the given transformable is
	 *             <code>null</code>
	 */
	public void transformToRelative(Transformable io_transformable);
}
