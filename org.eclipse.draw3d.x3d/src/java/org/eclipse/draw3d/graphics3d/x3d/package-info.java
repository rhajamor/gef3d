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
 * <p>This package is the main package for the X3D implementation of the 
 * Graphics3D interface.</p>
 * <p>It contains the main class for that purpose: Graphics3DX3D. This class 
 * is a facade, delegating the calls to more specialized classes in the sub 
 * packages. There are also some other classes which have a general purpose:<br>
 * <ul>
 * <li>ExportInterceptor: To perform several operations at defined steps in 
 * the export process</li>
 * <li>PropertyContainer: To store the current rendering properties.</li>
 * <li>TransformationManager: To maintain the current transformation node.</li>
 * </p>
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since 30.01.2009
 */
package org.eclipse.draw3d.graphics3d.x3d;