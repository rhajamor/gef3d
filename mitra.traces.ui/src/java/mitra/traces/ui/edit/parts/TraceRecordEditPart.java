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
import java.util.logging.Level;
import java.util.logging.Logger;

import mitra.traces.ui.edit.TraceRecordPartRefresher;
import mitra.traces.ui.edit.figures.TraceRecordFigure;
import mitra.traces.ui.util.TraceUtil;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw3d.Figure3D;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef3d.ext.intermodel.ConnectedElementAdapter;
import org.eclipse.gef3d.ext.intermodel.IInterModelRootEditPart;
import org.eclipse.gef3d.ext.multieditor.MultiEditorModelContainerEditPart;
import org.eclipse.gef3d.ext.reverselookup.ReverseLookupManager;

import de.feu.mitra.traces.Trace;
import de.feu.mitra.traces.TraceElement;
import de.feu.mitra.traces.TraceRecord;

/**
 * Root of the trace model, since traces are only connections, there is no plane
 * visualized, i.e. the diagram itself is invisible.
 * <p>
 * Current constraint: exactly one target and one source model root are
 * expected.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 18.01.2008
 * @see $HeadURL:
 *      https://gorgo.fernuni-hagen.de/OpenglGEF/trunk/de.feu.gef3d.samples
 *      .multieditor
 *      /src/java/de/feu/gef3d/samples/unitrace/edit/MergedTraceDiagramEditPart
 *      .java $
 */
public class TraceRecordEditPart extends AbstractGraphicalEditPart implements
		IInterModelRootEditPart {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(TraceRecordEditPart.class.getName());

	/**
	 * Lazily initialized in {@link #getModelChildren()}
	 */
	ReverseLookupManager<EditPart> reverseLookupManager = null;

	private Adapter modelListener;

	// HashMap<Object, ConnectedElementAdapter>
	// connectedElementAdaptersByModelElement = new HashMap<Object,
	// ConnectedElementAdapter>();

	public TraceRecordEditPart(TraceRecord model) {
		setModel(model);
	}

	/**
	 * Convenience method returning the casted parent edit part.
	 * 
	 * @return
	 */
	MultiEditorModelContainerEditPart getBasePart() {
		return (MultiEditorModelContainerEditPart) getParent();
	}

	/**
	 * Convenience method returning casted model.
	 * 
	 * @return
	 */
	public TraceRecord getTraceRecord() {
		return (TraceRecord) getModel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {

		Figure3D fig = new TraceRecordFigure();
		fig.setLocation3D(IVector3f.NULLVEC3f);

		return fig;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO implement method TraceDiagramEditPart.createEditPolicies

	}

	public void doStructureRefresh() {
		refreshChildren();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {

		if (reverseLookupManager == null) {
			reverseLookupManager =
				ReverseLookupManager.getEditPartLookupManager(getViewer());
		}

		ArrayList list = new ArrayList();
		int iNumberOfVisibleSourceElements = 0;
		int iNumberOfVisibleTargetElements = 0;

		List<ConnectedElementAdapter> listConnectedElementAdapters =
			new ArrayList<ConnectedElementAdapter>(5);

		for (Trace trace : getTraceRecord().getTraces()) {
			listConnectedElementAdapters.clear();

			addConnectedElementAdapters(listConnectedElementAdapters, 0,
				TraceUtil.getSourceElements(trace));
			iNumberOfVisibleSourceElements =
				listConnectedElementAdapters.size();
			if (iNumberOfVisibleSourceElements > 0) {
				addConnectedElementAdapters(listConnectedElementAdapters, 1,
					TraceUtil.getTargetElements(trace));
				iNumberOfVisibleTargetElements =
					listConnectedElementAdapters.size()
						- iNumberOfVisibleSourceElements;
				if (iNumberOfVisibleTargetElements > 0) {
					// at least one target and source connected element visible
					list.add(trace);
					list.addAll(listConnectedElementAdapters);
					// and add adapters to lookup table
					// for (ConnectedElementAdapter adapter :
					// listConnectedElementAdapters) {
					// connectedElementAdaptersByModelElement.put(adapter
					// .getIntermodelConnector(), adapter);
					// }
				}
			}
			if (log.isLoggable(Level.INFO)) {
				if (iNumberOfVisibleTargetElements > 0
					&& iNumberOfVisibleSourceElements > 0) {
					log
						.info("trace=" + trace + ", iNumberOfVisibleSourceElements=" + iNumberOfVisibleSourceElements + ", iNumberOfVisibleTargetElements=" + iNumberOfVisibleTargetElements); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else
					log.warning("trace not visible: " + trace);
			}
		}

		return list;
	}

	/**
	 * @param o_listConnectedElementAdapters
	 * @param modelRootEditPart
	 * @param i_TraceElements
	 */
	private void addConnectedElementAdapters(
		List<ConnectedElementAdapter> o_listConnectedElementAdapters, int dir,
		List<TraceElement> i_TraceElements) {

		ConnectedElementAdapter adapter;

		for (TraceElement element : i_TraceElements) {
			EObject semanticObject = element.getElement();
			EditPart editPart =	reverseLookupManager
					.findNotationElementForDomainElement(semanticObject);
			// getReverseLookupHelper().findEditPart(semanticObject);

			if (editPart != null) {
				adapter =
					new ConnectedElementAdapter((GraphicalEditPart) editPart,
						element);
				if (dir == 0)
					adapter.addTargetConnection(element);
				else
					adapter.addSourceConnection(element);
				o_listConnectedElementAdapters.add(adapter);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Registers as listeners at model.
	 * </p>
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();

		if (log.isLoggable(Level.INFO)) {
			log.info("TraceRecordEditPart listens to traceRecord"); //$NON-NLS-1$
		}

		modelListener = new Adapter() {

			public Notifier getTarget() {
				return getTraceRecord();
			}

			public boolean isAdapterForType(Object i_type) {
				if (log.isLoggable(Level.INFO)) {
					log.info("IsAdapterFor " + i_type); //$NON-NLS-1$
				}
				return false;
			}

			public void notifyChanged(Notification i_notification) {
				if (log.isLoggable(Level.INFO)) {
					log.info("TraceRecord changed: " + i_notification); //$NON-NLS-1$
				}
				refresh();
			}

			public void setTarget(Notifier i_newTarget) {
				if (i_newTarget != getTraceRecord() && i_newTarget != null) {
					String strMessage =
						"TraceRecord EditPart can only listen to its model, not to: "
							+ i_newTarget;
					if (log.isLoggable(Level.INFO)) {
						log.info(strMessage); //$NON-NLS-1$
					}
					throw new IllegalStateException(strMessage);
				}

			}

		};

		// getTraceRecord().eAdapters().add(modelListener);
		TraceRecordPartRefresher.registerTraceRecordEditPart(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		// getTraceRecord().eAdapters().remove(modelListener);
		TraceRecordPartRefresher.unregisterTraceRecordEditPart(this);
		modelListener = null;
	}

}
