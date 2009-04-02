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

package org.eclipse.draw3d.graphics3d.x3d;

import java.awt.Color;

import org.eclipse.draw3d.graphics3d.x3d.model.X3DAttribute;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DModel;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DNode;

/**
 * One instance of this class is intended to be stored into each
 * Graphics3DX3D-instance and to be notified on the specified events.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DExportInterceptor {

	/**
	 * The standard constructor.
	 */
	public X3DExportInterceptor() {

	}

	/**
	 * To be called directly before the model is written to disk. Adds the
	 * Viewpoint-Node and Background-Node to the model.
	 * 
	 * @param i_model The X3DModel which is about to be written to disk.
	 * @param i_properties The current rendering properties.
	 */
	public void beforeWrite(X3DModel i_model, X3DPropertyContainer i_properties) {

		X3DNode viewpointNode = new X3DNode("Viewpoint");

		// Set the Viewpoint
		float[] center = (float[]) i_properties.getProperties().get(
				X3DPropertyContainer.PRP_VIEWPOINT_CENTER);
		StringBuilder sb = new StringBuilder();
		sb.append(center[0] + " ");
		sb.append(center[1] + " ");
		sb.append(center[2]);
		X3DAttribute centerOfRotation = new X3DAttribute("centerOfRotation", sb
				.toString());
		viewpointNode.addAttribute(centerOfRotation);

		float[] pos = (float[]) i_properties.getProperties().get(
				X3DPropertyContainer.PRP_VIEWPOINT_POSITION);
		sb = new StringBuilder();
		sb.append(pos[0] + " ");
		sb.append(pos[1] + " ");
		sb.append(pos[2]);
		X3DAttribute position = new X3DAttribute("position", sb.toString());
		viewpointNode.addAttribute(position);
		viewpointNode.addAttribute(new X3DAttribute("orientation",
				"1 0 0 3.14159"));

		i_model.getSceneGraph().addNode(viewpointNode);

		// Set the background color
		X3DNode backgroundNode = new X3DNode("Background");

		Color bgColor = (Color) i_properties.getProperties().get(
				X3DPropertyContainer.PRP_BG_COLOR);
		sb = new StringBuilder();
		sb.append((bgColor.getRed() / 255.0f) + " ");
		sb.append((bgColor.getGreen() / 255.0f) + " ");
		sb.append((bgColor.getBlue() / 255.0f));
		X3DAttribute skyColor = new X3DAttribute("skyColor", sb.toString());
		backgroundNode.addAttribute(skyColor);

		i_model.getSceneGraph().addNode(backgroundNode);

	}
}
