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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IMatrix4f;
import org.eclipse.draw3d.picking.ColorProvider;
import org.eclipse.draw3d.shapes.SolidCube;
import org.eclipse.draw3d.shapes.WiredCube;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef3d.tools.DragEditPartsTracker3D;
import org.eclipse.swt.graphics.Color;


/**
 * 3D version of {@link MoveHandle}.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 15, 2008
 */
public class MoveHandle3D extends AbstractHandle3D {

	/**
	 * Creates a MoveHandle for the given <code>GraphicalEditPart</code> using
	 * a default {@link Locator}.
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
	 * Overridden to create a {@link DragEditPartsTracker}.
	 * <p>
	 * Copied from {@link MoveHandle#MoveHandle(GraphicalEditPart, Locator)},
	 * returns a {@link DragEditPartsTracker3D} instead of its 2D version
	 * {@link DragEditPartsTracker}.
	 * </p>
	 * 
	 * @see org.eclipse.gef.handles.AbstractHandle#createDragTracker()
	 */
	protected DragTracker createDragTracker() {
		DragEditPartsTracker3D tracker = new DragEditPartsTracker3D(getOwner());
		tracker.setDefaultCursor(getCursor());
		return tracker;
	}

	// /**
	// * Returns <code>true</code> if the point (x,y) is contained within this
	// handle.
	// * @param x The x coordinate.
	// * @param y The y coordinate.
	// * @return <code>true</code> if the point (x,y) is contained within this
	// handle.
	// */
	// public boolean containsPoint(int x, int y) {
	// if (!super.containsPoint(x, y))
	// return false;
	// return !Rectangle.SINGLETON.
	// setBounds(getBounds()).
	// shrink(INNER_PAD, INNER_PAD).
	// contains(x, y);
	// }
	//
	// /**
	// * Returns a point along the right edge of the handle.
	// * @see org.eclipse.gef.Handle#getAccessibleLocation()
	// */
	// public Point getAccessibleLocation() {
	// Point p = getBounds().
	// getTopRight().
	// translate(-1, getBounds().height / 4);
	// translateToAbsolute(p);
	// return p;
	// }

	private WiredCube wiredcube = new WiredCube();

	private SolidCube solidcube = new SolidCube();

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

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#postrender()
	 */
	@Override
	public void postrender() {
		RenderContext renderContext = RenderContext.getContext();

		int alpha = getAlpha();
		IMatrix4f modelMatrix = getModelMatrix();

		if (renderContext.getMode().isPaint()) {
			Color color = getForegroundColor();
			wiredcube.setColor(color, alpha);
			wiredcube.setModelMatrix(modelMatrix);
			wiredcube.render();

			// solidcube.setModelMatrix(modelMatrix);
			// solidcube.setColor(SolidCube.Face.ALL, color, alpha);
			// solidcube.setTexture(SolidCube.Face.FRONT, null);
			// solidcube.render();
			//			

		} else {
			// only for picking
			int color = renderContext.getColor(this);
			if (color != ColorProvider.IGNORE) {
				solidcube.setModelMatrix(modelMatrix);
				solidcube.setColor(color, 255);
				solidcube.setTexture(null);
				solidcube.render();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#render(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	public void render() {
	}

}
