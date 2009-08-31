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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef3d.ext.multieditor.IMultiEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;

/**
 * Drop target listener to be used by a multi editor to enable opening new
 * diagrams (or models) in the editor by simply droping the editor input onto
 * the 3D scene. This listener is to be added to the viewer during
 * configuration, i.e. in {@link GraphicalEditor#configureGraphicalViewer()}
 * just like that:
 * <p>
 * <code><pre>
 * getGraphicalViewer().addDropTargetListener(
 * 		new EditorInputTransferDropTargetListener(this,
 * 		getGraphicalViewer()));
 * </pre></code>
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 15, 2009
 */
public class EditorInputTransferDropTargetListener extends
		AbstractTransferDropTargetListener {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(EditorInputTransferDropTargetListener.class.getName());

	private IMultiEditor graphicalEditor;

	/**
	 * @param i_graphicalEditor
	 * @param i_viewer
	 */
	public EditorInputTransferDropTargetListener(
			IMultiEditor i_graphicalEditor, EditPartViewer i_viewer) {
		super(i_viewer, EditorInputTransfer.getInstance());
		graphicalEditor = i_graphicalEditor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#updateTargetRequest()
	 */
	@Override
	protected void updateTargetRequest() {
		EditorInputDropRequest request =
			(EditorInputDropRequest) getTargetRequest();
		DropTargetEvent event = getCurrentEvent();
		request.setLocation(new Point(event.x, event.y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#createTargetRequest()
	 */
	@Override
	protected Request createTargetRequest() {
		EditorInputDropRequest request = new EditorInputDropRequest();
		DropTargetEvent event = getCurrentEvent();
		request.setLocation(new Point(event.x, event.y));
		request.setMultiEditor(graphicalEditor);
		return request;
	}

	private boolean editorAccepts(DropTargetEvent i_event) {

		if (i_event.data instanceof EditorInputTransfer.EditorInputData[]) {
			EditorInputTransfer.EditorInputData[] editorInputsData =
				(EditorInputData[]) i_event.data;

			for (EditorInputTransfer.EditorInputData data : editorInputsData)
				if (!graphicalEditor.acceptsInput(data.input))
					return false;
		} else
			return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#handleDragOver()
	 */
	@Override
	protected void handleDragOver() {
		// if (log.isLoggable(Level.INFO)) {
		//			log.info("handleDragOver"); //$NON-NLS-1$
		// }

		DropTargetEvent event = getCurrentEvent();
		event.detail = DND.DROP_COPY;

		super.handleDragOver();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#handleDrop()
	 */
	@Override
	protected void handleDrop() {
		if (log.isLoggable(Level.INFO)) {
			log.info("handleDrop"); //$NON-NLS-1$
		}

		DropTargetEvent event = getCurrentEvent();

		boolean accept = editorAccepts(event);
		event.detail = accept ? DND.DROP_COPY : DND.DROP_NONE;

		if (!accept)
			return;

		EditorInputDropRequest request =
			(EditorInputDropRequest) getTargetRequest();

		if (event.data instanceof EditorInputTransfer.EditorInputData[]) {
			List<IEditorInput> editorInputs = new ArrayList<IEditorInput>();
			EditorInputTransfer.EditorInputData[] editorInputsData =
				(EditorInputData[]) event.data;
			for (EditorInputTransfer.EditorInputData data : editorInputsData) {
				editorInputs.add(data.input);

			}
			request.setEditorInputs(editorInputs);
		}

		try {
			super.handleDrop();
		} catch (Exception ex) {
			// TODO Implement catch block for Exception
			ex.printStackTrace();
		}

	}

}
