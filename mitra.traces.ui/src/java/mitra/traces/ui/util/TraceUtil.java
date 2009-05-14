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
package mitra.traces.ui.util;

import java.util.ArrayList;
import java.util.List;

import de.feu.mitra.traces.ParameterType;
import de.feu.mitra.traces.Trace;
import de.feu.mitra.traces.TraceElement;

/**
 * TraceUtil
 * There should really be more documentation here.
 *
 * @author 	Jens von Pilgrim
 * @version	$Revision$
 * @since 	May 14, 2009
 */
public class TraceUtil {
	
	public static List<TraceElement> getSourceElements(Trace trace) {
		return getElements(trace, ParameterType.SOURCE);
	}
	
	public static List<TraceElement> getTargetElements(Trace trace) {
		return getElements(trace, ParameterType.TARGET);
	}
	
	public static List<TraceElement> getElements(Trace trace, ParameterType type) {
		List<TraceElement> result = new ArrayList<TraceElement>(); 
		for (TraceElement traceElement: trace.getElements()) {
			if (traceElement.getTraceType()==type) {
				result.add(traceElement);
			}
		}
		return result;
	}
	
	public static boolean isAuto(Trace trace) {
		if (trace==null || trace.getRuleInfo()==null) return false;
		return trace.getRuleInfo().contains("auto");
	}

}
