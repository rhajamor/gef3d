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

import java.util.Collection;

import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * An equilateral triangle. The following assumptions are made about the
 * vertices of this triangle:
 * <ul>
 * <li>The are on the surface of a sphere with a radius of 1.</li>
 * <li>The center of the sphere is the origin of the coordinate system.</li>
 * <li>The triangle made up of the vertices is equilateral.</li>
 * </ul>
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 04.06.2009
 */
public class SphereTriangle {

	private IVector3f m_a;

	private IVector3f m_b;

	private IVector3f m_c;

	/**
	 * @return the a
	 */
	public IVector3f getA() {
		return m_a;
	}

	/**
	 * @return the b
	 */
	public IVector3f getB() {
		return m_b;
	}

	/**
	 * @return the c
	 */
	public IVector3f getC() {
		return m_c;
	}

	/**
	 * Creates a new sphere triangle with the given vertices.
	 * 
	 * @param i_a
	 *            the first vertex
	 * @param i_b
	 *            the second vertex
	 * @param i_c
	 *            the third vertex
	 */
	public SphereTriangle(IVector3f i_a, IVector3f i_b, IVector3f i_c) {

		m_a = i_a;
		m_b = i_b;
		m_c = i_c;
	}
	
	/** 
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder b = new StringBuilder();
		
		b.append("SphereTriangle [A:");
		b.append(m_a);
		b.append(", B:");
		b.append(m_b);
		b.append(", C:");
		b.append(m_c);
		b.append("]\n");
		
		return b.toString();
	}

	/**
	 * Divides this triangle into 4 equilateral triangles whose vertices are
	 * located on the sphere with radius 1. The new triangle vertices d, e and f
	 * are inserted in the following way:
	 * 
	 * <pre>
	 *                    d
	 *       c     a ---------- b
	 *      /\       \   /\   /
	 *     /  \       \ /  \ /
	 *  f /----\ e   f \----/ e
	 *   / \  / \       \  /
	 * a/___\/___\b      \/
	 *       d           c
	 * </pre>
	 * 
	 * @return an array containing the subtriangles in the following order: fec, adf,
	 *         fed, dbe
	 */
	public SphereTriangle[] divide() {

		Vector3f d = new Vector3fImpl();
		Math3D.scale(0.5f, Math3D.add(m_a, m_b, d), d);
		Math3D.normalise(d, d);

		Vector3f e = new Vector3fImpl();
		Math3D.scale(0.5f, Math3D.add(m_b, m_c, e), e);
		Math3D.normalise(e, e);

		Vector3f f = new Vector3fImpl();
		Math3D.scale(0.5f, Math3D.add(m_c, m_a, f), f);
		Math3D.normalise(f, f);

		SphereTriangle[] result = new SphereTriangle[4];
		result[0] = new SphereTriangle(f, e, m_c);
		result[1] = new SphereTriangle(m_a, d, f);
		result[2] = new SphereTriangle(f, e, d);
		result[3] = new SphereTriangle(d, m_b, e);

		return result;
	}
}
