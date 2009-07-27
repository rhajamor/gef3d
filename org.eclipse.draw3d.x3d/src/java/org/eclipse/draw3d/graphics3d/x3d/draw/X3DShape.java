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

package org.eclipse.draw3d.graphics3d.x3d.draw;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.x3d.X3DPropertyContainer;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DAttribute;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * An abstract super class for all X3D shapes.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public abstract class X3DShape implements X3DDrawTarget {

	/**
	 * The appearance node, containing color and/or texture of the shape.
	 */
	protected final X3DNode m_appearanceNode;

	/**
	 * The transformation node which determines the shapes position and
	 * dimensions.
	 */
	protected X3DNode m_transformationNode;

	/**
	 * Whether this primitive has completed drawing. If true, drawing to this
	 * shape is not longer allowed.
	 */
	protected boolean m_bIsCompleted;

	/**
	 * Constructs a shape.
	 */
	public X3DShape() {
		m_appearanceNode = new X3DNode("Appearance");
		m_transformationNode = null;

		m_bIsCompleted = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawTarget#addGraphics2D(java.lang.String,
	 *      java.lang.String)
	 */
	public void addGraphics2D(String i_strImageFile, String i_strExportPath) {

		// Check here, that a texture is only added once.
		if (m_appearanceNode.getNodeByName("ImageTexture") == null) {
			File texture = new File(i_strImageFile);
			if (!texture.exists()) {
				throw new Graphics3DException("Graphics2D to add not found at "
						+ i_strImageFile);
			}

			X3DNode imageTextureNode = new X3DNode("ImageTexture");
			imageTextureNode.addAttribute(new X3DAttribute("url", texture
					.getName()));
			m_appearanceNode.addNode(imageTextureNode);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawTarget#draw(org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawCommand)
	 */
	public boolean draw(X3DDrawCommand i_command) {

		if (m_bIsCompleted) {
			throw new Graphics3DException(
					"This draw target is already completed. It cannot receive draw commands any more.");
		}

		if (i_command.getName() == X3DDrawCommand.CMD_NAME_BEGIN) {
			// Set transformation node on begin
			m_transformationNode = i_command.getTransformatioNode();

			// Set color on begin
			Color color = (Color) i_command.getRenderingProperties()
					.getProperties()
					.get(X3DPropertyContainer.PRP_CURRENT_COLOR);
			StringBuilder sb = new StringBuilder();
			sb.append((color.getRed() / 255.0f) + " ");
			sb.append((color.getGreen() / 255.0f) + " ");
			sb.append((color.getBlue() / 255.0f) + " ");
			sb.append((color.getAlpha() / 255.0f));

			X3DNode materialNode = new X3DNode("Material");
			materialNode.addAttribute(new X3DAttribute("diffuseColor", sb
					.toString()));
			m_appearanceNode.addNode(materialNode);

			// Set fill properties on begin, if polygon fill mode is set.
			if ((Boolean) i_command.getRenderingProperties().getProperties()
					.get(X3DPropertyContainer.PRP_POLYGON_MODE_DO_FILL)) {
				X3DNode fillPropertiesNode = new X3DNode("FillProperties");
				fillPropertiesNode.addAttribute(new X3DAttribute("filled",
						"TRUE"));
				fillPropertiesNode.addAttribute(new X3DAttribute("hashed",
						"FALSE"));
				m_appearanceNode.addNode(fillPropertiesNode);
			}

		} else if (i_command.getName() == X3DDrawCommand.CMD_NAME_END) {
			// Nothing to do, subclasses have to call complete() after having
			// finished their processing.
		}

		return false;
	}

	/**
	 * Handles the completion of the drawing. This method shall be called from
	 * them sub-classes which can overwrite the complete()-method but should
	 * nevertheless call super.complete().
	 */
	protected void complete() {
		// Remember completed state
		m_bIsCompleted = true;
	}

	/**
	 * Gets the transformation node of this shape.
	 * 
	 * @return The transformation node.
	 */
	public X3DNode getTransformationNode() {

		X3DNode transformationNode = (X3DNode) m_transformationNode.clone();

		X3DNode shapeNode = new X3DNode("Shape");
		transformationNode.addNode(shapeNode);

		X3DNode appearanceNode = (X3DNode) m_appearanceNode.clone();
		shapeNode.addNode(appearanceNode);

		return transformationNode;
	}

	/**
	 * Extracts the parameters from a parameter list. Checks that the expected
	 * number of parameters where actually handed in.
	 * 
	 * @param it The iterator on the parameter list. Has to be set to zero.
	 * @param paraCount This number of parameter is expected.
	 * @return An array with the extracted parameter.
	 */
	protected Object[] getParameters(ListIterator<Object> it, int paraCount) {

		List<Object> parameter = new ArrayList<Object>();

		while (it.hasNext()) {
			parameter.add(it.next());
		}

		if (parameter.size() != paraCount) {
			throw new Graphics3DException("Parameter count not as expected: "
					+ parameter.size() + " != " + paraCount);
		}

		return parameter.toArray();
	}
}
