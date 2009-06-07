/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.examples.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Activator There should really be more documentation here.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 16.11.2007
 */
public class GEF3DGraphExamplePlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.gef3d.examples.graph";

	// The shared instance
	private static GEF3DGraphExamplePlugin plugin;

	/**
	 * The constructor
	 */
	public GEF3DGraphExamplePlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		initLogging();

	}

	private void initLogging() {
		
		InputStream is = this.getClass().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(is);
		} catch (SecurityException ex) {
			// TODO Implement catch block for SecurityException
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Implement catch block for IOException
			ex.printStackTrace();
		}
		
//		System.out.println(System.getProperties());
		
//		Logger logger = Logger.getLogger("de.feu");
//		logger.setLevel(Level.FINEST);
//
//		EclipseConsoleFormatter formatter = new EclipseConsoleFormatter();
//		Handler[] aHandlers = Logger.getLogger("").getHandlers();
//		for (Handler handler : aHandlers) {
//			handler.setLevel(Level.FINEST);
//			handler.setFormatter(formatter);
//		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GEF3DGraphExamplePlugin getDefault() {
		return plugin;
	}
}
