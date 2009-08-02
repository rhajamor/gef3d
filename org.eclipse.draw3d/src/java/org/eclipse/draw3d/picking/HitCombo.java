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
package org.eclipse.draw3d.picking;

/**
 * A combination of a figure and a surface hit. Also contains some logic to
 * compare hits with each other.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 02.08.2009
 */
public class HitCombo {

	private HitImpl m_figureHit;

	private HitImpl m_surfaceHit;

	/**
	 * Creates a new combination of the given two hits.
	 * 
	 * @param i_figureHit the figure hit
	 * @param i_surfaceHit the surface hit
	 */
	public HitCombo(HitImpl i_figureHit, HitImpl i_surfaceHit) {

		m_figureHit = i_figureHit;
		m_surfaceHit = i_surfaceHit;
	}

	/**
	 * Returns the hit for the current figure search.
	 * 
	 * @return the hit for the current figure search
	 */
	public HitImpl getFigureHit() {
		return m_figureHit;
	}

	/**
	 * Returns the hit for the current surface search.
	 * 
	 * @return the hit for the current surface search
	 */
	public HitImpl getSurfaceHit() {

		return m_surfaceHit;
	}

	/**
	 * Sets the hit for the current figure search.
	 * 
	 * @param i_figureHit this figure hit
	 */
	public void setFigureHit(HitImpl i_figureHit) {

		m_figureHit = i_figureHit;
	}

	/**
	 * Sets the hit for the current surface search.
	 * 
	 * @param i_surfaceHit the surface hit
	 */
	public void setSurfaceHit(HitImpl i_surfaceHit) {

		m_surfaceHit = i_surfaceHit;
	}
}
