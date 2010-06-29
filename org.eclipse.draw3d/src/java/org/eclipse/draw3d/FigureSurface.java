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
package org.eclipse.draw3d;

import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Math3DCache;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * A surface that belongs to a 3D figure.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 10.07.2009
 */
public class FigureSurface extends AbstractSurface {

	private FigureListener m_figureListener = new FigureListener() {

		public void figureMoved(IFigure i_source) {
			coordinateSystemChanged();
		}
	};

	private IFigure3D m_host;

	/**
	 * Creates a new surface for the given figure.
	 * 
	 * @param i_host the host figure of this surface
	 */
	public FigureSurface(IFigure3D i_host) {
		if (i_host == null)
			throw new NullPointerException("i_host must not be null");

		m_host = i_host;
		m_host.addFigureListener(m_figureListener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.AbstractSurface#calculateNormal(org.eclipse.draw3d.geometry.Vector3f)
	 */
	@Override
	protected void calculateNormal(Vector3f io_normal) {
		io_normal.set(IVector3f.Z_AXIS_NEG);
		IMatrix4f rotation = m_host.getPosition3D().getAbsoluteRotationMatrix();
		Math3D.transform(io_normal, rotation, io_normal);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ISurface#findFigureAt(int, int,
	 *      org.eclipse.draw2d.TreeSearch)
	 */
	@SuppressWarnings("unchecked")
	public IFigure findFigureAt(int i_sx, int i_sy, TreeSearch i_search) {
		// host pruned?
		if (i_search != null && i_search.prune(m_host))
			return null;

		IFigure hit = null;

		// try connections first
		ConnectionLayer connectionLayer = m_host.getConnectionLayer(null);
		if (connectionLayer != null) {
			if (i_search != null)
				hit = connectionLayer.findFigureAt(i_sx, i_sy, i_search);
			else
				hit = connectionLayer.findFigureAt(i_sy, i_sy);

			if (hit != null && hit != connectionLayer)
				return hit;
		}

		List<IFigure> children = m_host.getChildren();
		for (IFigure child : children) {
			if (!(child instanceof IFigure3D)) {
				if (i_search != null)
					hit = child.findFigureAt(i_sx, i_sy, i_search);
				else
					hit = child.findFigureAt(i_sx, i_sy);

				if (hit != null)
					return hit;
			}
		}

		// now we have only found a 3D figure, and we must check whether it
		// is accepted by the search
		if (i_search == null || i_search.accept(m_host))
			return m_host;

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ISurface#getHost()
	 */
	public IFigure3D getHost() {
		return m_host;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.AbstractSurface#getOrigin(org.eclipse.draw3d.geometry.Vector3f)
	 */
	@Override
	protected Vector3f getOrigin(Vector3f io_result) {
		Vector3f result = io_result;
		if (result == null)
			result = new Vector3fImpl();

		Position3D position3d = getHost().getPosition3D();
		IMatrix4f matrix = position3d.getRotationLocationMatrix();

		result.set(IVector3f.NULLVEC3f);
		result.transform(matrix);

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ISurface#getSurfaceRotation(org.eclipse.draw3d.geometry.Vector3f)
	 */
	public Vector3f getSurfaceRotation(Vector3f o_result) {
		Vector3f result = o_result;
		if (result == null)
			result = new Vector3fImpl();

		Position3D absolute = Draw3DCache.getPosition3D();
		try {
			m_host.getPosition3D().getAbsolute(absolute);
			result.set(absolute.getRotation3D());

			return result;
		} finally {
			Draw3DCache.returnPosition3D(absolute);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.AbstractSurface#getXAxis(org.eclipse.draw3d.geometry.Vector3f)
	 */
	@Override
	protected Vector3f getXAxis(Vector3f io_result) {
		Vector3f result = io_result;
		if (result == null)
			result = new Vector3fImpl();

		result.set(IVector3f.X_AXIS);
		rotateVector(result);

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.AbstractSurface#getYAxis(org.eclipse.draw3d.geometry.Vector3f)
	 */
	@Override
	protected Vector3f getYAxis(Vector3f io_result) {
		Vector3f result = io_result;
		if (result == null)
			result = new Vector3fImpl();

		result.set(IVector3f.Y_AXIS);
		rotateVector(result);

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.AbstractSurface#getZAxis(org.eclipse.draw3d.geometry.Vector3f)
	 */
	@Override
	protected Vector3f getZAxis(Vector3f io_result) {
		Vector3f result = io_result;
		if (result == null)
			result = new Vector3fImpl();

		result.set(IVector3f.Z_AXIS);
		rotateVector(result);

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ISurface#is2DHost()
	 */
	public boolean is2DHost() {
		return true;
	}

	private void rotateVector(Vector3f i_vector) {
		Matrix4f rot = Math3DCache.getMatrix4f();
		try {
			IVector3f angles = m_host.getPosition3D().getRotation3D();
			Math3D.rotate(angles, IMatrix4f.IDENTITY, rot);

			i_vector.transform(rot);
		} finally {
			Math3DCache.returnMatrix4f(rot);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		b.append("Figure surface for host [");
		b.append(m_host);
		b.append("]");

		return b.toString();
	}
}
