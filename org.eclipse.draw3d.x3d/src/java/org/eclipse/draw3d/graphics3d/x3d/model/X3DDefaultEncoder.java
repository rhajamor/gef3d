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
import java.util.Formatter;
import java.util.Iterator;

import org.eclipse.draw3d.geometry.IVector2f;
import org.eclipse.draw3d.geometry.IVector3f;

/**
 * Default X3D encoder.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 09.06.2009
 */
public class X3DDefaultEncoder implements X3DEncoder {

	private static final String FMT_2D_COORD = "%f %f";

	private static final String FMT_3D_COORD = "%f %f %f";

	private static final String FMT_FLOAT = "%f";

	private static final String FMT_INT = "%d";

	/**
	 * The single instance.
	 */
	public static final X3DEncoder INSTANCE = new X3DDefaultEncoder();

	private X3DDefaultEncoder() {

		// private constructor to enforce singleton pattern
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.model.X3DEncoder#encode(java.util.Collection)
	 */
	public String encode(Collection<?> i_values) {

		StringBuilder b = new StringBuilder();
		Formatter formatter = new Formatter(b);

		for (Iterator<?> iter = i_values.iterator(); iter.hasNext();) {
			Object value = iter.next();
			encode(value, formatter);

			if (iter.hasNext())
				b.append(", ");
		}

		return toString(formatter);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.model.X3DEncoder#encode(float)
	 */
	public String encode(float i_value) {

		Formatter formatter = new Formatter();
		encode(i_value, formatter);

		return toString(formatter);
	}

	private void encode(Float i_value, Formatter i_formatter) {

		i_formatter.format(FMT_FLOAT, i_value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.model.X3DEncoder#encode(int)
	 */
	public String encode(int i_value) {

		Formatter formatter = new Formatter();
		encode(i_value, formatter);

		return toString(formatter);
	}

	private void encode(Integer i_value, Formatter i_formatter) {

		i_formatter.format(FMT_INT, i_value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.model.X3DEncoder#encode(org.eclipse.draw3d.geometry.IVector2f)
	 */
	public String encode(IVector2f i_value) {

		Formatter formatter = new Formatter();
		encode(i_value, formatter);

		return toString(formatter);
	}

	private void encode(IVector2f i_value, Formatter i_formatter) {

		i_formatter.format(FMT_2D_COORD, i_value.getX(), i_value.getY());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.model.X3DEncoder#encode(org.eclipse.draw3d.geometry.IVector3f)
	 */
	public String encode(IVector3f i_value) {

		Formatter formatter = new Formatter();
		encode(i_value, formatter);

		return toString(formatter);
	}

	private void encode(IVector3f i_value, Formatter i_formatter) {

		i_formatter.format(FMT_3D_COORD, i_value.getX(), i_value.getY(),
				i_value.getZ());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.model.X3DEncoder#encode(java.lang.Object[])
	 */
	public String encode(Object... i_values) {

		StringBuilder b = new StringBuilder();
		Formatter formatter = new Formatter(b);

		for (int i = 0; i < i_values.length; i++) {
			Object value = i_values[i];
			encode(value, formatter);

			if (i < i_values.length - 1)
				b.append(", ");
		}

		return toString(formatter);
	}

	private void encode(Object value, Formatter formatter) {

		if (value instanceof Integer)
			encode((Integer) value, formatter);
		else if (value instanceof Float)
			encode((Float) value, formatter);
		else if (value instanceof IVector2f)
			encode((IVector2f) value, formatter);
		else if (value instanceof IVector3f)
			encode((IVector3f) value, formatter);
		else
			throw new IllegalArgumentException("unknown value type: "
					+ value.getClass().getName());
	}

	private String toString(Formatter i_formatter) {

		i_formatter.flush();
		return i_formatter.out().toString();
	}
}
