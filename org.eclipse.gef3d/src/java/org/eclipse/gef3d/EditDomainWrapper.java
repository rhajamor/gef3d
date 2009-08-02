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
package org.eclipse.gef3d;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.MouseEvent3D;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Picker;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Tool;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef3d.ui.parts.GraphicalViewer3D;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.widgets.Event;

/**
 * Wraps an edit domain in order to provide 2D tools with surface coordinates.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 28.07.2009
 */
public class EditDomainWrapper extends DefaultEditDomain {

	@SuppressWarnings("unused")
	private static final Logger log =
		Logger.getLogger(EditDomainWrapper.class.getName());

	private DefaultEditDomain m_domain;

	private Map<EditPartViewer, EditPartViewer> m_viewers =
		new HashMap<EditPartViewer, EditPartViewer>();

	/**
	 * Creates a new edit domain wrapper that delegates to the given edit
	 * domain.
	 * 
	 * @param i_domain the wrapped edit domain
	 * @throws NullPointerException if the given edit domain is
	 *             <code>null</code>
	 */
	public EditDomainWrapper(DefaultEditDomain i_domain) {

		super(i_domain.getEditorPart());

		m_domain = i_domain;
		loadDefaultTool();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#addViewer(org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void addViewer(EditPartViewer i_viewer) {

		if (!m_viewers.containsKey(i_viewer)) {
			if (!(i_viewer instanceof GraphicalViewer3D))
				throw new IllegalArgumentException(
					"can only use 3D graphical viewers");

			EditPartViewer wrapper =
				new GraphicalViewerWrapper3D((GraphicalViewer3D) i_viewer,
					getScene(i_viewer));

			m_viewers.put(i_viewer, wrapper);
			i_viewer.setEditDomain(this);
		}
	}

