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

import mitra.traces.ui.edit.figures.TraceElementFigure;
import mitra.traces.ui.util.TraceUtil;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef3d.ext.intermodel.ConnectedElementAdapter;
import org.eclipse.gef3d.ext.intermodel.ConnectedElementEditPart;
import org.eclipse.swt.graphics.Color;

import de.feu.mitra.traces.Trace;
import de.feu.mitra.traces.TraceElement;

/**
 * This is the connection between the center (i.e. the
 * {@link TraceEditPart} and the source or target. Note that the actual
 * source or target are not directly connected since the source and target model
 * must not be modified (which would be the case if the actual model elements
 * were connected). Instead, the connection here connects the center box and
 * {@link ConnectedElementAdapter} managed by {@link ConnectedElementEditPart}.
 * <p>
 * The color of the line reflects the depth of the model element, i.e.
 * the root element connection (the diagrams themselves) is drawn dark red,
 * top level element connections red and nested element connections green or 
 * magenta. 
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 21.01.2008
 * @see $HeadURL:
 *      https://gorgo.fernuni-hagen.de/OpenglGEF/trunk/de.feu.gef3d.samples.multieditor/src/java/de/feu/gef3d/samples/unitrace/edit/MergedTraceElementEditPart.java $
 */
public class TraceElementEditPart extends AbstractConnectionEditPart {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger
			.getLogger(TraceElementEditPart.class.getName());

	public static Color LEVEL_COLORS[] = { 
		ColorConstants.red, ColorConstants.red, ColorConstants.orange, ColorConstants.orange 
		}; 
	public static Color LEVEL_COLOR_AUTO[] = {
		ColorConstants.yellow, ColorConstants.yellow, ColorConstants.gray, ColorConstants.lightGray 
	};
	
	

	/**
	 * 
	 */
	public TraceElementEditPart(TraceElement i_model) {
		setModel(i_model);

		// if (log.isLoggable(Level.INFO)) {
		// log.info("TraceElement - created link edit part for " +
		// i_model); //$NON-NLS-1$
		// }
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		TraceElementFigure fig = new TraceElementFigure();

		int iLevel = calcLevel();
		if (iLevel >= LEVEL_COLORS.length)
			iLevel = LEVEL_COLORS.length - 1;

		boolean bAuto = TraceUtil.isAuto((Trace) getTraceElement().eContainer());
		
		fig.setForegroundColor(
				bAuto ? LEVEL_COLOR_AUTO[iLevel] : LEVEL_COLORS[iLevel]
				                                                );
			
		// if (log.isLoggable(Level.INFO)) {
		// log.info("Level: " + calcLevel()); //$NON-NLS-1$
		// }

		return fig;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// TODO implement method
		// TraceElementEditPart.createEditPolicies

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#setSource(org.eclipse.gef.EditPart)
	 */
	@Override
	public void setSource(EditPart i_editPart) {
		super.setSource(i_editPart);

		// if (log.isLoggable(Level.INFO)) {
		// log.info("EditPart - i_editPart=" + i_editPart); //$NON-NLS-1$
		// }
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#setTarget(org.eclipse.gef.EditPart)
	 */
	@Override
	public void setTarget(EditPart i_editPart) {
		super.setTarget(i_editPart);

		// if (log.isLoggable(Level.INFO)) {
		// log.info("EditPart - i_editPart=" + i_editPart); //$NON-NLS-1$
		// }
	}

	/**
	 * Convenience method, returns casted {@link #getModel()}
	 * 
	 * @return
	 */
	public TraceElement getTraceElement() {
		return (TraceElement) getModel();
	}

	public int calcLevel() {
		TraceElement traceElement = getTraceElement();
		EObject eobj = traceElement.getElement();
//		if (eobj instanceof Relationship)
//			return 1;
		int iLevel = 0;
		while (eobj.eContainer() != null && eobj.eContainer() != eobj) {
			eobj = eobj.eContainer();
			iLevel++;
		}
		return iLevel;

	}

	/**
	 * @param i_visible
	 */
	public void setVisible(boolean i_visible) {
		getFigure().setVisible(i_visible);
		refreshVisuals();
	}

}
