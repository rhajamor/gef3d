/*
 * Type:    org.eclipse.emf.ecoretools.diagram.edit.parts.DiagramFigure3D
 * File:  	DiagramFigure3D.java
 * Project:	de.feu.gef3d.ecoretools
 * Date: 	06.12.2008
 * Author: 	Kristian Duske
 * Version:	$Revision$
 * Changed: $Date$ by $Author$ 
 * URL:     $HeadURL$
 *
 * Copyright 2007, FernUniversitaet in Hagen
 */

package org.eclipse.gef3d.examples.uml2.figures;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.TransparentObject;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.shapes.CuboidFigureShape;
import org.eclipse.draw3d.shapes.Shape;

public class DiagramFigure3D extends Figure3D implements TransparentObject {

	private static final Vector3f TMP_V3 = new Vector3fImpl();

	private Shape m_shape = new CuboidFigureShape(this);

	protected int headerStyle;

	public DiagramFigure3D() {

		setLayoutManager(new FreeformLayout());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.TransparentObject#getTransparencyDepth(RenderContext)
	 */
	public float getTransparencyDepth(RenderContext renderContext) {
		ICamera camera = renderContext.getCamera();

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