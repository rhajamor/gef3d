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

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * EclipseConsoleFormatter is an JDK formatter for the Eclipse console. The
 * messages are printed in one line if possible, the location of the code
 * producing the log message can be linked to the actual source code.
 * <p>
 * This formatter is configured in logging.properties.
 * 
 * @author Jens von Pilgrim
 * @version $Revision$
 * @since 01.10.2004
 */
public class EclipseConsoleFormatter extends Formatter {

	private String m_strLineSeparator;

	private HashSet m_setStacktracePackagePrefixes;

	private boolean m_bAbbreviatePackagenames;

	private boolean m_bShowAllStacktraces;

	private boolean m_bShowTime;

	private boolean m_bWrapLines;

	private int m_iLocationTab;

	private String m_strPrefix;

	private static SimpleDateFormat ISO_LONG = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 
	 */
	public EclipseConsoleFormatter() {
		m_strLineSeparator = System.getProperty("line.separator");
		m_setStacktracePackagePrefixes = new HashSet();
		m_bShowAllStacktraces = true;
		m_bShowTime = false;
		m_bWrapLines = true;
		m_iLocationTab = 80;
		m_strPrefix = null;
		m_bAbbreviatePackagenames = false;
	}

	/**
	 * Adds a package name to the list of packages which are to be displayed in
	 * stack trace.
	 * 
	 * @param i_strPackage
	 */
	public void addStacktracePackage(String i_strPackage) {
		synchronized (m_setStacktracePackagePrefixes) {
			m_setStacktracePackagePrefixes.add(i_strPackage);
		}
	}

	public void removeStacktracePackage(String i_strPackage) {
		String str;

		synchronized (m_setStacktracePackagePrefixes) {

			for (Iterator iter = m_setStacktracePackagePrefixes.iterator(); iter
					.hasNext();) {

				str = (String) iter.next();
				if (str.startsWith(i_strPackage)) {
					iter.remove();
				}

			}
		}
	}

	public void showAllStacktraces(boolean i_bFlag) {
		m_bShowAllStacktraces = i_bFlag;
	}

	/**
	 * Overriddes super class definition.
	 * 012345678901234567890123456789012345678901234567890123456789 WARNING
	 * ValidatorPlugIn#initResources: Loading validation rules file from
	 * '/WEB-INF/validator-rules.xml' 1234567890123456#1234567890123456 FINEST
	 * 
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		String strClassname = record.getSourceClassName();
		// String strMethodname = record.getSourceMethodName();
		String strMsg = record.getMessage();
		String strLevelName = record.getLevel().getName().substring(0, 1);

		StringBuffer strb = new StringBuffer();

		if (m_strPrefix != null && m_strPrefix.length() != 0) {
			strb.append('[');
			strb.append(m_strPrefix);
			strb.append("] ");
		}

		if (m_bShowTime) {
			Date date = new Date();
			strb.append(ISO_LONG.format(date));
			strb.append(' ');
		}

		strb.append(strLevelName);
		strb.append(' ');

		// iLength = strMethodname.length();
		// iBuffer = (iLength<16) ? 16-iLength : 0;
		// iBuffer = appendMax(strb, strClassname, 12+iBuffer, LEFT);
		// strb.append('#');
		// appendMax(strb, strMethodname, 16+iBuffer, RIGHT);
		// strb.append(':');
		// strb.append(' ');

		strb.append(strMsg);

		int iTabs = m_iLocationTab - strMsg.length();
		if (iTabs < 1) {
			if (m_bWrapLines) {
				strb.append(m_strLineSeparator);
				iTabs = m_iLocationTab + 2;
			} else {
				iTabs = 4;
			}
		}
		fillSpaces(strb, iTabs);

		Exception ex = new Exception();
		StackTraceElement[] elements = ex.getStackTrace();
		for (StackTraceElement element : elements) {
			if (element.getClassName().equals(strClassname)) {
				strb.append("at ");
				if (m_bAbbreviatePackagenames) {
					abbreviate(strb, element.getClassName());
				} else {
					strb.append(element.getClassName());
				}
				strb.append('.').append(element.getMethodName()).append('(')
						.append(element.getFileName()).append(':').append(
								element.getLineNumber()).append(")");
				break;
			}
		}

		Throwable thrown = record.getThrown();
		if (thrown != null) {

			printStacktrace(strb, thrown);

		}

		strb.append(m_strLineSeparator);
		return strb.toString();
	}

	/**
	 * @param i_className
	 * @return
	 */
	private void abbreviate(StringBuffer io_strb, String i_className) {

		StringTokenizer st = new StringTokenizer(i_className, ".", false);
		int total = st.countTokens();
		int i = 0;
		for (; i < total - 2; i++) {
			io_strb.append(st.nextToken().charAt(0));
			io_strb.append('.');
		}
		while (st.hasMoreTokens()) {
			io_strb.append(st.nextToken());
			if (st.hasMoreTokens()) {
				io_strb.append('.');
			}
		}

	}

