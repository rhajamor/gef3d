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

import org.eclipse.draw2d.TreeSearch;

/**
 * An interface that is used to manage the GEF3D pickers.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 26.07.2009
 */
public interface PickerManager {

    /**
     * Creates a subset picker with the given key and search. Only figues
     * accepted and not pruned by the given search can be picked in the newly
     * created picker. All other figures are ignored.
     * 
     * @param i_key
     *            the key of the new picker
     * @param i_search
     *            the tree search that identifies the figures that can be picked
     * @return the newly created picker
     * 
     * @throws NullPointerException
     *             if any of the given arguments is <code>null</code>
     */
    public Picker createSubsetPicker(Object i_key, TreeSearch i_search);

    /**
     * Deletes the subset picker with the given key. If no picker with the given
     * key exists, it is ignored.
     * 
     * @param i_key
     *            the key of the picker to delete
     * 
     * @throws NullPointerException
     *             if the given key is <code>null</code>
     */
    public void deleteSubsetPicker(Object i_key);

    /**
     * Returns the main picker that is used to pick all figures that are not
     * assigned to a subset picker.
     * 
     * @return the main picker
     */
    public Picker getMainPicker();

    /**
     * Returns the subset picker with the given key.
     * 
     * @param i_key
     *            the key of the picker to return
     * @return the picker with the given key or <code>null</code> if no such
     *         picker exists
     * 
     * @throws NullPointerException
     *             if the given key is <code>null</code>
     */
    public Picker getSubsetPicker(Object i_key);

}