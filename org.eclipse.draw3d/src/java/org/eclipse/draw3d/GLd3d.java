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
package org.eclipse.draw3d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.util.BufferUtils;

/**
 * GLd3d There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 13.12.2007
 */
public class GLd3d {
	// ByteBuffer.allocateDirect(4 * 16)
	// .order(ByteOrder.nativeOrder()).asIntBuffer();
	private static FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

	private static IntBuffer intBuffer = BufferUtils.createIntBuffer(16);

	private static final Pattern VERSION_PATTERN = Pattern
			.compile("(\\d+)\\.(\\d+).*");

	/**
	 * @param i_width
	 */
	public static int getAlignedWidth(Graphics3D g3d, int i_width) {
		int unpackAlignment = GLd3d
				.glGetInteger(g3d, Graphics3DDraw.GL_UNPACK_ALIGNMENT);
		int alignedWidth = i_width;
		int rest = alignedWidth % unpackAlignment;
		if (rest > 0) {
			alignedWidth += unpackAlignment - rest;
		}
		return alignedWidth;
	}

	/**
	 * Returns the Open GL version supported by the runtime system. The returned
	 * array contains the major and minor version.
	 * 
	 * @return an array containing the major and minor version in that order
	 */
	public static int[] getVersion(Graphics3D g3d) {
		String versionString = g3d.glGetString(Graphics3DDraw.GL_VERSION);
		Matcher matcher = VERSION_PATTERN.matcher(versionString);

		if (!matcher.matches())
			throw new IllegalStateException("invalid OpenGL version string: "
					+ versionString);

		int majorVersion = Integer.parseInt(matcher.group(1));
		int minorVersion = Integer.parseInt(matcher.group(2));

		return new int[] { majorVersion, minorVersion };
	}

	public static float glGetFloat(Graphics3D g3d, int pname) {
		g3d.glGetFloat(pname, floatBuffer);
		float value = floatBuffer.get(0);
		return value;
	}

	public static int glGetInteger(Graphics3D g3d, int pname) {
		g3d.glGetInteger(pname, intBuffer);
		int value = intBuffer.get(0);
		return value;
	}
}
