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

import java.util.logging.Logger;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef3d.ext.intermodel.ConnectedElementAdapter;
import org.eclipse.gef3d.ext.intermodel.ConnectedElementEditPart;

import de.feu.mitra.traces.Trace;
import de.feu.mitra.traces.TraceElement;
import de.feu.mitra.traces.TraceRecord;


/**
 * Edit part factory for creating merged edit parts. The following edit parts are
 * created:
 * 
 * <table>
 * <tr><th>Edit Part</th><th>Model Element</th></tr>
 * <tr><td>{@link TraceRecordEditPart}</td><td>{@link MergedTraceModel}</td></tr>
 * <tr><td>{@link TraceElementEditPart}</td><td>{@link MergedTraceableElement}</td></tr>
 * <tr><td>{@link TraceEditPart}</td><td>{@link MergedTrace}</td></tr>
 * <tr><td>{@link ConnectedElementEditPart}</td><td>{@link ConnectedElementAdapter}</td></tr>
 * </table>
 * 
 * Note that the last pair is not part of this package here.
 * 
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 18.01.2008
 * @see $HeadURL:
 *      https://gorgo.fernuni-hagen.de/OpenglGEF/trunk/de.feu.gef3d.samples.multieditor/src/java/de/feu/gef3d/samples/unitrace/edit/MergedTraceEditPartFactory.java $
 */
public class TraceEditPartFactory implements EditPartFactory {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(TraceEditPartFactory.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 *      java.lang.Object)
	 */
	public EditPart createEditPart(EditPart i_context, Object i_model) {

//		if (log.isLoggable(Level.INFO)) {
//			log.info("create TraceModel Edit Part- i_model=" + i_model); //$NON-NLS-1$
//		}

		if (i_model instanceof TraceRecord) {
			return new TraceRecordEditPart((TraceRecord) i_model);
		}
		if (i_model instanceof TraceElement) {
			return new TraceElementEditPart(
					(TraceElement) i_model);
		}
		if (i_model instanceof Trace) {
			return new TraceEditPart((Trace) i_model);
		}
		if (i_model instanceof ConnectedElementAdapter) {
			return new ConnectedElementEditPart(
					(ConnectedElementAdapter) i_model);
		}
		return null; // no nodes supported yet
	}

}
