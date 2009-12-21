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

	SOLID_POLYGON, SOLID_QUAD, LINE, OUTLINE_POLYGON, OUTLINE_QUAD, POLYLINE, IMAGE, GRADIENT_QUAD;

	public boolean isImage() {

		return this == IMAGE;
	}

	public boolean isFilled() {

		return this == SOLID_POLYGON || this == SOLID_QUAD;
	}

	public boolean isOutlined() {

		return this == OUTLINE_POLYGON || this == OUTLINE_QUAD;
	}

	public boolean isQuad() {

		return this == OUTLINE_QUAD || this == SOLID_QUAD;
	}

	public boolean isPolygon() {

		return this == OUTLINE_POLYGON || this == SOLID_POLYGON;
	}

	public boolean isPolyline() {

		return this == POLYLINE;
	}

	public boolean isLine() {

		return this == LINE;
	}

	public boolean isGradientQuad() {

		return this == GRADIENT_QUAD;
	}

}
