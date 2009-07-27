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
package org.eclipse.draw3d.graphics3d.x3d.model;

import java.util.Collection;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * Encodes X3D attribute values.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.06.2009
 */
public interface X3DEncoder {

	/**
	 * Encodes the given collection.
	 * 
	 * @param i_values
	 *            the values to encode
	 * @return the encoded string
	 */
	public String encode(Collection<?> i_values);

	/**
	 * Encodes the given float value.
	 * 
	 * @param i_value
	 *            the value to encode
	 * @return the encoded string
	 */
	public String encode(float i_value);

	/**
	 * Encodes the given integer value.
	 * 
	 * @param i_value
	 *            the value to encode
	 * @return the encoded string
	 */
	public String encode(int i_value);

	/**
	 * Encodes the given 2D coordinate.
	 * 
	 * @param i_value
	 *            the coordinate to encode
	 * @return the encoded string
	 */
	public String encode(IVector2f i_value);

	/**
	 * Encodes the given 3D coordinate.
	 * 
	 * @param i_value
	 *            the coordinate to encode
	 * @return the encoded string
	 */
	public String encode(IVector3f i_value);

	/**
	 * Encodes the given values.
	 * 
	 * @param i_values
	 *            the values to encode
	 * @return the encoded string
	 */
	public String encode(Object... i_values);
}
