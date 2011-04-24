/*******************************************************************************
 * Copyright (c) 2011 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.editpolicies;

import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.geometry.Position3D;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef3d.commands.UpdateConstraintCommand;
import org.eclipse.gef3d.requests.ChangeBounds3DRequest;

/**
 * Layout policy creating commands for updating constraints of figures,
 * regardless of edit part and model. Children of a (3D) figure using this
 * layout can be moved and rotated in 3D space, however the change of position
 * is not propagated to the edit part and thus cannot be persisted.
 * <p>
 * This policy is usually only used for multi editors to enable the user to
 * temporarily move planes around.
 * 
 * @author Jens von Pilgrim (developer@jevopi.de)
 * @version $Revision$
 * @since Apr 24, 2011
 */
public class XYZConstraintLayoutPolicy extends XY3DLayoutPolicy {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart,
	 *      java.lang.Object)
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart i_child,
		Object i_constraint) {
		if (i_child instanceof GraphicalEditPart) {
			return new UpdateConstraintCommand(
				((GraphicalEditPart) i_child).getFigure(), i_constraint,
				getHostFigure().getLayoutManager());
		}
		return null;
	}

	/**
	 * Returns null, no new elements can be created with this policy.
	 * 
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#getConstraintFor(org.eclipse.gef.requests.ChangeBoundsRequest,
	 *      org.eclipse.gef.GraphicalEditPart)
	 */
	@Override
	protected Object getConstraintFor(ChangeBoundsRequest request,
		GraphicalEditPart child) {

		if (request instanceof ChangeBounds3DRequest
			&& child.getFigure() instanceof IFigure3D) {

			ChangeBounds3DRequest cbr3D = (ChangeBounds3DRequest) request;
			Position3D childPos =
				((IFigure3D) child.getFigure()).getPosition3D();
			Position3D newPos = childPos.getAbsolute(null);
			cbr3D.getTransformedPosition(newPos);
			return getConstraintFor(newPos);
		} else {
			return super.getConstraintFor(request, child);
		}
	}

	/**
	 * @param i_newPos
	 * @return
	 */
	protected Object getConstraintFor(Position3D i_newPos) {
		return i_newPos;
	}

}
