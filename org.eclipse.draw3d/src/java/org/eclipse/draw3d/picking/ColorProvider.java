/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.picking;

import org.eclipse.draw3d.IFigure3D;

/**
 * Provides a color for painting figures when rendering a color picking buffer.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 08.05.2008
 */
public interface ColorProvider {

	/**
	 * If {@link #getColor(IFigure3D)} returns this value for a given figure,
	 * the figure is currently ignored and should not be painted in color mode.
	 */
	public static final int IGNORE = -1;

	/**
	 * Returns the color index for the given figure. Be aware that calling this
	 * method multiple times with the same given figure may return a different
	 * color index each time.
	 * 
	 * @param i_figure the figure to obtain a color for
	 * @return a positive color index or {@value #IGNORE} if the given figure is
	 *         currently ignored
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public int getColor(IFigure3D i_figure);
}
