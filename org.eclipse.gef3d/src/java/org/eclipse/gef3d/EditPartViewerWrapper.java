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

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.IScene;
import org.eclipse.draw3d.ISurface;
import org.eclipse.draw3d.camera.ICamera;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.picking.Picker;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.SelectionManager;
import org.eclipse.gef.dnd.TransferDragSourceListener;
import org.eclipse.gef.dnd.TransferDropTargetListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * EditPartViewerWrapper There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 28.07.2009
 */
@SuppressWarnings("deprecation")
public class EditPartViewerWrapper implements EditPartViewer {

	/**
	 * The scene.
	 */
	protected IScene m_scene;

	/**
	 * The viewer to delegate to.
	 */
	protected EditPartViewer m_viewer;

	/**
	 * Creates a new wrapper that delegates to the given edit part viewer.
	 * 
	 * @param i_viewer the edit part viewer to delegate to
	 * @param i_scene the scene
	 * @throws NullPointerException if any of the given arguments is
	 *             <code>null</code>
	 */
	public EditPartViewerWrapper(EditPartViewer i_viewer, IScene i_scene) {

		if (i_viewer == null)
			throw new NullPointerException("i_viewer must not be null");

		if (i_scene == null)
			throw new NullPointerException("i_scene must not be null");

		m_viewer = i_viewer;
		m_scene = i_scene;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#addDragSourceListener(org.eclipse.jface.util.TransferDragSourceListener)
	 */
	public void addDragSourceListener(
		org.eclipse.jface.util.TransferDragSourceListener i_listener) {

		m_viewer.addDragSourceListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#addDragSourceListener(org.eclipse.gef.dnd.TransferDragSourceListener)
	 */
	public void addDragSourceListener(TransferDragSourceListener i_listener) {

		m_viewer.addDragSourceListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#addDropTargetListener(org.eclipse.jface.util.TransferDropTargetListener)
	 */
	public void addDropTargetListener(
		org.eclipse.jface.util.TransferDropTargetListener i_listener) {

		m_viewer.addDropTargetListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#addDropTargetListener(org.eclipse.gef.dnd.TransferDropTargetListener)
	 */
	public void addDropTargetListener(TransferDropTargetListener i_listener) {

		m_viewer.addDropTargetListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener i_listener) {

		m_viewer.addPropertyChangeListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener i_listener) {

		m_viewer.addSelectionChangedListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#appendSelection(org.eclipse.gef.EditPart)
	 */
	public void appendSelection(EditPart i_editpart) {

		m_viewer.appendSelection(i_editpart);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl(Composite i_composite) {

		return m_viewer.createControl(i_composite);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#deselect(org.eclipse.gef.EditPart)
	 */
	public void deselect(EditPart i_editpart) {

		m_viewer.deselect(i_editpart);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#deselectAll()
	 */
	public void deselectAll() {

		m_viewer.deselectAll();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#findObjectAt(org.eclipse.draw2d.geometry.Point)
	 */
	public EditPart findObjectAt(Point i_sLocation) {

		return findObjectAtExcluding(i_sLocation, Collections.EMPTY_SET);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#findObjectAtExcluding(org.eclipse.draw2d.geometry.Point,
	 *      java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public EditPart findObjectAtExcluding(Point i_sLocation,
		Collection i_exclusionSet) {

		return findObjectAtExcluding(i_sLocation, i_exclusionSet, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#findObjectAtExcluding(org.eclipse.draw2d.geometry.Point,
	 *      java.util.Collection, org.eclipse.gef.EditPartViewer.Conditional)
	 */
	@SuppressWarnings("unchecked")
	public EditPart findObjectAtExcluding(Point i_sLocation,
		Collection i_exclude, Conditional i_condition) {

		Point mLocation = Draw3DCache.getPoint();
		Vector3f wLocation = Draw3DCache.getVector3f();
		try {
			Picker picker = m_scene.getPicker();
			ISurface surface = picker.getCurrentSurface();

			surface.getWorldLocation(i_sLocation, wLocation);

			ICamera camera = m_scene.getCamera();
			camera.project(wLocation, mLocation);

			return m_viewer.findObjectAtExcluding(mLocation, i_exclude,
				i_condition);
		} finally {
			Draw3DCache.returnPoint(mLocation);
			Draw3DCache.returnVector3f(wLocation);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#flush()
	 */
	public void flush() {

		m_viewer.flush();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getContents()
	 */
	public EditPart getContents() {

		return m_viewer.getContents();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getContextMenu()
	 */
	public MenuManager getContextMenu() {

		return m_viewer.getContextMenu();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getControl()
	 */
	public Control getControl() {

		return m_viewer.getControl();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getEditDomain()
	 */
	public EditDomain getEditDomain() {

		return m_viewer.getEditDomain();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getEditPartFactory()
	 */
	public EditPartFactory getEditPartFactory() {

		return m_viewer.getEditPartFactory();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getEditPartRegistry()
	 */
	@SuppressWarnings("unchecked")
	public Map getEditPartRegistry() {

		return m_viewer.getEditPartRegistry();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getFocusEditPart()
	 */
	public EditPart getFocusEditPart() {

		return m_viewer.getFocusEditPart();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getKeyHandler()
	 */
	public KeyHandler getKeyHandler() {

		return m_viewer.getKeyHandler();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getProperty(java.lang.String)
	 */
	public Object getProperty(String i_key) {

		return m_viewer.getProperty(i_key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getResourceManager()
	 */
	public ResourceManager getResourceManager() {

		return m_viewer.getResourceManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getRootEditPart()
	 */
	public RootEditPart getRootEditPart() {

		return m_viewer.getRootEditPart();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getSelectedEditParts()
	 */
	@SuppressWarnings("unchecked")
	public List getSelectedEditParts() {

		return m_viewer.getSelectedEditParts();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getSelection()
	 */
	public ISelection getSelection() {

		return m_viewer.getSelection();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getSelectionManager()
	 */
	public SelectionManager getSelectionManager() {

		return m_viewer.getSelectionManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#getVisualPartMap()
	 */
	@SuppressWarnings("unchecked")
	public Map getVisualPartMap() {

		return m_viewer.getVisualPartMap();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#registerAccessibleEditPart(org.eclipse.gef.AccessibleEditPart)
	 */
	public void registerAccessibleEditPart(AccessibleEditPart i_acc) {

		m_viewer.registerAccessibleEditPart(i_acc);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#removeDragSourceListener(org.eclipse.jface.util.TransferDragSourceListener)
	 */
	public void removeDragSourceListener(
		org.eclipse.jface.util.TransferDragSourceListener i_listener) {

		m_viewer.removeDragSourceListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#removeDragSourceListener(org.eclipse.gef.dnd.TransferDragSourceListener)
	 */
	public void removeDragSourceListener(TransferDragSourceListener i_listener) {

		m_viewer.removeDragSourceListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#removeDropTargetListener(org.eclipse.jface.util.TransferDropTargetListener)
	 */
	public void removeDropTargetListener(
		org.eclipse.jface.util.TransferDropTargetListener i_listener) {

		m_viewer.removeDropTargetListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#removeDropTargetListener(org.eclipse.gef.dnd.TransferDropTargetListener)
	 */
	public void removeDropTargetListener(TransferDropTargetListener i_listener) {

		m_viewer.removeDropTargetListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener i_listener) {

		m_viewer.removePropertyChangeListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
		ISelectionChangedListener i_listener) {

		m_viewer.removeSelectionChangedListener(i_listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#reveal(org.eclipse.gef.EditPart)
	 */
	public void reveal(EditPart i_editpart) {

		m_viewer.reveal(i_editpart);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#select(org.eclipse.gef.EditPart)
	 */
	public void select(EditPart i_editpart) {

		m_viewer.select(i_editpart);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setContents(org.eclipse.gef.EditPart)
	 */
	public void setContents(EditPart i_editpart) {

		m_viewer.setContents(i_editpart);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setContents(java.lang.Object)
	 */
	public void setContents(Object i_contents) {

		m_viewer.setContents(i_contents);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setContextMenu(org.eclipse.jface.action.MenuManager)
	 */
	public void setContextMenu(MenuManager i_contextMenu) {

		m_viewer.setContextMenu(i_contextMenu);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setControl(org.eclipse.swt.widgets.Control)
	 */
	public void setControl(Control i_control) {

		m_viewer.setControl(i_control);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setCursor(org.eclipse.swt.graphics.Cursor)
	 */
	public void setCursor(Cursor i_cursor) {

		m_viewer.setCursor(i_cursor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setEditDomain(org.eclipse.gef.EditDomain)
	 */
	public void setEditDomain(EditDomain i_domain) {

		m_viewer.setEditDomain(i_domain);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setEditPartFactory(org.eclipse.gef.EditPartFactory)
	 */
	public void setEditPartFactory(EditPartFactory i_factory) {

		m_viewer.setEditPartFactory(i_factory);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setFocus(org.eclipse.gef.EditPart)
	 */
	public void setFocus(EditPart i_focus) {

		m_viewer.setFocus(i_focus);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setKeyHandler(org.eclipse.gef.KeyHandler)
	 */
	public void setKeyHandler(KeyHandler i_keyHandler) {

		m_viewer.setKeyHandler(i_keyHandler);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setProperty(String i_propertyName, Object i_value) {

		m_viewer.setProperty(i_propertyName, i_value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setRootEditPart(org.eclipse.gef.RootEditPart)
	 */
	public void setRootEditPart(RootEditPart i_root) {

		m_viewer.setRootEditPart(i_root);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setRouteEventsToEditDomain(boolean)
	 */
	public void setRouteEventsToEditDomain(boolean i_value) {

		m_viewer.setRouteEventsToEditDomain(i_value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection i_selection) {

		m_viewer.setSelection(i_selection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#setSelectionManager(org.eclipse.gef.SelectionManager)
	 */
	public void setSelectionManager(SelectionManager i_manager) {

		m_viewer.setSelectionManager(i_manager);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartViewer#unregisterAccessibleEditPart(org.eclipse.gef.AccessibleEditPart)
	 */
	public void unregisterAccessibleEditPart(AccessibleEditPart i_acc) {

		m_viewer.unregisterAccessibleEditPart(i_acc);
	}

}
