/*******************************************************************************
 * Copyright (c) 2009 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.draw3d;

import java.util.EventObject;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw3d.geometry.IVector3f;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

/**
 * MouseEvent3D There should really be more documentation here.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 21.06.2009
 */
public class MouseEvent3D extends MouseEvent {

	private static final Event DUMMY = new Event();

	private static final long serialVersionUID = -7581719169381368361L;

	private static Event getEvent(Object i_object) {

		if (i_object instanceof Event)
			return (Event) i_object;

		EventObject eventObject = (EventObject) i_object;
		DUMMY.widget = (Widget) eventObject.getSource();

		return DUMMY;
	}

	/**
	 * The depth value.
	 */
	public float depth;

	public int mouseX;

	public int mouseY;

	/**
	 * The 3D location in world space that has been derived from the mouse
	 * coordinates.
	 */
	public IVector3f worldLoc;

	/**
	 * Creates a new event from the given base event.
	 * 
	 * @param i_e
	 *            the base event
	 */
	public MouseEvent3D(MouseEvent i_e, Point i_surfaceLoc,
			IVector3f i_worldLoc, float i_depth) {

		super(getEvent(i_e));

		this.x = i_surfaceLoc.x;
		this.y = i_surfaceLoc.y;

		this.mouseX = i_e.x;
		this.mouseY = i_e.y;
		this.depth = i_depth;
		this.worldLoc = i_worldLoc;

		// from MouseEvent
		this.button = i_e.button;
		this.stateMask = i_e.stateMask;
		this.count = i_e.count;

		// from TypedEvent
		this.display = i_e.display;
		this.widget = i_e.widget;
		this.time = i_e.time;
		this.data = i_e.data;

		// from EventObject
		this.source = i_e.widget;
	}

}
