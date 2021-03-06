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
package org.eclipse.gef3d.examples.uml2.clazz.edit.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.util.Log;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.diagram.clazz.edit.parts.PackageEditPart;
import org.eclipse.uml2.diagram.clazz.edit.parts.UMLEditPartFactory;
import org.eclipse.uml2.diagram.clazz.part.UMLVisualIDRegistry;

/**
 * UMLEditPartFactory3D There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 7, 2009
 */
public class UMLEditPartFactory3D extends UMLEditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		
		
		
		if (model instanceof View) {
			View view = (View) model;
			switch (UMLVisualIDRegistry.getVisualID(view)) {

			case PackageEditPart.VISUAL_ID:
				return new PackageEditPart3D(view);
			}
		}
		return super.createEditPart(context, model);
	}

}
