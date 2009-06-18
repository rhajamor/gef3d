/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthias Thiele - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Graphics3DUtil interface defines high-level 3D draw operations.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since 06.12.2008
 */
public interface Graphics3DUtil {

	public static final int UNDEFINED_CONST_VAL = -1;

	/*
	 * Some GL drawing constants, mainly used for controlling the drawing
	 * methods. As the default renderer is an OpenGL implementation, these
	 * constant have the corresponding values. Other renderer implementations
	 * have to interpret their meaning towards their specifics.
	 */

	public static final int GLU_FILL = 100012; // org.lwjgl.util.glu.GLU.GLU_FILL;

	public static final int GLU_FLAT = 100001; // org.lwjgl.util.glu.GLU.GLU_FLAT;

	public static final int GLU_INSIDE = 100021; // org.lwjgl.util.glu.GLU.GLU_INSIDE;

	public static final int GLU_SILHOUETTE = 100013; // org.lwjgl.util.glu.GLU.GLU_SILHOUETTE;

	public static final int GLU_POINT = 100010; // org.lwjgl.util.glu.GLU.GLU_POINT;

	public static final int GLU_LINE = 100011; // org.lwjgl.util.glu.GLU.GLU_LINE;

	public static final int GLU_OUTSIDE = 100020; // org.lwjgl.util.glu.GLU.GLU_OUTSIDE;

	public static final int GLU_SMOOTH = 100000; // org.lwjgl.util.glu.GLU.GLU_SMOOTH;

	public static final int GLU_NONE = 100002; // org.lwjgl.util.glu.GLU.GLU_NONE;

	/*
	 * These are the high-level 3D methods. As the default renderer
	 * implementation is OpenGL, the methods are named accordingly and will fit
	 * well to the OpenGL renderer. Other renderer implementation have to
	 * interpret the methods according to their specifics.
	 */

	public abstract void gluLookAt(float eyex, float eyey, float eyez,
			float centerx, float centery, float centerz, float upx, float upy,
			float upz);

	public abstract void gluOrtho2D(int left, int right, int bottom, int top);

	public abstract void gluPerspective(int fovy, float aspect, int zNear,
			int zFar);

	public abstract void gluUnProject(int winx, int winy, float winz,
			FloatBuffer modelMatrix, FloatBuffer projMatrix,
			IntBuffer viewport, FloatBuffer obj_pos);
}