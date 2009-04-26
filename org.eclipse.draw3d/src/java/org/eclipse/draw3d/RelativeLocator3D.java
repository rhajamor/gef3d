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
package org.eclipse.draw3d;

import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RelativeLocator;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.geometry.BoundingBox;
import org.eclipse.draw3d.geometry.BoundingBoxImpl;
import org.eclipse.draw3d.geometry.IBoundingBox;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;

/**
 * RelativeLocator3D, 3D version of the 2D relative locator
 * {@link RelativeLocator}. This 3D version extends its 2D version, so it can be
 * used instead of the 2D version.
 * <p>
 * This locator (as its 2D version) is used for positioning handles on figures,
 * see GEF's <code>ResizableEditPolicy</code> (and other selection policies) of
 * how this locator and handles are used.
 * 
 * @author IBM Corporation (original 2D version)
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 24, 2008
 */
public class RelativeLocator3D extends RelativeLocator {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(RelativeLocator3D.class.getName());

	private static final Vector3fImpl TMP_V2 = new Vector3fImpl();

	private static final Vector3fImpl TMP_V1 = new Vector3fImpl();

	/**
	 * Contains the relative offset factors.
	 */
	protected Vector3fImpl m_relativeVec = new Vector3fImpl(0, 0, 0);

	/**
	 * Creates a new instance with no relative offset.
	 */
	public RelativeLocator3D() {

		// nothing to initialize
	}

	/**
	 * Just as {@link RelativeLocator#RelativeLocator(IFigure, double, double)},
	 * relative z is set to front face (1.0)
	 * 
	 * @param i_reference the reference figure
	 * @param i_relativeX the relative X offset
	 * @param i_relativeY the relative Y offset
	 */
	public RelativeLocator3D(IFigure i_reference, double i_relativeX,
			double i_relativeY) {

		this(i_reference, i_relativeX, i_relativeY, 1);
	}

	/**
	 * Creates a new relative locator with the given reference figure and
	 * relative X, Y and Z offsets
	 * 
	 * @param i_reference the reference figure
	 * @param i_relativeX the relative X offset
	 * @param i_relativeY the relative Y offset
	 * @param i_relativeZ the relative Z offset
	 */
	public RelativeLocator3D(IFigure i_reference, double i_relativeX,
			double i_relativeY, double i_relativeZ) {

		super(i_reference, i_relativeX, i_relativeY);
		m_relativeVec.set((float) i_relativeX, (float) i_relativeY,
			(float) i_relativeZ);
	}

	/**
	 * Creates a new relative locator with the given reference figure and
	 * relative location. The given location is one of {@link PositionConstants}
	 * and the relative Z location is set to {@link PositionConstants3D#ZMIDDLE}
	 * .
	 * 
	 * @param i_reference the reference figure
	 * @param i_location the relative 2D location
	 */
	public RelativeLocator3D(IFigure i_reference, int i_location) {

		this(i_reference, i_location, PositionConstants3D.ZMIDDLE);
	}

	/**
	 * Creates a new relative locator with the given reference figure and
	 * relative location. The given location is one of {@link PositionConstans}
	 * and the given Z location is one of {@link PositionConstants3D}.
	 * 
	 * @param i_reference the reference figure
	 * @param i_location the relative 2D location
	 * @param i_zlocation the relative Z location
	 */
	public RelativeLocator3D(IFigure i_reference, int i_location,
			PositionConstants3D i_zlocation) {

		super(i_reference, i_location);
		switch (i_location & PositionConstants.NORTH_SOUTH) {
		case PositionConstants.NORTH:
			m_relativeVec.y = 0;
			break;
		case PositionConstants.SOUTH:
			m_relativeVec.y = 1.0f;
			break;
		default:
			m_relativeVec.y = 0.5f;
		}

		switch (i_location & PositionConstants.EAST_WEST) {
		case PositionConstants.WEST:
			m_relativeVec.x = 0;
			break;
		case PositionConstants.EAST:
			m_relativeVec.x = 1.0f;
			break;
		default:
			m_relativeVec.x = 0.5f;
		}

		switch (i_zlocation) {
		case FRONT:
			m_relativeVec.z = 1.0f;
			break;
		case BACK:
			m_relativeVec.z = 0;
			break;
		default: // ZMIDDLE
			m_relativeVec.z = 0.5f;
		}

	}

