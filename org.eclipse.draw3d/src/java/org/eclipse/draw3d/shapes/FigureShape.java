/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.ParaxialBoundingBox;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.swt.graphics.Color;

/**
 * An abstract base class for shapes that belong to a figure.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 27.02.2008
 */
public abstract class FigureShape implements TransparentShape {

	private IFigure3D m_figure;

	/**
	 * Creates a new shape that belongs to the given figure.
	 * 
	 * @param i_figure the figure to which this shape belongs
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public FigureShape(IFigure3D i_figure) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		m_figure = i_figure;
	}

	/**
	 * Returns the distance of the point of intersection between the picking ray
	 * that is stored in the given query and this shape.
	 * 
	 * @param i_figure the figure to which this shape belongs
	 * @param i_query the query
	 * @param i_position the position of this shape
	 * @return the distance or {@link Float#NaN} if this shape is not hit by the
	 *         picking ray
	 */
	protected abstract float doGetDistance(IFigure3D i_figure, Query i_query);

	/**
	 * Returns a paraxial (to the world coordinate system) bounding box that
	 * contains this object.
	 * 
	 * @param i_figure the figure which this shape belongs to
	 * @param o_result the result bounding box, if <code>null</code>, a new
	 *            bounding box will be returned
	 * @return the paraxial bounding box
	 */
	protected abstract ParaxialBoundingBox doGetParaxialBoundingBox(
		IFigure3D i_figure, ParaxialBoundingBox o_result);

	/**
	 * Perform the actual rendering. Extenders of this class must override and
	 * implement this method.
	 * 
	 * @param i_figure the figure which this shape belongs to
	 * @param i_renderContext the current render context
	 */
	protected abstract void doRender(IFigure3D i_figure,
		RenderContext i_renderContext);

	/**
	 * Returns the background color of the figure to which this shape belongs.
	 * 
	 * @return a float array containing the red, green, blue and alpha values
	 */
	protected float[] getBackgroundColor() {

		int alpha = m_figure.getAlpha();
		Color backgroundColor = m_figure.getBackgroundColor();

		return ColorConverter.toFloatArray(backgroundColor, alpha, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.Shape#getDistance(org.eclipse.draw3d.picking.Query)
	 */
	public float getDistance(Query i_query) {

		return doGetDistance(m_figure, i_query);
	}

	/**
	 * Returns the foreground color of the figure to which this shape belongs.
	 * 
	 * @return a float array containing the red, green, blue and alpha values
	 */
	protected float[] getForegroundColor() {

		int alpha = m_figure.getAlpha();
		Color foregroundColor = m_figure.getForegroundColor();

		return ColorConverter.toFloatArray(foregroundColor, alpha, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.picking.Pickable#getParaxialBoundingBox(org.eclipse.draw3d.geometry.ParaxialBoundingBox)
	 */
	public ParaxialBoundingBox getParaxialBoundingBox(
		ParaxialBoundingBox o_result) {

		return doGetParaxialBoundingBox(m_figure, o_result);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Shape#render(RenderContext)
	 */
	public final void render(RenderContext i_renderContext) {
		if (isTransparent()) {
			i_renderContext.addTransparentObject(this);
		} else {
			doRender(m_figure, i_renderContext);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#renderTransparent(org.eclipse.draw3d.RenderContext)
	 */
	public void renderTransparent(RenderContext i_renderContext) {
		doRender(m_figure, i_renderContext);
	}

	/**
	 * Returns true if this figure shape is to be rendered transparently, i.e.
	 * if it has to be rendered after all opaque objects have been rendered. The
	 * default implementation examines the alpha value of the figure.
	 * 
	 * @return
	 */
	public boolean isTransparent() {
		int alpha = m_figure.getAlpha();
		return alpha < 255; // true; // m_figure.getAlpha()<255;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#getTransparencyDepth(org.eclipse.draw3d.RenderContext)
	 */
	public float getTransparencyDepth(RenderContext i_renderContext) {
		ICamera camera = i_renderContext.getScene().getCamera();

		Vector3f center = Draw3DCache.getVector3f();
		try {
			m_figure.getBounds3D().getCenter(center);
			float dist = camera.getDistance(center);
			return dist;
		} finally {
			Draw3DCache.returnVector3f(center);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String className = getClass().getName();
		className = className.substring(className.lastIndexOf("."));

		return className;
	}

}
