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

import junit.framework.TestCase;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Matrix4f;
import org.eclipse.draw3d.geometry.Matrix4fImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * Test cases for the {@link AbstractSurface} class.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 22.07.2009
 */
public class AbstractSurfaceTest extends TestCase {

    private TestSurface m_surface;

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();

        Vector3f origin = new Vector3fImpl(0, 0, 10);
        Vector3f xAxis = new Vector3fImpl(1, 0, 0);
        Vector3f yAxis = new Vector3fImpl(0, 1, 0);
        Vector3f zAxis = new Vector3fImpl(0, 0, 1);

        Matrix4f rot = new Matrix4fImpl();
        rot.setIdentity();
        Math3D.rotate(new Vector3fImpl((float) Math.PI / 2, 0, 0), rot, rot);

        xAxis.transform(rot);
        yAxis.transform(rot);
        zAxis.transform(rot);

        m_surface = new TestSurface();
        m_surface.set(origin, xAxis, yAxis, zAxis);
    }

    /**
     * Tests the
     * {@link AbstractSurface#getSurfaceLocation2D(org.eclipse.draw3d.geometry.IVector3f, org.eclipse.draw2d.geometry.Point)}
     * method.
     */
    public void testGetSurfaceLocation2D() {

        Vector3f w = new Vector3fImpl(1, 0, 1);
        Point s = m_surface.getSurfaceLocation2D(w, null);
        
        System.out.println(s);
    }
}
