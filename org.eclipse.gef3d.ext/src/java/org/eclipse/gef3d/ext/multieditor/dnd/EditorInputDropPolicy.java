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
package org.eclipse.gef3d.ext.multieditor.dnd;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef3d.ext.multieditor.MultiEditorModelContainer;
import org.eclipse.gef3d.ext.multieditor.MultiEditorModelContainerEditPart;
import org.eclipse.ui.IEditorInput;

/**
 * Enables native drop of {@link IEditorInput} objects onto the 3D scene. This
 * policy is to be installed at the {@link MultiEditorModelContainerEditPart},
 * it is not installed by default.
 * <p>
 * The policy is usually installed in the graphical editor during intialization,
 * that is in {@link GraphicalEditor#ini}. Right after the content has been
 * added to the viewer, it should be possible to install this policy at the
 * newly created edit part. The following snippet illustrates how to install
 * this policy:
 * <p>
 * <code><pre>
 * protected void initializeGraphicalViewer() {
 * 		m_container = new MultiEditorModelContainer();
 * 		GraphicalViewer viewer = getGraphicalViewer();
 * 		viewer.setContents(m_container);
 * 		viewer.getContents().installEditPolicy(EditorInputDropPolicy.EDITOR_INPUT_ROLE,
 * 			new EditorInputDropPolicy());
 * 		addEditor(getEditorInput());
 * }
 * </pre></code>
 * </p>
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 15, 2009
 */
public class EditorInputDropPolicy extends AbstractEditPolicy {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(EditorInputDropPolicy.class.getName());

	public static final Object EDITOR_INPUT_ROLE = "EDITOR_INPUT_ROLE";

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	@Override
	public EditPart getTargetEditPart(Request i_request) {
//		if (log.isLoggable(Level.INFO)) {
//			log.info("getTargetEditPart - i_req=" + i_request); //$NON-NLS-1$
//		}
		if (i_request instanceof EditorInputDropRequest) {
			return this.getHost();
		}
		return super.getTargetEditPart(i_request);
	}

	/**
	 * if the request is an {@link EditorInputDropRequest}, a new
	 * {@link EditorInputAddCommand} is created.
	 * 
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	@Override
	public Command getCommand(Request i_request) {
//		if (log.isLoggable(Level.INFO)) {
//			log.info("getCommand - i_req=" + i_request); //$NON-NLS-1$
//		}
		if (i_request instanceof EditorInputDropRequest) {
			EditorInputDropRequest dor = (EditorInputDropRequest) i_request;

			EditorInputAddCommand cmd = new EditorInputAddCommand();
			cmd.setEditorInputs(dor.getEditorInputs());
			cmd.setMultiEditor(dor.getMultiEditor());

			return cmd;

		}

		return super.getCommand(i_request);
	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#understandsRequest(org.eclipse.gef.Request)
	 */
	@Override
	public boolean understandsRequest(Request i_req) {
//		if (log.isLoggable(Level.INFO)) {
//			log.info("understandsRequest - i_req=" + i_req); //$NON-NLS-1$
//		}

		return super.understandsRequest(i_req);
	}
	
	

}
