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

import java.util.logging.Logger;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.TransparencyAdapter;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.SquareHandle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;

/**
 * CubeHandle is the 3D version of {@link SquareHandle}.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 25, 2008
 */
public abstract class CubeHandle extends AbstractHandle3D {
	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CubeHandle.class
			.getName());

	/**
	 * The default size for square handles. (copied from {@link SquareHandle}
	 * and made public)
	 */
	protected static final float DEFAULT_HANDLE_SIZE = 7;

	private TransparencyAdapter m_alphashape = new TransparencyAdapter(this,
			new CuboidFigureShape(this));

	private TransparencyAdapter m_supershape = new TransparencyAdapter(this,
			new CuboidFigureShape(this));

	/**
	 * Null constructor
	 */
	public CubeHandle() {
		init();
	}

	/**
	 * Creates a SquareHandle for the given <code>GraphicalEditPart</code> with
	 * the given <code>Locator</code>.
	 * 
	 * @param owner the owner
	 * @param loc the locator
	 */
	public CubeHandle(GraphicalEditPart owner, Locator loc) {
		super(owner, loc);
		init();
	}

	/**
	 * Creates a SquareHandle for the given <code>GraphicalEditPart</code> with
	 * the given <code>Cursor</code> using the given <code>Locator</code>.
	 * 
	 * @param owner The editpart which provided this handle
	 * @param loc The locator to position the handle
	 * @param c The cursor to display when the mouse is over the handle
	 */
	public CubeHandle(GraphicalEditPart owner, Locator loc, Cursor c) {
		super(owner, loc, c);
		init();
	}

	/**
	 * Initializes the handle.
	 */
	protected void init() {

		setPreferredSize3D(new Vector3fImpl(DEFAULT_HANDLE_SIZE,
				DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE));
		setAlpha(40);
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
		return (isPrimary()) ? ColorConstants.darkGray : ColorConstants.white;

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
		return (isPrimary()) ? ColorConstants.black : ColorConstants.black;
	}

	/**
	 * Returns <code>true</code> if the handle's owner is the primary selection.
	 * 
	 * @return <code>true</code> if the handles owner has primary selection.
	 *         <p>
	 *         Copied (and not modified yet) from {@link SquareHandle}.
	 *         </p>
	 */
	protected boolean isPrimary() {
		return getOwner().getSelected() == EditPart.SELECTED_PRIMARY;
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#render()
	 */
	@Override
	public void render(RenderContext renderContext) {
		if (renderContext.getMode().isPaint()) {
			renderContext.addTransparentObject(m_alphashape);
			renderContext.addSuperimposedObject(m_supershape);
		} else {
			m_alphashape.renderTransparent(renderContext); // render for picking
		}
	}

}
