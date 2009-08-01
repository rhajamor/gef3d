/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.shapes;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw3d.DisplayListManager;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Matrix4fImpl;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.draw3d.geometry.Position3DUtil;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.swt.graphics.Color;

/**
 * Renders a model matrix as a cone.
 * 
 * @todo Disk and Cylinder are not defined yet (were defined in LWJGL), thus
 *       this class is not working yet!
 * @author Kristian Duske
 * @version $Revision$
 * @since 19.05.2008
 */
public class ConeFigureShape extends AbstractModelShape {
	private static final String DL_SOLID = "solid_cone";

	private static final String DL_WIRED = "wired_cone";

	private static final float HEIGHT = 16;

	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(ConeFigureShape.class.getName());

	private static final int LOOPS = 1;

	private static final float RADIUS = 6;

	private static final int SLICES = 8;

	private static final int SUBDIVS = 1;

	private static final float[] TMP_4F = new float[4];

	private static final Matrix4fImpl TMP_M4 = new Matrix4fImpl();

	private static final Vector3fImpl TMP_V3 = new Vector3fImpl();

	private static boolean WARN_ONCE = true;

	private final IFigure3D m_figure;

	private boolean m_solid = true;

	/**
	 * Creates a new shape for the given figure.
	 * 
	 * @param i_figure the figure to render
	 * @throws NullPointerException if the given figure is <code>null</code>
	 */
	public ConeFigureShape(IFigure3D i_figure) {

		if (WARN_ONCE && log.isLoggable(Level.WARNING)) {
			log
				.warning("Cone figure is not working yet, since disc and cylinder are not implemented.");
			WARN_ONCE = false;
		}

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		m_figure = i_figure;
	}

	private void initDisplayLists(DisplayListManager i_displayListManager) {

		if (i_displayListManager.isDisplayList(DL_SOLID, DL_WIRED))
			return;

		i_displayListManager.createDisplayList(DL_SOLID, new Runnable() {

			public void run() {

				// Cylinder cylinder = new Cylinder();
				// cylinder.setNormals(Graphics3DUtil.GLU_FLAT);
				// cylinder.setDrawStyle(Graphics3DUtil.GLU_FILL);
				//
				// Disk disk = new Disk();
				// disk.setNormals(Graphics3DUtil.GLU_FLAT);
				// disk.setDrawStyle(Graphics3DUtil.GLU_FILL);
				// disk.setOrientation(Graphics3DUtil.GLU_INSIDE);
				//
				// cylinder.draw(RADIUS, 0, HEIGHT, SLICES, SUBDIVS);
				// disk.draw(0, RADIUS, SLICES, LOOPS);

				// dummy code:
				// TODO which g3d to be used, figures g3d or renderers?
				Graphics3D g3d = m_figure.getRenderContext().getGraphics3D();
				g3d.glBegin(Graphics3DDraw.GL_QUADS);
				g3d.glNormal3f(0, 0, -1);
				g3d.glVertex3f(0, 0, 0);
				g3d.glVertex3f(0, 1, 0);
				g3d.glVertex3f(1, 1, 0);
				g3d.glVertex3f(1, 0, 0);
				g3d.glEnd();
			}
		});

		i_displayListManager.createDisplayList(DL_WIRED, new Runnable() {

			public void run() {

				// Cylinder cylinder = new Cylinder();
				// cylinder.setNormals(Graphics3DUtil.GLU_FLAT);
				// cylinder.setDrawStyle(Graphics3DUtil.GLU_SILHOUETTE);
				//
				// cylinder.draw(RADIUS, 0, HEIGHT, SLICES, SUBDIVS);
				// dummy code:
				Graphics3D g3d = m_figure.getRenderContext().getGraphics3D();
				g3d.glBegin(Graphics3DDraw.GL_QUADS);
				g3d.glNormal3f(0, 0, -1);
				g3d.glVertex3f(0, 0, 0);
				g3d.glVertex3f(0, 1, 0);
				g3d.glVertex3f(1, 1, 0);
				g3d.glVertex3f(1, 0, 0);
				g3d.glEnd();
			}

		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.AbstractModelShape#performRender()
	 */
	@Override
	protected void performRender(RenderContext renderContext) {

		DisplayListManager displayListManager =
			renderContext.getDisplayListManager();
		initDisplayLists(displayListManager);
		Graphics3D g3d = renderContext.getGraphics3D();

		Color color = m_figure.getForegroundColor();
		int alpha = m_figure.getAlpha();

		ColorConverter.toFloatArray(color, alpha, TMP_4F);
		g3d.glColor4f(TMP_4F[0], TMP_4F[1], TMP_4F[2], TMP_4F[3]);

		if (m_solid)
			displayListManager.executeDisplayList(DL_SOLID);
		else
			displayListManager.executeDisplayList(DL_WIRED);
	}

	/**
	 * Specifies whether the cone shape is solid or a wireframe.
	 * 
	 * @param i_solid <code>true</code> if the cone is solid or
	 *            <code>false</code> if it is a wireframe
	 */
	public void setSolid(boolean i_solid) {

		m_solid = i_solid;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.AbstractModelShape#setup()
	 */
	@Override
	protected void setup(RenderContext renderContext) {

		// TMP_M4.set(m_figure.getLocationMatrix());
		// TMP_V3.set(0, 0, -HEIGHT);
		// Math3D.translate(TMP_V3, TMP_M4, TMP_M4);
		// setModelMatrix(TMP_M4);

		if (m_figure == null) {
			setPosition(null);
			return;
		}

		Position3D pos =
			Position3DUtil.createRelativePosition(m_figure.getParentHost3D());
		pos.setLocation3D(m_figure.getPosition3D().getLocation3D());
		pos.setRotation3D(m_figure.getPosition3D().getRotation3D());
		pos.setSize3D(IVector3f.UNITVEC3f);
		Vector3fImpl v = new Vector3fImpl(pos.getLocation3D());
		v.z -= HEIGHT;
		pos.setLocation3D(v);

		setPosition(pos);

	}

}
