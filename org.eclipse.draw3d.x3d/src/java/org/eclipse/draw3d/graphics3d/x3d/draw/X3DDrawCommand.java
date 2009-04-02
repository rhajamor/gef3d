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

import java.util.ListIterator;

import org.eclipse.draw3d.graphics3d.x3d.X3DPropertyContainer;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * This is a container for a draw command. It contains an identifying name and a
 * list of parameter.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DDrawCommand {

	/* Command name constants. */

	public static final String CMD_NAME_BEGIN = "X3D_CMD_BEGIN";

	public static final String CMD_NAME_END = "X3D_CMD_END";

	public static final String CMD_NAME_VERTEX2F = "X3D_CMD_VERTEX2F";

	public static final String CMD_NAME_VERTEX3F = "X3D_CMD_VERTEX3F";

	public static final String CMD_NAME_TEXCOORDS = "X3D_CMD_TEXCOORDS";

	public static final String CMD_NAME_NORMAL = "X3D_CMD_NORMAL";

	/**
	 * The name of the command.
	 */
	private final String m_cmdName;

	/**
	 * The list of parameter.
	 */
	private final X3DParameterList m_cmdParameter;

	/**
	 * A copy of the rendering properties which was made when the command was
	 * issued.
	 */
	public final X3DPropertyContainer m_renderingProperties;

	/**
	 * A copy of the transformation node which was made when the command was
	 * issued.
	 */
	public final X3DNode m_transformationNode;

	/**
	 * Constructs a draw command.
	 * 
	 * @param i_cmdName The command's name
	 * @param i_cmdParameter The command's parameter
	 * @param i_renderingProperties A copy of the rendering properties
	 * @param i_transformationNode A copy of the current transformation node
	 */
	public X3DDrawCommand(String i_cmdName, X3DParameterList i_cmdParameter,
			X3DPropertyContainer i_renderingProperties,
			X3DNode i_transformationNode) {

		m_cmdName = i_cmdName;
		m_cmdParameter = i_cmdParameter;
		m_renderingProperties = i_renderingProperties;
		m_transformationNode = i_transformationNode;
	}

	/**
	 * Gets the command's name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return m_cmdName;
	}

	/**
	 * Gets the command's parameter list.
	 * 
	 * @return The commands parameter list.
	 */
	public ListIterator<Object> getParameterIterator() {
		return m_cmdParameter.getParameters().listIterator();
	}

	/**
	 * Gets the attached transformation node.
	 * 
	 * @return The attached transformation node.
	 */
	public X3DNode getTransformatioNode() {
		return m_transformationNode;
	}

	/**
	 * Gets the attached rendering properties.
	 * 
	 * @return The attached rendering properties.
	 */
	public X3DPropertyContainer getRenderingProperties() {
		return m_renderingProperties;
	}

}
