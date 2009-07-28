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
package org.eclipse.draw3d;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;

/**
 * An implementation of {@link TreeSearch} that only accepts figures that are
 * instances of a given set of types. At the same time, rejected figures are
 * pruned from this search.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.07.2009
 */
public class TypeSearch implements TreeSearch {

    private Collection<Class<?>> m_acceptedTypes = new HashSet<Class<?>>();

    /**
     * Creates a new search that will accept all figures which are of the same
     * type or a subtype of any of the types in the given collection.
     * 
     * @param i_acceptedTypes
     *            a collection of types which should be accepted
     * 
     * @throws NullPointerException
     *             if the given collection is <code>null</code>
     */
    public TypeSearch(Collection<Class<?>> i_acceptedTypes) {

        if (i_acceptedTypes == null)
            throw new NullPointerException("i_acceptedTypes must not be null");

        m_acceptedTypes.addAll(i_acceptedTypes);
    }

    /**
     * Creates a new search that will accept all figures which are of the same
     * type or a subtype of any of the given types.
     * 
     * @param i_acceptedTypes
     *            the types to accept
     */
    public TypeSearch(Class<?>... i_acceptedTypes) {

        for (Class<?> aype : i_acceptedTypes)
            m_acceptedTypes.add(aype);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.TreeSearch#accept(org.eclipse.draw2d.IFigure)
     */
    public boolean accept(IFigure i_figure) {

        for (Class<?> type : m_acceptedTypes)
            if (type.isAssignableFrom(i_figure.getClass()))
                return true;

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.TreeSearch#prune(org.eclipse.draw2d.IFigure)
     */
    public boolean prune(IFigure i_figure) {

        return !accept(i_figure);
    }
}
