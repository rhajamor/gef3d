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
 * <p>This package contains the drawing capabilities of the X3D-export. For each
 * possible graphics primitive supported by the Graphics3D interface, there is a
 * counterpart in here.</p>
 * <p>In addition, a natural class hierarchy of the graphic primitives is 
 * established which corresponds to the X3D specifics. All primitives are Shapes,
 * while Lines and Polygons are LineSets. These classes summarize common 
 * characteristics.</p>
 * 
 * <p>All graphic primitives are create via the DrawTargetFactory. They have to
 * implement the DrawTarget interface to be usable in a common sense. The
 * DrawCommand and the ParameterList are used to send generic commands to the
 * primitives which are interpreted only by them.</p>
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since 30.01.2009
 */
package org.eclipse.draw3d.graphics3d.x3d.draw;