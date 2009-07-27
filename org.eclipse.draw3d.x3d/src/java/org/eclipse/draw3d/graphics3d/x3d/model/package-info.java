/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthias Thiele - initial API and implementation
 ******************************************************************************/

/**
 * <p>THis package provides a domain model which allows to build up the structure
 * of an X3D file in the memory before writing it to disk. It consists of an 
 * overall model (X3DModel), a simple all-purpose container to store the nodes 
 * (X3DNodeContainer), nodes which may contains children nodes again (X3DNode)
 * and attributes which have to be attached to node (X3DAttribute).
 * </p>
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since 30.01.2009
 */
package org.eclipse.draw3d.graphics3d.x3d.model;