	public void printStacktrace(StringBuffer o_strb, Throwable i_thrown) {
		printStacktrace(o_strb, i_thrown, "");
	}

	private void printStacktrace(StringBuffer o_strb, Throwable i_thrown,
			String i_strIndent) {
		StackTraceElement[] elements;
		o_strb.append(m_strLineSeparator);
		o_strb.append("   ").append(i_thrown.getClass().getName());
		if (i_thrown.getMessage() != null) {
			o_strb.append("   ").append(i_thrown.getMessage());
		}

		// StackTraceElement[]
		elements = i_thrown.getStackTrace();
		// StringBuffer strbSpaces = new StringBuffer();
		int iStackLength = elements.length;
		int iSkipped = 0;
		boolean bForceNext = true; // show first
		boolean bIsStacktracePackagePrefix;
		for (int i = 0; i < iStackLength; i++) {
			bIsStacktracePackagePrefix = isStacktracePackage(elements[i]
					.getClassName());

			if (i == iStackLength - 1 || m_bShowAllStacktraces || bForceNext
					|| bIsStacktracePackagePrefix) {

				bForceNext = bIsStacktracePackagePrefix;

				if (iSkipped > 0) {
					o_strb.append(m_strLineSeparator);
					o_strb.append(i_strIndent);
					o_strb.append("    ... skipped ").append(iSkipped).append(
							" lines");
					iSkipped = 0;
				}

				o_strb.append(m_strLineSeparator);
				// strb.append(strbSpaces);
				o_strb.append(i_strIndent);
				o_strb.append("    at ");
				if (m_bAbbreviatePackagenames) {
					abbreviate(o_strb, elements[i].getClassName());
				} else {
					o_strb.append(elements[i].getClassName());
				}
				o_strb.append('.').append(elements[i].getMethodName()).append(
						'(').append(elements[i].getFileName()).append(':')
						.append(elements[i].getLineNumber()).append(")");
			} else {
				iSkipped++;
			}
		}

		Throwable rootCause = i_thrown.getCause();
		try {
			if (rootCause == null) {
				Class clazz = i_thrown.getClass();
				Method method = clazz.getMethod("getRootCause", new Class[] {});
				if (method != null) {
					Object obj = method.invoke(i_thrown, new Object[] {});
					if (obj instanceof Throwable) {
						rootCause = (Throwable) obj;
					}
				}
			}
		} catch (Exception ex) {
		}

		if (rootCause != null) {
			o_strb.append(m_strLineSeparator);
			o_strb.append(i_strIndent);
			o_strb.append("  root cause: ");
			printStacktrace(o_strb, rootCause, i_strIndent + "    ");
		}
	}

	private boolean isStacktracePackage(final String i_strClassname) {
		for (Iterator iter = m_setStacktracePackagePrefixes.iterator(); iter
				.hasNext();) {
			if (i_strClassname.startsWith((String) iter.next())) {
				return true;
			}
		}
		return false;
	}

	private final static String SPACES = "                                                                    "
			+ "                                                                    "
			+ "                                                                    "
			+ "                                                                    ";

	private void fillSpaces(StringBuffer o_strStringBuffer, int i_iCount) {
		o_strStringBuffer.append(SPACES.substring(0, i_iCount));
	}

	/**
	 * @param i_b
	 */
	public void setShowTime(boolean i_b) {
		m_bShowTime = i_b;
	}

	/**
	 * @param i_b
	 */
	public void setWrapLines(boolean i_b) {
		m_bWrapLines = i_b;
	}

	/**
	 * @param i_i
	 */
	public void setLocationTab(int i_i) {
		m_iLocationTab = i_i;
	}

	/**
	 * @return
	 */
	public String getPrefix() {
		return m_strPrefix;
	}

	/**
	 * @param i_string
	 */
	public void setPrefix(String i_string) {
		m_strPrefix = i_string;
	}

	public boolean isAbbreviatePackagenames() {
		return m_bAbbreviatePackagenames;
	}

	public void setAbbreviatePackagenames(boolean i_abbreviatePackagenames) {
		m_bAbbreviatePackagenames = i_abbreviatePackagenames;
	}

}
