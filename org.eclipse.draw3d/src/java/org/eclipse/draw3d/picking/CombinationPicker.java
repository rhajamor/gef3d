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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.DummyGraphics;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * A picker that delegates the actual picking to a main picker and a number of
 * subset pickers which can be created and deleted using the
 * {@link PickerManager} interface.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.07.2009
 */
public class CombinationPicker implements Picker, PickerManager {

    /**
     * An implementation of tree search that delegates to a number of sub
     * searches and inverts the result. The delegates are combined using the
     * boolean OR operator so that a figure is accepted by this search if it is
     * rejected by all of the delegates and a figure is pruned from this search
     * if it is pruned from none of the delegates.
     * 
     * @author Kristian Duske
     * @version $Revision$
     * @since 26.07.2009
     */
    private class InvertedCombinationSearch implements TreeSearch {

        private Collection<TreeSearch> m_subSearches = new HashSet<TreeSearch>();

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.draw2d.TreeSearch#accept(org.eclipse.draw2d.IFigure)
         */
        public boolean accept(IFigure i_figure) {

            for (TreeSearch search : m_subSearches)
                if (search.accept(i_figure))
                    return false;

            return true;
        }

        /**
         * Adds the given search to the delegates of this search. If the given
         * search is already a delegate of this search, it is ignored.
         * 
         * @param i_search
         *            the search to add
         * 
         * @throws NullPointerException
         *             if the given search is <code>null</code>
         */
        public void addSearch(TreeSearch i_search) {

            if (i_search == null)
                throw new NullPointerException("i_search must not be null");

            m_subSearches.add(i_search);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.draw2d.TreeSearch#prune(org.eclipse.draw2d.IFigure)
         */
        public boolean prune(IFigure i_figure) {

            for (TreeSearch search : m_subSearches)
                if (search.prune(i_figure))
                    return false;

            return true;
        }

        /**
         * Removes the given search delegate from this search. If the given
         * search is not a delegate of this search, it is ignored.
         * 
         * @param i_search
         *            the search to remove
         * 
         * @throws NullPointerException
         *             if the given search is <code>null</code>
         */
        public void removeSearch(TreeSearch i_search) {

            if (i_search == null)
                throw new NullPointerException("i_search must not be null");

            m_subSearches.remove(i_search);
        }
    }

    private GLCanvas m_canvas;

    private boolean m_disposed = false;

    private FigurePicker m_mainPicker;

    private InvertedCombinationSearch m_mainSearch;

    private IFigure3D m_rootFigure;

    private Map<Object, FigurePicker> m_subsetPickers = new HashMap<Object, FigurePicker>();

    private Map<Object, TreeSearch> m_subsetSearches = new HashMap<Object, TreeSearch>();

    /**
     * Creates a new picker for the given root figure and canvas.
     * 
     * @param i_rootFigure
     *            the root figure
     * @param i_canvas
     *            the canvas
     * 
     * @throws NullPointerException
     *             if any of the given arguments is <code>null</code>
     */
    public CombinationPicker(IFigure3D i_rootFigure, GLCanvas i_canvas) {

        if (i_rootFigure == null)
            throw new NullPointerException("i_rootFigure must not be null");

        if (i_canvas == null)
            throw new NullPointerException("i_canvas must not be null");

        m_rootFigure = i_rootFigure;
        m_canvas = i_canvas;

        m_mainSearch = new InvertedCombinationSearch();
        m_mainPicker = new FigurePicker(m_rootFigure, m_canvas, m_mainSearch);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#createSubsetPicker(java.lang.Object,
     *      org.eclipse.draw2d.TreeSearch)
     */
    public Picker createSubsetPicker(Object i_key, TreeSearch i_search) {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        if (i_key == null)
            throw new NullPointerException("i_key must not be null");

        if (i_search == null)
            throw new NullPointerException("i_search must not be null");

        if (m_subsetPickers.containsKey(i_key))
            throw new IllegalArgumentException("a picker with the key " + i_key
                    + " already exists");

        m_mainSearch.addSearch(i_search);
        m_subsetSearches.put(i_key, i_search);

        FigurePicker picker = new FigurePicker(m_rootFigure, m_canvas, i_search);
        m_subsetPickers.put(i_key, picker);

        m_mainPicker.invalidate();
        picker.invalidate();

        return picker;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#deleteSubsetPicker(java.lang.Object)
     */
    public void deleteSubsetPicker(Object i_key) {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        if (i_key == null)
            throw new NullPointerException("i_key must not be null");

        TreeSearch search = m_subsetSearches.get(i_key);
        if (search != null)
            m_mainSearch.removeSearch(search);

        FigurePicker picker = m_subsetPickers.remove(i_key);
        picker.dispose();

        m_mainPicker.invalidate();
    }

    /**
     * Disposes of all resources associated with this picker.
     */
    public void dispose() {

        m_mainPicker.dispose();
        for (FigurePicker subsetPicker : m_subsetPickers.values())
            subsetPicker.dispose();

        m_subsetPickers.clear();
        m_subsetSearches.clear();

        m_disposed = true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#getCurrentSurface()
     */
    public ISurface getCurrentSurface() {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        return m_mainPicker.getCurrentSurface();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#getDepth(int, int)
     */
    public float getDepth(int i_mx, int i_my) {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        float depth = m_mainPicker.getDepth(i_mx, i_my);
        for (Picker subsetPicker : m_subsetPickers.values()) {
            float subDepth = subsetPicker.getDepth(i_mx, i_my);
            if (subDepth < depth)
                depth = subDepth;
        }

        return depth;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#getFigure3D(int, int)
     */
    public IFigure3D getFigure3D(int i_mx, int i_my) {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        IFigure3D figure = m_mainPicker.getFigure3D(i_mx, i_my);
        float depth = m_mainPicker.getDepth(i_mx, i_my);

        for (Picker subsetPicker : m_subsetPickers.values())
            if (subsetPicker.getDepth(i_mx, i_my) < depth)
                figure = subsetPicker.getFigure3D(i_mx, i_my);

        return figure;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#getSubsetPicker(java.lang.Object)
     */
    public Picker getSubsetPicker(Object i_key) {

        if (i_key == null)
            throw new NullPointerException("i_key must not be null");

        return m_subsetPickers.get(i_key);
    }

    /**
     * Informs this figure picker that it needs to validate itself.
     */
    public void invalidate() {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        m_mainPicker.invalidate();
        for (FigurePicker subsetPicker : m_subsetPickers.values())
            subsetPicker.invalidate();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.Picker#updateCurrentSurface(int, int)
     */
    public void updateCurrentSurface(int i_mx, int i_my) {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        m_mainPicker.updateCurrentSurface(i_mx, i_my);
        for (FigurePicker subsetPicker : m_subsetPickers.values())
            subsetPicker.updateCurrentSurface(i_mx, i_my);
    }

    /**
     * Triggers a validation of this figure picker.
     */
    public void validate() {

        if (m_disposed)
            throw new IllegalStateException("picker is disposed");

        boolean repairBackbuffer = m_mainPicker.validate();
        for (FigurePicker subsetPicker : m_subsetPickers.values())
            repairBackbuffer |= subsetPicker.validate();

        if (repairBackbuffer) {
            m_rootFigure.paint(new DummyGraphics());
            m_canvas.swapBuffers();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#getMainPicker()
     */
    public Picker getMainPicker() {

        return m_mainPicker;
    }
}
