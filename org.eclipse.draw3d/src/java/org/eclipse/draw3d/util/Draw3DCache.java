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
package org.eclipse.draw3d.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.geometry.Math3DCache;

/**
 * Extends {@link org.eclipse.draw3d.geometry.Math3DCache} with support for
 * draw2d primitives. This way, only the plugin containing this class needs to
 * depend on draw2d.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 29.07.2009
 */
public class Draw3DCache extends Math3DCache {

	/**
	 * Holds a buffer and information about its capacity.
	 * 
	 * @author Kristian Duske
	 * @version $Revision$
	 * @since 05.08.2009
	 */
	private static class BufferHolder {

		private Buffer m_buffer;

		private int m_capacity;

		public BufferHolder(Buffer i_buffer) {

			m_buffer = i_buffer;
			m_capacity = m_buffer.capacity();
		}

		public BufferHolder(int i_capacity) {

			m_capacity = i_capacity;
		}

		public Buffer getBuffer() {

			return m_buffer;
		}

		public int getCapacity() {

			return m_capacity;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			return "BufferHolder [capacity=" + m_capacity + "]";
		}
	}

	private static final Comparator<BufferHolder> m_bufferComparator =
		new Comparator<BufferHolder>() {
			public int compare(BufferHolder i_o1, BufferHolder i_o2) {
				if (i_o1.getCapacity() < i_o2.getCapacity())
					return -1;
				return 0;
			}
		};

	private static final List<BufferHolder> m_byteBuffer =
		new ArrayList<BufferHolder>();

	private static final Queue<Dimension> m_dimension =
		new LinkedList<Dimension>();

	private static final List<BufferHolder> m_doubleBuffer =
		new ArrayList<BufferHolder>();

	private static final List<BufferHolder> m_floatBuffer =
		new ArrayList<BufferHolder>();

	private static final List<BufferHolder> m_intBuffer =
		new ArrayList<BufferHolder>();

	private static final Queue<Point> m_point = new LinkedList<Point>();

	private static Queue<Rectangle> m_rectangle = new LinkedList<Rectangle>();

	private static Buffer doGetBuffer(List<BufferHolder> i_buffers,
		BufferHolder i_holder) {

		int index =
			Collections.binarySearch(i_buffers, i_holder, m_bufferComparator);

		if (index >= 0) {
			BufferHolder holder = i_buffers.remove(index);
			Buffer buffer = holder.getBuffer();
			buffer.limit(i_holder.getCapacity());
			return buffer;
		}

		return null;
	}

	private static void doReturnBuffer(List<BufferHolder> i_buffers,
		Buffer i_buffer) {

		BufferHolder holder = new BufferHolder(i_buffer);
		int index =
			Collections.binarySearch(i_buffers, holder, m_bufferComparator);

		if (index < 0)
			index = -index - 1;

		i_buffers.add(index, holder);
	}

	/**
	 * Returns a buffer with at least the given capacity. The returned buffer
	 * will be limited to the given capacity, but it will not be rewound.
	 * 
	 * @param i_capacity the desired minimum capacity
	 * @return a buffer with the desired minimum capacity
	 */
	public static ByteBuffer getByteBuffer(int i_capacity) {

		BufferHolder holder = new BufferHolder(i_capacity);
		ByteBuffer buffer;
		if (m_synchronized)
			synchronized (m_byteBuffer) {
				buffer = (ByteBuffer) doGetBuffer(m_byteBuffer, holder);
			}
		else
			buffer = (ByteBuffer) doGetBuffer(m_byteBuffer, holder);

		if (buffer == null)
			buffer = BufferUtils.createByteBuffer(i_capacity);
		else
			buffer.limit(i_capacity);

		return buffer;
	}

	/**
	 * Returns a cached {@link Dimension}.
	 * 
	 * @return a cached dimension
	 */
	public static Dimension getDimension() {

		if (m_synchronized) {
			synchronized (m_dimension) {
				if (m_dimension.isEmpty())
					return new Dimension();
				else
					return m_dimension.remove();
			}
		} else {
			if (m_dimension.isEmpty())
				return new Dimension();
			else
				return m_dimension.remove();
		}
	}

	/**
	 * Returns a buffer with at least the given capacity. The returned buffer
	 * will be limited to the given capacity, but it will not be rewound.
	 * 
	 * @param i_capacity the desired minimum capacity
	 * @return a buffer with the desired minimum capacity
	 */
	public static DoubleBuffer getDoubleBuffer(int i_capacity) {

		BufferHolder holder = new BufferHolder(i_capacity);
		DoubleBuffer buffer;
		if (m_synchronized)
			synchronized (m_doubleBuffer) {
				buffer = (DoubleBuffer) doGetBuffer(m_doubleBuffer, holder);
			}
		else
			buffer = (DoubleBuffer) doGetBuffer(m_doubleBuffer, holder);

		if (buffer == null)
			buffer = BufferUtils.createDoubleBuffer(i_capacity);
		else
			buffer.limit(i_capacity);

		return buffer;
	}

	/**
	 * Returns a buffer with at least the given capacity. The returned buffer
	 * will be limited to the given capacity, but it will not be rewound.
	 * 
	 * @param i_capacity the desired minimum capacity
	 * @return a buffer with the desired minimum capacity
	 */
	public static FloatBuffer getFloatBuffer(int i_capacity) {

		BufferHolder holder = new BufferHolder(i_capacity);
		FloatBuffer buffer;
		if (m_synchronized)
			synchronized (m_floatBuffer) {
				buffer = (FloatBuffer) doGetBuffer(m_floatBuffer, holder);
			}
		else
			buffer = (FloatBuffer) doGetBuffer(m_floatBuffer, holder);

		if (buffer == null)
			buffer = BufferUtils.createFloatBuffer(i_capacity);
		else
			buffer.limit(i_capacity);

		return buffer;
	}

