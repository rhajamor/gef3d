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

import java.util.logging.Logger;

import org.eclipse.draw3d.DisplayListManager;
import org.eclipse.draw3d.RenderContext;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector2fImpl;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.picking.Query;
import org.eclipse.draw3d.util.ColorConverter;
import org.eclipse.swt.graphics.Color;

/**
 * A cube with a color and an optional texture on its front face.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 27.02.2008
 */
public class CuboidShape extends PositionableShape {

	private static final String DL_FILL_FRONT = "fill_cube_front";

	private static final String DL_FILL_REST = "fill_cube_rest";

	private static final String DL_OUTLINE = "outline_cube";

	private static final String DL_TEXTURE = "cube_texture";

	/**
	 * Contains the indices of the vertices of the faces in the following order:
	 * <ol start="0">
	 * <li>The face on the plane Z=0.</li>
	 * <li>The face on the plane Z=1.</li>
	 * <li>The face on the plane X=0.</li>
	 * <li>The face on the plane X=1.</li>
	 * <li>The face on the plane Y=0.</li>
	 * <li>The face on the plane Y=1.</li>
	 * </ol>
	 * The vertices of each face are wound in counter-clockwise order.
	 */
	private static final int[][] FACES = new int[6][4];

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(CuboidShape.class.getName());

	/**
	 * Contains the normal vectors of the faces in the same order as the faces
	 * are in the {@link #FACES} array.
	 */
	private static final IVector3f[] NORMALS = new IVector3f[6];

	/**
	 * Contains the texture coords for the face Z=0.
	 */
	private static final IVector2f[] TEX_COORDS = new IVector2f[4];

	/**
	 * Contains the vertices of a unit cube. The position <code>i</code> of a
	 * vertex in the array determines its spacial position because i in binary
	 * equals xyz. So if a vector is at position 3, which is 011 in binary, its
	 * X component is 0 and its Y and Z component is 1.
	 */
	private static final IVector3f[] VERTICES = new IVector3f[8];

	static {
		for (int i = 0; i < 8; i++) {
			int z = i & 1;
			int y = (i & 2) >> 1;
			int x = (i & 4) >> 2;
			VERTICES[i] = new Vector3fImpl(x, y, z);
		}

		FACES[0][0] = 2;
		FACES[0][1] = 6;
		FACES[0][2] = 4;
		FACES[0][3] = 0;
		NORMALS[0] = IVector3f.Z_AXIS_NEG;

		FACES[1][0] = 5;
		FACES[1][1] = 7;
		FACES[1][2] = 3;
		FACES[1][3] = 1;
		NORMALS[1] = IVector3f.Z_AXIS;

		FACES[2][0] = 1;
		FACES[2][1] = 3;
		FACES[2][2] = 2;
		FACES[2][3] = 0;
		NORMALS[2] = IVector3f.X_AXIS_NEG;

		FACES[3][0] = 6;
		FACES[3][1] = 7;
		FACES[3][2] = 5;
		FACES[3][3] = 4;
		NORMALS[3] = IVector3f.X_AXIS;

		FACES[4][0] = 4;
		FACES[4][1] = 5;
		FACES[4][2] = 1;
		FACES[4][3] = 0;
		NORMALS[4] = IVector3f.Y_AXIS_NEG;

		FACES[5][0] = 3;
		FACES[5][1] = 7;
		FACES[5][2] = 6;
		FACES[5][3] = 2;
		NORMALS[5] = IVector3f.Y_AXIS;

		TEX_COORDS[0] = new Vector2fImpl(0, 0);
		TEX_COORDS[1] = new Vector2fImpl(1, 0);
		TEX_COORDS[2] = new Vector2fImpl(1, 1);
		TEX_COORDS[3] = new Vector2fImpl(0, 1);
	}

	private boolean m_fill = true;

	private float[] m_fillColor = new float[] { 1, 1, 1, 1 };

	private boolean m_outline = true;

	private float[] m_outlineColor = new float[] { 0, 0, 0, 1 };

	private Integer m_textureId;