	/**
	 * Returns the reference box in the reference figure's coordinate system.
	 * 
	 * @see #getReferenceBox()
	 * @return the reference box
	 */
	public IBoundingBox getReferenceBox3D() {

		IFigure ref = getReferenceFigure();
		if (ref instanceof IFigure3D) {
			IFigure3D ref3D = (IFigure3D) ref;
			return ref3D.getBounds3D();
		} else {
			// do not call super method here, since this method may be
			// overridden causing infinite calls or inconsistent behavior
			// with 3D version
			Rectangle rect = ref.getBounds();
			return Figure3DHelper.convertBoundsToBounds3D(ref, rect);
		}
	}

	/**
	 * {@inheritDoc} This method is exactly implemented as its 2D version, using
	 * 3D methods instead.
	 * 
	 * @see org.eclipse.draw2d.RelativeLocator#relocate(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void relocate(IFigure target) {

		// if everything is 2D, behave like original 2D locator
		if (!(getReferenceFigure() instanceof IFigure3D || target instanceof IFigure3D)) {
			super.relocate(target);
		} else { // use 3D locators
			// super: IFigure reference = getReferenceFigure(); // get reference
			// figure
			IFigure reference = getReferenceFigure();

			// super: Rectangle targetBounds = // get "frame" of reference
			// new PrecisionRectangle(getReferenceBox().getResized(-1, -1));
			BoundingBox targetBox = new BoundingBoxImpl(getReferenceBox3D());
			targetBox.resize(-1, -1, -1);

			// super: reference.translateToAbsolute(targetBounds); //
			// translate coordinates to absolute (from reference relative)
			IFigure3D ref3D = Figure3DHelper.getAncestor3D(reference);
			ref3D.transformToAbsolute(targetBox);

			// now targetBox is resized bounds of reference, resized by -1 pixel
			// in world (absolute) coordinates, proceed as in 2D version (which
			// makes only sense here for a 3D target
			if (target instanceof IFigure3D) {
				IFigure3D target3D = (IFigure3D) target;

				// super: target.translateToRelative(targetBounds); // translate
				// coordinates to relative (from absolute)
				target3D.transformToRelative(targetBox);

				// super: targetBounds.resize(1, 1); // and resize back to
				// bounds or reference
				targetBox.resize(1, 1, 1);

				// super: Dimension targetSize = target.getPreferredSize(); //
				// get preferred size of target
				IVector3f targetSize = target3D.getPreferredSize3D();

				// super: targetBounds.x // position target figure (x)
				// super: += (int) (targetBounds.width * relativeX -
				// ((targetSize.width + 1) / 2));
				// super: targetBounds.y // position target figure (y)
				// super: += (int) (targetBounds.height * relativeY -
				// ((targetSize.height + 1) / 2));

				targetBox.getPosition(TMP_V1);
				targetBox.getSize(TMP_V2);

				TMP_V1.x += TMP_V2.x * m_relativeVec.x //
						- ((targetSize.getX() + 1) / 2);
				TMP_V1.y += TMP_V2.y * m_relativeVec.y //
						- ((targetSize.getY() + 1) / 2);
				TMP_V1.z += TMP_V2.z * m_relativeVec.z //
						- ((targetSize.getZ() + 1) / 2);

				// super: targetBounds.setSize(targetSize); // target size is
				// its preferred size
				// super: target.setBounds(targetBounds); // set target position
				target3D.setLocation3D(TMP_V1);
				target3D.setSize3D(targetSize);
			} else {
				log.warning("Cannot position 2D Figure based "
						+ "on 3D reference, reference: " + reference
						+ ", target: " + target);
			}
		}
	}
}
