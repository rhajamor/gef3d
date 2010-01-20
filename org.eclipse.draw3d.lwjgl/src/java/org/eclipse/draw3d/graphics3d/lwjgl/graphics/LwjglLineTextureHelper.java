/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d.lwjgl.graphics;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw3d.graphics3d.DisplayListManager;

/**
 * LineTextureHelper There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 12.01.2010
 */
public class LwjglLineTextureHelper {

	private Map<Object, LwjglLinePattern> m_linePatterns;

	private DisplayListManager m_displayManager;

	public void addTextureCoordinate(int[] i_dashPattern, float i_x1,
		float i_y1, float i_x2, float i_y2, FloatBuffer i_buffer) {

		if (i_dashPattern == null)
			throw new NullPointerException("i_dashPattern must not be null");

		if (i_buffer == null)
			throw new NullPointerException("i_buffer must not be null");

		LwjglLinePattern linePattern = getLinePattern(i_dashPattern);
		i_buffer.put(linePattern.getS(i_x1, i_y1, i_x2, i_y2));
	}

	private LwjglLinePattern getLinePattern(int[] i_dashPattern) {

		Object key = LwjglLinePattern.getKey(i_dashPattern);
		LwjglLinePattern linePattern = null;

		if (m_linePatterns != null)
			linePattern = m_linePatterns.get(key);

		if (linePattern == null) {
			linePattern = new LwjglLinePattern(i_dashPattern, m_displayManager);

			if (m_linePatterns == null)
				m_linePatterns = new HashMap<Object, LwjglLinePattern>();

			m_linePatterns.put(key, linePattern);
		}

		return linePattern;
	}
}
