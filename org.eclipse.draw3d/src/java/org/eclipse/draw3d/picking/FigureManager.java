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
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.TreeSearch;
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
    private static final Logger log = Logger.getLogger(FigureManager.class.getName());

    private static final int MIN_INDEX = 500;

    /**
     * Contains the figures, indexed by their color value minus
     * {@link #MIN_COLOR_INDEX}.
     */
    private List<IFigure3D> m_figures = new ArrayList<IFigure3D>();

    /**
     * The next color to be returned when
     * {@link #nextColorIndex(RenderMode, IFigure3D)} is called.
     */
    private int m_nextColor = MIN_INDEX;

    private IFigure3D m_rootFigure;

    /**
     * All figures that are rejected by or pruned from this search are ignored.
     */
    private TreeSearch m_search;

    /**
     * Creates and initializes a figure manager. If the given tree search is not
     * <code>null</code>, all figures which are rejected by or pruned from the
     * given search will be ignored by this manager, e.g.
     * {@link #getColor(IFigure3D)} will return {@link ColorProvider#IGNORE} for
     * such figures.
     * 
     * @param i_search
     *            the tree search
     */
    public FigureManager(TreeSearch i_search) {

        m_search = i_search;
    }

    /**
     * Clears all colored figures and resets the color index.
     */
    public void clear() {

        m_figures.clear();
        m_nextColor = MIN_INDEX - 1;
    }

    /**
     * Returns the color index of a given figure.
     * 
     * @param i_figure
     *            the figure
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
     * @param i_color
     *            the color of the figure to return
     * @return the figure with the given color or <code>null</code> if there is
     *         no such figure
     */
    public IFigure3D getFigure(int i_color) {

        if (i_color == 0xFFFFFF)
            return m_rootFigure;

        int index = i_color - MIN_INDEX;
        if (index < 0 || index >= m_figures.size())
            return m_rootFigure;

        return m_figures.get(index);
    }

    private boolean isIgnored(IFigure3D i_figure) {

        if (m_search == null)
            return false;

        return !m_search.accept(i_figure) || m_search.prune(i_figure);
    }
}
