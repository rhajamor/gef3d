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
package org.eclipse.draw3d.graphics.optimizer.classification;

import org.eclipse.draw3d.graphics.optimizer.primitive.LinePrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.PolygonPrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.PolylinePrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.QuadPrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.RenderRule;

/**
 * TextPrimitiveClass There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 27.12.2009
 */
public class PrimitiveClassImpl implements PrimitiveClass {

	private Class<? extends Primitive> m_primitiveClass;

	private RenderRule m_renderRule;

	public PrimitiveClassImpl(Class<? extends Primitive> i_primitiveClass,
			RenderRule i_renderRule) {

		m_primitiveClass = i_primitiveClass;
		m_renderRule = i_renderRule;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#contains(org.eclipse.draw3d.graphics.optimizer.primitive.Primitive)
	 */
	public boolean contains(Primitive i_primitive) {

		if (!m_primitiveClass.isAssignableFrom(i_primitive.getClass()))
			return false;

		return m_renderRule.equals(i_primitive.getRenderRule());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#getRenderRule()
	 */
	public RenderRule getRenderRule() {

		return m_renderRule;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isGradient()
	 */
	public boolean isGradient() {

		return m_renderRule.isGradient();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isImage()
	 */
	public boolean isImage() {

		return m_renderRule.isImage();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isLine()
	 */
	public boolean isLine() {

		return LinePrimitive.class.isAssignableFrom(m_primitiveClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isOutline()
	 */
	public boolean isOutline() {

		return m_renderRule.isOutline();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isPolygon()
	 */
	public boolean isPolygon() {

		return PolygonPrimitive.class.isAssignableFrom(m_primitiveClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isPolyline()
	 */
	public boolean isPolyline() {

		return PolylinePrimitive.class.isAssignableFrom(m_primitiveClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isQuad()
	 */
	public boolean isQuad() {

		return QuadPrimitive.class.isAssignableFrom(m_primitiveClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isSolid()
	 */
	public boolean isSolid() {

		return m_renderRule.isSolid();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass#isText()
	 */
	public boolean isText() {

		return m_renderRule.isText();
	}

	@Override
	public String toString() {
		return "PrimitiveClassImpl [m_primitiveClass=" + m_primitiveClass
			+ ", m_renderRule=" + m_renderRule + "]";
	}
}
