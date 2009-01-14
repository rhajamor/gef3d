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
import java.util.logging.Logger;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.geometryext.Plane;
import org.eclipse.draw3d.geometryext.SyncedBounds3D;
import org.eclipse.draw3d.geometryext.SyncedVector3f;
import org.eclipse.draw3d.geometry.Transformable;
import org.eclipse.draw3d.util.CoordinateConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Matrix4fImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * 3D version of GEF's Figure. This class extends Figure and can be 
 * used instead of a 2D figure in 2D and 3D editors.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 08.11.2007
 */
public class Figure3D extends Figure implements IFigure3D {

	/**
	 * Logger for this class
	 */
	protected static final Logger log = Logger.getLogger(Figure3D.class
			.getName());

	private static final Vector3fImpl TMP_V3_1 = new Vector3fImpl();

	private static final Vector3fImpl TMP_V3_2 = new Vector3fImpl();

	private static final Vector3fImpl TMP_V3_3 = new Vector3fImpl();

	/**
	 * The 3D bounds of this figure. The 3D bounds are sychronized with the 2d
	 * bounds.
	 */
	protected SyncedBounds3D bounds3D;

	/**
	 * The texture needs to be invalidated every time a child is moved so that
	 * the changes are drawn on the screen.
	 */
	private FigureListener childMovedListener = new FigureListener() {

		public void figureMoved(IFigure i_source) {
			repaint2DComponents = true;
		}
	};

	/**
	 * The connection layer for his figure's 2D children.
	 */
	protected ConnectionLayer connectionLayer = null;

	/**
	 * This figure's friend.
	 */
	protected Figure3DFriend friend;

	/**
	 * This figure's helper.
	 */
	protected Figure3DHelper helper;

	private transient Matrix4fImpl locationMatrix = new Matrix4fImpl();

	/**
	 * The alpha value of this figure.
	 * 
	 * @see IFigure3D#setAlpha(byte)
	 */
	protected int m_alpha = 255;

	private MatrixState matrixState;

	/**
	 * The object matrix is the matrix that transforms a unit cube into the
	 * cuboid that represents this figure's shape in world space. It is used as
	 * the OpenGL modelview matrix when the figure is drawn. <br /> <br /> The
	 * object matrix is derived from
	 * <ol>
	 * <li>the figure's dimension</li>
	 * <li>the figure's rotation</li>
	 * <li>the figure's location</li>
	 * <li>the figure's parent's location</li>
	 * </ol>
	 */
	private transient Matrix4fImpl modelMatrix = new Matrix4fImpl();

	/**
	 * The preferred 3D size of this figure. The preferred 3D size is
	 * synchronized with the preferred 2D size. preferredSize object is created
	 * lazily if it has not been created before in
	 * {@link #setPreferredSize3D(IVector3f)} and
	 * {@link #getPreferredSize3D()}. Thus, if you are using this member in
	 * subclasses directly, remember that it might be null (and will be in most
	 * cases, since it is only used by some special layout managers).
	 */
	protected SyncedVector3f preferredSize3D;

	/**
	 * In
	 */
	protected boolean repaint2DComponents = true;

	/**
	 * The rotation angles ofthis figure.
	 */
	protected Vector3f rotation;

	/**
	 * Boolean semaphore used by {@link #syncSize()} and {@link #syncSize3D()}
	 * to avoid infinite loop.
	 */
	protected boolean updatingBounds = false;

	/**
	 * 
	 */
	public Figure3D() {

		friend = new Figure3DFriend(this) {

			@Override
			public Font getLocalFont() {
				return Figure3D.this.getLocalFont();
			}

			@Override
			public boolean is2DContentDirty() {
				return repaint2DComponents;
			}
		};
		helper = new Figure3DHelper(friend);

		bounds3D = new SyncedBounds3D();
		bounds3D.setDepth(1);
		rotation = new Vector3fImpl(0, 0, 0);

		matrixState = MatrixState.INVALID;
	}

