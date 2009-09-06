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
import org.eclipse.gef3d.gmf.runtime.core.service.ProviderAcceptor;
import org.eclipse.gef3d.gmf.runtime.draw2d.ui.internal.figures.ConnectionLayerEx.DispatchingConnectionLayerEx;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.CreateGraphicEditPartOperation;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

/**
 * EcoreEditPartProvider3D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 05.01.2009
 */
public class EcoreEditPartProvider3D extends EcoreEditPartProvider {

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.emf.ecoretools.diagram.providers.EcoreEditPartProvider#createEditPart(org.eclipse.gmf.runtime.notation.View)
	 */
	@Override
	protected IGraphicalEditPart createEditPart(View i_view) {
		// TODO implement method EcoreEditPartProvider3D.createEditPart
		return super.createEditPart(i_view);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecoretools.diagram.providers.EcoreEditPartProvider#createGraphicEditPart(org.eclipse.gmf.runtime.notation.View)
	 */
	@Override
	public synchronized IGraphicalEditPart createGraphicEditPart(View i_view) {
		// TODO implement method EcoreEditPartProvider3D.createGraphicEditPart
		return super.createGraphicEditPart(i_view);
	}
	
	/**
	 * Creates a new edit part provider for the 3D Ecore editor.
	 */
	public EcoreEditPartProvider3D() {
		super(); // super constructor sets a 2D factory
		// which is here replaced by a 3D version:
		setFactory(new EcoreEditPartFactory3D());
		setAllowCaching(true);
	}

	/**
	 * Returns true if editor is accepts this provider, this is evaluated using
	 * the {@link ProviderAcceptor} retrieved from the operation. The acceptor
	 * must accept 3D providers, i.e.
	 * {@link ProviderAcceptor#evaluate3DAcceptance(org.eclipse.gmf.runtime.common.core.service.IProvider, IOperation)}
	 * must return true.
	 * 
	 * @see org.eclipse.emf.ecoretools.diagram.providers.EcoreEditPartProvider#provides(org.eclipse.gmf.runtime.common.core.service.IOperation)
	 */
	@Override
	public synchronized boolean provides(IOperation i_operation) {
		if (i_operation instanceof CreateGraphicEditPartOperation) {
			// ProviderAcceptor providerAcceptor =
			// ProviderAcceptor.retrieveProviderAcceptor(i_operation);
			boolean bIsAccepted =
				ProviderAcceptor.evaluate3DAcceptance(this, i_operation);
			// boolean bIsSupported = isSupported();
			// CreateGraphicEditPartOperation op =
			// (CreateGraphicEditPartOperation) i_operation;

			bIsAccepted &= super.provides(i_operation);

			return bIsAccepted;

		}
		return false;
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
