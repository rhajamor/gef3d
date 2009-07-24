/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.shapes.ConeFigureShape;
import org.eclipse.draw3d.shapes.Shape;

/**
 * A connection decoration that renders a shape.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.05.2008
 */
public class ShapeDecoration extends Figure3D implements RotatableDecoration3D {

    private static final Vector3fImpl TMP_V3 = new Vector3fImpl();

    private static final IVector3f Z_AXIS_NEG = new Vector3fImpl(0, 0, -1);

    private Vector3fImpl m_lastReference = new Vector3fImpl(0, 0, 0);

    /**
     * The shape that represents this decoration visually.
     */
    protected Shape m_shape = new ConeFigureShape(this);

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.Figure3D#render()
     */
    @Override
    public void render(RenderContext renderContext) {

        m_shape.render(renderContext);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.RotatableDecoration#setReferencePoint(org.eclipse.draw2d.geometry.Point)
     */
    public void setReferencePoint(Point i_p) {

        throw new UnsupportedOperationException("reference point must be 3D");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.RotatableDecoration3D#setReferencePoint3D(org.eclipse.draw3d.geometry.Vector3f)
     */
    public void setReferencePoint3D(IVector3f i_reference) {

        if (i_reference == null) {
            if (m_lastReference.x == 0 && m_lastReference.y == 0
                    && m_lastReference.z == 0)
                return;

            getPosition3D().setRotation3D(m_lastReference);
        } else {
            if (i_reference.equals(m_lastReference))
                return;

            TMP_V3.set(getPosition3D().getLocation3D());
            Math3D.sub(i_reference, TMP_V3, TMP_V3);
            Math3D.getEulerAngles(Z_AXIS_NEG, TMP_V3, TMP_V3);

            getPosition3D().setRotation3D(TMP_V3);
            m_lastReference.set(TMP_V3);
        }
    }

    /**
     * Sets the shape of this decoration.
     * 
     * @param i_shape
     *            the shape of this decoration
     */
    public void setShape(Shape i_shape) {

        if (i_shape == null)
            throw new NullPointerException("i_shape must not be null");

        m_shape = i_shape;
    }

}
