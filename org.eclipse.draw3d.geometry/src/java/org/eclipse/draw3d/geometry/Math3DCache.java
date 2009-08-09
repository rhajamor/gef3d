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
package org.eclipse.draw3d.geometry;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Caches objects that are often used as temporary variables during
 * calculcations.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.07.2009
 */
public class Math3DCache {

	private static final Queue<BoundingBox> m_boundingBox =
		new LinkedList<BoundingBox>();

	private static final Queue<Matrix2f> m_matrix2f =
		new LinkedList<Matrix2f>();

	private static final Queue<Matrix3f> m_matrix3f =
		new LinkedList<Matrix3f>();

	private static final Queue<Matrix4f> m_matrix4f =
		new LinkedList<Matrix4f>();

	private static final Queue<ParaxialBoundingBox> m_paraxialBoundingBox =
		new LinkedList<ParaxialBoundingBox>();

	/**
	 * Synchronize access to the cache queues.
	 */
	protected static boolean m_synchronized = false;

	private static final Queue<Vector2f> m_vector2f =
		new LinkedList<Vector2f>();

	private static final Queue<Vector3f> m_vector3f =
		new LinkedList<Vector3f>();

	private static final Queue<Vector4f> m_vector4f =
		new LinkedList<Vector4f>();

	/**
	 * Returns a cached {@link BoundingBox}.
	 * 
	 * @return a cached bounding box
	 */
	public static BoundingBox getBoundingBox() {

		if (m_synchronized) {
			synchronized (m_boundingBox) {
				if (m_boundingBox.isEmpty())
					return new BoundingBoxImpl();
				else
					return m_boundingBox.remove();
			}
		} else {
			if (m_boundingBox.isEmpty())
				return new BoundingBoxImpl();
			else
				return m_boundingBox.remove();
		}
	}

	/**
	 * Returns a cached {@link Matrix2f}.
	 * 
	 * @return a cached matrix
	 */
	public static Matrix2f getMatrix2f() {

		if (m_synchronized) {
			synchronized (m_matrix2f) {
				if (m_matrix2f.isEmpty())
					return new Matrix2fImpl();
				else
					return m_matrix2f.remove();
			}
		} else {
			if (m_matrix2f.isEmpty())
				return new Matrix2fImpl();
			else
				return m_matrix2f.remove();
		}
	}

	/**
	 * Returns a cached {@link Matrix3f}.
	 * 
	 * @return a cached matrix
	 */
	public static Matrix3f getMatrix3f() {

		if (m_synchronized) {
			synchronized (m_matrix3f) {
				if (m_matrix3f.isEmpty())
					return new Matrix3fImpl();
				else
					return m_matrix3f.remove();
			}
		} else {
			if (m_matrix3f.isEmpty())
				return new Matrix3fImpl();
			else
				return m_matrix3f.remove();
		}
	}

	/**
	 * Returns a cached {@link Matrix4f}.
	 * 
	 * @return a cached matrix
	 */
	public static Matrix4f getMatrix4f() {

		if (m_synchronized) {
			synchronized (m_matrix4f) {
				if (m_matrix4f.isEmpty())
					return new Matrix4fImpl();
				else
					return m_matrix4f.remove();
			}
		} else {
			if (m_matrix4f.isEmpty())
				return new Matrix4fImpl();
			else
				return m_matrix4f.remove();
		}
	}

	/**
	 * Returns a cached {@link ParaxialBoundingBox}.
	 * 
	 * @return a cached paraxial bounding box
	 */
	public static ParaxialBoundingBox getParaxialBoundingBox() {

		if (m_synchronized) {
			synchronized (m_paraxialBoundingBox) {
				if (m_paraxialBoundingBox.isEmpty())
					return new ParaxialBoundingBoxImpl();
				else
					return m_paraxialBoundingBox.remove();
			}
		} else {
			if (m_paraxialBoundingBox.isEmpty())
				return new ParaxialBoundingBoxImpl();
			else
				return m_paraxialBoundingBox.remove();
		}
	}

	/**
	 * Returns a cached {@link Vector2f}.
	 * 
	 * @return a cached vector
	 */
	public static Vector2f getVector2f() {

		if (m_synchronized) {
			synchronized (m_vector2f) {
				if (m_vector2f.isEmpty())
					return new Vector2fImpl();
				else
					return m_vector2f.remove();
			}
		} else {
			if (m_vector2f.isEmpty())
				return new Vector2fImpl();
			else
				return m_vector2f.remove();
		}
	}

	/**
	 * Returns a cached {@link Vector3f}.
	 * 
	 * @return a cached vector
	 */
	public static Vector3f getVector3f() {

		if (m_synchronized) {
			synchronized (m_vector3f) {
				if (m_vector3f.isEmpty())
					return new Vector3fImpl();
				else
					return m_vector3f.remove();
			}
		} else {
			if (m_vector3f.isEmpty())
				return new Vector3fImpl();
			else
				return m_vector3f.remove();
		}
	}

