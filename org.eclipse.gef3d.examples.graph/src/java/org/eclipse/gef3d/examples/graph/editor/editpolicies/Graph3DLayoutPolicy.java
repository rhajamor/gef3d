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
package org.eclipse.gef3d.examples.graph.editor.editpolicies;

import java.util.logging.Logger;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.Figure3DHelper;
import org.eclipse.draw3d.IFigure3D;
import org.eclipse.draw3d.ISurface;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef3d.editpolicies.FeedbackHelper3D;
import org.eclipse.gef3d.editpolicies.XY3DLayoutPolicy;
import org.eclipse.gef3d.examples.graph.editor.commands.VertexCreateCommand;
import org.eclipse.gef3d.examples.graph.editor.commands.VertexResizeCommand;
import org.eclipse.gef3d.examples.graph.model.Graph;
import org.eclipse.gef3d.examples.graph.model.Vertex;
import org.eclipse.gef3d.handles.FeedbackFigure3D;

/**
 * Graph3DLayoutPolicy There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Mar 28, 2008
 */
public class Graph3DLayoutPolicy extends XY3DLayoutPolicy {

    /**
     * Logger for this class
     */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Graph3DLayoutPolicy.class.getName());

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart,
     *      java.lang.Object)
     */
    @Override
    protected Command createChangeConstraintCommand(EditPart i_child,
            Object i_constraint) {

        Object obj = i_child.getModel();
        Rectangle rect = (Rectangle) i_constraint;
        if (obj instanceof Vertex) {
            return new VertexResizeCommand((Vertex) obj, rect);
        } else {
            return null;
        }
    }

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

            FeedbackHelper3D.update(feedback, surface,
                i_createRequest.getLocation(), i_createRequest.getSize());

            addFeedback(feedback);
            return feedback;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
     */
    @Override
    protected Command getCreateCommand(CreateRequest i_request) {

        Object obj = i_request.getNewObject();

        if (obj instanceof Vertex) {
            Graph g = (Graph) this.getHost().getModel();
            Rectangle rect = (Rectangle) getConstraintFor(i_request);

            return new VertexCreateCommand((Vertex) obj, g, rect.x, rect.y,
                rect.width, rect.height);
        }
        return null;
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
            IFigure3D feedback = (IFigure3D) getSizeOnDropFeedback(createRequest);

            IFigure3D host3D = Figure3DHelper.getAncestor3D(getHostFigure());
            ISurface surface = host3D.getSurface();

            FeedbackHelper3D.update(feedback, surface,
                createRequest.getLocation(), createRequest.getSize());
        }
    }
}
