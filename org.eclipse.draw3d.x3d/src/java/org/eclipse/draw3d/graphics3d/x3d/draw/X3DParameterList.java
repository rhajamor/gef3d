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

package org.eclipse.draw3d.graphics3d.x3d.draw;

import java.util.List;

/**
 * A generic parameter list.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DParameterList {

	/**
	 * The list with the parameters.
	 */
	private final List<Object> m_parameters;

	/**
	 * Constructs a parameter list.
	 * 
	 * @param i_parameters The parameters.
	 */
	public X3DParameterList(List<Object> i_parameters) {

		if (i_parameters == null)
			throw new NullPointerException(
					"Parameter list may be empty but must not be null.");

		m_parameters = i_parameters;
	}

	/**
	 * Adds a parameter to the list.
	 * 
	 * @param i_parameter The parameter to add.
	 */
	public void addParameter(Object i_parameter) {
		m_parameters.add(i_parameter);
	}

	/**
	 * Gets the list with all parameters.
	 * 
	 * @return The list with all parameters.
	 */
	public List<Object> getParameters() {
		return m_parameters;
	}
}
