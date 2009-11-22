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
package org.eclipse.draw3d.graphics.optimizer;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.graphics.StatefulGraphics;
import org.eclipse.swt.graphics.Image;

/**
 * OptimizingGraphics
 * There should really be more documentation here.
 *
 * @author 	Kristian Duske
 * @version	$Revision$
 * @since 	18.11.2009
 */
public class OptimizingGraphics extends StatefulGraphics {

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int i_x, int i_y, int i_w, int i_h, int i_offset,
		int i_length) {
		// TODO implement method OptimizingGraphics.drawArc

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawFocus(int, int, int, int)
	 */
	@Override
	public void drawFocus(int i_x, int i_y, int i_w, int i_h) {
		// TODO implement method OptimizingGraphics.drawFocus

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image, int, int)
	 */
	@Override
	public void drawImage(Image i_srcImage, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.drawImage

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image, int, int, int, int, int, int, int, int)
	 */
	@Override
	public void drawImage(Image i_srcImage, int i_x1, int i_y1, int i_w1,
		int i_h1, int i_x2, int i_y2, int i_w2, int i_h2) {
		// TODO implement method OptimizingGraphics.drawImage

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int i_x1, int i_y1, int i_x2, int i_y2) {
		// TODO implement method OptimizingGraphics.drawLine

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int i_x, int i_y, int i_w, int i_h) {
		// TODO implement method OptimizingGraphics.drawOval

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolygon(PointList i_points) {
		// TODO implement method OptimizingGraphics.drawPolygon

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawPolyline(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolyline(PointList i_points) {
		// TODO implement method OptimizingGraphics.drawPolyline

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawRectangle(int, int, int, int)
	 */
	@Override
	public void drawRectangle(int i_x, int i_y, int i_width, int i_height) {
		// TODO implement method OptimizingGraphics.drawRectangle

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle, int, int)
	 */
	@Override
	public void drawRoundRectangle(Rectangle i_r, int i_arcWidth,
		int i_arcHeight) {
		// TODO implement method OptimizingGraphics.drawRoundRectangle

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawString(java.lang.String, int, int)
	 */
	@Override
	public void drawString(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.drawString

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#drawText(java.lang.String, int, int)
	 */
	@Override
	public void drawText(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.drawText

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int i_x, int i_y, int i_w, int i_h, int i_offset,
		int i_length) {
		// TODO implement method OptimizingGraphics.fillArc

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillGradient(int, int, int, int, boolean)
	 */
	@Override
	public void fillGradient(int i_x, int i_y, int i_w, int i_h,
		boolean i_vertical) {
		// TODO implement method OptimizingGraphics.fillGradient

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int i_x, int i_y, int i_w, int i_h) {
		// TODO implement method OptimizingGraphics.fillOval

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void fillPolygon(PointList i_points) {
		// TODO implement method OptimizingGraphics.fillPolygon

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillRectangle(int, int, int, int)
	 */
	@Override
	public void fillRectangle(int i_x, int i_y, int i_width, int i_height) {
		// TODO implement method OptimizingGraphics.fillRectangle

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle, int, int)
	 */
	@Override
	public void fillRoundRectangle(Rectangle i_r, int i_arcWidth,
		int i_arcHeight) {
		// TODO implement method OptimizingGraphics.fillRoundRectangle

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillString(java.lang.String, int, int)
	 */
	@Override
	public void fillString(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.fillString

	}

	/** 
	 * {@inheritDoc}
	 * @see org.eclipse.draw2d.Graphics#fillText(java.lang.String, int, int)
	 */
	@Override
	public void fillText(String i_s, int i_x, int i_y) {
		// TODO implement method OptimizingGraphics.fillText

	}

}
