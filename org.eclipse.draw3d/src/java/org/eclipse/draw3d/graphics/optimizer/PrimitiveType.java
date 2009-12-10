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
package org.eclipse.draw3d.graphics.optimizer;

/**
 * PrimitiveType There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 18.11.2009
 */
public enum PrimitiveType {

	FILLED_POLYGON, FILLED_QUAD, LINE, OUTLINED_POLYGON, OUTLINED_QUAD, POLYLINE;

	public boolean isFilled() {

		return this == FILLED_POLYGON || this == FILLED_QUAD;
	}

	public boolean isOutlined() {

		return this == OUTLINED_POLYGON || this == OUTLINED_QUAD;
	}

}
