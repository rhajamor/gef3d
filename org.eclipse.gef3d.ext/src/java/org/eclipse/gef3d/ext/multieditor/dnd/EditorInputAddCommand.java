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

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef3d.ext.multieditor.IMultiEditor;
import org.eclipse.ui.IEditorInput;

/**
 * This command causes a multi editor to open a new editor input. This command
 * cannot be undone.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Apr 15, 2009
 */
public class EditorInputAddCommand extends Command {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(EditorInputAddCommand.class.getName());

	List<IEditorInput> editorInputs;

	private IMultiEditor multiEditor;
	
	/**
	 * @param i_request
	 */
	public EditorInputAddCommand() {
	}
	
	

	/** 
	 * Returns true if multi editor is set and at least one editor input
	 * is set. This is usually the case when this command has been created by
	 * the {@link EditorInputDropPolicy}.
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return multiEditor!=null && editorInputs!=null && editorInputs.size()>0;
	}



	/** 
	 * Always returns false, this command cannot be undone.
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return false;
	}



	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (editorInputs==null) return;
		for (IEditorInput editorInput: editorInputs) {
			multiEditor.addEditor(editorInput);
		}
		
		
	}

	/**
	 * @param i_editorInputs
	 */
	public void setEditorInputs(List<IEditorInput> i_editorInputs) {
		editorInputs = i_editorInputs;
		
	}

	/**
	 * @param i_dropableMultiEditor
	 */
	public void setMultiEditor(IMultiEditor i_dropableMultiEditor) {
		multiEditor = i_dropableMultiEditor;
		
	}
	
	

}
