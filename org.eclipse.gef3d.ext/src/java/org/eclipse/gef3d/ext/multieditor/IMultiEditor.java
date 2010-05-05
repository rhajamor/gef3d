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

import org.eclipse.gef.EditPart;
import org.eclipse.ui.IEditorInput;

/**
 * Editor implementing this interface can be used in combination with the
 * Drag-and-Drop mechanisms provided in the dnd-package in order to open
 * diagrams in the multi-editor.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Apr 15, 2009
 */
public interface IMultiEditor {

	/**
	 * Indicates whether this multi editor accepts the given editor input.
	 * 
	 * @param i_editorInput the editor input to accept
	 * @return <code>true</code> if this multi editor accepts the given input
	 *         and <code>false</code> otherwise
	 */
	public boolean acceptsInput(IEditorInput i_editorInput);

	/**
	 * Adds the given editor input to this multi editor.
	 * 
	 * @param i_editorInput the input to add
	 */
	public void addEditor(IEditorInput i_editorInput);

	/**
	 * Retrieves an {@link INestableEditor} by an edit part. This method is used
	 * for example by {@link MultiEditorPropertySheetPage} in order to retrieve
	 * the nested editor based on the current selection.
	 * 
	 * @param part the part
	 * @return the nested editor or null, if no nested editor is found
	 */
	public INestableEditor findEditorByEditPart(EditPart part);

	/**
	 * Adds a new {@link IMultiEditorListener} which is notified when editors
	 * are newly nested or remove.
	 * 
	 * @param multiEditorListener, must not be null
	 */
	public void addMultiEditorListener(IMultiEditorListener multiEditorListener);

	/**
	 * Removes an {@link IMultiEditorListener}.
	 * 
	 * @param multiEditorListener
	 */
	public void removeMultiEditorListener(
		IMultiEditorListener multiEditorListener);
}