	/**
	 * Creates a new cuboid shape with the given position.
	 * 
	 * @param i_position3D the position of this cuboid shape
	 * @throws NullPointerException if the given position is <code>null</code>
	 */
	public CuboidShape(IPosition3D i_position3D) {

		super(i_position3D);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#doGetDistance(org.eclipse.draw3d.picking.Query)
	 */
	@Override
	protected float doGetDistance(Query i_query) {

		IVector3f rayOrigin = i_query.getRayOrigin();
		IVector3f rayDirection = i_query.getRayDirection();

		IVector3f[] face = new Vector3f[4];
		float distance;

		for (int i = 0; i < FACES.length; i++) {
			for (int j = 0; j < FACES[i].length; j++)
				face[j] = VERTICES[FACES[i][j]];

			distance =
				Math3D.rayIntersectsPolygon(rayOrigin, rayDirection, face,
					NORMALS[i], null);

			if (!Float.isNaN(distance))
				return distance;
		}

		return Float.NaN;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#doRender(org.eclipse.draw3d.RenderContext)
	 */
	@Override
	protected void doRender(RenderContext i_renderContext) {
		DisplayListManager displayListManager =
			i_renderContext.getDisplayListManager();

		Graphics3D g3d = i_renderContext.getGraphics3D();
		initDisplayLists(displayListManager, g3d);

		if (!isTransparent()) {
			if (m_fill) {
				renderFill(displayListManager, g3d);
			}
			if (m_outline) {
				renderOutline(displayListManager, g3d);
			}
		} else {
			if (m_outline) {
				renderOutline(displayListManager, g3d);
			}
			if (m_fill) {
				renderFill(displayListManager, g3d);
			}

		}
	}

	/**
	 * @param displayListManager
	 * @param g3d
	 */
	private void renderOutline(DisplayListManager displayListManager,
		Graphics3D g3d) {
		g3d.glColor4f(m_outlineColor);
		displayListManager.executeDisplayList(DL_OUTLINE);
	}

	/**
	 * @param displayListManager
	 * @param g3d
	 */
	private void renderFill(DisplayListManager displayListManager,
		Graphics3D g3d) {
		g3d.glPolygonMode(Graphics3DDraw.GL_FRONT_AND_BACK,
			Graphics3DDraw.GL_FILL);

		if (m_textureId != null) {
			g3d.glColor4f(0, 0, 0, 0);

			g3d.glBindTexture(Graphics3DDraw.GL_TEXTURE_2D, m_textureId);
			g3d.glTexEnvi(Graphics3DDraw.GL_TEXTURE_ENV,
				Graphics3DDraw.GL_TEXTURE_ENV_MODE, Graphics3DDraw.GL_REPLACE);
			displayListManager.executeDisplayList(DL_TEXTURE);
			g3d.glBindTexture(Graphics3DDraw.GL_TEXTURE_2D, 0);

			g3d.glColor4f(m_fillColor);
		} else {
			g3d.glColor4f(m_fillColor);
			displayListManager.executeDisplayList(DL_FILL_FRONT);
		}

		displayListManager.executeDisplayList(DL_FILL_REST);
	}

	private void initDisplayLists(DisplayListManager i_displayListManager,
		final Graphics3D g3d) {

		if (i_displayListManager.isDisplayList(DL_OUTLINE, DL_FILL_REST,
			DL_FILL_FRONT, DL_TEXTURE))
			return;

		Runnable outline = new Runnable() {

			public void run() {
				g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
				g3d.glVertex3f(0, 0, 0);
				g3d.glVertex3f(1, 0, 0);
				g3d.glVertex3f(1, 0, 1);
				g3d.glVertex3f(0, 0, 1);
				g3d.glEnd();

				g3d.glBegin(Graphics3DDraw.GL_LINE_LOOP);
				g3d.glVertex3f(0, 1, 0);
				g3d.glVertex3f(1, 1, 0);
				g3d.glVertex3f(1, 1, 1);
				g3d.glVertex3f(0, 1, 1);
				g3d.glEnd();

				g3d.glBegin(Graphics3DDraw.GL_LINES);
				g3d.glVertex3f(0, 0, 0);
				g3d.glVertex3f(0, 1, 0);
				g3d.glVertex3f(1, 0, 0);
				g3d.glVertex3f(1, 1, 0);
				g3d.glVertex3f(0, 0, 1);
				g3d.glVertex3f(0, 1, 1);
				g3d.glVertex3f(1, 0, 1);
				g3d.glVertex3f(1, 1, 1);
				g3d.glEnd();
			}
		};

		Runnable front = new Runnable() {
			public void run() {
				g3d.glBegin(Graphics3DDraw.GL_QUADS);
				g3d.glNormal3f(NORMALS[0]);
				for (int i = 0; i < 4; i++)
					g3d.glVertex3f(VERTICES[FACES[0][i]]);
				g3d.glEnd();
			}
		};

		Runnable texture = new Runnable() {
			public void run() {
				g3d.glBegin(Graphics3DDraw.GL_QUADS);
				g3d.glNormal3f(NORMALS[0]);
				for (int i = 0; i < 4; i++) {
					g3d.glTexCoord2f(TEX_COORDS[i]);
					g3d.glVertex3f(VERTICES[FACES[0][i]]);
				}

				g3d.glNormal3f(NORMALS[1]);
				for (int i = 3; i >= 0; i--) {
					g3d.glTexCoord2f(TEX_COORDS[i]);
					g3d.glVertex3f(VERTICES[FACES[0][i]]);
				}
				g3d.glEnd();
			}
		};

		Runnable rest = new Runnable() {
			public void run() {
				g3d.glBegin(Graphics3DDraw.GL_QUADS);
				for (int j = 1; j < FACES.length; j++) {
					g3d.glNormal3f(NORMALS[j]);
					for (int i = 0; i < 4; i++)
						g3d.glVertex3f(VERTICES[FACES[j][i]]);
				}
				g3d.glEnd();
			}
		};

		i_displayListManager.createDisplayList(DL_OUTLINE, outline);
		i_displayListManager.createDisplayList(DL_FILL_FRONT, front);
		i_displayListManager.createDisplayList(DL_TEXTURE, texture);
		i_displayListManager.createDisplayList(DL_FILL_REST, rest);
	}

	/**
	 * Specifies whether this cuboid should render its faces.
	 * 
	 * @param i_fill <code>true</code> if the cuboid should render its faces and
	 *            <code>false</code> otherwise
	 */
	public void setFill(boolean i_fill) {

		m_fill = i_fill;
	}

	/**
	 * Sets the fill color.
	 * 
	 * @param i_color the fill color
	 * @param i_alpha the alpha value
	 */
	public void setFillColor(Color i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_fillColor);
	}

	/**
	 * Sets the fill color.
	 * 
	 * @param i_red the red component
	 * @param i_green the green component
	 * @param i_blue the blue component
	 * @param i_alpha the alpha value
	 */
	public void setFillColor(float i_red, float i_green, float i_blue,
		float i_alpha) {

		m_fillColor[0] = i_red;
		m_fillColor[1] = i_green;
		m_fillColor[2] = i_blue;
		m_fillColor[3] = i_alpha;
	}

	/**
	 * Specifies whether this cuboid should render its outline.
	 * 
	 * @param i_outline <code>true</code> if the cuboid should render its
	 *            outline and <code>false</code> otherwise
	 */
	public void setOutline(boolean i_outline) {

		m_outline = i_outline;
	}

	/**
	 * Sets the outline color.
	 * 
	 * @param i_color the outline color
	 * @param i_alpha the alpha value
	 */
	public void setOutlineColor(Color i_color, int i_alpha) {

		ColorConverter.toFloatArray(i_color, i_alpha, m_outlineColor);
	}

	/**
	 * Sets the outline color.
	 * 
	 * @param i_red the red component
	 * @param i_green the green component
	 * @param i_blue the blue component
	 * @param i_alpha the alpha value
	 */
	public void setOutlineColor(float i_red, float i_green, float i_blue,
		float i_alpha) {

		m_outlineColor[0] = i_red;
		m_outlineColor[1] = i_green;
		m_outlineColor[2] = i_blue;
		m_outlineColor[3] = i_alpha;
	}

	/**
	 * Sets the id of the texture to render on the front face. If the given id
	 * is <code>null</code>, no texture is rendered.
	 * 
	 * @param i_textureId the texture id
	 */
	public void setTextureId(Integer i_textureId) {

		m_textureId = i_textureId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.shapes.PositionableShape#isTransparent()
	 */
	@Override
	public boolean isTransparent() {
		return (m_fill && m_fillColor[3] < 255)
			|| (m_outline && m_outlineColor[3] < 255);
	}

}
