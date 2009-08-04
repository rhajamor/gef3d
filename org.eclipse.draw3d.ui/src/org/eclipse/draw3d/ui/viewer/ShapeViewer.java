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
package org.eclipse.draw3d.ui.viewer;

import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.shapes.Cylinder;
import org.eclipse.draw3d.shapes.Sphere;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

/**
 * ShapeViewer There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 03.06.2009
 */
public class ShapeViewer extends Draw3DViewer {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.ui.viewer.Draw3DViewer#createContents()
	 */
	@Override
	protected IFigure3D createContents() {

		IFigure3D figure = new Figure3D() {

			private Cylinder m_cylinder = new Cylinder(12, 0.4f);

			private Sphere m_sphere = new Sphere(2);

			/**
			 * {@inheritDoc}
			 * 
			 * @see org.eclipse.draw3d.Figure3D#getDistance(org.eclipse.draw3d.picking.Query)
			 */
			@Override
			public float getDistance(Query i_query) {

				return m_sphere.getDistance(i_query, getPosition3D());
			}

			/**
			 * {@inheritDoc}
			 * 
			 * @see org.eclipse.draw3d.Figure3D#render(org.eclipse.draw3d.RenderContext)
			 */
			@Override
			public void render(RenderContext i_renderContext) {

				m_sphere.setFill(false);
				m_sphere.setPosition(getPosition3D());
				m_sphere.render(i_renderContext);

				/*
				 * m_cylinder.setOutlineColor(getForegroundColor(), 127);
				 * m_cylinder.setFillColor(getBackgroundColor(), 127);
				 * m_cylinder.setPosition(getPosition3D());
				 * m_cylinder.setOutline(true); m_cylinder.setFill(true);
				 * m_cylinder.setRadiusProportions(0);
				 * m_cylinder.setSegments(30);
				 * m_cylinder.render(i_renderContext);
				 */
			}
		};

		figure.getPosition3D().setLocation3D(new Vector3fImpl(0, 0, 0));
		figure.getPosition3D().setSize3D(new Vector3fImpl(100, 100, 100));

		Device dev = Display.getCurrent();
		figure.setBackgroundColor(dev.getSystemColor(SWT.COLOR_DARK_BLUE));
		figure.setForegroundColor(dev.getSystemColor(SWT.COLOR_CYAN));

		return figure;
	}
}
