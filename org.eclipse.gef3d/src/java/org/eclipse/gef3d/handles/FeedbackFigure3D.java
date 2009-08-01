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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.TransparentObject;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.Shape;

/**
 * Cube like transparent figure used for visualizing feedback during edit
 * operations like resizing. While GEF uses a RectangleFigure, here an explicit
 * figure is used.
 * <p>
 * Important note: The feedback figure must not be painted in picking mode!
 * 
 * @todo: Why must a feedback figure not be painted in picking mode?
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 31, 2008
 */
public class FeedbackFigure3D extends Figure3D implements TransparentObject {

	private static final Vector3f TMP_V3 = new Vector3fImpl();

	private Shape m_shape = new CuboidFigureShape(this);

	/**
	 * 
	 */
	public FeedbackFigure3D() {

		setBackgroundColor(ColorConstants.blue);
		setForegroundColor(ColorConstants.black);
		setAlpha(100);
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
	 * @see org.eclipse.draw3d.Figure3D#render(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	public void render(RenderContext renderContext) {

		renderContext.addTransparentObject(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#renderTransparent(org.eclipse.draw3d.RenderContext)
	 */
	public void renderTransparent(RenderContext renderContext) {

		m_shape.render(renderContext);
	}

}
