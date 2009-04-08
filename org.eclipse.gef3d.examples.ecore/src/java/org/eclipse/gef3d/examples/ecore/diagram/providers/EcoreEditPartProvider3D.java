/*******************************************************************************
 * Copyright (c) 2008 Kristian Duske and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.ecore.diagram.providers;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw3d.DispatchingConnectionLayer;
import org.eclipse.emf.ecoretools.diagram.providers.EcoreEditPartProvider;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef3d.examples.ecore.diagram.parts.EcoreEditPartFactory3D;
import org.eclipse.gef3d.examples.ecore.figures.FeedbackLayer3D;
import org.eclipse.gef3d.gmf.runtime.draw2d.ui.internal.figures.ConnectionLayerEx.DispatchingConnectionLayerEx;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.CreateRootEditPartOperation;
import org.eclipse.gmf.runtime.notation.Diagram;


/**
 * EcoreEditPartProvider3D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.01.2009
 */
public class EcoreEditPartProvider3D extends EcoreEditPartProvider {

	/**
	 * Creates a new edit part provider for the 3D Ecore editor.
	 */
	public EcoreEditPartProvider3D() {
		setFactory(new EcoreEditPartFactory3D());
		setAllowCaching(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecoretools.diagram.providers.EcoreEditPartProvider#provides(org.eclipse.gmf.runtime.common.core.service.IOperation)
	 */
	@Override
	public synchronized boolean provides(IOperation i_operation) {

		if (i_operation instanceof CreateRootEditPartOperation)
			return true;

		return super.provides(i_operation);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider#createRootEditPart(org.eclipse.gmf.runtime.notation.Diagram)
	 */
	@Override
	public RootEditPart createRootEditPart(Diagram i_diagram) {

		return new DiagramRootEditPart() {

			/**
			 * {@inheritDoc}
			 * 
			 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getFigure()
			 */
			@Override
			public IFigure getFigure() {
				// TODO implement method .getFigure
				return super.getFigure();
			}

			@Override
			protected void createLayers(LayeredPane layeredPane) {
				super.createLayers(layeredPane);
				// replace 2D version with 3D one:
				IFigure oldLayer = layeredPane.getLayer(FEEDBACK_LAYER);
				layeredPane.remove(oldLayer);
				layeredPane.add(new FeedbackLayer3D(), FEEDBACK_LAYER);
			}

			/**
			 * {@inheritDoc}
			 * 
			 * @see org.eclipse.gef.editparts.FreeformGraphicalRootEditPart#createPrintableLayers()
			 */
			@Override
			protected LayeredPane createPrintableLayers() {
				FreeformLayeredPane layeredPane = new FreeformLayeredPane() {
					@Override
					public void paint(Graphics graphics) {
						for (Object child : getChildren()) {
							if (child instanceof DispatchingConnectionLayer) {
								((DispatchingConnectionLayer) child)
										.dispatchPendingConnections();
							}
						}
						super.paint(graphics);
					}
				};

				layeredPane.add(new FreeformLayer(), PRIMARY_LAYER);
				layeredPane.add(new DispatchingConnectionLayerEx(),
						CONNECTION_LAYER);
				layeredPane.add(new FreeformLayer(),
						DiagramRootEditPart.DECORATION_PRINTABLE_LAYER);

				return layeredPane;
			}

		};
	}
}
