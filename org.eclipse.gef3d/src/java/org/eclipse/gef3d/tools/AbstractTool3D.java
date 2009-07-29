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
package org.eclipse.gef3d.tools;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.MouseEvent3D;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef.Tool;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.swt.events.MouseEvent;

/**
 * Abstract base implementation for {@link Tool}s that use 3D coordinates for
 * their work.
 * 
 * @see AbstractTool
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 23.06.2009
 */
public abstract class AbstractTool3D extends AbstractTool {

    /**
     * 3D version of {@link Input}.
     * 
     * @author Kristian Duske
     * @version $Revision$
     * @since 24.06.2009
     */
    public static class Input3D extends Input {

        Point mouseLocation = new Point();

        Vector3f worldLocation = new Vector3fImpl();

        /**
         * Returns the current location of the mouse pointer. This is what
         * {@link #getMouseLocation()} would return in a pure 2D editor. In a 3D
         * editor, {@link #getMouseLocation()} returns the current surface
         * coordinates however, so we need this method to access the mouse
         * location in mouse coordinates.
         * 
         * @return the current mouse location
         */
        public Point getRealMouseLocation() {

            return mouseLocation;
        }

        /**
         * Returns the current 3D world location of the mouse pointer.
         * 
         * @return the current 3D world location
         */
        public IVector3f getWorldLocation() {

            return worldLocation;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.gef.tools.AbstractTool.Input#setInput(org.eclipse.swt.events.MouseEvent)
         */
        @Override
        public void setInput(MouseEvent i_me) {

            super.setInput(i_me);

            if (i_me instanceof MouseEvent3D) {
                MouseEvent3D me3D = (MouseEvent3D) i_me;

                worldLocation.set(me3D.worldLoc);
                mouseLocation.x = me3D.mouseX;
                mouseLocation.y = me3D.mouseY;
            }
        }
    }

    private Input3D m_current;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef.tools.AbstractTool#deactivate()
     */
    @Override
    public void deactivate() {

        super.deactivate();
        m_current = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef.tools.AbstractTool#getCurrentInput()
     */
    @Override
    protected Input getCurrentInput() {

        if (m_current == null)
            m_current = new Input3D();

        return m_current;
    }

    /**
     * Returns the current 3D input.
     * 
     * @return the current 3D input
     */
    protected Input3D getCurrentInput3D() {

        return (Input3D) getCurrentInput();
    }

}
