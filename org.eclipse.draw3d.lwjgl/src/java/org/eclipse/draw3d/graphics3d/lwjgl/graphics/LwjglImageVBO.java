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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw3d.graphics.optimizer.PrimitiveSet;
import org.eclipse.draw3d.graphics.optimizer.classification.PrimitiveClass;
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
 * Vertex buffer object that renders images as textures. The images are combined
 * into a single texture.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 05.01.2010
 */
public class LwjglImageVBO extends LwjglVBO {

	private static final int VERTEX_SIZE = (2 + 2) * 4;

	private PrimitiveSet m_primitives;

	private int m_textureId;

	private int m_vertexCount;

	/**
	 * Creates a new VBO that renders the given image primites.
	 * 
	 * @param i_primitives the image primitives to render
	 */
	public LwjglImageVBO(PrimitiveSet i_primitives) {

		if (i_primitives == null)
			throw new NullPointerException("i_primitives must not be null");

		if (i_primitives.getSize() == 0)
			throw new IllegalArgumentException(i_primitives
				+ " must not be empty");

		PrimitiveClass clazz = i_primitives.getPrimitiveClass();
		if (!clazz.isImage())
			throw new IllegalArgumentException(
i_primitives
				+ " does not contain images");

		m_primitives = i_primitives;
		m_vertexCount = m_primitives.getVertexCount();
	}

	private void addTextureCoordinates(FloatBuffer i_buffer, int i_tw,
		int i_th, int i_x, int i_y) {

		float s = i_x / (float) i_tw;
		float t = i_y / (float) i_th;

		i_buffer.put(s);
		i_buffer.put(t);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#cleanup(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void cleanup(Graphics3D i_g3d) {

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		GL11.glPopAttrib();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#dispose()
	 */
	@Override
	public void dispose() {

		if (m_textureId != 0) {
			IntBuffer idBuffer = Draw3DCache.getIntBuffer(1);
			try {
				idBuffer.put(0, m_textureId);
				idBuffer.rewind();
				GL11.glDeleteTextures(idBuffer);

				m_textureId = 0;
			} finally {
				Draw3DCache.returnIntBuffer(idBuffer);
			}
		}

		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#doRender(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void doRender(Graphics3D i_g3d) {

		i_g3d.glColor4f(1, 1, 1, 1);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, m_vertexCount);
	}

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

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#initialize(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	public void initialize(Graphics3D i_g3d) {

		RectanglePacker<ImagePrimitive> packer =
			new RectanglePacker<ImagePrimitive>();

		for (Primitive primitive : m_primitives.getPrimitives()) {
			ImagePrimitive imagePrimitive = (ImagePrimitive) primitive;
			Rectangle source = imagePrimitive.getSource();

			packer.add(source.width, source.height, imagePrimitive);
		}

		packer.pack();

		FloatBuffer buffer =
			BufferUtils.createFloatBuffer(VERTEX_SIZE * m_vertexCount);

		Device device = Display.getCurrent();
		int tw = packer.getLength();
		int th = packer.getLength();

		ImageData textureData =
			new ImageData(tw, th, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));

		Point p = Draw3DCache.getPoint();
		try {
			for (Primitive primitive : m_primitives.getPrimitives()) {
				ImagePrimitive imagePrimitive = (ImagePrimitive) primitive;
				Image image = imagePrimitive.getImage();
				Rectangle source = imagePrimitive.getSource();

				packer.getPosition(imagePrimitive, p);
				drawImage(image, source, textureData, p);

				float[] vertices = imagePrimitive.getVertices();
				buffer.put(vertices[0]);
				buffer.put(vertices[1]);
				addTextureCoordinates(buffer, tw, th, p.x, p.y);

				buffer.put(vertices[2]);
				buffer.put(vertices[3]);
				addTextureCoordinates(buffer, tw, th, p.x, p.y + source.height);

				buffer.put(vertices[4]);
				buffer.put(vertices[5]);
				addTextureCoordinates(buffer, tw, th, p.x + source.width, p.y
					+ source.height);

				buffer.put(vertices[6]);
				buffer.put(vertices[7]);
				addTextureCoordinates(buffer, tw, th, p.x + source.width, p.y);
			}

			uploadBuffer(buffer);

			Image textureImage = new Image(device, textureData);
			m_textureId = initializeTexture(textureImage);
			textureImage.dispose();
		} finally {
			Draw3DCache.returnPoint(p);
		}
	}

	private int initializeTexture(Image i_texture) {

		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		try {
			int w = i_texture.getBounds().width;
			int h = i_texture.getBounds().height;

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
					converter.imageToBuffer(i_texture, info, buffer, false);

				nameBuffer.rewind();
				GL11.glGenTextures(nameBuffer);

				int id = nameBuffer.get(0);

				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
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

				return id;
			} finally {
				Draw3DCache.returnIntBuffer(nameBuffer);
				Draw3DCache.returnByteBuffer(buffer);
			}
		} finally {
			GL11.glPopAttrib();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.lwjgl.graphics.LwjglVBO#prepare(org.eclipse.draw3d.graphics3d.Graphics3D)
	 */
	@Override
	protected void prepare(Graphics3D i_g3d) {

		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getBufferId());

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, VERTEX_SIZE, 0);

		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, VERTEX_SIZE, 2 * 4);
	}

}
