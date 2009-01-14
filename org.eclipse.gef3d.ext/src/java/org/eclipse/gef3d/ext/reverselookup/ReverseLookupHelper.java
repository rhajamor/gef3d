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

import java.util.HashMap;
import java.util.Map;

/**
 * ReverseLookupHelper There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 17.01.2008
 */
public class ReverseLookupHelper<T> {

	Map<T, IDomainNotationTranslator<T>> translators;

	/**
	 * 
	 */
	public ReverseLookupHelper() {
		translators = new HashMap<T, IDomainNotationTranslator<T>>();
	}

	/**
	 * Delegate method
	 * 
	 * @param i_arg0
	 * @param i_arg1
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public IDomainNotationTranslator<T> addTranslator(T i_root,
			IDomainNotationTranslator<T> i_translator) {
		return translators.put(i_root, i_translator);
	}

	/**
	 * Delegate method
	 * 
	 * @param i_arg0
	 * @return
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public IDomainNotationTranslator<T> removeTranslator(T i_root) {
		return translators.remove(i_root);
	}

	/**
	 * Finds a model in the edit part tree with given root edit part, using the
	 * given root edit part as a key for selecting a registered translator.
	 * 
	 * @param i_root
	 * @param i_model
	 * @return
	 */
	public TranslationResult<T> findNotationByDomain(T i_root, Object i_model) {
		IDomainNotationTranslator<T> translator = translators.get(i_root);
		if (translator == null) {
			return TranslationResult.EMPTY_RESULT;
		} else {
			return translator.findNotationByDomain(i_root, i_model);
		}

	}

}
