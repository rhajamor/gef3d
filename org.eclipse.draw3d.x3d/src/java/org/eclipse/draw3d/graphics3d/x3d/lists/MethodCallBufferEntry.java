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

package org.eclipse.draw3d.graphics3d.x3d.lists;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An instance of this class represents one entry in the method call buffer.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class MethodCallBufferEntry {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(MethodCallBufferEntry.class.getName());

	/**
	 * The instance on which is method was called.
	 */
	private final Object m_instance;

	/**
	 * The called method.
	 */
	private final Method m_method;

	/**
	 * The call's arguments.
	 */
	private final Object[] m_args;

	/**
	 * Constructs a new method call buffer entry.
	 * 
	 * @param i_instance The instance on which the call was invoked.
	 * @param i_method The method which was called.
	 * @param i_args The call's arguments.
	 */
	public MethodCallBufferEntry(Object i_instance, Method i_method,
			Object[] i_args) {

		if (i_instance == null || i_method == null || i_args == null)
			throw new NullPointerException("No argument shall be null.");

		m_instance = i_instance;
		m_method = i_method;
		m_args = i_args;

		if (!validate()) {
			if (log.isLoggable(Level.INFO)) {
				log
						.info("Object, Method, Object[] - Method on specified instance not found. - i_instance="
								+ i_instance + ", i_method=" + i_method);
			}

			throw new IllegalArgumentException(
					"Method on specified instance not found. - i_instance="
							+ i_instance + ", i_method=" + i_method);
		}
	}

	/**
	 * Invokes the method call.
	 * 
	 * @return The result of the invocation.
	 */
	public Object invoke() {

		try {
			return m_method.invoke(m_instance, m_args);
		} catch (IllegalArgumentException ex) {
			// Never here, call should be validated.
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			// Never here, call should be validated.
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			// Never here, call should be validated.
			ex.printStackTrace();
		}

		// Never here, call should be validated.
		return null;
	}

	/**
	 * Validates this entry in a way that it should be executable.
	 * 
	 * @return True, if validation was successful. False otherwise.
	 */
	public boolean validate() {

		boolean bMethodFoundAndEquals = false;

		Class c = m_instance.getClass();
		Method[] methods = c.getMethods();

		for (Method method : methods) {
			if (method.equals(m_method)) {
				bMethodFoundAndEquals = true;
			}
		}

		return bMethodFoundAndEquals;
	}

}