	/**
	 * Returns a buffer with at least the given capacity. The returned buffer
	 * will be limited to the given capacity, but it will not be rewound.
	 * 
	 * @param i_capacity the desired minimum capacity
	 * @return a buffer with the desired minimum capacity
	 */
	public static IntBuffer getIntBuffer(int i_capacity) {

		BufferHolder holder = new BufferHolder(i_capacity);
		IntBuffer buffer;
		if (m_synchronized)
			synchronized (m_intBuffer) {
				buffer = (IntBuffer) doGetBuffer(m_intBuffer, holder);
			}
		else
			buffer = (IntBuffer) doGetBuffer(m_intBuffer, holder);

		if (buffer == null)
			buffer = BufferUtils.createIntBuffer(i_capacity);
		else
			buffer.limit(i_capacity);

		return buffer;
	}

	/**
	 * Returns a cached {@link Point}.
	 * 
	 * @return a cached point
	 */
	public static Point getPoint() {

		if (m_synchronized) {
			synchronized (m_point) {
				if (m_point.isEmpty())
					return new Point();
				else
					return m_point.remove();
			}
		} else {
			if (m_point.isEmpty())
				return new Point();
			else
				return m_point.remove();
		}
	}

	/**
	 * Returns a cached {@link Rectangle}.
	 * 
	 * @return a cached rectangle
	 */
	public static Rectangle getRectangle() {

		if (m_synchronized) {
			synchronized (m_rectangle) {
				if (m_rectangle.isEmpty())
					return new Rectangle();
				else
					return m_rectangle.remove();
			}
		} else {
			if (m_rectangle.isEmpty())
				return new Rectangle();
			else
				return m_rectangle.remove();
		}
	}

	/**
	 * Returns the given byte buffers to the cache. If any of the given buffers
	 * is <code>null</code>, it is ignored.
	 * 
	 * @param i_bs the buffers to return
	 */
	public static void returnByteBuffer(ByteBuffer... i_bs) {

		if (m_synchronized) {
			synchronized (m_floatBuffer) {
				for (ByteBuffer b : i_bs)
					if (b != null)
						doReturnBuffer(m_byteBuffer, b);
			}
		} else {
			for (ByteBuffer b : i_bs)
				if (b != null)
					doReturnBuffer(m_byteBuffer, b);
		}
	}

	/**
	 * Returns the given dimensions to the cache. If any of the given dimensions
	 * is <code>null</code>, it is ignored.
	 * 
	 * @param i_ds the dimensions to return
	 */
	public static void returnDimension(Dimension... i_ds) {

		if (m_synchronized)
			synchronized (m_dimension) {
				for (Dimension d : i_ds)
					if (d != null)
						m_dimension.offer(d);
			}
		else
			for (Dimension d : i_ds)
				if (d != null)
					m_dimension.offer(d);
	}

	/**
	 * Returns the given double buffers to the cache. If any of the given
	 * buffers is <code>null</code>, it is ignored.
	 * 
	 * @param i_bs the buffers to return
	 */
	public static void returnDoubleBuffer(DoubleBuffer... i_bs) {

		if (m_synchronized) {
			synchronized (m_doubleBuffer) {
				for (DoubleBuffer b : i_bs)
					if (b != null)
						doReturnBuffer(m_doubleBuffer, b);
			}
		} else {
			for (DoubleBuffer b : i_bs)
				if (b != null)
					doReturnBuffer(m_doubleBuffer, b);
		}
	}

	/**
	 * Returns the given float buffers to the cache. If any of the given buffers
	 * is <code>null</code>, it is ignored.
	 * 
	 * @param i_bs the buffers to return
	 */
	public static void returnFloatBuffer(FloatBuffer... i_bs) {

		if (m_synchronized) {
			synchronized (m_floatBuffer) {
				for (FloatBuffer b : i_bs)
					if (b != null)
						doReturnBuffer(m_floatBuffer, b);
			}
		} else {
			for (FloatBuffer b : i_bs)
				if (b != null)
					doReturnBuffer(m_floatBuffer, b);
		}
	}

	/**
	 * Returns the given int buffers to the cache. If any of the given buffers
	 * is <code>null</code>, it is ignored.
	 * 
	 * @param i_bs the buffers to return
	 */
	public static void returnIntBuffer(IntBuffer... i_bs) {

		if (m_synchronized) {
			synchronized (m_intBuffer) {
				for (IntBuffer b : i_bs)
					if (b != null)
						doReturnBuffer(m_intBuffer, b);
			}
		} else {
			for (IntBuffer b : i_bs)
				if (b != null)
					doReturnBuffer(m_intBuffer, b);
		}
	}

	/**
	 * Returns the given points to the cache. If any of the given points is
	 * <code>null</code>, it is ignored.
	 * 
	 * @param i_ps the points to return
	 */
	public static void returnPoint(Point... i_ps) {

		if (m_synchronized)
			synchronized (m_point) {
				for (Point p : i_ps)
					if (p != null)
						m_point.offer(p);
			}
		else
			for (Point p : i_ps)
				if (p != null)
					m_point.offer(p);
	}

	/**
	 * Returns the given rectangles to the cache. If any of the given rectangles
	 * is <code>null</code>, it is ignored.
	 * 
	 * @param i_rs the rectangles to return
	 */
	public static void returnRectangle(Rectangle... i_rs) {

		if (m_synchronized)
			synchronized (m_rectangle) {
				for (Rectangle r : i_rs)
					if (r != null)
						m_rectangle.offer(r);
			}
		else
			for (Rectangle r : i_rs)
				if (r != null)
					m_rectangle.offer(r);
	}
}