	@Override
	public void add(IFigure i_figure, Object i_constraint, int i_index) {

		super.add(i_figure, i_constraint, i_index);

		// register as figure listener with 2D children so that we know when
		// they move
		if (!(i_figure instanceof IFigure3D)) {
			i_figure.addFigureListener(childMovedListener);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#findFigureAt(int, int,
	 *      org.eclipse.draw2d.TreeSearch)
	 * @see Figure3DHelper#findFigureAt(int, int, TreeSearch)
	 */
	@Override
	public IFigure findFigureAt(int i_x, int i_y, TreeSearch i_search) {

		return helper.findFigureAt(i_x, i_y, i_search);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getAlpha()
	 */
	public int getAlpha() {

		return m_alpha;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getAncestor3D()
	 */
	public IFigure3D getAncestor3D() {
		return Figure3DHelper.getAncestor3D(getParent());
	}

	/**
	 * Returns the object matrix of this figure's closest 3D ancestor. If this
	 * figure does not have any 3D ancestors, the identity matrix is returned.
	 * 
	 * @return the ancestor's object matrix
	 */
	protected IMatrix4f getAncestorLocationMatrix() {

		IFigure3D fig = getAncestor3D();
		if (fig == null)
			return IMatrix4f.IDENTITY;

		return fig.getLocationMatrix();
	}

	// Overriding setBounds instead and update bounds in setSize3D/setLocatoin3D
	//	
	/**
	 * Returns 2D bounds of this figure. If resolution is not disabled, the
	 * bounds are converted from the 3D bounds, simply ignoring the z values of
	 * position and size. Otherwise, the 2D bounds are returned.
	 * 
	 * @see org.eclipse.draw2d.Figure#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		return super.getBounds();
	}

	/**
	 * {@inheritDoc} Returns bounds, i.e. lower left back corner and size. The
	 * coordinates are parent relative coordinates.
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getBounds3D()
	 */
	public IBoundingBox getBounds3D() {
		return bounds3D.getBoundingBox(getBounds());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure2DHost3D#getChildren2D()
	 */
	public List<IFigure> getChildren2D() {

		return helper.getChildren2D();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getChildren3D()
	 */
	public List<IFigure3D> getChildren3D() {

		return helper.getChildren3D();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure2DHost3D#getConnectionLayer(org.eclipse.draw3d.ConnectionLayerFactory)
	 */
	public ConnectionLayer getConnectionLayer(ConnectionLayerFactory i_clfactory) {
		if (connectionLayer == null && i_clfactory != null) {
			connectionLayer = i_clfactory.createConnectionLayer(this);
			// add(connectionLayer); // or else it doesn't have an update
			// manager
		}
		return connectionLayer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getSuccessor3D()
	 */
	public List<IFigure3D> getDescendants3D() {
		return helper.getDescendants3D();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getLocation3D()
	 */
	public IVector3f getLocation3D() {
		return bounds3D.getLocation3D(getBounds());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure2DHost3D#getLocation3D(org.eclipse.draw2d.geometry.Point)
	 */
	public IVector3f getLocation3D(Point i_point2D) {
		return CoordinateConverter.surfaceToWorld(i_point2D.x, i_point2D.y,
				this, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getLocationMatrix()
	 */
	public IMatrix4f getLocationMatrix() {

		recalculateMatrices();
		return locationMatrix;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getMatrixState()
	 */
	public MatrixState getMatrixState() {

		IFigure3D ancestor3D = getAncestor3D();
		if (ancestor3D != null
				&& ancestor3D.getMatrixState() == MatrixState.INVALID)
			matrixState = MatrixState.INVALID;

		return matrixState;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getModelMatrix()
	 */
	public IMatrix4f getModelMatrix() {
		recalculateMatrices();
		return modelMatrix;
	}

	/**
	 * Returns preferred 3D size, this size is synchronized with 2D dimension.
	 * Actually the returned vector is a synchronized version of the 2D object.
	 * {@inheritDoc}
	 * <p>
	 * Internal note: preferredSize object is created lazily if it has not been
	 * created before
	 * </p>
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getPreferredSize3D()
	 */
	public IVector3f getPreferredSize3D() {
		if (preferredSize3D == null) {
			preferredSize3D = new SyncedVector3f();
		}
		return preferredSize3D.getVector3f(getPreferredSize());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getRotation3D()
	 */
	public IVector3f getRotation3D() {
		return rotation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getSize3D()
	 */
	public IVector3f getSize3D() {
		return bounds3D.getSize3D(getBounds());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#getSurfacePlane(float,
	 *      org.eclipse.draw3d.geometryext.Plane)
	 */
	public Plane getSurfacePlane(float i_z, Plane io_result) {

		Plane result = io_result;
		if (result == null)
			result = new Plane();

		TMP_V3_1.set(1, 1, i_z);
		TMP_V3_2.set(0, 1, i_z);
		TMP_V3_3.set(1, 0, i_z);

		getModelMatrix(); // recalculate model matrix if neccessary
		TMP_V3_1.transform(modelMatrix);
		TMP_V3_2.transform(modelMatrix);
		TMP_V3_3.transform(modelMatrix);

		// if the three points are not colinear (non-invertible model
		// matrix), they will be ignored
		result.set(TMP_V3_1, TMP_V3_2, TMP_V3_3);
		return result;
	}

	/**
	 * Returns always true -- clipping and optimized redraw of 3D figures is
	 * handled differently. There is no way to determine whether a 3D figure
	 * intersects with a 2D rectangle. Since this method is used during redraw,
	 * it must return true in order to get 3D figures painted if they are
	 * children of 2D figures. Possible 2D parents include layers and panes,
	 * which are in fact only structural nodes and not real figures.
	 * 
	 * @todo is this really ok?
	 * @see org.eclipse.draw2d.Figure#intersects(org.eclipse.draw2d.geometry.Rectangle)
	 */
	@Override
	public boolean intersects(Rectangle i_rect) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#invalidate()
	 */
	@Override
	public void invalidate() {

		matrixState = MatrixState.INVALID;
		super.invalidate();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method is overridden here because it is neccessary to change the
	 * order of paint operations: The client area must be painted before the
	 * figure itself is painted, because otherwise the texture will not have
	 * been painted yet before it needs to be rendered.
	 * </p>
	 * 
	 * @see org.eclipse.draw2d.Figure#paint(org.eclipse.draw2d.Graphics)
	 */
	@Override
	public void paint(Graphics i_graphics) {

		paintBorder(i_graphics);
		paintClientArea(i_graphics);
		paintFigure(i_graphics);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#paintBorder(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintBorder(Graphics i_graphics) {

		helper.paintBorder(i_graphics);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#paintChildren(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintChildren(Graphics i_graphics) {

		helper.paintChildren(i_graphics);

		RenderContext renderContext = RenderContext.getContext();
		if (renderContext.getMode().isPaint())
			repaint2DComponents = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics i_graphics) {

		helper.paintFigure(i_graphics);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#postrender()
	 */
	public void postrender() {

		// nothing to do
	}

	/**
	 * Recalculates the location matrix.
	 */
	protected void recalculateLocationMatrix() {

		IMatrix4f ancestorLocationMatrix = getAncestorLocationMatrix();
		locationMatrix.set(ancestorLocationMatrix);

		IVector3f location = getLocation3D();
		Math3D.translate(location, locationMatrix, locationMatrix);

		rotate(locationMatrix, rotation);
	}

	/**
	 * Precondition: matrixstate == invalid Postcondition: matrixstate ==
	 * updated
	 */
	public void recalculateMatrices() {

		if (getMatrixState() != MatrixState.INVALID)
			return;

		recalculateLocationMatrix();
		recalculateModelMatrix();

		matrixState = MatrixState.VALID;
	}

	/**
	 * Recalculates the model matrix. Make sure that
	 * {@link #recalculateLocationMatrix()} has been called before calling this
	 * method.
	 */
	protected void recalculateModelMatrix() {
		modelMatrix.set(locationMatrix);
		IVector3f size = getSize3D();
		Math3D.scale(size, modelMatrix, modelMatrix);
	}

	@Override
	public void remove(IFigure i_figure) {

		super.remove(i_figure);
		if (!(i_figure instanceof IFigure3D)) {
			i_figure.removeFigureListener(childMovedListener);
		}
	}

	/**
	 * {@inheritDoc} Render itself.
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#render()
	 */
	public void render() {
		// default implementation renders nothing ;-)
		log.warning("Render method should be overridden! This is a " + //
				this.getClass());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#revalidate()
	 */
	@Override
	public void revalidate() {
		super.revalidate();
		helper.revalidate();
	}

	/**
	 * Rotates serially the given matrix by angles defined in rotation vector.
	 * First, the matrix is rotated around the x axis by the x value of the
	 * vector, then around the y axis by the y value and so on.
	 * 
	 * @param io_matrix
	 * @param i_rotate
	 */
	private static void rotate(Matrix4f io_matrix, IVector3f i_rotate) {

		float yAngle = i_rotate.getY();
		if (yAngle != 0)
			Math3D.rotate(yAngle, IVector3f.Y_AXIS, io_matrix, io_matrix);

		float zAngle = i_rotate.getZ();
		if (zAngle != 0)
			Math3D.rotate(zAngle, IVector3f.Z_AXIS, io_matrix, io_matrix);

		float xAngle = i_rotate.getX();
		if (xAngle != 0)
			Math3D.rotate(xAngle, IVector3f.X_AXIS, io_matrix, io_matrix);

	}

	/**
	 * @see org.eclipse.draw3d.IFigure3D#setAlpha(int)
	 */
	public void setAlpha(int i_alpha) {

		m_alpha = i_alpha;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#setLocation3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setLocation3D(IVector3f i_point) {

		IVector3f loc = getLocation3D();
		boolean translateXY = i_point.getX() != loc.getX()
				|| i_point.getY() != loc.getY();
		boolean translateZ = i_point.getZ() != loc.getZ();

		if (!translateXY && !translateZ)
			return;

		Rectangle newBounds = bounds3D.setBounds3D(i_point, getSize3D());
		if (translateXY)
			setBounds(newBounds);

		matrixState = MatrixState.INVALID;

		if (!translateXY && translateZ) {
			fireFigureMoved();
			repaint();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Internal note: preferredSize object is created lazily if it has not been
	 * created before
	 * </p>
	 * 
	 * @param i_preferredSize3D new preferred size, must not be null and all
	 *            values must not be less 0
	 * @see org.eclipse.draw3d.IFigure3D#setPreferredSize3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setPreferredSize3D(IVector3f i_preferredSize3D) {
		if (i_preferredSize3D == null) // parameter precondition
			throw new NullPointerException("i_preferredSize3D must not be null");
		if (i_preferredSize3D.getX() < 0 || i_preferredSize3D.getY() < 0
				|| i_preferredSize3D.getZ() < 0) // parameter
			// precondition
			throw new IllegalArgumentException(
					"no value of given vector must be less 0, , was "
							+ i_preferredSize3D);

		if (preferredSize3D == null) {
			preferredSize3D = new SyncedVector3f();
		}
		Dimension size = preferredSize3D
				.setVector3fAsDimension(i_preferredSize3D);
		setPreferredSize(size);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#setRotation3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setRotation3D(IVector3f i_rotation) {

		if (rotation.equals(i_rotation))
			return;

		rotation.set(i_rotation);
		matrixState = MatrixState.INVALID;

		fireFigureMoved();
		repaint();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param i_size new size, must not be null, no value must be less 0
	 * @see org.eclipse.draw3d.IFigure3D#setSize3D(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void setSize3D(IVector3f i_size) {
		if (i_size == null) // parameter precondition
			throw new NullPointerException("i_size must not be null");
		if (i_size.getX() < 0 || i_size.getY() < 0 || i_size.getZ() < 0) // parameter
			// precondition
			throw new IllegalArgumentException(
					"no value of given vector must be less 0, , was " + i_size);

		IVector3f size3D = getSize3D();
		boolean resizeXY = i_size.getX() != size3D.getX()
				|| i_size.getY() != size3D.getY();
		boolean resizeZ = i_size.getZ() != size3D.getZ();

		if (!resizeXY && !resizeZ)
			return;

		Rectangle newBounds = bounds3D.setBounds3D(getLocation3D(), i_size);

		if (resizeXY)
			setBounds(newBounds);

		matrixState = MatrixState.INVALID;

		if (!resizeXY && resizeZ) {
			invalidate();
			fireFigureMoved();
			repaint();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append(this.getClass().getName()).append(" at (");
		strb.append(getLocation3D().getX()).append(",").append(
				getLocation3D().getY()).append(",").append(
				getLocation3D().getZ()).append(")");
		strb.append(", size (");
		strb.append(getSize3D().getX()).append(",").append(getSize3D().getY())
				.append(",").append(getSize3D().getZ()).append(")");
		return strb.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#transformFromParent(org.eclipse.draw3d.geometry.Transformable)
	 */
	public void transformFromParent(Transformable i_transformable) {

		IVector3f location3D = getLocation3D();
		float dX = -location3D.getX();
		float dY = -location3D.getY();
		float dZ = -location3D.getZ();

		i_transformable.translate(dX, dY, dZ);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#transformToAbsolute(org.eclipse.draw3d.geometry.Transformable)
	 */
	public void transformToAbsolute(Transformable io_transformable) {
		IMatrix4f matrix = getAncestorLocationMatrix();
		io_transformable.transform(matrix);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#transformToParent(org.eclipse.draw3d.geometry.Transformable)
	 */
	public void transformToParent(Transformable io_transformable) {

		IVector3f location3D = getLocation3D();
		float dX = location3D.getX();
		float dY = location3D.getY();
		float dZ = location3D.getZ();

		io_transformable.translate(dX, dY, dZ);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.IFigure3D#transformToRelative(org.eclipse.draw3d.geometry.Transformable)
	 */
	public void transformToRelative(Transformable io_transformable) {

		IMatrix4f matrix = getAncestorLocationMatrix();
		Matrix4f inverted = Math3D.invert(matrix, null);

		if (inverted == null)
			throw new IllegalStateException("loation matrix cannot be inverted");

		io_transformable.transform(inverted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	@Override
	protected boolean useLocalCoordinates() {

		// otherwise, 2D children get drawn at their absolute positions and are
		// not visible on the texture
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#validate()
	 */
	@Override
	public void validate() {
		super.validate();
		repaint2DComponents = true;
	}
}
