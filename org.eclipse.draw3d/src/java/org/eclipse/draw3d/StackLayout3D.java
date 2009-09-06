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

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.util.Draw3DCache;

/**
 * A layout that stacks 3D figures along the Z axis in a configurable distance.
 * The X and Y position is set to 0. 2D children are ignored. The size and
 * rotation of the children is not changed.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 06.09.2009
 */
public class StackLayout3D extends AbstractLayout {

	private float m_distance = 1000;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.AbstractLayout#calculatePreferredSize(org.eclipse.draw2d.IFigure,
	 *      int, int)
	 */
	@Override
	protected Dimension calculatePreferredSize(IFigure i_container,
		int i_wHint, int i_hHint) {

		return i_container.getPreferredSize(i_wHint, i_hHint);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
	 */
	@SuppressWarnings("unchecked")
	public void layout(IFigure i_container) {

		Vector3f location = Draw3DCache.getVector3f();
		try {
			location.set(0, 0, 0);
			List children = i_container.getChildren();
			for (Object child : children) {
				if (!(child instanceof IFigure3D))
					continue;

				IFigure3D child3D = (IFigure3D) child;
				child3D.getPosition3D().setLocation3D(location);

				location.translate(0, 0, m_distance);
			}
		} finally {
			Draw3DCache.returnVector3f(location);
		}
	}
}
