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

package org.eclipse.gef3d.ext.reverselookup;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.NodeEditPart;

import static org.eclipse.gef3d.ext.reverselookup.TranslationResult.EMPTY_RESULT;

/**
 * Default domain-notation-translator, searching an editor using
 * {@link EditPart#getModel()} as a key. That is an 1:1 representation of the
 * model elements is assumed. This translator can serve as a super class for
 * other translators, using a deepth first search. To avoid cycles, connections
 * are tested but not traversed. Other translators with more domain knowledge
 * may implement more efficient traverse strategies or initially create a hash
 * map.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 23.01.2008
 */
public class DefaultDomainEditPartTranslator implements
		IDomainNotationTranslator<EditPart> {

	public static final DefaultDomainEditPartTranslator INSTANCE = new DefaultDomainEditPartTranslator();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef3d.ext.reverselookup.IDomainNotationTranslator#findNotationByDomain(Object,
	 *      Object)
	 */
	public TranslationResult<EditPart> findNotationByDomain(
			EditPart i_rootEditPart, Object i_model) {

		TranslationResult<EditPart> tr = EMPTY_RESULT;
		if (i_rootEditPart != null) {
			tr = doIsModelRepresentedByEditPart(i_rootEditPart, i_model);

			if (tr == EMPTY_RESULT) {
				tr = doFindEditPartByModelInList(i_rootEditPart.getChildren(),
						i_model);
				if (tr == EMPTY_RESULT) {
					if (i_rootEditPart instanceof NodeEditPart) {
						NodeEditPart nodePart = (NodeEditPart) i_rootEditPart;
						tr = doFindEditPartByModelInList(nodePart
								.getSourceConnections(), i_model);
					}
				}

			}
		}
		return tr;
	}

	/**
	 * @param i_children
	 * @param i_model
	 * @return
	 */
	private TranslationResult<EditPart> doFindEditPartByModelInList(
			List i_listOfEditParts, Object i_model) {
		int size = i_listOfEditParts.size();
		EditPart part;
		TranslationResult<EditPart> tr = EMPTY_RESULT;
		for (int i = 0; i < size && tr == EMPTY_RESULT; i++) {
			part = (EditPart) i_listOfEditParts.get(i);
			tr = findNotationByDomain(part, i_model);
		}
		return tr;
	}

	/**
	 * Usually only this method has to be overridden by subclasses.
	 * 
	 * @param i_editPart
	 * @param i_model
	 * @return
	 */
	protected TranslationResult<EditPart> doIsModelRepresentedByEditPart(
			EditPart i_editPart, Object i_model) {
		if (i_editPart != null && i_editPart.getModel() == i_model) {
			return new TranslationResult<EditPart>(i_editPart, i_model);
		} else {
			return EMPTY_RESULT;
		}
	}

	public static String dumpEditPartHierarchy(EditPart i_editPart) {
		StringBuffer strb = new StringBuffer();
		dumpEditPartHierarchy(i_editPart, 0, strb);
		return strb.toString();
	}

	private static void dumpEditPartHierarchy(EditPart i_editPart, int level,
			StringBuffer o_strb) {
		for (int i = level; i > 0; i--) {
			o_strb.append("    ");
		}
		o_strb.append(i_editPart);
		o_strb.append(" ----> ").append(i_editPart.getModel());
		o_strb.append("\n");

		for (Iterator iter = i_editPart.getChildren().iterator(); iter
				.hasNext();) {
			EditPart childPart = (EditPart) iter.next();
			dumpEditPartHierarchy(childPart, level + 1, o_strb);

		}

	}

}
