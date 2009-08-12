/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others,
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation of 2D version
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.handles;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.Shape;
import org.eclipse.draw3d.shapes.TransparentShape;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef3d.editpolicies.ResizableEditPolicy3D;

/**
 * 3D version of {@link MoveHandle}. The move handle is created via a
 * {@link MoveHandle3DFactory}, which is used by appropriate policies such as
 * the {@link ResizableEditPolicy3D}.
 * <p>
 * Parts of this class (methods and/or comments) were copied and modified from
 * {@link MoveHandle}, copyright (c) 2000, 2005 IBM Corporation and others and
 * distributed under the EPL license.
 * </p>
 * 
 * @author IBM Corporation (original 2D version)
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 15, 2008
 */
public class MoveHandle3D extends AbstractHandle3D {

	/**
	 * Creates a MoveHandle for the given <code>GraphicalEditPart</code> using a
	 * default {@link Locator}.
	 * <p>
	 * Copied from {@link MoveHandle#MoveHandle(GraphicalEditPart)}, using a 3D
	 * locator instead of a 2D one
	 * </p>
	 * 
	 * @param owner The GraphicalEditPart to be moved by this handle.
	 */
	public MoveHandle3D(GraphicalEditPart owner) {

		this(owner, new MoveHandleLocator3D(owner.getFigure()));
	}

	/**
	 * Creates a MoveHandle for the given <code>GraphicalEditPart</code> using
	 * the given <code>Locator</code>.
	 * <p>
	 * Copied from {@link MoveHandle#MoveHandle(GraphicalEditPart, Locator)} and
	 * not yet modified.
	 * </p>
	 * 
	 * @param owner The GraphicalEditPart to be moved by this handle.
	 * @param loc The Locator used to place the handle.
	 */
	public MoveHandle3D(GraphicalEditPart owner, Locator loc) {

		super(owner, loc);
		initialize();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Copied from {@link MoveHandle#MoveHandle(GraphicalEditPart, Locator)}.
	 * </p>
	 * 
	 * @see org.eclipse.gef3d.handles.AbstractHandle3D#createDragTracker()
	 */
	@Override
	protected DragTracker createDragTracker() {

		DragEditPartsTracker tracker = new DragEditPartsTracker(getOwner());
		tracker.setDefaultCursor(getCursor());

		return tracker;
	}

	
	

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw3d.ShapeFigure3D#createShape()
	 */
	@Override
	protected Shape createShape() {
		CuboidFigureShape shape = new CuboidFigureShape(this);
		shape.setFill(false);

		return new TransparentShape(this, shape);
	}

	/**
	 * Initializes the handle, i.e. sets figure's properties.
	 * <p>
	 * Copied from {@link MoveHandle#initialize()}, additionally 3D properties
	 * are set.
	 * <p>
	 */
	protected void initialize() {
		// from MoveHandle:
		setOpaque(false);
		setBorder(new LineBorder(1));
		setCursor(Cursors.SIZEALL);

		// 3D:
		setAlpha(100);
		setBackgroundColor(ColorConstants.green);
		setForegroundColor(ColorConstants.green);
	}
}
