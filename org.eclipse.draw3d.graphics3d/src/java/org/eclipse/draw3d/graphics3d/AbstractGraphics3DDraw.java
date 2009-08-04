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
package org.eclipse.draw3d.graphics3d;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * AbstractGraphics3DDraw There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 04.08.2009
 */
public abstract class AbstractGraphics3DDraw implements Graphics3DDraw {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glColor3f(float[])
	 */
	public void glColor3f(float[] rgb) {

		glColor3f(rgb[0], rgb[1], rgb[2]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glColor4f(float[])
	 */
	public void glColor4f(float[] rgba) {

		glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glNormal3f(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void glNormal3f(IVector3f normal) {

		glNormal3f(normal.getX(), normal.getY(), normal.getZ());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glTexCoord2f(org.eclipse.draw3d.geometry.IVector2f)
	 */
	public void glTexCoord2f(IVector2f coords) {

		glTexCoord2f(coords.getX(), coords.getY());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glVertex2f(org.eclipse.draw3d.geometry.IVector2f)
	 */
	public void glVertex2f(IVector2f vertex) {

		glVertex2f(vertex.getX(), vertex.getY());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glVertex3f(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public void glVertex3f(IVector3f vertex) {

		glVertex3f(vertex.getX(), vertex.getY(), vertex.getZ());
	}
}
