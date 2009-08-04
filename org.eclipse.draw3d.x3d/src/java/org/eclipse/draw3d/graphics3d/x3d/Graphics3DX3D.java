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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw3d.geometry.IPosition3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.draw3d.graphics3d.AbstractGraphics3DDraw;
import org.eclipse.draw3d.graphics3d.Graphics3D;
import org.eclipse.draw3d.graphics3d.Graphics3DDescriptor;
import org.eclipse.draw3d.graphics3d.Graphics3DDraw;
import org.eclipse.draw3d.graphics3d.Graphics3DException;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig;
import org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBuffers;
import org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawCommand;
import org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawTarget;
import org.eclipse.draw3d.graphics3d.x3d.draw.X3DDrawTargetFactory;
import org.eclipse.draw3d.graphics3d.x3d.draw.X3DParameterList;
import org.eclipse.draw3d.graphics3d.x3d.graphics2d.X3DGraphics2DManager;
import org.eclipse.draw3d.graphics3d.x3d.lists.MethodCallBuffer;
import org.eclipse.draw3d.graphics3d.x3d.model.X3DModel;
import org.eclipse.draw3d.util.LogGraphics;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.opengl.GLCanvas;

/**
 * The implementation of {@link org.eclipse.draw3d.graphics3d.Graphics3D},
 * providing the export to the X3D format. Export is executing by setting an
 * instance of this class as the current renderer and then triggering a full
 * rendering pass.
 * 
 * @author Matthias Thiele
 * @version $Revision$
 * @since Dec 15, 2008
 * @see <a * href="http://www.web3d.org/x3d/specifications/ISO-IEC-FDIS-19775-1.2-X3D-AbstractSpecification/"
 *      >X3D-Specification< /a>
 */
public class Graphics3DX3D extends AbstractGraphics3DDraw implements Graphics3D {
	/**
	 * To remember the current state of the export run.
	 * 
	 * @author Matthias Thiele
	 * @version $Revision$
	 * @since Dec 15, 2008
	 */
	private enum ExportState {
		DONE, IN_PROGRESS, INITIAL
	}

	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(Graphics3DX3D.class
			.getName());

	/**
	 * Descriptor of this instance.
	 */
	protected Graphics3DDescriptor descriptor = null;

	/**
	 * The graphics primitive currently drawn.
	 */
	private X3DDrawTarget m_activeDrawTarget = null;

	/**
	 * The GLCanvas displaying the scene graph on screen. For further usage.
	 */
	@SuppressWarnings("unused")
	private GLCanvas m_canvas = null;

	/**
	 * The rendering context for the on-screen display. For further usage.
	 */
	@SuppressWarnings("unused")
	private Object m_context = null;

	/**
	 * The display list which is current build, or null.
	 */
	private MethodCallBuffer m_currentDisplayList = null;

	/**
	 * The list of "precompiled" rendering calls.
	 */
	private final Map<Integer, MethodCallBuffer> m_displayLists;

	/**
	 * The export interceptor is notified on specific steps in the rendering
	 * process.
	 */
	private final X3DExportInterceptor m_exportInterceptor;

	/**
	 * The current export state, for further usage.
	 */
	@SuppressWarnings("unused")
	private ExportState m_exportState;

	/**
	 * All 2D related calls are forwarded to the graphics2DManager.
	 */
	private final X3DGraphics2DManager m_g2dManager;

	private boolean m_log2D;

	/**
	 * The current rendering properties.
	 */
	private final X3DPropertyContainer m_propertyContainer;

	/**
	 * The ID of the current texture.
	 */
	private int m_texture;

	/**
	 * All transformation-related calls are forwarded to the transformation
	 * manager.
	 */
	private final X3DTransformationManager m_transformationManager;

	/**
	 * This is the model where all nodes are collected and finally exported
	 * from.
	 */
	private final X3DModel m_x3dModel;

	Properties properties = new Properties();

