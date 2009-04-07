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

package org.eclipse.draw3d.graphics3d.x3d.model;

/**
 * An attribute of a X3DNode. Contains a key and a value, both semantic-free as
 * String values.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DAttribute implements Cloneable {

	/**
	 * The attribute's keys.
	 */
	private final String m_strKey;

	/**
	 * The attribute's value.
	 */
	private final String m_strValue;

	/**
	 * Constructs a new X3DAttribute.
	 * 
	 * @param i_strKey
	 * @param i_strValue
	 */
	public X3DAttribute(String i_strKey, String i_strValue) {

		m_strKey = i_strKey;
		m_strValue = i_strValue;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return this.m_strKey;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.m_strValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {

		X3DAttribute other = new X3DAttribute(this.m_strKey, this.m_strValue);

		return other;

	}
}