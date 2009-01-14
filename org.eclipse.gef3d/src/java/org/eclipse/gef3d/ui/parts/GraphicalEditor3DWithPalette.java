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
package org.eclipse.gef3d.ui.parts;

import org.eclipse.draw3d.LightweightSystem3D;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef3d.preferences.ScenePreferenceListener;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;


/**
 * GraphicalEditor3DWithPalette There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 16.11.2007
 */
public abstract class GraphicalEditor3DWithPalette extends
		GraphicalEditorWithPalette {

	/**
	 * The preference listener for this editor.
	 */
	protected ScenePreferenceListener sceneListener;

	/**
	 * {@inheritDoc} Here, a {@link GraphicalViewer3DImpl} is created instead of
	 * a ScrollingGraphicalViewer.
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createGraphicalViewer(Composite i_parent) {
		GraphicalViewer3DImpl viewer = new GraphicalViewer3DImpl();

		// 1:1 from GraphicalEditor.createGraphicalViewer(Composite)
		Control control = viewer.createControl(i_parent);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();

		IEditorSite editorSite = getEditorSite();
		IActionBars actionBars = editorSite.getActionBars();
		IStatusLineManager statusLine = actionBars.getStatusLineManager();

		FpsStatusLineItem fpsCounter = new FpsStatusLineItem();
		LightweightSystem3D lightweightSystem3D = viewer.getLightweightSystem3D();
		lightweightSystem3D.addRendererListener(fpsCounter);
		
		statusLine.add(fpsCounter);
		
		sceneListener = new ScenePreferenceListener(viewer);
		sceneListener.start();

		control.addDisposeListener(lightweightSystem3D);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#dispose()
	 */
	@Override
	public void dispose() {

		if (sceneListener != null)
			sceneListener.stop();

		super.dispose();
	}
}
