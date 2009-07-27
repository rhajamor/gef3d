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

package org.eclipse.draw3d.graphics3d.x3d.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw3d.graphics3d.Graphics3DException;

/**
 * The X3Model contains the whole XML-node structure during the export and
 * finally writes it into a file.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 */
public class X3DModel {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(X3DModel.class.getName());

	/**
	 * The scene graph node.
	 */
	private final X3DNodeContainer m_sceneGraph;

	/**
	 * THe head node.
	 */
	private final X3DNodeContainer m_head;

	/**
	 * The file to which the XML node structure is finally written.
	 */
	private  String m_strExportFile;

	/**
	 * Constructs a new X3DModel.
	 * 
	 * @param i_strExportFile The file to which the XML node structure shall be
	 *            finally written.
	 */
	public X3DModel() {

		m_strExportFile = null;

		m_head = new X3DNodeContainer();

		m_sceneGraph = new X3DNodeContainer();

	}
	
	public void setExportFile(String i_strFilename) {
		m_strExportFile = i_strFilename;
	}

	/**
	 * @return The scene graph node.
	 */
	public X3DNodeContainer getSceneGraph() {
		return m_sceneGraph;
	}

	/**
	 * Writes the XML node structure to disk in the file specified on
	 * construction.
	 */
	public void doExport() {
		
		// Add static content to the head node.
		fillHeadNode();

		File expFile = new File(m_strExportFile);
		BufferedWriter bw = null;

		try {

			// Create the file, if not yet existing.
			if (!expFile.exists()) {
				expFile.createNewFile();

				if (log.isLoggable(Level.INFO)) {
					log.info("Export file created. - expFile=" + expFile);
				}
			}

			// Open file for writing
			bw = new BufferedWriter(new FileWriter(expFile));

			// Write the (static) start section.
			bw.write(getStartSection());

			// Write the head
			ListIterator<X3DNode> it = m_head.getNodeIterator();
			while (it.hasNext()) {
				bw.write(it.next().toString());
			}

			// Write the scene graph
			bw.write("<Scene>" + System.getProperty("line.separator"));
			it = m_sceneGraph.getNodeIterator();
			while (it.hasNext()) {
				bw.write(it.next().toString());
			}
			bw.write("</Scene>" + System.getProperty("line.separator"));

			// Write the (static) end section
			bw.write(getEndSection());

			// Close file.
			bw.close();

			if (log.isLoggable(Level.INFO)) {
				log.info("Successfully exported scene graph. - expFile="
						+ expFile);
			}

		} catch (FileNotFoundException ex) {
			throw new Graphics3DException(ex);
		} catch (IOException ex) {
			throw new Graphics3DException(ex);
		}
	}

	/**
	 * Returns the path to the directory, which will contain the export file.
	 * 
	 * @return The path to the export files's directory.
	 */
	public String getExportPath() {
		File f = new File(m_strExportFile);

		return f.getParent();
	}

	/**
	 * Builds the (static) X3D start section.
	 * 
	 * @return The X3D start section.
	 */
	private String getStartSection() {

		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(System.getProperty("line.separator"));
		sb
				.append("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.2//EN\" \"http://www.web3d.org/specifications/x3d-3.2.dtd\">");
		sb.append(System.getProperty("line.separator"));
		sb
				.append("<X3D profile='Immersive' version='3.0' xmlns:xsd='http://www.w3.org/2001/XMLSchema-instance' xsd:noNamespaceSchemaLocation='http://www.web3d.org/specifications/x3d-3.2.xsd'>");
		sb.append(System.getProperty("line.separator"));

		return sb.toString();
	}

	/**
	 * Gets the (static) X3D end section.
	 * 
	 * @return The X3D end section.
	 */
	private String getEndSection() {
		StringBuilder sb = new StringBuilder();

		sb.append("</X3D>");
		sb.append(System.getProperty("line.separator"));

		return sb.toString();
	}

	/**
	 * Fills the head node with the appropriate information.
	 */
	private void fillHeadNode() {

		X3DNode headNode = new X3DNode("head");

		X3DNode createdNode = new X3DNode("meta");
		createdNode.addAttribute(new X3DAttribute("content",
				new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
						.format(new GregorianCalendar().getTime())));
		createdNode.addAttribute(new X3DAttribute("name", "created"));
		headNode.addNode(createdNode);

		X3DNode creatorNode = new X3DNode("meta");
		creatorNode.addAttribute(new X3DAttribute("content", System
				.getProperty("user.name")));
		creatorNode.addAttribute(new X3DAttribute("name", "creator"));
		headNode.addNode(creatorNode);

		X3DNode generatorNode = new X3DNode("meta");
		generatorNode.addAttribute(new X3DAttribute("content",
				"GEF3D X3D Export"));
		generatorNode.addAttribute(new X3DAttribute("name", "generator"));
		headNode.addNode(generatorNode);

		m_head.addNode(headNode);
	}
}
