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
package org.eclipse.gef3d.ui.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw3d.Draw3DCanvas;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef3d.factories.IFigureFactory;
import org.eclipse.gef3d.factories.IFigureFactoryProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Creates GLCanvas, RootEditPart, and LightweightSystem. Here, 3D versions of
 * these objects are created.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 16.11.2007
 */
public class GraphicalViewer3DImpl extends GraphicalViewerImpl implements
		IScene, IFigureFactoryProvider {

	protected IFigureFactory m_FigureFactory = null;

	/**
	 * {@inheritDoc} Here, a {@link GLCanvas} is created (with double buffer).
	 * The viewer itself doesn't do much, but it's a container for all that
	 * other things:
	 * <ul>
	 * <li>The lightweight system manages the drawing process (and its root
	 * figure can display a coordinate system)</li>
	 * <li>The root edit part and its figure manage the layers</li>
	 * </ul>
	 * Internal Note: Fixed deepth buffer problem on Mac OS X, thanks to Nicolas
	 * Richeton
	 * 
	 * @see http://nricheton.homeip.net/?p=53
	 * @see org.eclipse.gef.ui.parts.GraphicalViewerImpl#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(Composite i_composite) {

		final GLCanvas canvas = Draw3DCanvas
				.createCanvas(i_composite, SWT.NONE, getLightweightSystem3D());

		setControl(canvas);
		return getControl();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalViewerImpl#createDefaultRoot()
	 */
	@Override
	protected void createDefaultRoot() {
		setRootEditPart(new ScalableRootEditPart());
	}

	/**
	 * {@inheritDoc} Here, a {@link LightweightSystem3D} is created.
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalViewerImpl#createLightweightSystem()
	 */
	@Override
	protected LightweightSystem createLightweightSystem() {

		return new LightweightSystem3D();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.IScene#getCamera()
	 */
	public ICamera getCamera() {

		LightweightSystem3D lightweightSystem3D = getLightweightSystem3D();
		if (lightweightSystem3D == null)
			return null;

		return lightweightSystem3D.getCamera();
	}

	/**
	 * Returns the 3D lightweight system.
	 * 
	 * @return the 3D lightweightsystem or <code>null</code> if the lightweight
	 *         system is not 3D capable
	 */
	public LightweightSystem3D getLightweightSystem3D() {

		LightweightSystem lightweightSystem = getLightweightSystem();
		if (lightweightSystem instanceof LightweightSystem3D)
			return (LightweightSystem3D) lightweightSystem;

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.IScene#getUpdateManager()
	 */
	public UpdateManager getUpdateManager() {

		LightweightSystem lws = getLightweightSystem();
		if (lws == null)
			return null;

		return lws.getUpdateManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.IScene#render()
	 */
	public void render() {

		LightweightSystem3D lightweightSystem3D = getLightweightSystem3D();
		if (lightweightSystem3D == null)
			return;
		
		lightweightSystem3D.getRenderContext().activate();

		UpdateManager updateManager = getUpdateManager();
		if (updateManager == null)
			return;

		IFigure root = lightweightSystem3D.getRootFigure();
		updateManager.addDirtyRegion(root, 0, 0, 10000, 10000);
		updateManager.performUpdate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.IScene#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBackgroundColor(Color i_backgroundColor) {

		LightweightSystem3D lightweightSystem3D = getLightweightSystem3D();
		if (lightweightSystem3D == null)
			return;

		lightweightSystem3D.setBackgroundColor(i_backgroundColor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.IScene#setCamera(org.eclipse.draw3d.camera.ICamera)
	 */
	public void setCamera(ICamera i_camera) {

		LightweightSystem3D lightweightSystem3D = getLightweightSystem3D();
		if (lightweightSystem3D == null)
			return;

		lightweightSystem3D.setCamera(i_camera);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.AbstractEditPartViewer#setContents(java.lang.Object)
	 */
	@Override
	public void setContents(Object i_contents) {
		try {
			super.setContents(i_contents);
		} catch (RuntimeException ex) {

			// Mac OS X Leopard issue:
			// dispose GLd3d canvas, otherwise Eclipse will crash
			GLCanvas canvas = (GLCanvas) getControl();
			// canvas.dispose();
			setControl(null);

			throw ex;

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.IScene#setDrawAxes(boolean)
	 */
	public void setDrawAxes(boolean i_drawAxes) {

		LightweightSystem3D lightweightSystem3D = getLightweightSystem3D();
		if (lightweightSystem3D == null)
			return;

		lightweightSystem3D.setDrawAxes(i_drawAxes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.factories.IFigureFactoryProvider#getFigureFactory()
	 */
	public IFigureFactory getFigureFactory() {
		return m_FigureFactory;
	}

	/**
	 * Sets the figure factory of this viewer.
	 * 
	 * @param i_factory
	 */
	public void setFigureFactory(IFigureFactory i_factory) {
		m_FigureFactory = i_factory;
	}

}
