/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.ext.multieditor;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * INestableEMFEditor
 * There should really be more documentation here.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Sep 22, 2009
 */
public interface INestableEMFEditor extends INestableEditor {

	/**
	 * Sets the given resource set, returns true if this set is actually
	 * used for loading or saving the models.
	 * @param resourceSet
	 * @return
	 */
	boolean setResourceSet(ResourceSet resourceSet);
	
}
