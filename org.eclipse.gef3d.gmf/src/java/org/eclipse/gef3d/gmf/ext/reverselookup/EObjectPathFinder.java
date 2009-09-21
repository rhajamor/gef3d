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
package org.eclipse.gef3d.gmf.ext.reverselookup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef3d.ext.reverselookup.IModelPathFinder;

/**
 * EObjectPathFinder There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 23, 2009
 */
public class EObjectPathFinder implements IModelPathFinder {

	/**
	 * The singleton instance, object has no state
	 */
	public final static EObjectPathFinder INSTANCE = new EObjectPathFinder();
	


	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ext.reverselookup.IModelPathFinder#findPath(java.lang.Object)
	 */
	public List findPath(Object i_modelElement) {
		EObject eobj = (EObject) i_modelElement;

		List<EObject> path = new ArrayList<EObject>();
		EObject container;

		while (eobj != null) {
			container = eobj.eContainer(); // resolves eobj;
			path.add(eobj); // do not insert proxies
			eobj = container;
		}
		return path;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ext.reverselookup.ILookupHelper#handlesElement(java.lang.Object)
	 */
	public int handlesElement(Object i_modelElement) {
		if (i_modelElement instanceof EObject)
			return 1;
		return 0;
	}

}
