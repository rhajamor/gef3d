/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthias Thiele - initial API and implementation
 *    Kristian Duske - initial API and implementation
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d.graphics3d;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.opengl.GLCanvas;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Graphics3DRegistry is aware of the existing Graphics3D implementors and can
 * create instances of them. Please note, that the Graphics3DRegistry does not
 * manage the Graphics3D instances. Once created and returned, the
 * Graphics3DRegistry does not store any reference to them any more.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since 16.12.2008
 */
public class Graphics3DRegistry {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(Graphics3DRegistry.class
			.getName());

	/**
	 * Identifies the LWJGL-Renderer implementation of Graphics3D.
	 */
	public static final String G3D_IMPL_LWJGL = "LWJGL";

	/**
	 * Identifies the X3D-Export-Renderer implementation of Graphics3D (Not
	 * existing yet).
	 */
	public static final String G3D_IMPL_X3D = "X3D";

	/**
	 * Identifies the default renderer implementation of Graphics3D.
	 */
	public static final String G3D_IMPL_DEFAULT = G3D_IMPL_LWJGL;

	private static Graphics3D defaultGraphics3D = null;

	/**
	 * Creates and returns a Graphics3D-Implementation of the specified type.
	 * 
	 * @param i_strRenderer String identifying the renderer type to create.
	 * @param i_context The context which the renderer should use.
	 * @return The created Graphics3D-instance.
	 */
	public static Graphics3D createGraphics3D(String i_strRenderer,
			GLCanvas i_context) {
		// TODO Adapt for more renderer as soon as they become available.
		if (defaultGraphics3D == null) {
			defaultGraphics3D = getDefaultScreenRenderer();
			defaultGraphics3D.setGLCanvas(i_context);
		}
		// return new Graphics3DLwjgl(i_context);
		return defaultGraphics3D;
	}

	/**
	 * we expect at least
	 * <code>org.eclipse.draw3d.graphics3d.lwjgl.Graphics3DLwjgl</code>
	 */
	private static Graphics3D getDefaultScreenRenderer() {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry
				.getExtensionPoint("org.eclipse.draw3d.graphics3d");
		if (point == null)
			return null;
		IExtension[] extensions = point.getExtensions();

		for (IExtension extension : extensions) {
			String strContributorName = extension.getContributor().getName();
			if (log.isLoggable(Level.INFO)) {
				log.info("Extension found: " + extension
						+ ", Contributor: " + strContributorName); //$NON-NLS-1$
			}

			IConfigurationElement[] ices = extension.getConfigurationElements();
			for (IConfigurationElement element : ices) {
				String strClassName = element.getAttribute("class");
				String strType = element.getAttribute("type");

				if (log.isLoggable(Level.INFO)) {
					log.info("Class: " + strClassName + ", Type: " + strType); //$NON-NLS-1$
				}

				if ("screen".equals(strType)) {
					try {

						Bundle bundle = Platform.getBundle(strContributorName);
						Class clazz = bundle.loadClass(strClassName);
						Graphics3D g3d = (Graphics3D) clazz.newInstance();
						return g3d;

					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		log.severe("No Graphics3D implementation with type \"screen\" found."); //$NON-NLS-2$
		return null;

	}
}
