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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.ExclusionSearch;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.Draw3DCanvas;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.draw3d.geometry.Math3DCache;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Hit;
import org.eclipse.draw3d.picking.Picker;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef3d.factories.IFigureFactory;
import org.eclipse.gef3d.factories.IFigureFactoryProvider;
import org.eclipse.swt.SWT;
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
		GraphicalViewer3D, IFigureFactoryProvider {

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
	 * @see "http://nricheton.homeip.net/?p=53"
	 * @see org.eclipse.gef.ui.parts.GraphicalViewerImpl#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(Composite i_composite) {

		return createControl3D(i_composite);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ui.parts.GraphicalViewer3D#createControl3D(org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl3D(Composite i_composite) {

		final GLCanvas canvas =
			Draw3DCanvas.createCanvas(i_composite, SWT.NONE,
				getLightweightSystem3D());

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
	 * <p>
	 * This method was copied and subsequently modified.
	 * </p>
	 * 
	 * @author hudsonr (original implementation)
	 * @author Kristian Duske
	 * @see org.eclipse.gef.ui.parts.GraphicalViewerImpl#findHandleAt(org.eclipse.draw2d.geometry.Point)
	 */
	@Override
	public Handle findHandleAt(Point i_mLocation) {

		LayerManager layermanager =
			(LayerManager) getEditPartRegistry().get(LayerManager.ID);

		if (layermanager == null)
			return null;

		Vector3f rayStart = Draw3DCache.getVector3f();
		Vector3f rayPoint = Draw3DCache.getVector3f();
		Point sLocation = Draw3DCache.getPoint();
		try {
			List<IFigure> ignore = new ArrayList<IFigure>(3);
			ignore.add(layermanager.getLayer(LayerConstants.PRIMARY_LAYER));
			ignore.add(layermanager.getLayer(LayerConstants.CONNECTION_LAYER));
			ignore.add(layermanager.getLayer(LayerConstants.FEEDBACK_LAYER));
			TreeSearch search = new ExclusionSearch(ignore);

			LightweightSystem3D lws = getLightweightSystem3D();
			Picker picker = lws.getPicker();

			Hit hit = picker.getHit(i_mLocation.x, i_mLocation.y, search);
			if (hit == null)
				return null;

			IFigure3D figure3D = hit.getFigure();
			if (figure3D instanceof Handle)
				return (Handle) figure3D;

			// keep searching on the surface
			lws.getCamera().getPosition(rayStart);
			lws.getCamera().unProject(i_mLocation.x, i_mLocation.y, 0, null,
				rayPoint);

			ISurface surface = figure3D.getSurface();
			surface.getSurfaceLocation2D(rayStart, rayPoint, sLocation);

			IFigure figure2D =
				figure3D.findFigureAt(sLocation.x, sLocation.y, search);

			if (figure2D instanceof Handle)
				return (Handle) figure2D;

			return null;
		} finally {
			Draw3DCache.returnVector3f(rayStart);
			Draw3DCache.returnVector3f(rayPoint);
			Draw3DCache.returnPoint(sLocation);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method was copied and subsequently modified.
	 * </p>
	 * 
	 * @author hudsonr (original implementation)
	 * @author Kristian Duske
	 * @see org.eclipse.gef.ui.parts.GraphicalViewerImpl#findObjectAtExcluding(org.eclipse.draw2d.geometry.Point,
	 *      java.util.Collection, org.eclipse.gef.EditPartViewer.Conditional)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EditPart findObjectAtExcluding(Point i_mLocation,
		Collection i_exclude, final Conditional i_condition) {

		class ConditionalTreeSearch extends ExclusionSearch {

			ConditionalTreeSearch(Collection coll) {

				super(coll);
			}

			@Override
			public boolean accept(IFigure i_figure) {

				IFigure current = i_figure;
				EditPart editpart = null;
				while (editpart == null && current != null) {
					editpart = (EditPart) getVisualPartMap().get(current);
					current = current.getParent();
				}

				return editpart != null
					&& (i_condition == null || i_condition.evaluate(editpart));
			}
		}

		Vector3f rayStart = Math3DCache.getVector3f();
		Vector3f rayDirection = Math3DCache.getVector3f();
		try {

			LightweightSystem3D lws = getLightweightSystem3D();
			Picker picker = lws.getPicker();

			TreeSearch search = new ConditionalTreeSearch(i_exclude);
			Hit hit = picker.getHit(i_mLocation.x, i_mLocation.y, search);

			EditPart part = null;
			if (hit != null) {
				IFigure3D figure3D = hit.getFigure();
				IFigure figure2D =
					figure3D.findFigureAt(i_mLocation.x, i_mLocation.y, search);

				while (part == null && figure2D != null) {
					part = (EditPart) getVisualPartMap().get(figure2D);
					figure2D = figure2D.getParent();
				}
			}

			if (part == null)
				return getContents();

			return part;
		} finally {
			Math3DCache.returnVector3f(rayStart);
			Math3DCache.returnVector3f(rayDirection);
		}
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
	 * Sets the figure factory of this viewer.
	 * 
	 * @param i_factory
	 */
	public void setFigureFactory(IFigureFactory i_factory) {

		m_FigureFactory = i_factory;
	}

}
