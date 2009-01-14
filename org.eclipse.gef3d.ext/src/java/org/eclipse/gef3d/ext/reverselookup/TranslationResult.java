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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;

/**
 * Result class returned by a {@link IDomainNotationTranslator}. It contains
 * the mapped element (if found, null otherwise), e.g. an EditPart or a notation
 * element, and a list of (model) elements "leading" to the model object
 * initially searched. If the edit part contains the model element directly, the
 * model element chain contains only the model searched. The model element chain
 * does not necessarily reflect the navigation structure (e.g. of
 * {@link EditPart#getModel()}) but the model structure as interpreted by the
 * translator. The diagram interchange (DI) translator
 * {@link DomainEditPartUMLDITranslator} for example always returns only the
 * semantic model element in the chain and not the DI element. The length of the
 * model element chain and its content can be used for reflecting the notation --
 * domain differences in the editor, e.g. by using different colors.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 23.01.2008
 */
public class TranslationResult<T> {

	@SuppressWarnings("unchecked")
	public final static TranslationResult EMPTY_RESULT = new TranslationResult<Object>(
			null, Collections.EMPTY_LIST);

	protected T notationElement;

	protected List modelElementChain;

	public TranslationResult(T i_notationElement, List i_modelElementChain) {
		notationElement = i_notationElement;
		modelElementChain = i_modelElementChain;
	}

	/**
	 * @param i_mappedElement
	 */
	public TranslationResult(T i_notationElement, Object... modelElements) {
		modelElementChain = new ArrayList(Arrays.asList(modelElements));
		notationElement = i_notationElement;
	}

	/**
	 * @return the notationElement
	 */
	public T getNotationElement() {
		return notationElement;
	}

	/**
	 * @return the modelElementChain
	 */
	public List getModelElementChain() {
		return modelElementChain;
	}

	/**
	 * @param i_model
	 */
	public void appendModelElement(Object i_model) {
		modelElementChain.add(i_model);
	}

	/** 
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("TranslationResult (");
		result.append(notationElement);
		result.append(" via ").append(modelElementChain.size())
		.append(" elements)");
		return result.toString();
	}
	
	

}