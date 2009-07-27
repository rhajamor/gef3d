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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw3d.graphics3d.Graphics3DException;

/**
 * Represents a buffer of method calls. On request, this buffer may be executed.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class MethodCallBuffer {

	/**
	 * The entries of the buffer, each one corresponds to one method call.
	 */
	private final List<MethodCallBufferEntry> m_entries;

	/**
	 * Constructs a new method call buffer.
	 */
	public MethodCallBuffer() {
		m_entries = new ArrayList<MethodCallBufferEntry>();
	}

	/**
	 * Adds a new method call to the buffer.
	 * 
	 * @param i_instance The instance on which is the method is called.
	 * @param i_method The called method
	 * @param i_args The arguments of the method call.
	 */
	public void addMethodCall(Object i_instance, Method i_method,
			Object[] i_args) {

		m_entries.add(new MethodCallBufferEntry(i_instance, i_method, i_args));

	}

	/**
	 * Adds a new method call to the buffer
	 * 
	 * @param i_instance The instance on which the method is called.
	 * @param i_strMethodName The name of the called method.
	 * @param i_args The arguments of the method call.
	 */
	public void addMethodCall(Object i_instance, String i_strMethodName,
			Object[] i_args) {
		try {

			Class[] parameterClasses = new Class[i_args.length];
			int i = 0;
			for (Object arg : i_args) {
				parameterClasses[i] = arg.getClass();

				// As convention, primitive classes are used when necessary.
				if (parameterClasses[i] == Float.class) {
					parameterClasses[i] = float.class;
				}
				if (parameterClasses[i] == Integer.class) {
					parameterClasses[i] = int.class;
				}
				i++;
			}

			addMethodCall(i_instance, i_instance.getClass().getMethod(
					i_strMethodName, parameterClasses), i_args);
		} catch (SecurityException ex) {
			throw new Graphics3DException(ex);
		} catch (NoSuchMethodException ex) {
			throw new Graphics3DException(ex);
		}
	}

	/**
	 * Executes the method call buffer, each method is called in the order their
	 * were added (FIFO).
	 */
	public void executeBuffer() {
		for (MethodCallBufferEntry entry : m_entries) {
			entry.invoke();
		}
	}

}