	/**
	 * The standard constructor.
	 */
	public Graphics3DX3D() {

		m_propertyContainer = new X3DPropertyContainer();
		m_g2dManager = new X3DGraphics2DManager();
		m_displayLists = new HashMap<Integer, MethodCallBuffer>();
		m_transformationManager = new X3DTransformationManager();
		m_exportInterceptor = new X3DExportInterceptor();
		m_exportState = ExportState.INITIAL;

		// The export destination is retrieved from the ExportProperties
		m_x3dModel = new X3DModel();
		// Graphics3DRegistry.ExportProperties.strExportFile);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#activateGraphics2D(java.lang.Object,
	 *      int, int, int, org.eclipse.swt.graphics.Color)
	 */
	public Graphics activateGraphics2D(Object i_key, int i_width, int i_height,
			int i_alpha, Color i_color) {

		Graphics graphics = m_g2dManager.activateGraphics2D(i_key, i_width,
				i_height, i_alpha, i_color);

		if (m_log2D)
			return new LogGraphics(graphics);
		else
			return graphics;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#createRawPosition(org.eclipse.draw3d.geometryext.IPosition3D)
	 */
	public Object createRawPosition(IPosition3D i_position3D) {
		return i_position3D;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#deactivateGraphics2D()
	 */
	public void deactivateGraphics2D() {

		m_g2dManager.deactivateGraphics2D();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#dispose()
	 */
	public void dispose() {
		// No ressources need to be freed here.
	}

	/**
	 * Executes a draw command. If the draw target returns true from the command
	 * execution, it requests to be exchanged because it's completed. In that
	 * case, a glEnd and glBegin is simulated. This construction is required due
	 * to the fact that X3D has not a counterpart to every OpenGL primitive.
	 * 
	 * @param i_cmdName
	 *            The command's name.
	 * @param parameter
	 *            A list of the parameter.
	 */
	private void executeDraw(String i_cmdName, X3DParameterList parameter) {
		X3DDrawCommand command = new X3DDrawCommand(i_cmdName, parameter,
				m_propertyContainer, m_transformationManager
						.getTransformationNode());
		if (m_activeDrawTarget.draw(command)) {
			// The active draw target requested completion. To do this:
			// IF this was not an end command itself
			// 1. simulate END
			if (!i_cmdName.equals(X3DDrawCommand.CMD_NAME_END)) {
				X3DDrawCommand commandEnd = new X3DDrawCommand(
						X3DDrawCommand.CMD_NAME_END, new X3DParameterList(
								new ArrayList<Object>()), m_propertyContainer,
						m_transformationManager.getTransformationNode());
				m_activeDrawTarget.draw(commandEnd);
			}

			// 2. add draw target to scene graph
			m_x3dModel.getSceneGraph().addDrawTarget(m_activeDrawTarget);

			// IF this was not an end command itself
			// 3. exchange the active draw target against a new one of the same
			// type
			if (!i_cmdName.equals(X3DDrawCommand.CMD_NAME_END)) {
				m_activeDrawTarget = X3DDrawTargetFactory
						.createNewInstance(m_activeDrawTarget);

				// 4. simulate BEGIN.
				X3DDrawCommand commandBegin = new X3DDrawCommand(
						X3DDrawCommand.CMD_NAME_BEGIN, new X3DParameterList(
								new ArrayList<Object>()), m_propertyContainer,
						m_transformationManager.getTransformationNode());
				m_activeDrawTarget.draw(commandBegin);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getDescriptor()
	 */
	public Graphics3DDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getGraphics2DId(java.lang.Object)
	 */
	public int getGraphics2DId(Object i_key) {

		return m_g2dManager.getGraphics2DId(i_key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getGraphics3DOffscreenBuffer(int,
	 *      int, org.eclipse.draw3d.graphics3d.Graphics3DOffscreenBufferConfig)
	 */
	public Graphics3DOffscreenBuffers getGraphics3DOffscreenBuffer(
			int i_height, int i_width,
			Graphics3DOffscreenBufferConfig i_bufferConfig) {
		throw new Graphics3DException(
				"X3D getGraphics3DOffscreenBuffer not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getGraphics3DOffscreenBufferConfig(int,
	 *      int[])
	 */
	public Graphics3DOffscreenBufferConfig getGraphics3DOffscreenBufferConfig(
			int i_buffers, int... i_args) {
		throw new Graphics3DException(
				"X3D getGraphics3DOffscreenBufferConfig not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getID()
	 */
	public String getID() {
		return Graphics3DX3D.class.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#getPlatform()
	 */
	public int getPlatform() {
		String osName = System.getProperty("os.name");

		if (osName.startsWith("Windows")) {
			return PLATFORM_WINDOWS;
		} else if (osName.startsWith("Linux") || osName.startsWith("FreeBSD")
				|| osName.startsWith("SunOS")) {
			return PLATFORM_LINUX;
		} else if (osName.startsWith("Mac OS X")) {
			return PLATFORM_MACOSX;
		} else {
			throw new LinkageError("Unknown platform: " + osName);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#getProperty(java.lang.String)
	 */
	public String getProperty(String i_key) {
		return properties.getProperty(i_key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glBegin(int)
	 */
	public void glBegin(int i_mode) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glBegin",
					new Object[] { i_mode });
			return;
		}

		// Begin has to be followed by end, before called again.
		if (m_activeDrawTarget != null) {
			throw new Graphics3DException(
					"Begin before End called. I still have an active DrawTarget!");
		}

		// Depending on type, a new draw target is created.
		switch (i_mode) {

		case Graphics3DDraw.GL_LINES:
			m_activeDrawTarget = X3DDrawTargetFactory
					.createDrawTarget(X3DDrawTargetFactory.DRAW_TARGET_LINE);
			break;

		case Graphics3DDraw.GL_LINE_STRIP:
			m_activeDrawTarget = X3DDrawTargetFactory
					.createDrawTarget(X3DDrawTargetFactory.DRAW_TARGET_POLYGON);
			break;

		case Graphics3DDraw.GL_LINE_LOOP:
			m_activeDrawTarget = X3DDrawTargetFactory
					.createDrawTarget(X3DDrawTargetFactory.DRAW_TARGET_POLYGON_LOOP);
			break;

		case Graphics3DDraw.GL_QUADS:
			m_activeDrawTarget = X3DDrawTargetFactory
					.createDrawTarget(X3DDrawTargetFactory.DRAW_TARGET_QUAD);
			break;

		case Graphics3DDraw.GL_TRIANGLES:
			m_activeDrawTarget = X3DDrawTargetFactory
					.createDrawTarget(X3DDrawTargetFactory.DRAW_TARGET_TRIANGLE_SET);
			break;

		case Graphics3DDraw.GL_TRIANGLE_STRIP:
			m_activeDrawTarget = X3DDrawTargetFactory
					.createDrawTarget(X3DDrawTargetFactory.DRAW_TARGET_TRIANGLE_STRIP_SET);
			break;

		case Graphics3DDraw.GL_TRIANGLE_FAN:
			m_activeDrawTarget = X3DDrawTargetFactory
					.createDrawTarget(X3DDrawTargetFactory.DRAW_TARGET_TRIANGLE_FAN_SET);
			break;

		default:
			throw new Graphics3DException(
					"Cannot export this type of primitive: " + i_mode);

		}

		// forward the begin command to the just created draw target
		List<Object> parameterList = new ArrayList<Object>();
		parameterList.add(i_mode);
		X3DParameterList parameter = new X3DParameterList(parameterList);

		executeDraw(X3DDrawCommand.CMD_NAME_BEGIN, parameter);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glBindTexture(int, int)
	 */
	public void glBindTexture(int i_target, int i_texture) {

		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glBindTexture",
					new Object[] { i_target, i_texture });
			return;
		}

		if (i_target != Graphics3DDraw.GL_TEXTURE_2D) {
			throw new Graphics3DException("X3D does only support 2D textures.");
		}

		// Save the ID, to have it for upcoming calls.
		m_texture = i_texture;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glBlendFunc(int, int)
	 */
	public void glBlendFunc(int i_sfactor, int i_dfactor) {
		// No similar property for X3D.
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glCallList(int)
	 */
	public void glCallList(int i_list) {
		// Execute the call buffer with the given id.
		MethodCallBuffer buffer = m_displayLists.get(i_list);
		buffer.executeBuffer();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glClear(int)
	 */
	public void glClear(int i_mask) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glClear",
					new Object[] { i_mask });
			return;
		}

		// Clear is interpreted as the start of the render pass respectively the
		// export run.
		m_exportState = ExportState.IN_PROGRESS;

		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_BG_COLOR,
				X3DPropertyContainer.DEF_BG_COLOR);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glClearColor(float,
	 *      float, float, float)
	 */
	public void glClearColor(float i_red, float i_green, float i_blue,
			float i_alpha) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glClearColor",
					new Object[] { i_red, i_green, i_blue, i_alpha });
			return;
		}

		// Clear sets the background color.
		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_BG_COLOR,
				new float[] { i_red, i_green, i_blue, i_alpha });
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glClearDepth(double)
	 */
	public void glClearDepth(double i_depth) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glClearDepth",
					new Object[] { i_depth });
			return;
		}

		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_DEF_DEPTH, i_depth);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glColor3f(float, float,
	 *      float)
	 */
	public void glColor3f(float i_red, float i_green, float i_blue) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glColor3f", new Object[] {
					i_red, i_green, i_blue });
			return;
		}

		// Save the new current color
		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_CURRENT_COLOR,
				new java.awt.Color(i_red, i_green, i_blue, 1.0f));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glColor4f(float, float,
	 *      float, float)
	 */
	public void glColor4f(float i_red, float i_green, float i_blue,
			float i_alpha) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glColor4f", new Object[] {
					i_red, i_green, i_blue, i_alpha });
			return;
		}

		// Save the new current color
		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_CURRENT_COLOR,
				new java.awt.Color(i_red, i_green, i_blue, i_alpha));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glDeleteLists(int, int)
	 */
	public void glDeleteLists(int i_list, int i_range) {

		int i = 0;
		while (i > i_range) {
			if (m_displayLists.containsKey(i_list + i)) {
				m_displayLists.remove(i_list + i);
			}
			i++;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glDisable(int)
	 */
	public void glDisable(int i_cap) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glDisable",
					new Object[] { i_cap });
			return;
		}

		// Disable rendering property.
		switch (i_cap) {

		case Graphics3DDraw.GL_LINE_STIPPLE:
			m_propertyContainer.getProperties().put(
					X3DPropertyContainer.PRP_LINE_DASHED, false);
			break;

		case Graphics3DDraw.GL_TEXTURE_2D:
		case Graphics3DDraw.GL_MULTISAMPLE:
		case Graphics3DDraw.GL_CULL_FACE:
		case Graphics3DDraw.GL_BLEND:
		case Graphics3DDraw.GL_DEPTH_TEST:
		case Graphics3DDraw.GL_LINE_SMOOTH:
		case Graphics3DDraw.GL_DITHER:
			// These properties have no X3D counterpart.
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log.info("int - Property not supported for export. - i_cap="
						+ i_cap);
			}
			break;

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glEnable(int)
	 */
	public void glEnable(int i_cap) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glEnable",
					new Object[] { i_cap });
			return;
		}

		// Enable rendering property
		switch (i_cap) {

		case Graphics3DDraw.GL_LINE_STIPPLE:
			m_propertyContainer.getProperties().put(
					X3DPropertyContainer.PRP_LINE_DASHED, true);
			break;
		case Graphics3DDraw.GL_TEXTURE_2D:
		case Graphics3DDraw.GL_MULTISAMPLE:
		case Graphics3DDraw.GL_CULL_FACE:
		case Graphics3DDraw.GL_BLEND:
		case Graphics3DDraw.GL_DEPTH_TEST:
		case Graphics3DDraw.GL_LINE_SMOOTH:
		case Graphics3DDraw.GL_DITHER:
			// These properties have no X3D counterpart.
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log.info("int - Property not supported for export. - i_cap="
						+ i_cap);
			}
			break;

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glEnd()
	 */
	public void glEnd() {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glEnd", new Object[] {});
			return;
		}

		// Forward the end command and set the active draw target to null
		// afterwards. Before drawing is continued, a new draw target has to be
		// created with glBegin.
		X3DParameterList parameter = new X3DParameterList(
				new ArrayList<Object>());

		executeDraw(X3DDrawCommand.CMD_NAME_END, parameter);

		m_activeDrawTarget = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glEndList()
	 */
	public void glEndList() {
		m_currentDisplayList = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glFinish()
	 */
	public void glFinish() {
		// For X3D, we'll treat finish like flush.
		glFlush();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glFlush()
	 */
	public void glFlush() {

		// Do finalizing handling
		m_exportInterceptor.beforeWrite(m_x3dModel, m_propertyContainer);

		// Do the export
		m_x3dModel.doExport();

		// I'm done!
		m_exportState = ExportState.DONE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGenLists(int)
	 */
	public int glGenLists(int i_range) {

		int iCandidate = 0;
		// Walk through the IDs of the existing lists until a gap with
		// the specified range is found.
		for (int iPos = 0; iPos - iCandidate < i_range; iPos++) {
			if (m_displayLists.containsKey(iPos)) {
				iCandidate = iPos + 1;
			}
		}

		return iCandidate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGetFloat(int,
	 *      java.nio.FloatBuffer)
	 */
	public void glGetFloat(int i_pname, FloatBuffer i_params) {
		// no matrix support in X3D
		throw new Graphics3DException(
				"No modelview/projection matrix support in X3D.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGetInteger(int,
	 *      java.nio.IntBuffer)
	 */
	public void glGetInteger(int i_pname, IntBuffer i_params) {

		switch (i_pname) {

		case Graphics3DDraw.GL_VIEWPORT:
			int[] viewportParameter = (int[]) m_propertyContainer
					.getProperties().get(X3DPropertyContainer.PRP_VIEWPORT);
			for (int i = 0; i < viewportParameter.length; i++) {
				i_params.put(viewportParameter[i]);
			}
			break;

		case Graphics3DDraw.GL_UNPACK_ALIGNMENT:
			i_params.put(1);
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log
						.info("int, IntBuffer - Property not supported for export. - i_pname="
								+ i_pname);
			}
			break;

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glGetString(int)
	 */
	public String glGetString(int i_strName) {

		String ret = null;

		switch (i_strName) {

		case Graphics3DDraw.GL_VERSION:
			ret = (String) m_propertyContainer.getProperties().get(
					X3DPropertyContainer.PRP_VERSION);
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log
						.info("int - Property not supported for export. - i_strName="
								+ i_strName);
			}
			break;

		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glHint(int, int)
	 */
	public void glHint(int i_target, int i_mode) {

		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glHint", new Object[] {
					i_target, i_mode });
			return;
		}

		switch (i_target) {

		case Graphics3DDraw.GL_LINE_SMOOTH_HINT:
			// These properties have no X3D counterpart.
			break;
		default:
			if (log.isLoggable(Level.INFO)) {
				log
						.info("int, int - Hint not supported for export. - i_target="
								+ i_target + " i_mode=" + i_mode);
			}
			break;
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glIsEnabled(int)
	 */
	public boolean glIsEnabled(int i_cap) {

		boolean ret = false;

		switch (i_cap) {

		case Graphics3DDraw.GL_TEXTURE_2D:
			ret = true;
			break;

		case Graphics3DDraw.GL_MULTISAMPLE:
		case Graphics3DDraw.GL_CULL_FACE:
		case Graphics3DDraw.GL_BLEND:
		case Graphics3DDraw.GL_DEPTH_TEST:
		case Graphics3DDraw.GL_LINE_SMOOTH:
		case Graphics3DDraw.GL_DITHER:
			// Export is not a "real" renderer, therefore it cannot determine
			// whether the capabilities are supported.
			// This is determined by the viewer which is chosen by the user to
			// open the exported file.
			ret = false;
			break;

		case Graphics3DDraw.GL_LINE_STIPPLE:
			ret = (Boolean) m_propertyContainer.getProperties().get(
					X3DPropertyContainer.PRP_LINE_DASHED);
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log.info("int - Property not supported. - i_cap=" + i_cap);
			}
			break;
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glLineStipple(int,
	 *      short)
	 */
	public void glLineStipple(int i_factor, short i_pattern) {
		// Line stipple pattern (X3D: Dashed line property) cannot be specified
		// with X3D format.
		return;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glLineWidth(float)
	 */
	public void glLineWidth(float i_width) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glLineWidth",
					new Object[] { i_width });
			return;
		}

		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_LINE_WIDTH, i_width);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glLoadIdentity()
	 */
	public void glLoadIdentity() {
		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glLoadIdentity",
					new Object[] {});
			return;
		}
		m_transformationManager.setIdentity();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glMatrixMode(int)
	 */
	public void glMatrixMode(int i_mode) {
		// No matrix mode for X3D, ignore this call
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glNewList(int, int)
	 */
	public void glNewList(int i_list, int i_mode) {
		// Add this call to the current display list, if there is one.
		m_currentDisplayList = m_displayLists.get(i_list);

		if (m_currentDisplayList == null) {
			m_displayLists.put(i_list, new MethodCallBuffer());
			m_currentDisplayList = m_displayLists.get(i_list);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glNormal3f(float,
	 *      float, float)
	 */
	public void glNormal3f(float i_nx, float i_ny, float i_nz) {
		Vector3f normal = new Vector3fImpl(i_nx, i_ny, i_nz);
		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_NORMAL, normal);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glNormal3f(int, int,
	 *      int)
	 */
	public void glNormal3f(int i_nx, int i_ny, int i_nz) {
		glNormal3f((float) i_nx, (float) i_ny, (float) i_nz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPixelStorei(int, int)
	 */
	public void glPixelStorei(int i_pname, int i_param) {

		switch (i_pname) {

		case Graphics3DDraw.GL_PACK_ALIGNMENT:
		case Graphics3DDraw.GL_UNPACK_ALIGNMENT:
			// X3D export also does not support different pixel storage
			// strategies. It behaves like "1" (Byte Alignment)
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log.info("int, int - Unknown pixel storage setting. - i_pname="
						+ i_pname + ", i_param=" + i_param);
			}
			break;
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPolygonMode(int, int)
	 */
	public void glPolygonMode(int i_face, int i_mode) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glPolygonMode",
					new Object[] { i_face, i_mode });
			return;
		}

		switch (i_face) {

		case Graphics3DDraw.GL_FRONT_AND_BACK:
			if (i_mode == Graphics3DDraw.GL_FILL) {
				m_propertyContainer.getProperties().put(
						X3DPropertyContainer.PRP_POLYGON_MODE_DO_FILL, true);
			} else if (i_mode == Graphics3DDraw.GL_LINE) {
				m_propertyContainer.getProperties().put(
						X3DPropertyContainer.PRP_POLYGON_MODE_DO_FILL, false);
			} else {
				if (log.isLoggable(Level.INFO)) {
					log
							.info("int, int - Unknown mode for GL_FRONT_AND_BACK. - i_mode="
									+ i_mode);
				}
			}
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log.info("int, int - Unknown face and mode. - i_face=" + i_face
						+ ", i_mode=" + i_mode);
			}
			break;

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPopMatrix()
	 */
	public void glPopMatrix() {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glPopMatrix",
					new Object[] {});
			return;
		}

		m_transformationManager.popPosition();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPushMatrix()
	 */
	public void glPushMatrix() {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glPushMatrix",
					new Object[] {});
			return;
		}

		m_transformationManager.pushPosition();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glShadeModel(int)
	 */
	public void glShadeModel(int i_mode) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glShadeModel",
					new Object[] { i_mode });
			return;
		}

		switch (i_mode) {

		case Graphics3DDraw.GL_FLAT:
			// This is the general behavior of the X3D export.
			break;

		default:
			if (log.isLoggable(Level.INFO)) {
				log.info("int - Unknown shading mode. - i_mode=" + i_mode);
			}
			break;

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glTexCoord2f(float,
	 *      float)
	 */
	public void glTexCoord2f(float i_s, float i_t) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glTexCoord2f",
					new Object[] { i_s, i_t });
			return;
		}

		// Assign the current texture to the active draw target. The draw target
		// has to check, that every texture is only assigned once.
		String g2dPath = m_g2dManager.writeImage(m_texture, m_x3dModel
				.getExportPath());
		m_activeDrawTarget.addGraphics2D(g2dPath, m_x3dModel.getExportPath());

		// Execute the draw command afterwards.
		List<Object> parameterList = new ArrayList<Object>();
		parameterList.add(i_s);
		parameterList.add(i_t);
		X3DParameterList parameter = new X3DParameterList(parameterList);
		executeDraw(X3DDrawCommand.CMD_NAME_TEXCOORDS, parameter);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glTexEnvi(int, int,
	 *      int)
	 */
	public void glTexEnvi(int i_target, int i_pname, int i_param) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glTexEnvi", new Object[] {
					i_target, i_pname, i_param });
			return;
		}

		if (i_target == Graphics3DDraw.GL_TEXTURE_ENV
				&& i_pname == Graphics3DDraw.GL_TEXTURE_ENV_MODE) {
			switch (i_param) {

			case Graphics3DDraw.GL_REPLACE:
				m_propertyContainer.getProperties().put(
						X3DPropertyContainer.PRP_TEX_ENVI_REPLACE, true);
				break;

			default:
				if (log.isLoggable(Level.INFO)) {
					log
							.info("int, int, int - Unknown parameter for TEXTURE_ENV and TEXTURE_ENV_MODE. - i_param="
									+ i_param);
				}
				break;
			}
		} else {

			if (log.isLoggable(Level.INFO)) {
				log
						.info("int, int, int - Unknown target and parameter name. - i_target="
								+ i_target + ", i_pname=" + i_pname);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glTranslatef(float,
	 *      float, float)
	 */
	public void glTranslatef(float i_x, float i_y, float i_z) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glTranslatef",
					new Object[] { i_x, i_y, i_z });
			return;
		}

		m_transformationManager.translate(i_x, i_y, i_z);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluLookAt(float, float,
	 *      float, float, float, float, float, float, float)
	 */
	public void gluLookAt(float i_eyex, float i_eyey, float i_eyez,
			float i_centerx, float i_centery, float i_centerz, float i_upx,
			float i_upy, float i_upz) {

		float[] center = { i_centerx, i_centery, i_centerz };
		float[] position = { i_eyex, i_eyey, i_eyez };

		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_VIEWPOINT_CENTER, center);
		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_VIEWPOINT_POSITION, position);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluOrtho2D(int, int,
	 *      int, int)
	 */
	public void gluOrtho2D(int i_left, int i_right, int i_bottom, int i_top) {
		throw new Graphics3DException("X3D doesn't support gluOrtho2D");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluPerspective(int,
	 *      float, int, int)
	 */
	public void gluPerspective(int i_fovy, float i_aspect, int i_near, int i_far) {
		// The X3D perspective is determined by the viewpoint (gluLookAt)
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DUtil#gluUnProject(int, int,
	 *      float, java.nio.FloatBuffer, java.nio.FloatBuffer,
	 *      java.nio.IntBuffer, java.nio.FloatBuffer)
	 */
	public void gluUnProject(int i_winx, int i_winy, float i_winz,
			FloatBuffer i_modelMatrix, FloatBuffer i_projMatrix,
			IntBuffer i_viewport, FloatBuffer i_obj_pos) {
		throw new Graphics3DException("X3D doesn't support gluUnProject");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glVertex2f(float,
	 *      float)
	 */
	public void glVertex2f(float i_x, float i_y) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glVertex2f",
					new Object[] { i_x, i_y });
			return;
		}

		List<Object> parameter = new ArrayList<Object>();
		parameter.add(i_x);
		parameter.add(i_y);
		X3DParameterList parameterList = new X3DParameterList(parameter);

		executeDraw(X3DDrawCommand.CMD_NAME_VERTEX2F, parameterList);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glVertex3f(float,
	 *      float, float)
	 */
	public void glVertex3f(float i_x, float i_y, float i_z) {

		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glVertex3f",
					new Object[] { i_x, i_y, i_z });
			return;
		}

		List<Object> parameter = new ArrayList<Object>();
		parameter.add(i_x);
		parameter.add(i_y);
		parameter.add(i_z);
		X3DParameterList parameterList = new X3DParameterList(parameter);

		executeDraw(X3DDrawCommand.CMD_NAME_VERTEX3F, parameterList);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glViewport(int, int,
	 *      int, int)
	 */
	public void glViewport(int i_x, int i_y, int i_width, int i_height) {

		// Add this call to the current display list, if there is one.
		if (m_currentDisplayList != null) {
			m_currentDisplayList.addMethodCall(this, "glViewport",
					new Object[] { i_x, i_y, i_width, i_height });
			return;
		}

		m_propertyContainer.getProperties().put(
				X3DPropertyContainer.PRP_VIEWPORT,
				new int[] { i_x, i_y, i_width, i_height });
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#hasGraphics2D(java.lang.Object)
	 */
	public boolean hasGraphics2D(Object i_key) {

		return m_g2dManager.hasGraphics2D(i_key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#isPositionRawCompatible(java.lang.Object)
	 */
	public boolean isPositionRawCompatible(Object i_theRawPosition) {
		// X3D does not have a raw position. It accepts only a IPosition3D
		// itself.
		return (i_theRawPosition instanceof IPosition3D);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setDescriptor(org.eclipse.draw3d.graphics3d.Graphics3DDescriptor)
	 */
	public void setDescriptor(Graphics3DDescriptor i_graphics3DDescriptor) {
		descriptor = i_graphics3DDescriptor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setGLCanvas(org.eclipse.swt.opengl.GLCanvas)
	 */
	public void setGLCanvas(GLCanvas i_canvas) {
		m_canvas = i_canvas;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setLog2D(boolean)
	 */
	public void setLog2D(boolean i_log2D) {

		m_log2D = i_log2D;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#setPosition(java.lang.Object)
	 */
	public void setPosition(Object i_theRawPosition) {

		if (i_theRawPosition instanceof IPosition3D) {
			m_transformationManager.setPosition((IPosition3D) i_theRawPosition);
		} else {
			throw new Graphics3DException("Incompatibe raw position.");
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3D#setProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
		if ("exportfile".equals(key)) {
			m_x3dModel.setExportFile(value);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#useContext(java.lang.Object)
	 */
	public void useContext(Object i_context) throws Graphics3DException {
		m_context = i_context;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw3d.graphics3d.Graphics3DDraw#glPointSize(float)
	 */
	public void glPointSize(float i_size) {
		// TODO implement method Graphics3DDraw.glPointSize
	}
}
