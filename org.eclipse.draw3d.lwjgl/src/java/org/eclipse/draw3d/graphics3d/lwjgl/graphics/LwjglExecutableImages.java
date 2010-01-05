/*******************************************************************************
 * Copyright (c) 2010 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d.lwjgl.graphics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.primitive.ImagePrimitive;
import org.eclipse.draw3d.graphics.optimizer.primitive.Primitive;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.util.Draw3DCache;
import org.eclipse.draw3d.util.RectanglePacker;
import org.eclipse.draw3d.util.ImageConverter.ConversionSpecs;
import org.eclipse.draw3d.util.converter.BufferInfo;
import org.eclipse.draw3d.util.converter.ImageConverter;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * LwjglExecutableImages There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.01.2010
 */
public class LwjglExecutableImages extends LwjglExecutableVBO {

	private static final Logger log =
		Logger.getLogger(LwjglExecutableImages.class.getName());

	private FloatBuffer m_coordBuffer;

	private Image m_texture;

	private int m_textureId;

	private int m_coordBufferId;

	private int m_numQuads;

	private void drawImage(Image i_sourceImage, Rectangle i_sourceClip,
		ImageData i_targetData, Point i_targetPosition) {

		ImageData sourceData = i_sourceImage.getImageData();
		for (int y = 0; y < i_sourceClip.height; y++)
			for (int x = 0; x < i_sourceClip.width; x++) {
				int pixel =
					sourceData.getPixel(x + i_sourceClip.x, y + i_sourceClip.y);
				RGB rgb = sourceData.palette.getRGB(pixel);

				pixel = i_targetData.palette.getPixel(rgb);
				i_targetData.setPixel(i_targetPosition.x + x,
					i_targetPosition.y + y, pixel);

				int alpha = sourceData.getAlpha(x, y);
				i_targetData.setAlpha(i_targetPosition.x + x,
					i_targetPosition.y + y, alpha);
			}
	}

	public LwjglExecutableImages(PrimitiveSet i_primitives) {

		super(i_primitives);

		m_numQuads = i_primitives.getSize();
		m_coordBuffer = BufferUtils.createFloatBuffer(4 * 2 * m_numQuads);

		RectanglePacker<ImagePrimitive> packer =
			new RectanglePacker<ImagePrimitive>();

		for (Primitive primitive : i_primitives.getPrimitives()) {
			ImagePrimitive imagePrimitive = (ImagePrimitive) primitive;
			Rectangle source = imagePrimitive.getSource();

			packer.add(source.width, source.height, imagePrimitive);
		}

		packer.pack();

		Device device = Display.getCurrent();
		int tw = packer.getLength();
		int th = packer.getLength();

		ImageData textureData =
			new ImageData(tw, th, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));

		Point p = Draw3DCache.getPoint();
		try {
			for (Primitive primitive : i_primitives.getPrimitives()) {
				ImagePrimitive imagePrimitive = (ImagePrimitive) primitive;
				Image image = imagePrimitive.getImage();
				Rectangle source = imagePrimitive.getSource();

				packer.getPosition(imagePrimitive, p);
				drawImage(image, source, textureData, p);

				addTextureCoordinates(tw, th, p.x, p.y, source.width,
					source.height);
			}

			m_texture = new Image(device, textureData);
		} finally {
			Draw3DCache.returnPoint(p);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#preExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void preExecute(Graphics3D i_g3d) {

		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureId);

		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_coordBufferId);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#postExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void postExecute(Graphics3D i_g3d) {

		GL15.glBindBuffer(GL11.GL_TEXTURE_COORD_ARRAY, 0);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		GL11.glPopAttrib();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#dispose(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	public void dispose(Graphics3D i_g3d) {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.put(0, m_coordBufferId);
			idBuffer.rewind();
			GL15.glDeleteBuffers(idBuffer);
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}

		if (m_texture != null)
			m_texture.dispose();

		super.dispose(i_g3d);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	public void initialize(Graphics3D i_g3d) {

		super.initialize(i_g3d);

		initializeCoordBuffer();
		initializeTexture();
	}

	private void initializeTexture() {

		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		try {
			int w = m_texture.getBounds().width;
			int h = m_texture.getBounds().height;

			ConversionSpecs specs = new ConversionSpecs();
			specs.foregroundAlpha = 255;
			specs.textureWidth = w;
			specs.textureHeight = h;
			specs.clip = new org.eclipse.swt.graphics.Rectangle(0, 0, w, h);

			BufferInfo info =
				new BufferInfo(w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 1);

			ByteBuffer buffer = Draw3DCache.getByteBuffer(info.getSize());
			IntBuffer nameBuffer = Draw3DCache.getIntBuffer(1);
			try {
				ImageConverter converter = ImageConverter.getInstance();
				buffer =
					converter.imageToBuffer(m_texture, info, buffer, false);

				nameBuffer.rewind();
				GL11.glGenTextures(nameBuffer);

				m_textureId = nameBuffer.get(0);

				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureId);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

				GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
					GL11.GL_REPLACE);

				m_texture.dispose();
				m_texture = null;
			} finally {
				Draw3DCache.returnIntBuffer(nameBuffer);
				Draw3DCache.returnByteBuffer(buffer);
			}
		} finally {
			GL11.glPopAttrib();
		}
	}

	private void initializeCoordBuffer() {

		IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
		try {
			idBuffer.rewind();
			GL15.glGenBuffers(idBuffer);
			m_coordBufferId = idBuffer.get(0);

			m_coordBuffer.rewind();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_coordBufferId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, m_coordBuffer,
				GL15.GL_STATIC_DRAW);

			m_coordBuffer = null;
		} finally {
			Draw3DCache.returnIntBuffer(idBuffer);
		}
	}

	private void addTextureCoordinates(int i_tw, int i_th, int i_x, int i_y) {

		float s = i_x / (float) i_tw;
		float t = i_y / (float) i_th;

		m_coordBuffer.put(s);
		m_coordBuffer.put(t);
	}

	private void addTextureCoordinates(int i_tw, int i_th, int i_x, int i_y,
		int i_w, int i_h) {

		addTextureCoordinates(i_tw, i_th, i_x, i_y);
		addTextureCoordinates(i_tw, i_th, i_x, i_y + i_h);
		addTextureCoordinates(i_tw, i_th, i_x + i_w, i_y + i_h);
		addTextureCoordinates(i_tw, i_th, i_x + i_w, i_y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglExecutableVBO#doExecute(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void doExecute(Graphics3D i_g3d) {

		i_g3d.glColor4f(1, 1, 1, 1);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4 * m_numQuads);
	}

}
