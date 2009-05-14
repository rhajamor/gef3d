/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens von Pilgrim - initial API and implementation
 ******************************************************************************/

package mitra.traces.ui.edit.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mitra.traces.ui.edit.figures.TraceFigure;
import mitra.traces.ui.util.TraceUtil;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.ChopboxAnchor3D;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.draw3d.geometry.Math3D;
import org.eclipse.draw3d.geometry.Vector3f;
import org.eclipse.draw3d.geometry.Vector3fImpl;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef3d.ext.IConnectionAnchorFactory;
import org.eclipse.gef3d.ext.SingletonConnectionAnchorFactory;
import org.eclipse.gef3d.ext.intermodel.ConnectedElementEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import de.feu.mitra.traces.Trace;
import de.feu.mitra.traces.TraceElement;

/**
 * This is the center of the trace between source and target elements. The
 * center is visualized as a small box with a label showing the comment of the
 * trace. That is it is a node and not a connection! The connections are
 * implemented by {@link TraceElementEditPart}.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since Feb 4, 2008
 * @see $HeadURL:
 *      https://gorgo.fernuni-hagen.de/OpenglGEF/trunk/de.feu.gef3d.samples
 *      .multieditor
 *      /src/java/de/feu/gef3d/samples/Trace/edit/TraceEditPart.java $
 */
public class TraceEditPart extends AbstractGraphicalEditPart implements
		NodeEditPart {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(TraceEditPart.class.getName());

	IConnectionAnchorFactory m_anchorFactory;

	List<IFigure>[] connectingFigures;

	FigureListener connectingFiguresListener;

	// static int s_instanceCount = 0;

	/**
	 * @param i_obj
	 */
	public TraceEditPart(Trace i_model) {
		setModel(i_model);

		connectingFigures = new ArrayList[2];
		connectingFiguresListener = new FigureListener() {

			public void figureMoved(IFigure i_source) {
				IVector3f v = calcPosition();
				((Figure3D) getFigure()).setLocation3D(v);
			}

		};

		// if (log.isLoggable(Level.INFO)) {
		//			log.info("Trace - created TraceEditPart for " + i_model); //$NON-NLS-1$
		// }
	}

	/**
	 * Convenience method, returns casted model.
	 * 
	 * @return
	 */
	public Trace getTrace() {
		return (Trace) getModel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.topcased.modeler.edit.GraphNodeEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		TraceFigure fig = new TraceFigure();
		// fig.setResolution(IFigure3D.RESOLUTION_DISABLE);
		Color c =
			TraceUtil.isAuto(getTrace()) ? ColorConstants.yellow
					: ColorConstants.lightGray;
		fig.setBackgroundColor(c);

		Font f = new Font(Display.getCurrent(), "Arial", 8, 0);
		fig.setFont(f);

		String strComment = getTrace().getRuleName();
		if (strComment == null)
			strComment = "";
		// strComment = Integer.toString(++s_instanceCount);
		fig.setTag(strComment);
		IVector3f v = calcPosition();
		fig.setLocation3D(v);

		// fig.setSize(200, 50);
		if (strComment.length() == 0)
			fig.setSize3D(new Vector3fImpl(7, 7, 7));
		else
			fig.setSize3D(new Vector3fImpl(30, 20, 7));

		m_anchorFactory =
			new SingletonConnectionAnchorFactory(new ChopboxAnchor3D(fig));
		//
		// if (log.isLoggable(Level.INFO)) {
		//			log.info("create merged trace figure at " + v); //$NON-NLS-1$
		// }

		return fig;
	}

	protected IVector3f calcPosition() {
		// 1) get center of sources and targets
		IVector3f centerSource = getCenter(0);
		IVector3f centerTarget = getCenter(1);

		Vector3f center = Math3D.add(centerSource, centerTarget, null);
		center.scale(0.5f);
		return center;

	}

	/**
	 * @return
	 */
	private IVector3f getCenter(int dir) {
		if (connectingFigures[dir] == null) {
			connectingFigures[dir] = new ArrayList<IFigure>();
			List<TraceElement> elementlist =
				(dir == 0) ? TraceUtil.getSourceElements(getTrace())
						: TraceUtil.getTargetElements(getTrace());
			for (TraceElement element : elementlist) {
				TraceRecordEditPart parentPart =
					(TraceRecordEditPart) getParent();
				EditPart part =
					parentPart.getReverseLookupHelper().findEditPart(
						element.getElement()); // , subtreeRootEditPart);
				if (part != null) {
					IFigure fig = ((GraphicalEditPart) part).getFigure();
					if (fig != null) {
						connectingFigures[dir].add(fig);
						fig.addFigureListener(connectingFiguresListener);
					}
				}

			}
		}
		float fNodeCount = 0;
		Vector3f v = new Vector3fImpl();
		for (IFigure fig : connectingFigures[dir]) {
			Math3D
				.add(v, ConnectedElementEditPart.getTopCenter3D(fig, null), v);
			fNodeCount += 1.0;
		}
		if (fNodeCount != 0)
			v.scale(1 / fNodeCount);

		return v;
	}

	/**
	 * Delegate method
	 * 
	 * @param i_connection
	 * @return
	 * @see org.eclipse.gef3d.ext.IConnectionAnchorFactory#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
		ConnectionEditPart i_connection) {
		return m_anchorFactory.getSourceConnectionAnchor(i_connection);
	}

	/**
	 * Delegate method
	 * 
	 * @param i_request
	 * @return
	 * @see org.eclipse.gef3d.ext.IConnectionAnchorFactory#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request i_request) {
		return m_anchorFactory.getSourceConnectionAnchor(i_request);
	}

	/**
	 * Delegate method
	 * 
	 * @param i_connection
	 * @return
	 * @see org.eclipse.gef3d.ext.IConnectionAnchorFactory#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
		ConnectionEditPart i_connection) {
		return m_anchorFactory.getTargetConnectionAnchor(i_connection);
	}

	/**
	 * Delegate method
	 * 
	 * @param i_request
	 * @return
	 * @see org.eclipse.gef3d.ext.IConnectionAnchorFactory#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request i_request) {
		return m_anchorFactory.getTargetConnectionAnchor(i_request);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO implement method TraceEditPart.createEditPolicies
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	@Override
	protected List getModelSourceConnections() {
		return TraceUtil.getSourceElements(getTrace());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	@Override
	protected List getModelTargetConnections() {
		return TraceUtil.getTargetElements(getTrace());

	}

	/**
	 * @param i_visible
	 */
	public void setVisible(boolean i_visible) {
		getFigure().setVisible(i_visible);
		refreshVisuals();

	}

}
