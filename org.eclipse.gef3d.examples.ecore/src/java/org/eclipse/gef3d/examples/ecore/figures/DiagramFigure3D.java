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
package org.eclipse.gef3d.examples.ecore.figures;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.TransparentObject;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.Shape;

/**
 * A 3D figure that represents a diagram. The diagram elements are drawn on the
 * figure's surface.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 08.04.2009
 */
public class DiagramFigure3D extends Figure3D implements TransparentObject {

	private static final Vector3f TMP_V3 = new Vector3fImpl();

	protected int headerStyle;

	private Shape m_shape = new CuboidFigureShape(this);

	public DiagramFigure3D() {

		setLayoutManager(new FreeformLayout());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#getDistance(org.eclipse.draw3d.picking.Query)
	 */
	@Override
	public float getDistance(Query i_query) {

		return m_shape.getDistance(i_query, getPosition3D());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#getTransparencyDepth()
	 */
	public float getTransparencyDepth(RenderContext renderContext) {
		ICamera camera = renderContext.getScene().getCamera();

		getBounds3D().getCenter(TMP_V3);
		return camera.getDistance(TMP_V3);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#paint(org.eclipse.draw2d.Graphics)
	 */
	@Override
	public void paint(Graphics i_graphics) {
		// TODO implement method DiagramFigure3D.paint
		super.paint(i_graphics);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.Figure3D#render()
	 */
	@Override
	public void render(RenderContext renderContext) {
		renderContext.addTransparentObject(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#renderTransparent()
	 */
	public void renderTransparent(RenderContext renderContext) {
		m_shape.render(renderContext);
	}
}