	/**
	 * Returns a cached {@link Vector4f}.
	 * 
	 * @return a cached vector
	 */
	public static Vector4f getVector4f() {

		if (m_synchronized) {
			synchronized (m_vector4f) {
				if (m_vector4f.isEmpty())
					return new Vector4fImpl();
				else
					return m_vector4f.remove();
			}
		} else {
			if (m_vector4f.isEmpty())
				return new Vector4fImpl();
			else
				return m_vector4f.remove();
		}
	}

	/**
	 * Returns the given bounding boxes to the cache. If any of the given
	 * bounding boxes is <code>null</code>, it is ignored.
	 * 
	 * @param i_bs the bounding boxes to return
	 */
	public static void returnBoundingBox(BoundingBox... i_bs) {

		if (m_synchronized)
			synchronized (m_boundingBox) {
				for (BoundingBox b : i_bs)
					if (b != null)
						m_boundingBox.offer(b);
			}
		else
			for (BoundingBox b : i_bs)
				if (b != null)
					m_boundingBox.offer(b);
	}

	/**
	 * Returns the given matrices to the cache. If any of the given matrices is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_ms the matrices to return
	 */
	public static void returnMatrix2f(Matrix2f... i_ms) {

		if (m_synchronized)
			synchronized (m_matrix2f) {
				for (Matrix2f m : i_ms)
					if (m != null)
						m_matrix2f.offer(m);
			}
		else
			for (Matrix2f m : i_ms)
				if (m != null)
					m_matrix2f.offer(m);
	}

	/**
	 * Returns the given matrices to the cache. If any of the given matrices is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_ms the matrices to return
	 */
	public static void returnMatrix3f(Matrix3f... i_ms) {

		if (m_synchronized)
			synchronized (m_matrix3f) {
				for (Matrix3f m : i_ms)
					if (m != null)
						m_matrix3f.offer(m);
			}
		else
			for (Matrix3f m : i_ms)
				if (m != null)
					m_matrix3f.offer(m);
	}

	/**
	 * Returns the given matrices to the cache. If any of the given matrices is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_ms the matrices to return
	 */
	public static void returnMatrix4f(Matrix4f... i_ms) {

		if (m_synchronized)
			synchronized (m_matrix4f) {
				for (Matrix4f m : i_ms)
					if (m != null)
						m_matrix4f.offer(m);
			}
		else
			for (Matrix4f m : i_ms)
				if (m != null)
					m_matrix4f.offer(m);
	}

	/**
	 * Returns the given paraxial bounding boxes to the cache. If any of the
	 * given paraxial bounding boxes is <code>null</code>, it is ignored.
	 * 
	 * @param i_ps the paraxial bounding boxes to return
	 */
	public static void returnParaxialBoundingBox(ParaxialBoundingBox... i_ps) {

		if (m_synchronized)
			synchronized (m_paraxialBoundingBox) {
				for (ParaxialBoundingBox p : i_ps)
					if (p != null)
						m_paraxialBoundingBox.offer(p);
			}
		else
			for (ParaxialBoundingBox p : i_ps)
				if (p != null)
					m_paraxialBoundingBox.offer(p);
	}

	/**
	 * Returns the given vectors to the cache. If any of the given vectors is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_vs the vectors to return
	 */
	public static void returnVector2f(Vector2f... i_vs) {

		if (m_synchronized)
			synchronized (m_vector2f) {
				for (Vector2f v : i_vs)
					if (v != null)
						m_vector2f.offer(v);
			}
		else
			for (Vector2f v : i_vs)
				if (v != null)
					m_vector2f.offer(v);
	}

	/**
	 * Returns the given vectors to the cache. If any of the given vectors is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_vs the vectors to return
	 */
	public static void returnVector3f(Vector3f... i_vs) {

		if (m_synchronized)
			synchronized (m_vector3f) {
				for (Vector3f v : i_vs)
					if (v != null)
						m_vector3f.offer(v);
			}
		else
			for (Vector3f v : i_vs)
				if (v != null)
					m_vector3f.offer(v);
	}

	/**
	 * Returns the given vectors to the cache. If any of the given vectors is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_vs the vectors to return
	 */
	public static void returnVector4f(Vector4f... i_vs) {

		if (m_synchronized)
			synchronized (m_vector4f) {
				for (Vector4f v : i_vs)
					if (v != null)
						m_vector4f.offer(v);
			}
		else
			for (Vector4f v : i_vs)
				if (v != null)
					m_vector4f.offer(v);
	}
}
