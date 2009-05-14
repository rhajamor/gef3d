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
package mitra.traces.ui.edit;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Vector;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.EditorPart;

import mitra.traces.ui.edit.parts.TraceEditPartFactory;
import mitra.traces.ui.edit.parts.TraceRecordEditPart;

/**
 * This is a helper list attached as property to the graphical viewer. It is
 * used by refresh commands to retrieve the edit parts of the trace records in
 * order to perform an update. The trace record could update itself, but traces
 * are only visible after according edit parts of the connected elements have
 * been created, which is usually after the model has been updated. That is, the
 * update of the trace record usually occurs <em>before</em> before edit parts
 * of the connected elements were created. With this list, the trace record edit
 * parts can be updated programmatically <em>after</em> the needed edit parts
 * were created.
 * <p>
 * Use {@link #registerTraceRecordEditPart(TraceRecordEditPart)} to attach an
 * edit part to the refresher, this method will automatically create a refresher
 * instance and attach it to the viewer.
 * </p>
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since May 14, 2009
 */
public class TraceRecordPartRefresher {
	/**
	 * Logger for this class
	 */
	private static final Logger log =
		Logger.getLogger(TraceRecordPartRefresher.class.getName());

	public final static String KEY = "mitra.traces.ui.edit.TraceRecordPartList";

	Vector<TraceRecordEditPart> traceRecordEditParts;

	/**
	 * Adds the given trace record edit part to the refresher, the refresher is
	 * lazily created and attached to the edit part's viewer if necessary. This
	 * method is usually called by the edit parts
	 * {@link AbstractGraphicalEditPart#activate()} method. Do no forget to
	 * unregister the edit part in
	 * {@link AbstractGraphicalEditPart#deactivate()}.
	 * 
	 * @param traceRecordEditPart, edit part must be initialized, i.e. viewer
	 *            must be installed.
	 */
	public static void registerTraceRecordEditPart(
		TraceRecordEditPart traceRecordEditPart) {
		if (traceRecordEditPart == null) // parameter precondition
			throw new NullPointerException(
				"traceRecordEditPart must not be null");
		EditPartViewer viewer = traceRecordEditPart.getViewer();
		if (viewer == null) // parameter precondition
			throw new NullPointerException(
				"traceRecordEditPart's viewer must not be null, "
						+ "i.e. edit part must be fully initialized");

		TraceRecordPartRefresher refresher =
			(TraceRecordPartRefresher) viewer.getProperty(KEY);
		if (refresher == null) {
			refresher = new TraceRecordPartRefresher();
			viewer.setProperty(KEY, refresher);
		}
		refresher.add(traceRecordEditPart);
	}

	/**
	 * Removes the given trace record edit part from the refresher. This method
	 * is usually called by the edit part's
	 * {@link AbstractGraphicalEditPart#deactivate()} method.
	 * 
	 * @param traceRecordEditPart
	 */
	public static void unregisterTraceRecordEditPart(
		TraceRecordEditPart traceRecordEditPart) {
		EditPartViewer viewer = traceRecordEditPart.getViewer();
		TraceRecordPartRefresher refresher =
			(TraceRecordPartRefresher) viewer.getProperty(KEY);
		if (refresher != null) {
			refresher.remove(traceRecordEditPart);
		}
	}

	public static class RefreshRunner {
		Display display;

		TraceRecordPartRefresher refresher;

		/**
		 * @param i_display
		 * @param i_refresher
		 */
		public RefreshRunner(Display i_display,
				TraceRecordPartRefresher i_refresher) {
			super();
			display = i_display;
			refresher = i_refresher;
		}

		public void run() {
			if (display == null) {
				log
					.warning("Cannot refresh connections, no current display found"); //$NON-NLS-1$
				return;
			}
			if (refresher != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						refresher.refresh();
					}
				});
			}
		}

	}

	public static RefreshRunner prepareRefresh(EditPartViewer viewer) {
		final TraceRecordPartRefresher refresher =
			(TraceRecordPartRefresher) viewer.getProperty(KEY);
		Display display = Display.getCurrent();
		return new RefreshRunner(display, refresher); 
	}

	/**
	 * Refreshes all trace records inside a given viewer.
	 * 
	 * @param viewer
	 */
	public static void refresh(EditPartViewer viewer) {
		final TraceRecordPartRefresher refresher =
			(TraceRecordPartRefresher) viewer.getProperty(KEY);
		if (refresher != null) {
			// Display display = Display.getCurrent();
			// if (display == null) {
			// log
			//					.warning("Cannot refresh connections, no current display found"); //$NON-NLS-1$
			// } else {
			// display.asyncExec(new Runnable() {
			// public void run() {
			// refresher.refresh();
			// }
			// });
			// }
			refresher.refresh();

		}
	}

	/**
	 * Do not create this refreher directly, it is lazily created and attached
	 * to the viewer by
	 * {@link #registerTraceRecordEditPart(TraceRecordEditPart)}.
	 */
	protected TraceRecordPartRefresher() {
		traceRecordEditParts = new Vector<TraceRecordEditPart>(2);
	}

	/**
	 * Delegate method
	 * 
	 * @param traceRecordEditPart
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public void add(TraceRecordEditPart traceRecordEditPart) {
		traceRecordEditParts.add(traceRecordEditPart);
	}

	/**
	 * Delegate method
	 * 
	 * @see java.util.Vector#clear()
	 */
	public void clear() {
		traceRecordEditParts.clear();
	}

	/**
	 * Delegate method
	 * 
	 * @param traceRecordEditPart
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public void remove(TraceRecordEditPart traceRecordEditPart) {
		traceRecordEditParts.remove(traceRecordEditPart);
	}

	/**
	 * Refreshes all trace record edit parts.
	 */
	public void refresh() {
		for (TraceRecordEditPart traceRecordEditPart : traceRecordEditParts) {
			traceRecordEditPart.doStructureRefresh();
		}
	}

}
