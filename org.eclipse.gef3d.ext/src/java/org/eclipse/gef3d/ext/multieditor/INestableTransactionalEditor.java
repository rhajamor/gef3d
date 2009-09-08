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
package org.eclipse.gef3d.ext.multieditor;

import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * A {@link INestableEditor nestable editor} that can join a
 * {@link TransactionalEditingDomain transactional editing domain}.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 07.09.2009
 */
public interface INestableTransactionalEditor extends INestableEditor {

	/**
	 * Joins the given transactional editing domain. The editor is disconnected
	 * from its current editing domain if it has one.
	 * 
	 * @param i_domain the transactional editing domain to join
	 */
	void joinTransactionalEditingDomain(TransactionalEditingDomain i_domain);
}
