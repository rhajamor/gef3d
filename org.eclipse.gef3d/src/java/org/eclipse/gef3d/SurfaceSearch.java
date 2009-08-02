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
package org.eclipse.gef3d;

import static org.eclipse.gef.LayerConstants.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.editparts.LayerManager;

/**
 * SurfaceSearch There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 02.08.2009
 */
public class SurfaceSearch implements TreeSearch {

	private Set<IFigure> m_ignoredLayers;

	private Set<IFigure> getIgnoredLayers() {

		if (m_ignoredLayers == null) {
			m_ignoredLayers = new HashSet<IFigure>();

			addLayer(m_layerManager.getLayer(CONNECTION_LAYER));
			addLayer(m_layerManager.getLayer(FEEDBACK_LAYER));
			addLayer(m_layerManager.getLayer(GRID_LAYER));
			addLayer(m_layerManager.getLayer(GUIDE_LAYER));
			addLayer(m_layerManager.getLayer(HANDLE_LAYER));
			addLayer(m_layerManager.getLayer(SCALED_FEEDBACK_LAYER));
		}

		return m_ignoredLayers;
	}

	private void addLayer(IFigure i_layer) {

		if (i_layer == null)
			return;

		m_ignoredLayers.add(i_layer);
	}

	private LayerManager m_layerManager;

	/**
	 * Creates a new surface search. The given edit part viewer is used to
	 * obtain the layer manager. The actual set of ignored layers is created
	 * lazily in order to make sure that the layers in question have been
	 * created.
	 * 
	 * @param i_viewer the layer manager to get the layers from
	 */
	public SurfaceSearch(EditPartViewer i_viewer) {

		if (i_viewer == null)
			throw new NullPointerException("i_viewer must not be null");

		m_layerManager =
			(LayerManager) i_viewer.getEditPartRegistry().get(LayerManager.ID);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.TreeSearch#accept(org.eclipse.draw2d.IFigure)
	 */
	public boolean accept(IFigure i_figure) {

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.TreeSearch#prune(org.eclipse.draw2d.IFigure)
	 */
	public boolean prune(IFigure i_figure) {

		return getIgnoredLayers().contains(i_figure);
	}

}
