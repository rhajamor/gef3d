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

import org.eclipse.gef.EditPart;
import org.eclipse.gef3d.ext.reverselookup.EditPartNotationAdapter;
import org.eclipse.gmf.runtime.notation.View;

/**
 * GMFEditPartNotationAdapter There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 23, 2009
 */
public class GMFEditPartNotationAdapter extends EditPartNotationAdapter {

	/**
	 * The singleton instance, object has no state
	 */
	public final static GMFEditPartNotationAdapter INSTANCE = new GMFEditPartNotationAdapter();
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ext.reverselookup.INotationAdapter#getDomainObject(java.lang.Object)
	 */
	public Object getDomainObject(EditPart notationElement) {
		return ((View) notationElement.getModel()).getElement();
	}

	/**
	 * Returns 2, since the length of the navigation path is 2
	 * (getModel().getElement()).
	 * 
	 * @see org.eclipse.gef3d.ext.reverselookup.ILookupHelper#handlesElement(java.lang.Object)
	 */
	public int handlesElement(Object i_modelElement) {
		if (i_modelElement instanceof EditPart) {
			if (((EditPart) i_modelElement).getModel() instanceof View) {
				return 2;
			}
		}
		return 0;
	}

}
