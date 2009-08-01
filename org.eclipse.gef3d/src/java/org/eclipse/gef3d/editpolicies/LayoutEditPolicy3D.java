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
package org.eclipse.gef3d.editpolicies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef3d.handles.FeedbackFigure3D;

/**
 * 3D version of {@link LayoutEditPolicy}.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 30.07.2009
 */
public abstract class LayoutEditPolicy3D extends LayoutEditPolicy {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#createSizeOnDropFeedback(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected IFigure createSizeOnDropFeedback(CreateRequest i_createRequest) {

		IFigure3D host3D = Figure3DHelper.getAncestor3D(getHostFigure());
		if (host3D == null) {
			return super.createSizeOnDropFeedback(i_createRequest);
		} else { // use 3D implementation otherwise
			ISurface surface = host3D.getSurface();
			FeedbackFigure3D feedback = new FeedbackFigure3D();

			FeedbackHelper3D.update(feedback, surface, i_createRequest
				.getLocation(), i_createRequest.getSize());

			addFeedback(feedback);
			return feedback;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#showLayoutTargetFeedback(org.eclipse.gef.Request)
	 */
	@Override
	protected void showLayoutTargetFeedback(Request i_request) {

		if (i_request instanceof CreateRequest
			&& REQ_CREATE.equals(i_request.getType())) {

			CreateRequest createRequest = (CreateRequest) i_request;
			IFigure3D feedback =
				(IFigure3D) getSizeOnDropFeedback(createRequest);

			IFigure3D host3D = Figure3DHelper.getAncestor3D(getHostFigure());
			ISurface surface = host3D.getSurface();

			FeedbackHelper3D.update(feedback, surface, createRequest
				.getLocation(), createRequest.getSize());
		}
	}
}
