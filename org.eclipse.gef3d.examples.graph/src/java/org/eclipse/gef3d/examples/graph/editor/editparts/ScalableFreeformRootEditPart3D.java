package org.eclipse.gef3d.examples.graph.editor.editparts;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw3d.DispatchingConnectionLayer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;

/**
 * ScalableFreeformRootEditPart3D with special printable layers, a
 * {@link DispatchingConnectionLayer}, a primary layer (FreeformLayer).
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 22, 2009
 */
public class ScalableFreeformRootEditPart3D extends
		ScalableFreeformRootEditPart {
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
		layeredPane.add(new DispatchingConnectionLayer(), CONNECTION_LAYER);
		return layeredPane;
	}
}