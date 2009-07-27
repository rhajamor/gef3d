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

package org.eclipse.gef3d.ext.multieditor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.FreeformLayer3D;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.gef3d.ext.intermodel.IInterModelDiagram;
import org.eclipse.draw3d.geometry.Vector3fImpl;


/**
 * Figure of a {@link MultiEditorModelContainerEditPart}, this is a
 * {@link FreeformLayer3D} for updating connections.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 2, 2008
 */
public class MultiEditorModelContainerFigure extends FreeformLayer3D {
	float dz = 0;

	/**
	 * 
	 */
	public MultiEditorModelContainerFigure() {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#add(org.eclipse.draw2d.IFigure,
	 *      java.lang.Object, int)
	 */
	@Override
	public void add(IFigure i_figure, Object i_constraint, int i_index) {
		super.add(i_figure, i_constraint, i_index);

		if (i_figure instanceof IFigure3D) {
			IFigure3D fig3D = (Figure3D) i_figure;

			//TODO replace with layout manager
			if (!(fig3D instanceof IInterModelDiagram)) {
				Vector3fImpl vec = new Vector3fImpl(fig3D.getLocation3D());
				vec.z += dz;
				fig3D.setLocation3D(vec);
				
				Vector3fImpl v = new Vector3fImpl();
//				v.z = (float) (Math.PI/6 *dz/1000);
//				v.y = (float) (Math.PI/12 *dz/1000);
//				fig3D.setRotation3D(v);
				
				dz += 1000;
			}
		}

	}

}