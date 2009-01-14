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

import org.eclipse.gef.EditPart;

/**
 * A helper for finding an {@link EditPart} by a model element. Finding an edit
 * part by a model element is basically a reverse lookup. That is the edit part
 * hierarchy is traversed and for each edit part found the edit parts model ({@link EditPart#getModel()})
 * is compared with the searched model element. Unfortunately different editors
 * may represent model elements differently, the edit parts may translate the
 * domain model, that is some domain model elements may be suppressed, other
 * edit parts reflect derived properties and so on. Thus, a model element may
 * not be found as easy as using {@link EditPart#getModel()} but by
 * retranslating the edit parts translation. Since different editors translate
 * differently, different translators are to be used in order to find a model
 * element in different editors. This translator interface is used in
 * {@link ReverseLookupHelper}, an example of its usage can be found in the
 * UniTrace example.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 23.01.2008
 */
public interface IDomainNotationTranslator<T> {

	TranslationResult<T> findNotationByDomain(T i_rootNotationElement,
			Object i_model);

}
