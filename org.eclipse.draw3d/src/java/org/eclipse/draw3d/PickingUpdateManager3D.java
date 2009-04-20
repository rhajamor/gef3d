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

package org.eclipse.draw3d;

import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.picking.ColorPicker;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * Does the actual picking for 3D figures by using a color picker, see
 * {@link ColorPicker}.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 13.12.2007
 */
public class PickingUpdateManager3D extends DeferredUpdateManager3D {

	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(PickingUpdateManager3D.class.getName());

	private ColorPicker m_picker = new ColorPicker();

	protected ICamera m_camera;

	/**
	 * Indicates whether picking is enabled.
	 */
	boolean pickingEnabled = true;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.UpdateManager#dispose()
	 */
	@Override
	public void dispose() {
		if (m_picker != null)
			m_picker.dispose();
		super.dispose();
	}

	/**
	 * Returns the picker.
	 * 
	 * @return the picker
	 */
	public ColorPicker getPicker() {
		return m_picker;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.DeferredUpdateManager3D#repairDamage()
	 */
	@Override
	protected void repairDamage() {

		pickingEnabled = false;
		super.repairDamage();
		pickingEnabled = true;

		// TODO: this leads to the picking buffer being re-rendered when a
		// feedback figure is moved, which is unneccessary
		if (m_picker != null)
			m_picker.invalidate();
	}

	/**
	 * Sets the currently used camera.
	 * 
	 * @param i_camera the camera
	 */
	public void setCamera(ICamera i_camera) {
		m_camera = i_camera;
		m_picker.setCamera(i_camera);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.DeferredUpdateManager3D#setCanvas(org.eclipse.swt.opengl.GLCanvas)
	 */
	@Override
	public void setCanvas(GLCanvas i_canvas) {
		super.setCanvas(i_canvas);
		m_picker.setCanvas(i_canvas);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.DeferredUpdateManager3D#setRoot(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void setRoot(IFigure i_figure) {
		super.setRoot(i_figure);

		m_picker.setRootFigure(
			Figure3DHelper.getAncestor3D(i_figure));
	}
}
