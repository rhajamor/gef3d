/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d.picking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.RenderMode;


/**
 * Manages ignored figures and colored figures for the color picker.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 15.05.2008
 */
public class FigureManager implements ColorProvider {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(FigureManager.class
			.getName());

	private static final int MIN_INDEX = 500;

	/**
	 * Contains the figures, indexed by their color value minus
	 * {@link #MIN_COLOR_INDEX}.
	 */
	private List<IFigure3D> m_figures = new ArrayList<IFigure3D>();

	private Set<IFigure3D> m_ignoredFigures = new HashSet<IFigure3D>();

	private Set<Class<?>> m_ignoredTypes = new HashSet<Class<?>>();

	/**
	 * The next color to be returned when
	 * {@link #nextColorIndex(RenderMode, IFigure3D)} is called.
	 */
	private int m_nextColor = MIN_INDEX;

	/**
	 * Creates and initializes a figure manager.
	 */
	public FigureManager() {

		// nothing to initialize
	}

	/**
	 * Creates a new figure manager with the given figures and ignored figures
	 * and types.
	 * 
	 * @param i_figures the figures, indexed by their color
	 * @param i_ignoredFigures the ignored figures
	 * @param i_ignoredTypes the ignored types
	 */
	private FigureManager(List<IFigure3D> i_figures,
			Set<IFigure3D> i_ignoredFigures, Set<Class<?>> i_ignoredTypes) {

		if (i_figures == null)
			throw new NullPointerException("i_figures must not be null");

		if (i_ignoredFigures == null)
			throw new NullPointerException("i_ignoredFigures must not be null");

		if (i_ignoredTypes == null)
			throw new NullPointerException("i_ignoredTypes must not be null");

		m_figures.addAll(i_figures);
		m_ignoredFigures.addAll(i_ignoredFigures);
		m_ignoredTypes.addAll(i_ignoredTypes);
	}

	/**
	 * Clears all colored figures and resets the color index.
	 */
	public void clear() {

		m_figures.clear();
		m_nextColor = MIN_INDEX - 1;
	}

	/**
	 * Clears the ignored figures and types.
	 * 
	 * @return <code>true</code> if there were ignored figures or types and
	 *         <code>false</code> otherwise
	 */
	public boolean clearIgnored() {

		boolean modified = !m_ignoredFigures.isEmpty()
				|| !m_ignoredTypes.isEmpty();

		m_ignoredFigures.clear();
		m_ignoredTypes.clear();

		return modified;
	}

	/**
	 * Creates a snapshot of this figure manager.
	 * 
	 * @return a snapshot of this figure manager
	 */
	public FigureManager createSnapshot() {

		return new FigureManager(m_figures, m_ignoredFigures, m_ignoredTypes);
	}

	/**
	 * Returns the color index of a given figure.
	 * 
	 * @param i_figure the figure
	 * @return the color index for the given figure or
	 *         {@link ColorProvider#IGNORE} if the given figure is ignored
	 */
	public int getColor(IFigure3D i_figure) {

		if (isIgnored(i_figure))
			return ColorProvider.IGNORE;

		m_figures.add(i_figure);
		return ++m_nextColor;
	}

	/**
	 * Returns the figure with the given color.
	 * 
	 * @param i_color the color of the figure to return
	 * @return the figure with the given color or <code>null</code> if there
	 *         is no such figure
	 */
	public IFigure3D getFigure(int i_color) {

		if (i_color == 0xFFFFFF)
			return null;

		int index = i_color - MIN_INDEX;
		if (index < 0 || index >= m_figures.size())
			return null;

		return m_figures.get(index);
	}

	/**
	 * Ignore the given figure.
	 * 
	 * @param i_figure the figure to ignore
	 * @return <code>true</code> if the given figure was not ignored prior to
	 *         calling this method and <code>false</code> otherwise
	 */
	public boolean ignoreFigure(IFigure3D i_figure) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		return m_ignoredFigures.add(i_figure);
	}

	/**
	 * Ignore the given type.
	 * 
	 * @param i_type the type to ignore
	 * @return <code>true</code> if the given type was not ignored prior to
	 *         calling this method and <code>false</code> otherwise
	 */
	public boolean ignoreType(Class<?> i_type) {

		if (i_type == null)
			throw new NullPointerException("i_type must not be null");

		return m_ignoredTypes.add(i_type);
	}

	/**
	 * Indicates whether the given figure is currently ignored.
	 * 
	 * @param i_figure the figure to check
	 * @return <code>true</code> if the given figure is ignored and
	 *         <code>false</code> otherwise
	 */
	public boolean isIgnored(IFigure3D i_figure) {

		if (i_figure == null)
			throw new NullPointerException("i_figure must not be null");

		if (m_ignoredFigures.isEmpty() && m_ignoredTypes.isEmpty())
			return false;

		if (m_ignoredFigures.contains(i_figure))
			return true;

		for (Class<?> type : m_ignoredTypes)
			if (type.isInstance(i_figure))
				return true;

		return false;
	}
}