	private MouseEvent3D convert(EditPartViewer i_viewer, MouseEvent i_me) {

		Point sLocation = Draw3DCache.getPoint();
		try {
			Picker picker = getScene(i_viewer).getPicker();
			picker.updateCurrentSurface(i_me.x, i_me.y);

			getSurfaceLocation(i_viewer, i_me.x, i_me.y, sLocation);

			ISurface surface = picker.getCurrentSurface();
			Vector3f wLocation = surface.getWorldLocation(sLocation, null);

			return new MouseEvent3D(i_me, sLocation, wLocation);
		} finally {
			Draw3DCache.returnPoint(sLocation);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#focusGained(org.eclipse.swt.events.FocusEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void focusGained(FocusEvent i_event, EditPartViewer i_viewer) {

		m_domain.focusGained(i_event, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#focusLost(org.eclipse.swt.events.FocusEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void focusLost(FocusEvent i_event, EditPartViewer i_viewer) {

		m_domain.focusLost(i_event, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#getActiveTool()
	 */
	@Override
	public Tool getActiveTool() {

		return m_domain.getActiveTool();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#getCommandStack()
	 */
	@Override
	public CommandStack getCommandStack() {

		return m_domain.getCommandStack();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#getDefaultTool()
	 */
	@Override
	public Tool getDefaultTool() {

		return m_domain.getDefaultTool();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#getPaletteViewer()
	 */
	@Override
	public PaletteViewer getPaletteViewer() {

		return m_domain.getPaletteViewer();
	}

	private IScene getScene(EditPartViewer i_viewer) {

		return ((GraphicalViewer3D) i_viewer).getLightweightSystem3D();
	}

	private Point getSurfaceLocation(EditPartViewer i_viewer, int i_mx,
		int i_my, Point io_result) {

		Vector3f eye = Draw3DCache.getVector3f();
		Vector3f point = Draw3DCache.getVector3f();
		Vector3f direction = Draw3DCache.getVector3f();
		try {
			ICamera camera = getScene(i_viewer).getCamera();
			camera.getPosition(eye);

			camera.unProject(i_mx, i_my, 0, null, point);

			Picker picker = getScene(i_viewer).getPicker();
			ISurface surface = picker.getCurrentSurface();

			Math3D.getRayDirection(eye, point, direction);
			return surface.getSurfaceLocation2D(eye, direction, io_result);
		} finally {
			Draw3DCache.returnVector3f(eye);
			Draw3DCache.returnVector3f(point);
			Draw3DCache.returnVector3f(direction);
		}
	}

	private EditPartViewer getViewer(EditPartViewer i_viewer) {

		return m_viewers.get(i_viewer);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#keyDown(org.eclipse.swt.events.KeyEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void keyDown(KeyEvent i_keyEvent, EditPartViewer i_viewer) {

		m_domain.keyDown(i_keyEvent, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#keyTraversed(org.eclipse.swt.events.TraverseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void keyTraversed(TraverseEvent i_traverseEvent,
		EditPartViewer i_viewer) {

		m_domain.keyTraversed(i_traverseEvent, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#keyUp(org.eclipse.swt.events.KeyEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void keyUp(KeyEvent i_keyEvent, EditPartViewer i_viewer) {

		m_domain.keyUp(i_keyEvent, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#loadDefaultTool()
	 */
	@Override
	public void loadDefaultTool() {

		// can be null because of invocation in super constructor
		if (m_domain != null)
			m_domain.loadDefaultTool();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#mouseDoubleClick(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent i_mouseEvent,
		EditPartViewer i_viewer) {

		MouseEvent3D me3D = convert(i_viewer, i_mouseEvent);
		m_domain.mouseDoubleClick(me3D, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#mouseDown(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseDown(MouseEvent i_mouseEvent, EditPartViewer i_viewer) {

		MouseEvent3D me3D = convert(i_viewer, i_mouseEvent);
		m_domain.mouseDown(me3D, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#mouseDrag(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseDrag(MouseEvent i_mouseEvent, EditPartViewer i_viewer) {

		MouseEvent3D me3D = convert(i_viewer, i_mouseEvent);
		m_domain.mouseDrag(me3D, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#mouseHover(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseHover(MouseEvent i_mouseEvent, EditPartViewer i_viewer) {

		MouseEvent3D me3D = convert(i_viewer, i_mouseEvent);
		m_domain.mouseHover(me3D, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#mouseMove(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseMove(MouseEvent i_mouseEvent, EditPartViewer i_viewer) {

		MouseEvent3D me3D = convert(i_viewer, i_mouseEvent);
		m_domain.mouseMove(me3D, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#mouseUp(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseUp(MouseEvent i_mouseEvent, EditPartViewer i_viewer) {

		MouseEvent3D me3D = convert(i_viewer, i_mouseEvent);
		m_domain.mouseUp(me3D, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#mouseWheelScrolled(org.eclipse.swt.widgets.Event,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseWheelScrolled(Event i_e, EditPartViewer i_viewer) {

		Point sLocation = Draw3DCache.getPoint();
		try {
			getScene(i_viewer).getPicker().updateCurrentSurface(i_e.x, i_e.y);
			getSurfaceLocation(i_viewer, i_e.x, i_e.y, sLocation);
			i_e.x = sLocation.x;
			i_e.y = sLocation.y;

			m_domain.mouseWheelScrolled(i_e, getViewer(i_viewer));
		} finally {
			Draw3DCache.returnPoint(sLocation);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#nativeDragFinished(org.eclipse.swt.dnd.DragSourceEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void nativeDragFinished(DragSourceEvent i_e, EditPartViewer i_viewer) {

		Point sLocation = Draw3DCache.getPoint();
		try {
			getScene(i_viewer).getPicker().updateCurrentSurface(i_e.x, i_e.y);
			getSurfaceLocation(i_viewer, i_e.x, i_e.y, sLocation);
			i_e.x = sLocation.x;
			i_e.y = sLocation.y;

			m_domain.nativeDragFinished(i_e, getViewer(i_viewer));
		} finally {
			Draw3DCache.returnPoint(sLocation);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#nativeDragStarted(org.eclipse.swt.dnd.DragSourceEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void nativeDragStarted(DragSourceEvent i_e, EditPartViewer i_viewer) {

		Point sLocation = Draw3DCache.getPoint();
		try {
			getScene(i_viewer).getPicker().updateCurrentSurface(i_e.x, i_e.y);
			getSurfaceLocation(i_viewer, i_e.x, i_e.y, sLocation);
			i_e.x = sLocation.x;
			i_e.y = sLocation.y;

			m_domain.nativeDragStarted(i_e, getViewer(i_viewer));
		} finally {
			Draw3DCache.returnPoint(sLocation);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#removeViewer(org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void removeViewer(EditPartViewer i_viewer) {

		if (m_viewers.containsKey(i_viewer)) {
			m_viewers.remove(i_viewer);
			i_viewer.setEditDomain(null);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#setActiveTool(org.eclipse.gef.Tool)
	 */
	@Override
	public void setActiveTool(Tool i_tool) {

		m_domain.setActiveTool(i_tool);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#setCommandStack(org.eclipse.gef.commands.CommandStack)
	 */
	@Override
	public void setCommandStack(CommandStack i_stack) {

		m_domain.setCommandStack(i_stack);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#setDefaultTool(org.eclipse.gef.Tool)
	 */
	@Override
	public void setDefaultTool(Tool i_tool) {

		m_domain.setDefaultTool(i_tool);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#setPaletteRoot(org.eclipse.gef.palette.PaletteRoot)
	 */
	@Override
	public void setPaletteRoot(PaletteRoot i_root) {

		m_domain.setPaletteRoot(i_root);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#setPaletteViewer(org.eclipse.gef.ui.palette.PaletteViewer)
	 */
	@Override
	public void setPaletteViewer(PaletteViewer i_palette) {

		m_domain.setPaletteViewer(i_palette);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#viewerEntered(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void viewerEntered(MouseEvent i_mouseEvent, EditPartViewer i_viewer) {

		m_domain.viewerEntered(i_mouseEvent, getViewer(i_viewer));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditDomain#viewerExited(org.eclipse.swt.events.MouseEvent,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void viewerExited(MouseEvent i_mouseEvent, EditPartViewer i_viewer) {

		m_domain.viewerExited(i_mouseEvent, getViewer(i_viewer));
	}
}
