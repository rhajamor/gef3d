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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RelativeLocator3D;
import org.eclipse.draw3d.geometry.BoundingBox;
import org.eclipse.draw3d.geometry.BoundingBoxImpl;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gef.handles.MoveHandleLocator;
import org.eclipse.gef.handles.RelativeHandleLocator;

/**
 * MoveHandleLocator3D, 3D version of {@link MoveHandleLocator}.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 15, 2008
 */
public class MoveHandleLocator3D extends MoveHandleLocator {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(MoveHandleLocator3D.class.getName());

	private static Vector3fImpl TEMP_V_1 = new Vector3fImpl();

	/**
	 * @param i_ref
	 */
	public MoveHandleLocator3D(IFigure i_ref) {
		super(i_ref);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Algorithm copied from
	 * org.eclipse.gef.handles.MoveHandleLocator#relocate(org
	 * .eclipse.draw2d.IFigure) and modified to match 3D
	 * </p>
	 * 
	 * @see org.eclipse.gef.handles.MoveHandleLocator#relocate(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void relocate(IFigure target) {

		IFigure referenceFig = getReference();

		// if everything is 2D, behave like original 2D locator
		if (!(referenceFig instanceof IFigure3D || target instanceof IFigure3D))
			super.relocate(target);

		// GEF: Insets insets = target.getInsets();
		Insets insets = target.getInsets(); // TODO: port to 3D (see below)

		// GEF: Rectangle bounds;
		// GEF: if (getReference() instanceof HandleBounds)
		// GEF: bounds = ((HandleBounds)getReference()).getHandleBounds();
		// GEF: else bounds = getReference().getBounds();
		BoundingBox bounds = new BoundingBoxImpl(getReferenceBox3D());

		// GEF: bounds = new PrecisionRectangle(bounds.getResized(-1, -1));
		// bounds.resize(-1, -1, -1);

		// GEF: getReference().translateToAbsolute(bounds);
		IFigure3D ref3D = Figure3DHelper.getAncestor3D(referenceFig);

		// Transformable transformable = TransformableAdapter.adapt(bounds);
		ref3D.transformToAbsolute(bounds);

		if (target instanceof IFigure3D) {
			IFigure3D target3D = (IFigure3D) target;

			// GEF: target.translateToRelative(bounds);
			target3D.transformToRelative(bounds);

			// port next two lines to 3D
			// GEF: bounds.translate(-insets.left, -insets.top);
			// GEF: bounds.resize(insets.getWidth() + 1, insets.getHeight()
			// +
			// 1);
			// bounds.resize(-insets.left, -insets.top, 0);
			// bounds.resize(insets.getWidth() + 1, insets.getHeight() + 1,
			// 1);
			// bounds.resize(1, 1, 1); // TODO replace with 3D version

			// make the handle slightly larger so that it can be picked
			bounds.expand(0.01f);

			// GEF: target.setBounds(bounds);
			target3D.setLocation3D(bounds.getPosition(TEMP_V_1));
			target3D.setSize3D(bounds.getSize(TEMP_V_1));
		} else {
			log.warning("IFigure - cannot relocate 2D figure: " + target); //$NON-NLS-1$
		}
	}

	/**
	 * Returns reference box depending on type of reference figure.
	 * 
	 * @see RelativeHandleLocator#getReferenceBox()
	 * @see RelativeLocator3D#getReferenceBox3D()
	 * @return
	 */
	public IBoundingBox getReferenceBox3D() {
		IFigure referenceFig = getReference();
		if (referenceFig instanceof IFigure3D) {
			if (referenceFig instanceof HandleBounds3D)
				return ((HandleBounds3D) referenceFig).getHandleBounds3D();
			else
				return ((IFigure3D) referenceFig).getBounds3D();
		} else {
			// do not call super method here, since this method may be
			// overridden causing infinite calls or inconsistent behavior
			// with 3D version
			Rectangle rect = (referenceFig instanceof HandleBounds) ? ((HandleBounds) referenceFig)
					.getHandleBounds()
					: referenceFig.getBounds();

			referenceFig.translateToAbsolute(rect);

			return Figure3DHelper.convertBoundsToBounds3D(referenceFig, rect);
		}
	}

}
