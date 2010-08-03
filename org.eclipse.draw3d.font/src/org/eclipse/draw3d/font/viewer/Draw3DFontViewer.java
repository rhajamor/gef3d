package org.eclipse.draw3d.font.viewer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.GraphicsEnvironment;

import org.eclipse.draw3d.font.IDraw3DFont;
import org.eclipse.draw3d.font.IDraw3DGlyphVector;
import org.eclipse.draw3d.font.LwjglVectorFont;
import org.eclipse.draw3d.font.IDraw3DFont.Flag;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class Draw3DFontViewer extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID =
		"org.eclipse.draw3d.font.viewer.Draw3DFontViewer";

	private GLCanvas m_canvas;

	private Combo m_fontList;

	private Combo m_sizeList;

	private Button m_bold;

	private Button m_italic;

	/**
	 * The constructor.
	 */
	public Draw3DFontViewer() {
		// nothing to do
	}

	private void createGLCanvas(Composite i_parent) {
		GLData data = new GLData();
		data.doubleBuffer = true;
		data.sampleBuffers = 1;
		data.samples = 4;

		m_canvas = new GLCanvas(i_parent, SWT.NONE, data);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		m_canvas.setLayoutData(gridData);

		try {
			m_canvas.setCurrent();
			GLContext.useContext(m_canvas);
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite i_parent) {
		Composite container = createContainer(i_parent);
		createGLCanvas(container);
		createFontSelector(container);

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_MULTISAMPLE);

		glDisable(GL_CULL_FACE);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glShadeModel(GL_FLAT);

		glClearColor(1, 1, 1, 1);
		glColor4f(0, 0, 0, 1);

		// tell GL to pack pixels as tightly as possible
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		m_canvas.addControlListener(new ControlListener() {

			public void controlMoved(ControlEvent i_e) {
				// TODO implement method ControlListener.controlMoved

			}

			public void controlResized(ControlEvent i_e) {
				Rectangle bounds = m_canvas.getBounds();
				glViewport(0, 0, bounds.width, bounds.height);

				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();

				glOrtho(0, bounds.width, bounds.height, 0, -10, 10);
			}
		});

		m_canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent i_e) {
				glClear(GL_COLOR_BUFFER_BIT);

				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();

				String name = m_fontList.getText();
				int size = 0;
				String sizeStr = m_sizeList.getText();
				if (sizeStr != null) {
					try {
						size = Integer.parseInt(sizeStr);
					} catch (NumberFormatException e) {
						// ignore
					}
				}

				if (name != null && name.length() > 0 && size > 0) {
					Flag[] flags =
						Flag.getFlags(m_bold.getSelection(),
							m_italic.getSelection());
					IDraw3DFont font =
						new LwjglVectorFont(name, size, 1, flags);
					font.initialize();
					IDraw3DGlyphVector glyphs =
						font.createGlyphVector("The quick brown fox jumps over the lazy dog.");
					glyphs.render();
					glyphs.dispose();
					font.dispose();
				}

				m_canvas.swapBuffers();
			}
		});

		m_canvas.redraw();
	}

	private static final int[] FONT_SIZES =
		new int[] { 9, 10, 11, 12, 13, 14, 18, 24, 36, 48, 64 };

	private void createFontSelector(Composite i_parent) {
		Composite container = new Composite(i_parent, SWT.NONE);
		container.setLayout(new RowLayout(SWT.HORIZONTAL));

		m_fontList = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		GraphicsEnvironment ge =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		String fontNames[] = ge.getAvailableFontFamilyNames();
		m_fontList.setItems(fontNames);
		for (int i = 0; i < fontNames.length; i++)
			if ("Arial".equals(fontNames[i]))
				m_fontList.select(i);

		m_fontList.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent i_e) {
				m_canvas.redraw();
			}

			public void widgetDefaultSelected(SelectionEvent i_e) {
				widgetSelected(i_e);
			}
		});

		m_sizeList = new Combo(container, SWT.DROP_DOWN);
		m_sizeList.setLayoutData(new RowData(60, 24));
		String[] sizeNames = new String[FONT_SIZES.length];
		for (int i = 0; i < FONT_SIZES.length; i++)
			sizeNames[i] = Integer.toString(FONT_SIZES[i]);
		m_sizeList.setItems(sizeNames);
		m_sizeList.select(2);
		m_sizeList.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent i_e) {
				m_canvas.redraw();
			}

			public void widgetDefaultSelected(SelectionEvent i_e) {
				widgetSelected(i_e);
			}
		});

		m_bold = new Button(container, SWT.CHECK);
		m_bold.setText("Bold");
		m_bold.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent i_e) {
				m_canvas.redraw();
			}

			public void widgetDefaultSelected(SelectionEvent i_e) {
				widgetSelected(i_e);
			}
		});

		m_italic = new Button(container, SWT.CHECK);
		m_italic.setText("Italic");
		m_italic.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent i_e) {
				m_canvas.redraw();
			}

			public void widgetDefaultSelected(SelectionEvent i_e) {
				widgetSelected(i_e);
			}
		});
	}

	private Composite createContainer(Composite i_parent) {
		Composite container = new Composite(i_parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 2;
		container.setLayout(layout);
		return container;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		m_canvas.setFocus();
	}
}