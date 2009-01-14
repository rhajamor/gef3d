/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.handles;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw3d.PositionConstants3D;
import org.eclipse.draw3d.TransparentObject;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.RelativeHandleLocator;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.gef.handles.SquareHandle;
import org.eclipse.gef.tools.ResizeTracker;
import org.eclipse.gef3d.tools.ResizeTracker3D;
import org.eclipse.swt.graphics.Cursor;


/**
 * 3D version of {@link ResizeHandle}. Note that this handle is also used (even
 * in the original GEF) as <code>NonResizableHandle</code> (the class
 * {@link org.eclipse.gef.handles.NonResizableHandle} is marked deprecated).
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 26, 2008
 * @see $HeadURL:
 *      https://gorgo.fernuni-hagen.de/OpenglGEF/trunk/org.eclipse.gef3d/src
 *      /java/de/feu/gef3d/handles/ResizeHandle3D.java $
 */
public class ResizeHandle3D extends CubeHandle  {

	/**
	 * <p>
	 * Copied (and not modified yet) from {@link ResizeHandle}.
	 * </p>
	 */
	private int cursorDirection = 0;

	/**
	 * Creates a new ResizeHandle for the given GraphicalEditPart.
	 * <code>direction</code> is the relative direction from the center of the
	 * owner figure. For example, <code>SOUTH_EAST</code> would place the handle
	 * in the lower-right corner of its owner figure. These direction constants
	 * can be found in {@link org.eclipse.draw2d.PositionConstants}.
	 * <p>
	 * Copied from {@link ResizeHandle}. Instead of a 2D handle locator
	 * {@link RelativeHandleLocator}, the 3D version
	 * {@link RelativeHandleLocator3D} is created here.
	 * </p>
	 * 
	 * @param owner owner of the ResizeHandle
	 * @param direction relative direction from the center of the owner figure
	 */
	public ResizeHandle3D(GraphicalEditPart owner, int direction) {
		this(owner, direction, PositionConstants3D.ZMIDDLE);
	}

	/**
	 * Creates a new ResizeHandle for the given GraphicalEditPart.
	 * <code>direction</code> is the relative direction from the center of the
	 * owner figure. For example, <code>SOUTH_EAST</code> would place the handle
	 * in the lower-right corner of its owner figure. These direction constants
	 * can be found in {@link org.eclipse.draw2d.PositionConstants}.
	 * <p>
	 * Copied from {@link ResizeHandle}. Instead of a 2D handle locator
	 * {@link RelativeHandleLocator}, the 3D version
	 * {@link RelativeHandleLocator3D} is created here.
	 * </p>
	 * 
	 * @param owner
	 * @param direction
	 * @param i_zposition
	 */
	public ResizeHandle3D(GraphicalEditPart owner, int direction,
			PositionConstants3D i_zposition) {
		setOwner(owner);
		setLocator(new RelativeHandleLocator3D(owner.getFigure(), direction,
				i_zposition));
		setCursor(Cursors.getDirectionalCursor(direction, owner.getFigure()
				.isMirrored()));
		cursorDirection = direction;
	}

	/**
	 * Creates a new ResizeHandle for the given GraphicalEditPart.
	 * <p>
	 * Copied (and not modified yet) from {@link ResizeHandle}.
	 * </p>
	 * 
	 * @see SquareHandle#SquareHandle(GraphicalEditPart, Locator, Cursor)
	 */
	public ResizeHandle3D(GraphicalEditPart owner, Locator loc, Cursor c) {
		super(owner, loc, c);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Copied from {@link ResizeHandle}. Instead of a {@link ResizeTracker}, a
	 * {@link ResizeTracker3D} is created.
	 * </p>
	 * 
	 * @see org.eclipse.gef3d.handles.AbstractHandle3D#createDragTracker()
	 */
	@Override
	protected DragTracker createDragTracker() {
		return new ResizeTracker3D(getOwner(), cursorDirection);
	}

	

}
