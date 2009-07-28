/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/

package org.eclipse.draw3d;

import java.util.logging.Logger;

import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw3d.picking.CombinationPicker;
import org.eclipse.draw3d.picking.Picker;
import org.eclipse.draw3d.picking.PickerManager;

/**
 * Does the actual picking for 3D figures by using a color picker, see
 * {@link ColorPicker}.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 13.12.2007
 */
public class PickingUpdateManager3D extends DeferredUpdateManager3D implements
        PickerManager {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PickingUpdateManager3D.class.getName());

    private CombinationPicker m_picker;

    /**
     * Indicates whether picking is enabled.
     */
    boolean pickingEnabled = true;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#createSubsetPicker(java.lang.Object,
     *      org.eclipse.draw2d.TreeSearch)
     */
    public Picker createSubsetPicker(Object i_key, TreeSearch i_search) {

        return getPicker().createSubsetPicker(i_key, i_search);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#deleteSubsetPicker(java.lang.Object)
     */
    public void deleteSubsetPicker(Object i_key) {

        getPicker().deleteSubsetPicker(i_key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.UpdateManager#dispose()
     */
    @Override
    public void dispose() {

        if (m_picker != null)
            m_picker.dispose();

        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#getMainPicker()
     */
    public Picker getMainPicker() {

        return getPicker().getMainPicker();
    }

    /**
     * Returns the combinated picker, which is lazily created in this method.
     * This picker will pick all figures.
     * 
     * @return the combinated picker
     */
    public CombinationPicker getPicker() {

        if (m_picker == null)
            m_picker = new CombinationPicker(root3D, canvas);

        return m_picker;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.picking.PickerManager#getSubsetPicker(java.lang.Object)
     */
    public Picker getSubsetPicker(Object i_key) {

        return getPicker().getSubsetPicker(i_key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw3d.DeferredUpdateManager3D#repairDamage()
     */
    @Override
    protected void repairDamage() {

        pickingEnabled = false;
        super.repairDamage();
        pickingEnabled = true;

        // TODO: this leads to the picking buffer being re-rendered when a
        // feedback figure is moved, which is unneccessary
        if (m_picker != null)
            m_picker.invalidate();
    }
}
