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
package mitra.traces.ui.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gmf.runtime.notation.View;

/**
 * EditPartReverseLookup There should really be more documentation here.
 * <br/>
 * No caching at the moment, caching requires listening!
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 14, 2009
 */
public class ReverseLookupHelper {

	RootEditPart rootEditPart;

	/**
	 * @param i_rootEditPart
	 */
	public ReverseLookupHelper(RootEditPart i_rootEditPart) {
		super();
		rootEditPart = i_rootEditPart;
	}

	
	
	public EditPart findEditPart(EObject element) {
		return findEditPart(element, rootEditPart);
	}
	
	public EditPart findEditPart(EObject element, EditPart subtreeRootEditPart) {
		LinkedHashSet<EObject> path = getPathToRoot(element);

		Object partElement = getModelElement(subtreeRootEditPart);
		if (partElement == element)
			return rootEditPart;

		EditPart editPart = findEditPart(subtreeRootEditPart, element, path);
		return editPart;
		
	}

	/**
	 * @param i_rootEditPart
	 * @param i_element
	 * @return
	 */
	private EditPart findEditPart(EditPart editPart, Object element,
		LinkedHashSet<EObject> path) {
				
		List allSubEditParts;
		if (editPart instanceof NodeEditPart) {
			NodeEditPart nodeEditPart = (NodeEditPart) editPart;
			allSubEditParts = new ArrayList(nodeEditPart.getSourceConnections().size()+nodeEditPart.getChildren().size());
			allSubEditParts.addAll(nodeEditPart.getChildren());
			allSubEditParts.addAll(nodeEditPart.getSourceConnections());
		} else {
			allSubEditParts = new ArrayList(editPart.getChildren().size());
			allSubEditParts.addAll(editPart.getChildren());
		}
		int size = allSubEditParts.size();
		
		for (int i = 0; i < size; i++) {
			EditPart childPart = (EditPart) allSubEditParts.get(i);
			Object childElement = getModelElement(childPart);
			if (childElement == element)
				return childPart;
			if (path.contains(childElement)) {
				path.remove(childElement);
				// TODO maybe buggy:
				return findEditPart(childPart, element, path);
			}
		}
		
		// if this code is reached, the path hint was useless at this level,
		// i.e. the current element is not in the path, so we have to 
		// search all children
		for (int i = 0; i < size; i++) {
			EditPart childPart = (EditPart) allSubEditParts.get(i);
			// we already know that the childElement is not the searched one
			// from the previous loop, hence we do not test that
			childPart = findEditPart(childPart, element, path);
			if (childPart != null)
				return childPart;
		}
		return null;
		
	}


	/**
	 * @param i_element
	 * @param i_editPart
	 * @return
	 */
	private boolean isModelOfEditPart(Object element, EditPart editPart) {
		if (element == editPart.getModel())
			return true;
		if (editPart.getModel() instanceof View) {
			return ((View) editPart.getModel()).getElement() == element;
		}
		return false;
	}

	private Object getModelElement(EditPart editPart) {
		if (editPart.getModel() instanceof View) {
			return ((View) editPart.getModel()).getElement();
		} else {
			return editPart.getModel();
		}

	}

	/**
	 * @param i_element
	 * @return
	 */
	private LinkedHashSet<EObject> getPathToRoot(EObject element) {
		LinkedHashSet<EObject> path = new LinkedHashSet<EObject>();
		do {
			path.add(element);
			element = element.eContainer();
		} while (element != null);
		return path;
	}

}
