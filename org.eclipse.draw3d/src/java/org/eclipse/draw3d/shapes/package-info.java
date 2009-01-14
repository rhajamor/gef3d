/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/

/**
 * This package provide basic (3D) shapes. The can be used by figures to render
 * themselves.
 * <p>
 * Usually, a figure defines the shapes, e.g. as members:
 * <pre><code>
 * private Shape m_shape = new CuboidFigureShape(this);
 * </code></pre>
 * The shape is then rendered in the figures render or (usually) 
 * postrender method:
 * <pre><code>
 * public void postrender() {
 * 		m_shape.render();
 * }
 * </code></pre>
 * The key for rendering GEF3D conform shapes is to use the 
 * {@link org.eclipse.draw3d.RenderContext}. The following lines (taken from
 * {@link CuboidFigureShape#render()} illustrate its usage:
 * 
 * <pre><code>
 * public void render() {
 * </code></pre>
 * Retrieve render context:
 * <pre><code>
 *   RenderContext renderContext = RenderContext.getContext();
 * </code></pre>
 * Retrieve fgure specific settings (the figure is passed in the constructor and
 * then stored as a member):
 * <pre><code>
 * 		int alpha = m_figure.getAlpha();
 * 		Matrix4f modelMatrix = m_figure.getModelMatrix();
 * 		...
 * </code></pre>
 * GEF3D uses color picking. That is, nearly every figure is rendered two times:
 * The first time the actual figure is rendered, in the second pass, a single
 * colored version of the figure is rendered into the off screen color buffer.
 * For that reason, shapes must be rendered depending from the current mode:
 * <pre><code>
 * 		if (renderContext.getMode().isPaint()) {
 * 			Color color = m_figure.getForegroundColor();
 * 			...
 * 		} else if (renderContext.getMode().isColor()) {
 * 			int color = renderContext.getColor(m_figure);
 * 			if (color != ColorProvider.IGNORE) {
 * 				...
 * 			}
 * 		}
 * </code></pre>
 *  
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	Jul 15, 2008
 */
package org.eclipse.draw3d.shapes;


