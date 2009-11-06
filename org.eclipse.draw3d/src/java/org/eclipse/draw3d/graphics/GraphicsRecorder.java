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
package org.eclipse.draw3d.graphics;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.TextLayout;

/**
 * GraphicsRecorder There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.11.2009
 */
public class GraphicsRecorder extends StatefulGraphics {

	private static class MethodCall {

		private Object[] m_args;

		private Method m_method;

		/**
		 * @param i_method
		 * @param i_args
		 */
		public MethodCall(Method i_method, Object[] i_args) {

			m_method = i_method;
			m_args = i_args;
		}

		public void execute(Object i_target) {

			try {
				m_method.invoke(i_target, m_args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static Method m_clipRect;

	private static Method m_dispose;

	private static Method m_drawArc;

	private static Method m_drawFocus;

	private static Method m_drawImageLong;

	private static Method m_drawImageShort;

	private static Method m_drawLine;

	private static Method m_drawOval;

	private static Method m_drawPath;

	private static Method m_drawPoint;

	private static Method m_drawPolygon;

	private static Method m_drawPolygonWithArray;

	private static Method m_drawPolyline;

	private static Method m_drawPolylineWithArray;

	private static Method m_drawRectangle;

	private static Method m_drawRoundRectangle;

	private static Method m_drawString;

	private static Method m_drawTextLayout;

	private static Method m_drawTextLong;

	private static Method m_drawTextShort;

	private static Method m_fillArc;

	private static Method m_fillGradient;

	private static Method m_fillOval;

	private static Method m_fillPath;

	private static Method m_fillPolygon;

	private static Method m_fillPolygonWithArray;

	private static Method m_fillRectangle;

	private static Method m_fillRoundRectangle;

	private static Method m_fillString;

	private static Method m_fillText;

	private static Method m_popState;

	private static Method m_pushState;

	private static Method m_restoreState;

	private static Method m_rotate;

	private static Method m_scale;

	private static Method m_scaleUniform;

	private static Method m_setAdvanced;

	private static Method m_setAlpha;

	private static Method m_setAntialias;

	private static Method m_setBackgroundColor;

	private static Method m_setBackgroundPattern;

	private static Method m_setClip;

	private static Method m_setClipWithPath;

	private static Method m_setFillRule;

	private static Method m_setFont;

	private static Method m_setForegroundColor;

	private static Method m_setForegroundPattern;

	private static Method m_setInterpolation;

	private static Method m_setLineAttributes;

	private static Method m_setLineCap;

	private static Method m_setLineDashWithFloatArray;

	private static Method m_setLineDashWithIntArray;

	private static Method m_setLineJoin;

	private static Method m_setLineMiterLimit;

	private static Method m_setLineStyle;

	private static Method m_setLineWidth;

	private static Method m_setLineWidthFloat;

	private static Method m_setTextAntialias;

	private static Method m_setXORMode;

	private static Method m_shear;

	private static Method m_translate;

	private static Method m_translateWithFloat;

	static {

		Class<Graphics> cls = Graphics.class;
		try {
			m_clipRect = cls.getMethod("clipRect", Rectangle.class);
			m_dispose = cls.getMethod("dispose");
			m_drawArc =
				cls.getMethod("drawArc", Integer.class, Integer.class,
					Integer.class, Integer.class, Integer.class, Integer.class);
			m_drawFocus =
				cls.getMethod("drawFocus", Integer.class, Integer.class,
					Integer.class, Integer.class);
			m_drawImageShort =
				cls.getMethod("drawImage", Image.class, Integer.class,
					Integer.class);
			m_drawImageLong =
				cls.getMethod("drawImage", Image.class, Integer.class,
					Integer.class, Integer.class, Integer.class, Integer.class,
					Integer.class, Integer.class, Integer.class);
			m_drawLine =
				cls.getMethod("drawLine", Integer.class, Integer.class,
					Integer.class, Integer.class);
			m_drawOval =
				cls.getMethod("drawOval", Integer.class, Integer.class,
					Integer.class, Integer.class);
			m_drawPath = cls.getMethod("drawPath", Path.class);
			m_drawPoint =
				cls.getMethod("drawPoint", Integer.class, Integer.class);
			m_drawPolygonWithArray = cls.getMethod("drawPolygon", int[].class);
			m_drawPolygon = cls.getMethod("drawPolygon", PointList.class);
			m_drawPolylineWithArray =
				cls.getMethod("drawPolyline", int[].class);
			m_drawPolyline = cls.getMethod("drawPolyline", PointList.class);
			m_drawRectangle =
				cls.getMethod("drawRectangle", Integer.class, Integer.class,
					Integer.class, Integer.class);
			m_drawRoundRectangle =
				cls.getMethod("drawRoundRectangle", Rectangle.class,
					Integer.class, Integer.class);
			m_drawString =
				cls.getMethod("drawString", String.class, Integer.class,
					Integer.class);
			m_drawTextShort =
				cls.getMethod("drawText", String.class, Integer.class,
					Integer.class);
			m_drawTextLong =
				cls.getMethod("drawText", String.class, Integer.class,
					Integer.class, Integer.class);
			m_drawTextLayout =
				cls.getMethod("drawTextLayout", TextLayout.class,
					Integer.class, Integer.class, Integer.class, Integer.class,
					Color.class, Color.class);
			m_fillArc =
				cls.getMethod("fillArc", Integer.class, Integer.class,
					Integer.class, Integer.class, Integer.class, Integer.class);
			m_fillGradient =
				cls.getMethod("fillGradient", Integer.class, Integer.class,
					Integer.class, Integer.class, Boolean.class);
			m_fillOval =
				cls.getMethod("fillOval", Integer.class, Integer.class,
					Integer.class, Integer.class);
			m_fillPath = cls.getMethod("fillPath", Path.class);
			m_fillPolygonWithArray = cls.getMethod("fillPolygon", int[].class);
			m_fillPolygon = cls.getMethod("fillPolygon", PointList.class);
			m_fillRectangle =
				cls.getMethod("fillRectangle", Integer.class, Integer.class,
					Integer.class, Integer.class);
			m_fillRoundRectangle =
				cls.getMethod("fillRoundRectangle", Rectangle.class,
					Integer.class, Integer.class);
			m_fillString =
				cls.getMethod("fillString", String.class, Integer.class,
					Integer.class);
			m_fillText =
				cls.getMethod("fillText", String.class, Integer.class,
					Integer.class);
			m_popState = cls.getMethod("popState");
			m_pushState = cls.getMethod("pushState");
			m_restoreState = cls.getMethod("restoreState");
			m_rotate = cls.getMethod("rotate", Float.class);
			m_scaleUniform = cls.getMethod("scale", Double.class);
			m_scale = cls.getMethod("scale", Float.class, Float.class);
			m_setAdvanced = cls.getMethod("setAdvanced", Boolean.class);
			m_setAlpha = cls.getMethod("setAlpha", Integer.class);
			m_setAntialias = cls.getMethod("setAntialias", Integer.class);
			m_setBackgroundColor =
				cls.getMethod("setBackgroundColor", Color.class);
			m_setBackgroundPattern =
				cls.getMethod("setBackgroundPattern", Pattern.class);
			m_setClipWithPath = cls.getMethod("setClip", Path.class);
			m_setClip = cls.getMethod("setClip", Rectangle.class);
			m_setFillRule = cls.getMethod("setFillRule", Integer.class);
			m_setFont = cls.getMethod("setFont", Font.class);
			m_setForegroundColor =
				cls.getMethod("setForegroundColor", Color.class);
			m_setForegroundPattern =
				cls.getMethod("setForegroundPattern", Pattern.class);
			m_setInterpolation =
				cls.getMethod("setInterpolation", Integer.class);
			m_setLineAttributes =
				cls.getMethod("setLineAttributes", LineAttributes.class);
			m_setLineCap = cls.getMethod("setLineCap", Integer.class);
			m_setLineDashWithFloatArray =
				cls.getMethod("setLineDash", float[].class);
			m_setLineDashWithIntArray =
				cls.getMethod("setLineDash", int[].class);
			m_setLineJoin = cls.getMethod("setLineJoin", Integer.class);
			m_setLineMiterLimit =
				cls.getMethod("setLineMiterLimit", Float.class);
			m_setLineStyle = cls.getMethod("setLineStyle", Integer.class);
			m_setLineWidth = cls.getMethod("setLineWidth", Integer.class);
			m_setLineWidthFloat =
				cls.getMethod("setLineWidthFloat", Float.class);
			m_setTextAntialias =
				cls.getMethod("setTextAntialias", Integer.class);
			m_setXORMode = cls.getMethod("setXORMode", Boolean.class);
			m_shear = cls.getMethod("shear", Float.class, Float.class);
			m_translateWithFloat =
				cls.getMethod("translate", Float.class, Float.class);
			m_translate =
				cls.getMethod("translate", Integer.class, Integer.class);
		} catch (Exception e) {
			// TODO Implement catch block for SecurityException
			e.printStackTrace();
		}
	}

	private List<MethodCall> m_calls = new LinkedList<MethodCall>();

	/**
	 * Clears the recorded method calls.
	 */
	public void clear() {

		m_calls.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#clipRect(org.eclipse.draw2d.geometry.Rectangle)
	 */
	@Override
	public void clipRect(Rectangle i_r) {

		super.clipRect(i_r);
		record(m_clipRect, i_r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#dispose()
	 */
	@Override
	public void dispose() {

		super.dispose();
		record(m_dispose);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int i_x, int i_y, int i_w, int i_h, int i_offset,
		int i_length) {

		record(m_drawArc, i_x, i_y, i_w, i_h, i_offset, i_length);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawFocus(int, int, int, int)
	 */
	@Override
	public void drawFocus(int i_x, int i_y, int i_w, int i_h) {

		record(m_drawFocus, i_x, i_y, i_w, i_h);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image,
	 *      int, int)
	 */
	@Override
	public void drawImage(Image i_srcImage, int i_x, int i_y) {

		record(m_drawImageShort, i_srcImage, i_x, i_y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image,
	 *      int, int, int, int, int, int, int, int)
	 */
	@Override
	public void drawImage(Image i_srcImage, int i_x1, int i_y1, int i_w1,
		int i_h1, int i_x2, int i_y2, int i_w2, int i_h2) {

		record(m_drawImageLong, i_srcImage, i_x1, i_y1, i_w1, i_h1, i_x2, i_y2,
			i_w2, i_h2);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int i_x1, int i_y1, int i_x2, int i_y2) {

		record(m_drawLine, i_x1, i_y1, i_x2, i_y2);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int i_x, int i_y, int i_w, int i_h) {

		record(m_drawOval, i_x, i_y, i_w, i_h);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPath(org.eclipse.swt.graphics.Path)
	 */
	@Override
	public void drawPath(Path i_path) {

		record(m_drawPath, i_path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPoint(int, int)
	 */
	@Override
	public void drawPoint(int i_x, int i_y) {

		record(m_drawPoint, i_x, i_y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPolygon(int[])
	 */
	@Override
	public void drawPolygon(int[] i_points) {

		record(m_drawPolygonWithArray, i_points);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolygon(PointList i_points) {

		record(m_drawPolygon, i_points);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPolyline(int[])
	 */
	@Override
	public void drawPolyline(int[] i_points) {

		record(m_drawPolylineWithArray, i_points);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawPolyline(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolyline(PointList i_points) {

		record(m_drawPolyline, i_points);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawRectangle(int, int, int, int)
	 */
	@Override
	public void drawRectangle(int i_x, int i_y, int i_width, int i_height) {

		record(m_drawRectangle, i_x, i_y, i_width, i_height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void drawRoundRectangle(Rectangle i_r, int i_arcWidth,
		int i_arcHeight) {

		record(m_drawRoundRectangle, i_r, i_arcWidth, i_arcHeight);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawString(java.lang.String, int, int)
	 */
	@Override
	public void drawString(String i_s, int i_x, int i_y) {

		record(m_drawString, i_s, i_x, i_y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawText(java.lang.String, int, int)
	 */
	@Override
	public void drawText(String i_s, int i_x, int i_y) {

		record(m_drawTextShort, i_s, i_x, i_y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawText(java.lang.String, int, int,
	 *      int)
	 */
	@Override
	public void drawText(String i_s, int i_x, int i_y, int i_style) {

		record(m_drawTextLong, i_s, i_x, i_y, i_style);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#drawTextLayout(org.eclipse.swt.graphics.TextLayout,
	 *      int, int, int, int, org.eclipse.swt.graphics.Color,
	 *      org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void drawTextLayout(TextLayout i_layout, int i_x, int i_y,
		int i_selectionStart, int i_selectionEnd, Color i_selectionForeground,
		Color i_selectionBackground) {

		record(m_drawTextLayout, i_layout, i_x, i_y, i_selectionStart,
			i_selectionEnd, i_selectionForeground, i_selectionBackground);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int i_x, int i_y, int i_w, int i_h, int i_offset,
		int i_length) {

		record(m_fillArc, i_x, i_y, i_w, i_h, i_offset, i_length);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillGradient(int, int, int, int,
	 *      boolean)
	 */
	@Override
	public void fillGradient(int i_x, int i_y, int i_w, int i_h,
		boolean i_vertical) {

		record(m_fillGradient, i_x, i_y, i_w, i_h, i_vertical);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int i_x, int i_y, int i_w, int i_h) {

		record(m_fillOval, i_x, i_y, i_w, i_h);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillPath(org.eclipse.swt.graphics.Path)
	 */
	@Override
	public void fillPath(Path i_path) {

		record(m_fillPath, i_path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillPolygon(int[])
	 */
	@Override
	public void fillPolygon(int[] i_points) {

		record(m_fillPolygonWithArray, i_points);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void fillPolygon(PointList i_points) {

		record(m_fillPolygon, i_points);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillRectangle(int, int, int, int)
	 */
	@Override
	public void fillRectangle(int i_x, int i_y, int i_width, int i_height) {

		record(m_fillRectangle, i_x, i_y, i_width, i_height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void fillRoundRectangle(Rectangle i_r, int i_arcWidth,
		int i_arcHeight) {

		record(m_fillRoundRectangle, i_r, i_arcWidth, i_arcHeight);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillString(java.lang.String, int, int)
	 */
	@Override
	public void fillString(String i_s, int i_x, int i_y) {

		record(m_fillString, i_s, i_x, i_y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#fillText(java.lang.String, int, int)
	 */
	@Override
	public void fillText(String i_s, int i_x, int i_y) {

		record(m_fillText, i_s, i_x, i_y);
	}

	/**
	 * Play the recorded method calls against the given target.
	 * 
	 * @param i_target the target
	 */
	public void playback(Graphics i_target) {

		if (i_target == null)
			throw new NullPointerException("i_target must not be null");

		for (MethodCall call : m_calls)
			call.execute(i_target);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#popState()
	 */
	@Override
	public void popState() {

		super.popState();
		record(m_popState);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#pushState()
	 */
	@Override
	public void pushState() {

		super.pushState();
		record(m_pushState);
	}

	private void record(Method i_method, Object... args) {

		m_calls.add(new MethodCall(i_method, args));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#restoreState()
	 */
	@Override
	public void restoreState() {

		super.restoreState();
		record(m_restoreState);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#rotate(float)
	 */
	@Override
	public void rotate(float i_degrees) {

		super.rotate(i_degrees);
		record(m_rotate, i_degrees);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.StatefulGraphics#scale(double)
	 */
	@Override
	public void scale(double i_amount) {

		super.scale(i_amount);
		record(m_scaleUniform, i_amount);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#scale(float, float)
	 */
	@Override
	public void scale(float i_horizontal, float i_vertical) {

		super.scale(i_horizontal, i_vertical);
		record(m_scale, i_horizontal, i_vertical);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setAdvanced(boolean)
	 */
	@Override
	public void setAdvanced(boolean i_advanced) {

		super.setAdvanced(i_advanced);
		record(m_setAdvanced, i_advanced);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setAlpha(int)
	 */
	@Override
	public void setAlpha(int i_alpha) {

		super.setAlpha(i_alpha);
		record(m_setAlpha, i_alpha);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setAntialias(int)
	 */
	@Override
	public void setAntialias(int i_antialias) {

		super.setAntialias(i_antialias);
		record(m_setAntialias, i_antialias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackgroundColor(Color i_backgroundColor) {

		super.setBackgroundColor(i_backgroundColor);
		record(m_setBackgroundColor, i_backgroundColor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setBackgroundPattern(org.eclipse.swt.graphics.Pattern)
	 */
	@Override
	public void setBackgroundPattern(Pattern i_backgroundPattern) {

		super.setBackgroundPattern(i_backgroundPattern);
		record(m_setBackgroundPattern, i_backgroundPattern);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setClip(org.eclipse.swt.graphics.Path)
	 */
	@Override
	public void setClip(Path i_path) {

		super.setClip(i_path);
		record(m_setClipWithPath, i_path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setClip(org.eclipse.draw2d.geometry.Rectangle)
	 */
	@Override
	public void setClip(Rectangle i_r) {

		super.setClip(i_r);
		record(m_setClip, i_r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setFillRule(int)
	 */
	@Override
	public void setFillRule(int i_rule) {

		super.setFillRule(i_rule);
		record(m_setFillRule, i_rule);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(Font i_f) {

		super.setFont(i_f);
		record(m_setFont, i_f);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForegroundColor(Color i_foregroundColor) {

		super.setForegroundColor(i_foregroundColor);
		record(m_setForegroundColor, i_foregroundColor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setForegroundPattern(org.eclipse.swt.graphics.Pattern)
	 */
	@Override
	public void setForegroundPattern(Pattern i_pattern) {

		super.setForegroundPattern(i_pattern);
		record(m_setForegroundPattern, i_pattern);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setInterpolation(int)
	 */
	@Override
	public void setInterpolation(int i_interpolation) {

		super.setInterpolation(i_interpolation);
		record(m_setInterpolation, i_interpolation);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.StatefulGraphics#setLineAttributes(org.eclipse.swt.graphics.LineAttributes)
	 */
	@Override
	public void setLineAttributes(LineAttributes i_attributes) {

		super.setLineAttributes(i_attributes);
		record(m_setLineAttributes, i_attributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setLineCap(int)
	 */
	@Override
	public void setLineCap(int i_cap) {

		super.setLineCap(i_cap);
		record(m_setLineCap, i_cap);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics.StatefulGraphics#setLineDash(float[])
	 */
	@Override
	public void setLineDash(float[] i_dash) {

		super.setLineDash(i_dash);
		record(m_setLineDashWithFloatArray, i_dash);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setLineDash(int[])
	 */
	@Override
	public void setLineDash(int[] i_dash) {

		super.setLineDash(i_dash);
		record(m_setLineDashWithIntArray, i_dash);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setLineJoin(int)
	 */
	@Override
	public void setLineJoin(int i_join) {

		super.setLineJoin(i_join);
		record(m_setLineJoin, i_join);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setLineMiterLimit(float)
	 */
	@Override
	public void setLineMiterLimit(float i_miterLimit) {

		super.setLineMiterLimit(i_miterLimit);
		record(m_setLineMiterLimit, i_miterLimit);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setLineStyle(int)
	 */
	@Override
	public void setLineStyle(int i_style) {

		super.setLineStyle(i_style);
		record(m_setLineStyle, i_style);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setLineWidth(int)
	 */
	@Override
	public void setLineWidth(int i_width) {

		super.setLineWidth(i_width);
		record(m_setLineWidth, i_width);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setLineWidthFloat(float)
	 */
	@Override
	public void setLineWidthFloat(float i_width) {

		super.setLineWidthFloat(i_width);
		record(m_setLineWidthFloat, i_width);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setTextAntialias(int)
	 */
	@Override
	public void setTextAntialias(int i_value) {

		super.setTextAntialias(i_value);
		record(m_setTextAntialias, i_value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#setXORMode(boolean)
	 */
	@Override
	public void setXORMode(boolean i_b) {

		super.setXORMode(i_b);
		record(m_setXORMode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#shear(float, float)
	 */
	@Override
	public void shear(float i_horz, float i_vert) {

		super.shear(i_horz, i_vert);
		record(m_shear, i_horz, i_vert);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#translate(float, float)
	 */
	@Override
	public void translate(float i_dx, float i_dy) {

		super.translate(i_dx, i_dy);
		record(m_translateWithFloat, i_dx, i_dy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Graphics#translate(int, int)
	 */
	@Override
	public void translate(int i_dx, int i_dy) {

		super.translate(i_dx, i_dy);
		record(m_translate, i_dx, i_dy);
	}
}
