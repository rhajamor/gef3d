/*******************************************************************************
 * Copyright (c) 2008 Jens von Pilgrim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kristian Duske - initial API and implementation
 ******************************************************************************/
package org.eclipse.gef3d.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * Allows editing of bit fields.
 * 
 * @author Kristian Duske
 * @version $Revision$
 * @since 19.02.2009
 */
public class BitFieldEditor extends FieldEditor {

	private Button[] checkboxes;

	private Composite groupBox;

	private int indent = HORIZONTAL_GAP;

	private String[] labels;

	private int numColumns;

	private boolean useGroup;

	private int value;

	private int[] values;

	/**
	 * Creates a bit field editor. This constructor does not use a
	 * <code>Group</code> to contain the checkboxes. It is equivalent to using
	 * the following constructor with <code>false</code> for the
	 * <code>useGroup</code> argument.
	 * <p>
	 * Example usage:
	 * 
	 * <pre>
	 * 	BitFieldEditor editor= new BitFieldEditor(
	 * 		&quot;GeneralPage.Modifiers&quot;, resName, 1,
	 * 		new String[] {
	 * 			{&quot;Control key&quot;},
	 * 			{&quot;Shift key&quot;},
	 * 			{&quot;Alt key&quot;}
	 * 		},
	 *      new int[] {1, 2, 4}
	 *      parent);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param numColumns the number of columns for the checkbox presentation
	 * @param labels list of checkbox labels
	 * @param values list of integer values; the returned value is the bitwise
	 *            combination of these
	 * @param parent the parent of the field editor's control
	 */
	public BitFieldEditor(String name, String labelText, int numColumns,
			String[] labels, int[] values, Composite parent) {
		this(name, labelText, numColumns, labels, values, parent, false);
	}

	/**
	 * Creates a bit field editor.
	 * <p>
	 * Example usage:
	 * 
	 * <pre>
	 * BitFieldEditor editor = new BitFieldEditor(
	 * 		&quot;GeneralPage.Modifiers&quot;, resName, 1,
	 * 		new String[] {
	 * 			{&quot;Control key&quot;},
	 * 			{&quot;Shift key&quot;},
	 * 			{&quot;Alt key&quot;}
	 * 		},
	 *      new int[] {1, 2, 4}
	 *      parent, true);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param numColumns the number of columns for the radio button presentation
	 * @param labels list of checkbox labels
	 * @param values list of integer values; the returned value is the bitwise
	 *            combination of these
	 * @param parent the parent of the field editor's control
	 * @param useGroup whether to use a Group control to contain the radio
	 *            buttons
	 */
	public BitFieldEditor(String name, String labelText, int numColumns,
			String[] labels, int[] values, Composite parent, boolean useGroup) {

		init(name, labelText);
		Assert.isTrue(labels.length == values.length);
		this.labels = labels;
		this.values = values;
		this.numColumns = numColumns;
		this.useGroup = useGroup;
		createControl(parent);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int i_numColumns) {

		Control control = getLabelControl();
		if (control != null) {
			((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		}
		((GridData) groupBox.getLayoutData()).horizontalSpan = numColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite,
	 *      int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		if (useGroup) {
			Control control = getGroupControl(parent);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			control.setLayoutData(gd);
		} else {
			Control control = getLabelControl(parent);
			GridData gd = new GridData();
			gd.horizontalSpan = numColumns;
			control.setLayoutData(gd);
			control = getGroupControl(parent);
			gd = new GridData();
			gd.horizontalSpan = numColumns;
			gd.horizontalIndent = indent;
			control.setLayoutData(gd);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {

		updateValue(getPreferenceStore().getInt(getPreferenceName()));
	}

	/**
	 * Updates the checkboxes according to the given value.
	 * 
	 * @param newValue the value
	 */
	public void updateValue(int newValue) {

		this.value = newValue;
		if (checkboxes == null)
			return;

		for (int i = 0; i < checkboxes.length; i++) {
			Button checkbox = checkboxes[i];
			int data = (Integer) checkbox.getData();
			checkbox.setSelection((newValue & data) != 0);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {

		updateValue(getPreferenceStore().getDefaultInt(getPreferenceName()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), value);
	}

	private Control getGroupControl(Composite parent) {

		if (groupBox == null) {
			Font font = parent.getFont();
			if (useGroup) {
				Group group = new Group(parent, SWT.NONE);
				group.setFont(font);
				String text = getLabelText();
				if (text != null)
					group.setText(text);
				groupBox = group;
				GridLayout layout = new GridLayout();
				layout.horizontalSpacing = HORIZONTAL_GAP;
				layout.numColumns = numColumns;
				groupBox.setLayout(layout);
			} else {
				groupBox = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.marginWidth = 0;
				layout.marginHeight = 0;
				layout.horizontalSpacing = HORIZONTAL_GAP;
				layout.numColumns = numColumns;
				groupBox.setLayout(layout);
				groupBox.setFont(font);
			}

			checkboxes = new Button[labels.length];
			for (int i = 0; i < labels.length; i++) {
				Button checkbox = new Button(groupBox, SWT.CHECK | SWT.LEFT);
				checkboxes[i] = checkbox;
				checkbox.setText(labels[i]);
				checkbox.setData(values[i]);
				checkbox.setFont(font);
				checkbox.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						int oldValue = value;
						Button button = (Button) event.widget;
						int bit = (Integer) button.getData();
						if (button.getSelection())
							value |= bit;
						else
							value &= ~bit;

						setPresentsDefaultValue(false);
						fireValueChanged(VALUE, oldValue, value);
					}
				});
			}
			groupBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					groupBox = null;
					checkboxes = null;
				}
			});
		} else {
			checkParent(groupBox, parent);
		}
		return groupBox;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {

		return 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void setEnabled(boolean enabled, Composite parent) {

		if (!useGroup)
			super.setEnabled(enabled, parent);

		for (int i = 0; i < checkboxes.length; i++) {
			checkboxes[i].setEnabled(enabled);
		}

	}

	/**
	 * Sets the indent used for the first column of the checkbox matrix.
	 * 
	 * @param indent the indent (in pixels)
	 */
	public void setIndent(int indent) {
		if (indent < 0) {
			this.indent = 0;
		} else {
			this.indent = indent;
		}
	}

}
