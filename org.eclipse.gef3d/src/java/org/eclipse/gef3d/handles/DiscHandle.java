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
package org.eclipse.gef3d.handles;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.SquareHandle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;

/**
 * A handle that renders itself in the form of a disc.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.06.2009
 */
public abstract class DiscHandle extends AbstractHandle3D {

	/**
	 * The default size (height and diameter) of the handle.
	 */
	protected static final float DEFAULT_HANDLE_SIZE = 7;

	/**
	 * Creates a disc handle for the given <code>GraphicalEditPart</code> with
	 * the given <code>Locator</code>.
	 * 
	 * @param owner
	 *            the edit part which created this handle
	 * @param loc
	 *            the locator to position this handle
	 */
	public DiscHandle(GraphicalEditPart owner, Locator loc) {
		super(owner, loc);
		init();
	}

	/**
	 * Creates a disc handle for the given <code>GraphicalEditPart</code> with
	 * the given <code>Cursor</code> using the given <code>Locator</code>.
	 * 
	 * @param owner
	 *            the edit part which created this handle
	 * @param loc
	 *            the locator to position this handle
	 * @param c
	 *            the cursor to display when the mouse hovers over this handle
	 */
	public DiscHandle(GraphicalEditPart owner, Locator loc, Cursor c) {
		super(owner, loc, c);
		init();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the color for the inside of the handle. Like
	 * {@link SquareHandle#getFillColor()}.
	 * 
	 * @see org.eclipse.draw2d.Figure#getBackgroundColor()
	 */
	@Override
	public Color getBackgroundColor() {

		return isPrimary() ? ColorConstants.darkGray : ColorConstants.white;

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the color for the outside of the handle, GEF uses
	 * {@link SquareHandle#getBorderColor()} instead.
	 * 
	 * @see org.eclipse.draw2d.Figure#getForegroundColor()
	 */
	@Override
	public Color getForegroundColor() {

		return ColorConstants.black;
	}

	/**
	 * Initializes this handle.
	 */
	protected void init() {

		setPreferredSize3D(new Vector3fImpl(DEFAULT_HANDLE_SIZE,
				DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE));
		setAlpha(40);
	}

	/**
	 * Indicates whether the handle's owner is the primary selection.
	 * 
	 * @return <code>true</code> if the handles owner is the primary selection.
	 *         <p>
	 *         Copied (and not modified yet) from {@link SquareHandle}.
	 *         </p>
	 */
	protected boolean isPrimary() {

		return getOwner().getSelected() == EditPart.SELECTED_PRIMARY;
	}

}
