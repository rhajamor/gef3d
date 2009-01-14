/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.preferences;

import org.eclipse.core.runtime.Preferences;

/**
 * Provides a preference store.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 06.03.2008
 */
public interface PreferenceProvider {

	/**
	 * Returns the current preferences to be used.
	 * 
	 * @return the preferences
	 */
	public Preferences getPreferences();
}
